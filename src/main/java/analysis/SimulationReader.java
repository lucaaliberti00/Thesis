package analysis;

import analysis.utils.RuleMatch;
import commons.idea.Idea;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static commons.idea.Idea.readIdeasFromRawFile;

public class SimulationReader {

    public static void main(String[] args) throws IOException {
        String fileObs = "C:\\Users\\lucaa\\Desktop\\FullSimulation\\2019-03-17\\observations_2019-03-17_only.json";
        String filePred = "C:\\Users\\lucaa\\Desktop\\FullSimulation\\2019-03-17\\predictions_2019-03-17_only.json";

        List<RuleMatch> ruleMatches = run(filePred,fileObs);
        // Stampa le regole in ordine di rapporto decrescente
        for (RuleMatch ruleMatch : ruleMatches) {
            System.out.println("Rule: " + ruleMatch.getRule() + ", Success Rate: " + ruleMatch.getSuccessRate());
        }

        double averageTopKRatio = computeTopKRate(ruleMatches, 3, true);
    }

    public static List<RuleMatch> run(String filePred, String fileObs){
        List<Idea> predictions = readIdeasFromRawFile(new File(filePred));
        List<Idea> observations = readIdeasFromRawFile(new File(fileObs));

        HashMap<String,List<Idea>> predMap = ideasByRule(predictions);
        HashMap<String,List<Idea>> obsMap = ideasByRule(observations);

        List<RuleMatch> ruleMatches = new ArrayList<>();

        for (String rule : predMap.keySet()) {
            List<Idea> predList = predMap.get(rule);
            List<Idea> obsList = obsMap.getOrDefault(rule, new ArrayList<>());
            ruleMatches.add(new RuleMatch(rule, predList.size(), obsList.size()));
        }

        // Ordina la lista in base al rapporto (ratio) in ordine decrescente
        ruleMatches.sort((a, b) -> Double.compare(b.getSuccessRate(), a.getSuccessRate()));

        return ruleMatches;

    }

    public static double computeTopKRate(List<RuleMatch> ruleMatches, int k, boolean verbose){

        ruleMatches.sort((a, b) -> Double.compare(b.getSuccessRate(), a.getSuccessRate()));

        int numTopRules = Math.min(k, ruleMatches.size());
        double sumTopRatios = 0.0;

        for (int i = 0; i < numTopRules; i++) {
            sumTopRatios += ruleMatches.get(i).getSuccessRate();
        }

        double averageTopRatio = sumTopRatios / numTopRules;
        int predAlerts = 0;
        int succPreds = 0;

        for (int i = 0; i < numTopRules; i++){
            predAlerts += ruleMatches.get(i).getPartialMatches();
            succPreds += ruleMatches.get(i).getFullMatches();
        }
        if(verbose) {
            System.out.println("Predicted Alerts: " + predAlerts);
            System.out.println("Successful Predictions: " + succPreds);
            System.out.println("Success Rate: " + (double)succPreds/predAlerts*100 + "%");
            System.out.println("Average Success Rate of Top " + numTopRules + " Rules: " + averageTopRatio);
        }

        return averageTopRatio;
    }


    private static HashMap<String,List<Idea>> ideasByRule(List<Idea> ideas){
        HashMap<String,List<Idea>> mapByRule = new HashMap<>();
        for (Idea i : ideas){
            String rule = i.getNote();
            if (mapByRule.get(rule) == null) {
                ArrayList<Idea> list = new ArrayList<>();
                list.add(i);
                mapByRule.put(rule, list);
            }else
                mapByRule.get(rule).add(i);
        }
        return mapByRule;
    }






    }



