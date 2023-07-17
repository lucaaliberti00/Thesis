package services.mining;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.AlgoTNS;
import ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.AlgoTopSeqRules;

import ca.pfv.spmf.datastructures.redblacktree.RedBlackTree;
import ca.pfv.spmf.tools.MemoryLogger;
import commons.mining.model.KeyType;
import commons.mining.model.Rule;
import commons.mining.model.Rules;
import commons.mining.repository.RuleRepository;
import commons.mining.repository.SqliteRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import services.mining.spmf.IdeaSequenceDatabase;
import services.mining.spmf.SequenceDatabases;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class MiningAggregated {

    private static final Logger logger = LoggerFactory.getLogger(Mining.class);
    private double minConf = 0.5;
    private int k = 10;
    private boolean help;

    public static void main(String[] args) {
        MiningAggregated mining = new MiningAggregated();
        mining.run();
    }



    public void run() {

        // Create sequential database
        IdeaSequenceDatabase sequenceDb = SequenceDatabases.fromFile("data\\aggregated\\aggregated_train.idea", KeyType.SRC_IPV4);

        // Run algorithm
        logger.info("Running TopSeqRules algorithm");
        AlgoTopSeqRules algo = new AlgoTopSeqRules();
        RedBlackTree<ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.Rule> spmfRules = algo.runAlgorithm(k, sequenceDb.getDatabase(), minConf);
        ;
        logger.info("TopSeqRules algorithm discovered {} rules", spmfRules.size());
        logger.info("Metrics: max memory usage {} MB", MemoryLogger.getInstance().getMaxMemory());
        logger.info("Metrics: total time running alg {} s", algo.getTotalTime() / 1000d);

        // Save results to file
        if (spmfRules.isEmpty()) {
            // Have to exit execution because when the RedBlackTree is empty the iterator returns null -> NPE
            return;
        }

        Collection<Rule> rules = new ArrayList<>();

        for (ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.Rule spmfRule : spmfRules) {
            Rule rule = new Rule(
                    Arrays.stream(spmfRule.getItemset1()).mapToObj(sequenceDb.getItemMapping()::get).collect(Collectors.toSet()),
                    Arrays.stream(spmfRule.getItemset2()).mapToObj(sequenceDb.getItemMapping()::get).collect(Collectors.toSet()),
                    spmfRule.getAbsoluteSupport(),
                    spmfRule.getConfidence()
            );
            rules.add(rule);
        }

        try (FileWriter writer = new FileWriter("data\\rules\\ruleDBaggregated")) {
            for (Rule rule : rules) {
                String ruleString = Rules.toSpmf(rule);
                String line = String.format("%s %d %d %.4f %s %s\n",
                        ruleString, rule.getSupport(), rules.size(), rule.getConfidence(), KeyType.SRC_IPV4, "TopSeqRules");
                writer.write(line);
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Error while saving rules to file", e);
        }
    }
}

