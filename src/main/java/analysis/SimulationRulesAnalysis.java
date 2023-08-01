package analysis;

import analysis.utils.RuleMatch;
import commons.mining.model.Rule;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static analysis.SimulationReader.run;

public class SimulationRulesAnalysis {

    public static void main(String[] args) throws IOException {
        String dirSim = "C:\\Users\\lucaa\\Desktop\\FullSimulation\\";
        String csvPath = "data/csv/TopSeqRulesTotalSimulation.csv";


        ArrayList<String> days = new ArrayList<>();
        days.add("2019-03-11");
        days.add("2019-03-12");
        days.add("2019-03-13");
        days.add("2019-03-14");
        days.add("2019-03-15");
        days.add("2019-03-16");
        days.add("2019-03-17");

        for (String day : days) {
            List<String> performance = new ArrayList<>();
            String fileObs = dirSim + day + "\\observations_" + day + ".json";
            String filePred = dirSim + day + "\\predictions_" + day + ".json";
            String fileAggr = dirSim + day + "\\aggregated_" + day + ".json";


            System.out.println("\t\tDAY " + day + "\t");
            List<RuleMatch> ruleMatches = run(filePred, fileObs);

            for(RuleMatch r : ruleMatches){
                performance.add(r.toCSV(day));
            }
            saveToCSV(performance, csvPath);
            System.out.println("Alerts: " + countLines(fileAggr));
            System.out.println();



        }

    }

    private static int countLines(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            int lines = 0;
            while (reader.readLine() != null) {
                lines++;
            }
            return lines;
        }
    }

    public static void saveToCSV(List<String> stringList, String filePath) {
        try {
            boolean fileExists = new File(filePath).exists();

            try (FileWriter writer = new FileWriter(filePath, true)) {
                if (!fileExists) {
                    // Il file è vuoto, quindi scriviamo l'intestazione
                    writer.append("Date;Rule;SupA;SupAB;Confidence");
                    writer.append("\n");
                }

                for (String str : stringList) {
                    writer.append(str);
                    writer.append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}


