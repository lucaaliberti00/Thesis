package Analysis;

import commons.idea.Idea;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static commons.idea.Idea.readIdeasFromRawFile;

public class FileLineCounter {

    public static int countLines(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            int lines = 0;
            while (reader.readLine() != null) {
                lines++;
            }
            return lines;
        }
    }

    public static void main(String[] args) throws IOException {
        String file1Path = "C:\\Users\\lucaa\\Desktop\\FullSimulation\\2019-03-15\\observations_2019-03-15.json";
        String file2Path = "C:\\Users\\lucaa\\Desktop\\FullSimulation\\2019-03-15\\predictions_2019-03-15.json";
        String file3Path = "C:\\Users\\lucaa\\Desktop\\FullSimulation\\2019-03-16\\aggregated_2019-03-16.json";

        List<Idea> predictions = readIdeasFromRawFile(new File(file2Path));
        List<Idea> observations = readIdeasFromRawFile(new File(file1Path));

        HashMap<String,List<Idea>> predMap = new HashMap<>();
        HashMap<String,List<Idea>> obsMap = new HashMap<>();

        for (Idea i : predictions){
            String rule = i.getNote();

            if (predMap.get(rule) == null) {
                ArrayList<Idea> list = new ArrayList<>();
                list.add(i);
                predMap.put(rule, list);
            }else{
                predMap.get(rule).add(i);
            }

        }
        for (Idea i : observations){
            String rule = i.getNote();

            if (obsMap.get(rule) == null) {
                ArrayList<Idea> list = new ArrayList<>();
                list.add(i);
                obsMap.put(rule, list);
            }else{
                obsMap.get(rule).add(i);
            }
        }

        System.out.println("Ratio for rule:");

        List<RuleRatioPair> ruleRatioList = new ArrayList<>();

        for (String rule : predMap.keySet()) {
            List<Idea> predList = predMap.get(rule);
            List<Idea> obsList = obsMap.getOrDefault(rule, new ArrayList<>());

            double ratio = (double) obsList.size() / predList.size();
            ruleRatioList.add(new RuleRatioPair(rule, ratio));
        }

        // Ordina la lista in base al rapporto (ratio) in ordine decrescente
        ruleRatioList.sort((a, b) -> Double.compare(b.ratio, a.ratio));

        // Stampa le regole in ordine di rapporto decrescente
        for (RuleRatioPair pair : ruleRatioList) {
            System.out.println("Rule: " + pair.rule + ", Success Rate: " + pair.ratio);
        }

        // Calcola e stampa il rapporto medio delle prime 10 regole
        int numTopRules = Math.min(10, ruleRatioList.size());
        double sumTopRatios = 0.0;

        for (int i = 0; i < numTopRules; i++) {
            sumTopRatios += ruleRatioList.get(i).ratio;
        }

        double averageTopRatio = sumTopRatios / numTopRules;
        System.out.println("Average Ratio of Top 10 Rules: " + averageTopRatio);

        int predAlerts = 0;
        int succPreds = 0;

        for (int i = 0; i < 10; i++){
            predAlerts += predMap.get(ruleRatioList.get(i).rule).size();
            succPreds += obsMap.get(ruleRatioList.get(i).rule).size();
        }

        System.out.println("Predicted Alerts: " + predAlerts );
        System.out.println("Successful Predictions: " + succPreds);
        System.out.println("Predicted Alerts Total: " + predictions.size() );
        System.out.println("Successful Predictions Total: " + observations.size());
        System.out.println("Alerts: " + countLines(file3Path));
    }

    private static class RuleRatioPair {
        String rule;
        double ratio;

        RuleRatioPair(String rule, double ratio) {
            this.rule = rule;
            this.ratio = ratio;
        }
    }




    }



