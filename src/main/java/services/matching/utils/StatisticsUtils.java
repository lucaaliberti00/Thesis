package services.matching.utils;

import analysis.utils.RuleMatch;
import commons.mining.model.Rule;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsUtils {

    public static void computeStatsDays(HashMap<String, HashMap<String, Double>> statsDays, List<RuleMatch> ruleMatches, String day, int numAlerts, boolean verbose) {
        int predAlerts = 0;
        int succPreds = 0;

        //Se vuoi considerare la Top10
        //ruleMatches.sort(new SuccessRateComparator());
        //int numTopRules = Math.min(10, ruleMatches.size());


        for (int i = 0; i < ruleMatches.size(); i++) {
            predAlerts += ruleMatches.get(i).getPartialMatches();
            succPreds += ruleMatches.get(i).getFullMatches();
        }
        if (!statsDays.containsKey(day)) {
            statsDays.put(day, new HashMap<>());
        }
        statsDays.get(day).put("Alerts", (double) numAlerts);
        statsDays.get(day).put("Predicted Alerts", (double) predAlerts);
        statsDays.get(day).put("Successfull Predictions", (double) succPreds);
        statsDays.get(day).put("Success Rate", (double) (double) succPreds / predAlerts * 100);
        if (verbose) {
            System.out.println("Alerts: " + numAlerts);
            System.out.println("Predicted Alerts: " + predAlerts);
            System.out.println("Successful Predictions: " + succPreds);
            System.out.println("Success Rate: " + (double) succPreds / predAlerts * 100 + "%");
        }

    }

    public static void computeSupportRules(HashMap<Rule, List<Double>> supportXrule, List<Rule> rules, int alerts) {
        for (Rule r : rules) {
            if (!supportXrule.containsKey(r))
                supportXrule.put(r, new ArrayList<>());
            supportXrule.get(r).add((double) r.getSupport()/(double) alerts);
        }
    }

    public static void computeConfidenceRules(HashMap<Rule, List<Double>> confidenceXrule, List<Rule> rules) {
        for (Rule r : rules) {
            if (!confidenceXrule.containsKey(r))
                confidenceXrule.put(r, new ArrayList<>());
            confidenceXrule.get(r).add(r.getConfidence());
        }
    }

    public static void computeStatsSuccRate(HashMap<String, HashMap<Rule, Double>> statsSuccRate, List<RuleMatch> ruleMatches, String day) {
        if (!statsSuccRate.containsKey(day))
            statsSuccRate.put(day, new HashMap<>());

        for (RuleMatch rm : ruleMatches) {
            if (statsSuccRate.get(day).containsKey(rm.getCompleteRule()))
                System.err.println(" ATTENZIONE ERRORE ");
            statsSuccRate.get(day).put(rm.getCompleteRule(), rm.getSuccessRate());
        }
    }

    public static HashMap<Rule, List<Double>> computeSucRateXRule(HashMap<String, HashMap<Rule, Double>> statsSuccRate) {

        HashMap<Rule, List<Double>> succRateXrule = new HashMap<>();

        for (Map.Entry<String, HashMap<Rule, Double>> e : statsSuccRate.entrySet()) {
            for (Map.Entry<Rule, Double> f : e.getValue().entrySet()) {

                if (!succRateXrule.containsKey(f.getKey()))
                    succRateXrule.put(f.getKey(), new ArrayList<>());
                succRateXrule.get(f.getKey()).add(f.getValue());
            }
        }

        return succRateXrule;
    }

    public static double calculateMean(List<Double> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("The list is empty or null.");
        }

        double sum = 0.0;
        for (Double value : values) {
            sum += value;
        }
        return sum / values.size();
    }

    public static double calculateStandardDeviation(List<Double> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("The list is empty or null.");
        }

        double mean = calculateMean(values);
        double sumSquaredDifferences = 0.0;

        for (Double value : values) {
            double difference = value - mean;
            sumSquaredDifferences += difference * difference;
        }

        double variance = sumSquaredDifferences / values.size();
        return Math.sqrt(variance);
    }

    public static int countLines(String filePath) {
        int lineCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while (br.readLine() != null) {
                lineCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lineCount;
    }


    public static void printStatsDays(HashMap<String, HashMap<String, Double>> statsDays) {
        for (Map.Entry<String, HashMap<String, Double>> e : statsDays.entrySet()) {
            System.out.println("\t\tDAY " + e.getKey());

            for (Map.Entry<String, Double> f : e.getValue().entrySet()) {
                System.out.println(f.getKey() + ": " + f.getValue());
            }
        }
    }

    public static void printSupportRules(HashMap<Rule, List<Double>> supportXrule) {
        System.out.println("\t\tSUPPORT X RULE");
        for (Map.Entry<Rule, List<Double>> e : supportXrule.entrySet()) {
            System.out.println(e.getKey() + " --> " + e.getValue());
        }
    }

    public static void printConfidenceRules(HashMap<Rule, List<Double>> confidenceXrule) {
        System.out.println("\t\tCONFIDENCE X RULE");
        for (Map.Entry<Rule, List<Double>> e : confidenceXrule.entrySet()) {
            System.out.println(e.getKey() + " --> " + e.getValue());
        }
    }

    public static void printSuccRateRules(HashMap<Rule, List<Double>> succRateXrule) {
        System.out.println("\t\tSUCCESS RATE X RULE");
        for (Map.Entry<Rule, List<Double>> e : succRateXrule.entrySet()) {
            System.out.println(e.getKey() + " --> " + e.getValue());
        }
    }

    public static void printStatsSuccRate(HashMap<String, HashMap<Rule, Double>> statsSuccRate) {
        for (Map.Entry<String, HashMap<Rule, Double>> e : statsSuccRate.entrySet()) {
            System.out.println("\t\tDAY " + e.getKey());

            for (Map.Entry<Rule, Double> f : e.getValue().entrySet()) {
                System.out.println(f.getKey() + " --> " + f.getValue());
            }
        }
    }

    public static void saveStatsToFile(){

    }
    public static void saveMeansAndDev(){

    }




}