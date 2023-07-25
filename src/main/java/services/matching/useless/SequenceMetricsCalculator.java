package services.matching.useless;
import ca.pfv.spmf.input.sequence_database_array_integers.Sequence;
import commons.mining.model.Item;
import commons.mining.model.KeyType;
import commons.mining.model.Rule;
import services.mining.spmf.IdeaSequenceDatabase;
import services.mining.spmf.SequenceDatabases;

import java.util.*;

import static services.matching.Matching.*;

public class SequenceMetricsCalculator {
    public static double calculateConfidence(List<Integer> antecedent, List<Integer> consequent, List<List<Integer>> sequences) {
        int supportAntecedent = calculateSupport(antecedent, sequences);
        int supportBoth = calculateSupport(concatenateLists(antecedent, consequent), sequences);

        return (double) supportBoth / (double) supportAntecedent;
    }

    public static double calculateConfidenceTemporal(List<Integer> antecedent, List<Integer> consequent, List<List<Integer>> sequences) {
        int supportAntecedent = calculateSupport(antecedent, sequences);
        int supportBoth = calculateSupport(concatenateLists(antecedent, consequent), sequences);

        return (double) supportBoth / (double) supportAntecedent;
    }

    public static int calculateSupport(List<Integer> items, List<List<Integer>> sequences) {
        //Set<Integer> itemSet = new HashSet<>(items);
        int support = 0;

        for (List<Integer> sequence : sequences) {

            boolean containsAllItems = true;

            for (Integer item : items) {
                if (!sequence.contains(item)) {
                    containsAllItems = false;
                    break;
                }
            }

            if (containsAllItems) {
                support++;
            }
        }

        return support;
    }public static int calculateSupportTemporal(List<Integer> items, List<Integer> antecedent, List<Integer> consequent, List<List<Integer>> sequences) {
        //Set<Integer> itemSet = new HashSet<>(items);
        int support = 0;
        int index = -1;
        int i;

        for (List<Integer> sequence : sequences) {
            boolean containsAllItems = true;

            for (Integer item : items) {
                if (!sequence.contains(item)) {
                    containsAllItems = false;
                    break;
                }else {
                    index = sequence.indexOf(item);
                }
            }

            if (containsAllItems) {
                support++;
            }
        }

        return support;
    }

    public static List<Integer> concatenateLists(List<Integer> list1, List<Integer> list2) {
        List<Integer> concatenatedList = new ArrayList<>(list1);
        concatenatedList.addAll(list2);
        return concatenatedList;
    }

    public static void main(String[] args) {
        // Esempio di utilizzo
        String rulesFile = "data\\rules\\ruleDB"; // Percorso del file contenente le regole
        List<Rule> rules = readRulesFromFile(rulesFile);

        String inputFile = "data\\sanitized\\train.idea"; // Percorso del file contenente le idee
        //List<Idea> ideas = Idea.readIdeasFromFormattedFile(new File(inputFile));
        IdeaSequenceDatabase sequenceDb = SequenceDatabases.fromFile(inputFile, KeyType.SRC_IPV4);
        Map<Item, Integer> invertedMap = inverter(sequenceDb.getItemMapping());

        List<Sequence> seqs = sequenceDb.getDatabase().getSequences();

        List<List<Integer>> sequences = new ArrayList<>();

        for (Sequence s : seqs) {

            List<Integer> combinedList = new ArrayList<>();
            for (Integer[] array : s.getItemsets()) {
                combinedList.addAll(Arrays.asList(array));
            }
            sequences.add(combinedList);

        }

        for (Rule r : rules) {
            List<Item> antecedent = new ArrayList<>(r.getAntecedent());
            List<Integer> antecedentInt = itemToInt(antecedent, invertedMap);

            List<Item> consequent = new ArrayList<>(r.getConsequent());
            List<Integer> consequentInt = itemToInt(consequent, invertedMap);

            int support = calculateSupport(concatenateLists(antecedentInt, consequentInt), sequences);
            double confidence = calculateConfidence(antecedentInt, consequentInt, sequences);

            System.out.println(r);
            System.out.println("Supporto: " + support);
            System.out.println("Mining Supporto: " + r.getSupport());
            System.out.println("Confidenza: " + confidence);
            System.out.println("Mining Confidenza: " + r.getConfidence());
            System.out.println("\t----\t");
            }


        }




    }
