package analysis;

import java.io.File;
import java.io.IOException;

public class RulesAnalysis {

    public static void main(String[] args){

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


