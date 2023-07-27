package analysis;

import analysis.utils.RuleMatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static analysis.SimulationReader.run;
import static services.matching.Matching.computeTopKRate;

public class SimulationRulesAnalysis {

    public static void main(String[] args) throws IOException {
        String dirSim = "C:\\Users\\lucaa\\Desktop\\FullSimulation\\";

        ArrayList<String> days = new ArrayList<>();
        days.add("2019-03-11");
        days.add("2019-03-12");
        days.add("2019-03-13");
        days.add("2019-03-14");
        days.add("2019-03-15");
        days.add("2019-03-16");
        days.add("2019-03-17");

        for (String day : days) {

            String fileObs = dirSim + day + "\\observations_" + day + ".json";
            String filePred = dirSim + day + "\\predictions_" + day + ".json";
            String fileAggr = dirSim + day + "\\aggregated_" + day + ".json";


            System.out.println("\t\tDAY " + day + "\t");
            List<RuleMatch> ruleMatches = run(filePred, fileObs);

            System.out.println("Alerts: " + countLines(fileAggr));
            computeTopKRate(ruleMatches, 10, true);
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

}


