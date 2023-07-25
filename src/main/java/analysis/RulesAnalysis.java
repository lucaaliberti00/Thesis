package analysis;

import analysis.utils.RuleMatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static analysis.SimulationReader.computeTopKRate;
import static analysis.SimulationReader.run;

public class RulesAnalysis {

    public static void main(String[] args) throws IOException {
        String dirSim = "C:\\Users\\lucaa\\Desktop\\FullSimulation";

        ArrayList<String> days = new ArrayList<>();
        days.add("2019-03-11");
        days.add("2019-03-12");
        days.add("2019-03-13");
        days.add("2019-03-14");
        days.add("2019-03-15");
        days.add("2019-03-16");
        days.add("2019-03-17");

        for (String day : days) {

            String fileObs = "C:\\Users\\lucaa\\Desktop\\FullSimulation\\"+ day +"\\observations_" + day + ".json";
            String filePred = "C:\\Users\\lucaa\\Desktop\\FullSimulation\\"+ day +"\\predictions_" + day + ".json";
            String fileAggr = "C:\\Users\\lucaa\\Desktop\\FullSimulation\\"+ day +"\\aggregated_" + day + ".json";


            System.out.println("\t\tDAY " + day + "\t");
            List<RuleMatch> ruleMatches = run(filePred,fileObs);
            // Stampa le regole in ordine di rapporto decrescente
            /*for (RuleMatch ruleMatch : ruleMatches) {
                System.out.println("Rule: " + ruleMatch.getRule() + ", Success Rate: " + ruleMatch.getSuccessRate());
            }*/

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


    public static void readFilesInDirectory(String directoryPath) {
        File directory = new File(directoryPath);

        // Verifica se la directory esiste ed è effettivamente una directory
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                // Scansiona tutti i file nella directory e stampa i loro nomi
                for (File file : files) {
                    if (file.isFile()) {
                        System.out.println("Nome file: " + file.getName());
                        // Puoi fare ulteriori operazioni sui file qui, se necessario
                    }
                }
            } else {
                System.err.println("La directory è vuota.");
            }
        } else {
            System.err.println("La directory specificata non esiste o non è una directory valida.");
        }
    }
}


