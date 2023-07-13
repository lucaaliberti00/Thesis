package services.matching;

import commons.idea.Idea;
import commons.mining.model.Item;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static commons.idea.Idea.readIdeasFromFormattedFile;

public class KMP {

    public static int[] computeLPS(List<Item> pattern) {
        int m = pattern.size();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;

        while (i < m) {
            if (pattern.get(i).equals(pattern.get(len))) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }

    public static List<Integer> kmp(List<Idea> text, List<Item> pattern) {
        List<Integer> occurrences = new ArrayList<>();
        int n = text.size();
        int m = pattern.size();

        if (m == 0) {
            return occurrences;
        }

        int[] lps = computeLPS(pattern);

        int i = 0;
        int j = 0;
        //String expectedSourceIp = text.get(i).getSource().get(0).getIP4().get(0);


        while (i < n) {
            Item textItem = new Item(text.get(i)); // Creazione oggetto Item da Idea

           // if (j==0)
            //    expectedSourceIp = text.get(i).getSource().get(0).getIP4().get(0); // Indirizzo IP atteso nel pattern

            // Controllo che gli oggetti Idea associati abbiano lo stesso indirizzo IP
            if (textItem.equals(pattern.get(j)) /*&& text.get(i).getSource().get(0).getIP4().get(0).equals(expectedSourceIp)*/) {
                i++;
                j++;
                if (j == m) {
                    occurrences.add(i - j);
                    j = lps[j - 1];
                }
            } else {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return occurrences;
    }


    public static void main(String[] args) {
        List<Idea> text = Idea.readIdeasFromFormattedFile(new File("data/sanitized/train.idea")); // Metodo per leggere la lista di Idea
        text.sort(new IdeaSourceIpComparator());

        List<Item> pattern = new ArrayList<>();
        pattern.add(new Item("cz.cesnet.nemea.hoststats", "Recon.Scanning", null));
        //pattern.add(new Item("cz.cesnet.nemea.hoststats", "Recon.Scanning", null));
        //pattern.add(new Item("cz.cesnet.tarpit", "Recon.Scanning", 8080));
        //pattern.add(new Item("cz.cesnet.tarpit", "Recon.Scanning", 8080));

        List<Integer> occurrences = kmp(text, pattern);

        if (!occurrences.isEmpty()) {
            System.out.print("Pattern found ");
            System.out.print(occurrences.size());
            System.out.println(" times.");

        } else {
            System.out.println("Pattern not found");
        }
    }
}
