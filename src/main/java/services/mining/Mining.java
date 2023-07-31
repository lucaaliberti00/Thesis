package services.mining;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import analysis.utils.RuleMatch;
import ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.AlgoTNS;
import ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.AlgoTopSeqRules;

import ca.pfv.spmf.datastructures.redblacktree.RedBlackTree;
import ca.pfv.spmf.tools.MemoryLogger;
import commons.mining.model.KeyType;
import commons.mining.model.Rule;
import commons.mining.model.Rules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import services.mining.spmf.IdeaSequenceDatabase;
import services.mining.spmf.SequenceDatabases;

import static analysis.SimulationRulesAnalysis.saveToCSV;

public class Mining {

    private static final Logger logger = LoggerFactory.getLogger(Mining.class);
    private static double minConf = 0.5;
    private static int k = 10;

    private static int delta = 2;

    public static void main(String[] args) {
        String dirSim = "C:\\Users\\lucaa\\Desktop\\FullSimulation\\";
        String dirRules = "data/rules/FullSimulation/TNS/";

        ArrayList<String> days = new ArrayList<>();
        days.add("2019-03-11");
        days.add("2019-03-12");
        days.add("2019-03-13");
        days.add("2019-03-14");
        days.add("2019-03-15");
        days.add("2019-03-16");
        days.add("2019-03-17");

        for (String day :days){
            String inputFile = dirSim + day + "\\aggregated_" + day + ".json";
            String ruleDB = dirRules + "ruleDB_" + day;

            run(inputFile, ruleDB);
        }


    }


    public static void run(String dataset, String ruleDB) {

        // Create sequential database
        IdeaSequenceDatabase sequenceDb = SequenceDatabases.fromFile(dataset, KeyType.SRC_IPV4);

        // Run algorithm
        logger.info("Running TopSeqRules algorithm");
        //AlgoTopSeqRules algo = new AlgoTopSeqRules();
        AlgoTNS algo = new AlgoTNS();
        RedBlackTree<ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.Rule> spmfRules = algo.runAlgorithm(k, sequenceDb.getDatabase(), minConf, delta);
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

        writeRuleToFile(new ArrayList<>(rules), ruleDB);
    }

    public static void writeRuleToFile(List<Rule> rules, String ruleDB){
        try (FileWriter writer = new FileWriter(ruleDB)) {
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

