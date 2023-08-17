package analysis;

import services.matching.Matching;
import services.mining.Mining;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FineTuning {

    public static void main(String[] args) {

        int[] kValues = {50, 100,500,1000};

        for (int k : kValues){
            String dirRules= "data/rules/TopSeqRulesK" + k + "/";
            String dirCSV = "data/csv/TopSeqRulesK" + k + "/";

            creatDir(dirRules);
            creatDir(dirCSV);

            String[] argsMining = {dirRules, String.valueOf(k)};
            String[] argsMatching = {dirRules, dirCSV};

            Mining.main(argsMining);
            Matching.main(argsMatching);

        }



    }

    private static void creatDir(String directoryPath){
        Path directory = Paths.get(directoryPath);

        // Verifica se la cartella esiste
        if (Files.exists(directory)) {
            System.out.println("La cartella esiste.");
        } else {
            // Crea la cartella se non esiste
            try {
                Files.createDirectories(directory);
                System.out.println("Cartella creata con successo.");
            } catch (IOException e) {
                System.out.println("Impossibile creare la cartella: " + e.getMessage());
            }
        }
    }
}
