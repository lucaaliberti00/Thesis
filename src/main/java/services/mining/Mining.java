package services.mining;


import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import analysis.utils.RuleMatch;
import ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.AlgoTNS;
import ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.AlgoTopSeqClassRules;
import ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.AlgoTopSeqRules;

import ca.pfv.spmf.algorithms.sequential_rules.trulegrowth.AlgoTRuleGrowth;
import ca.pfv.spmf.datastructures.redblacktree.RedBlackTree;
import ca.pfv.spmf.tools.MemoryLogger;
import commons.mining.model.Item;
import commons.mining.model.KeyType;
import commons.mining.model.Rule;
import commons.mining.model.Rules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import services.mining.spmf.IdeaSequenceDatabase;
import services.mining.spmf.SequenceDatabases;


public class Mining {

    private static final Logger logger = LoggerFactory.getLogger(Mining.class);
    private static double minConf = 0.5;
    private static int k = 10;
    private static int delta = 2;

    public static void main(String[] args) {
        String dirSim = "C:\\Users\\lucaa\\Desktop\\FullSimulation\\";
        String dirRules= "data/rules/FullSimulation/TNSNoPort/";

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

        //Collection<Rule> rules = miningTopSeqRules(sequenceDb);
        Collection<Rule> rules = miningTNS(sequenceDb);
        //Collection<Rule> rules = miningTopSeqClassRules(sequenceDb);

        assert rules != null;
        writeRuleToFile(new ArrayList<>(rules), ruleDB, "TNS");
    }

    public static void writeRuleToFile(List<Rule> rules, String ruleDB, String algorithm){
        try (FileWriter writer = new FileWriter(ruleDB)) {
            for (Rule rule : rules) {
                String ruleString = Rules.toSpmf(rule);
                String line = String.format("%s %d %d %.4f %s %s\n",
                        ruleString, rule.getSupport(), rules.size(), rule.getConfidence(), KeyType.SRC_IPV4, algorithm);
                writer.write(line);
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Error while saving rules to file", e);
        }
    }

    public static Collection<Rule> miningTopSeqRules(IdeaSequenceDatabase sequenceDb){
        // Run algorithm
        logger.info("Running TopSeqRules algorithm");
        AlgoTopSeqRules algo = new AlgoTopSeqRules();
        RedBlackTree<ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.Rule> spmfRules = algo.runAlgorithm(k, sequenceDb.getDatabase(), minConf);
        logger.info("TopSeqRules algorithm discovered {} rules", spmfRules.size());
        logger.info("Metrics: max memory usage {} MB", MemoryLogger.getInstance().getMaxMemory());
        logger.info("Metrics: total time running alg {} s", algo.getTotalTime() / 1000d);

        if (spmfRules.isEmpty()) {
            return null;
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
        return rules;
    }
    public static Collection<Rule> miningTNS(IdeaSequenceDatabase sequenceDb){
        // Run algorithm
        logger.info("Running TNS algorithm");
        AlgoTNS algo = new AlgoTNS();
        RedBlackTree<ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.Rule> spmfRules = algo.runAlgorithm(k, sequenceDb.getDatabase(), minConf, delta);
        logger.info("TopSeqRules algorithm discovered {} rules", spmfRules.size());
        logger.info("Metrics: max memory usage {} MB", MemoryLogger.getInstance().getMaxMemory());
        logger.info("Metrics: total time running alg {} s", algo.getTotalTime() / 1000d);

        if (spmfRules.isEmpty()) {
            return null;
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
        return rules;
    }
    public static Collection<Rule> miningTopSeqClassRules(IdeaSequenceDatabase sequenceDb){
        // Run algorithm
        logger.info("Running TopSeqClassRules algorithm");

        List<Integer> consequents = new ArrayList<>();
        for (Map.Entry<Integer, Item> e : sequenceDb.getItemMapping().entrySet()){
            if (!e.getValue().getCategory().equals("Recon.Scanning"))
                consequents.add(e.getKey());
        }

        int[] intArray = new int[consequents.size()];
        for (int i = 0; i < consequents.size(); i++) {
            intArray[i] = consequents.get(i);
        }

        AlgoTopSeqClassRules algo = new AlgoTopSeqClassRules();
        RedBlackTree<ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.ClassRule> spmfRules = algo.runAlgorithm(k, sequenceDb.getDatabase(), minConf, intArray);


        logger.info("TopSeqRules algorithm discovered {} rules", spmfRules.size());
        logger.info("Metrics: max memory usage {} MB", MemoryLogger.getInstance().getMaxMemory());
        logger.info("Metrics: total time running alg {} s", algo.getTotalTime() / 1000d);

        if (spmfRules.isEmpty()) {
            return null;
        }
        Collection<Rule> rules = new ArrayList<>();
        for (ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.ClassRule spmfRule : spmfRules) {
            int[] item2 = {spmfRule.getItemset2()};

            Rule rule = new Rule(
                    Arrays.stream(spmfRule.getItemset1()).mapToObj(sequenceDb.getItemMapping()::get).collect(Collectors.toSet()),
                    Arrays.stream(item2).mapToObj(sequenceDb.getItemMapping()::get).collect(Collectors.toSet()),
                    spmfRule.getAbsoluteSupport(),
                    spmfRule.getConfidence()
            );
            rules.add(rule);
        }
        return rules;
    }


}

