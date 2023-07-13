package services.matching;

import ca.pfv.spmf.input.sequence_database_array_integers.Sequence;
import commons.mining.model.Item;
import commons.mining.model.KeyType;
import commons.mining.model.Rule;
import scala.Array;
import scala.Int;
import services.mining.spmf.IdeaSequenceDatabase;
import services.mining.spmf.SequenceDatabases;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DatabaseMatcher {

    public static void main(String[] args) {
        String rulesFile = "data/rules/ruleDB"; // Percorso del file contenente le regole
        List<Rule> rules = readRulesFromFile(rulesFile);

        String inputFile = "data/sanitized/train.idea"; // Percorso del file contenente le idee
        //List<Idea> ideas = Idea.readIdeasFromFormattedFile(new File(inputFile));
        IdeaSequenceDatabase sequenceDb = SequenceDatabases.fromFile(inputFile, KeyType.SRC_IPV4);
        Map<Item, Integer> invertedMap = inverter(sequenceDb.getItemMapping());

        List<Sequence> seqs = sequenceDb.getDatabase().getSequences();

        for (Rule r : rules) {
            List<Item> antecedent = new ArrayList<>(r.getAntecedent());
            List<Integer> antecedentInt = itemToInt(antecedent, invertedMap);

            List<Item> consequent = new ArrayList<>(r.getConsequent());
            List<Integer> consequentInt = itemToInt(consequent, invertedMap);

            int ant_count = 0;
            int cons_count = 0;
            int seq_ok = 0;

            boolean matched = false;

            for (Sequence s : seqs) {
                matched = false;
                List<Integer> combinedList = new ArrayList<>();
                for (Integer[] array : s.getItemsets()) {
                    combinedList.addAll(Arrays.asList(array));
                }

                int index = getLastMatch(combinedList, antecedentInt);
                if (index != -1) {
                    combinedList = combinedList.subList(index,combinedList.size());
                    matched = new HashSet<>(combinedList).containsAll(consequentInt);
                    ant_count++;
                }
                if (matched) {
                    cons_count++;
                    seq_ok++;

                }

            }
            System.out.println("\t----\t");
            System.out.println("Rule" + r);
            System.out.println("Support " + seq_ok);
            double confidence = (double) cons_count / ant_count;
            System.out.println("Confidence " + confidence);
            System.out.println("\t----\t");

        }


    }

    private static int getLastMatch(List<Integer> firstList, List<Integer> secondList) {
        int index = -1;

        for (int i = 0; i < firstList.size(); i++) {
            if (secondList.size() == 0)
                return index;

            int item = firstList.get(i);

            if (secondList.contains(item)) {
                try {
                    secondList.remove(Integer.valueOf(item));
                }catch (Exception e){
                    e.printStackTrace();
                }
                index = i;
            }

        }
        if (secondList.size() != 0)
            return -1;
        else
            return index;
    }

    private static List<Integer> itemToInt(List<Item> list, Map<Item, Integer> invertedMap) {
        List<Integer> listInt = new ArrayList<>();
        for (Item i : list) {
            if (invertedMap.get(i) == null) {
                i.setNodeName(i.getNodeName().replaceAll(" ", ""));
            }
            listInt.add(invertedMap.get(i));
        }

        return listInt;
    }

    private static Map<Item, Integer> inverter(Map<Integer, Item> itemMapping) {
        Map<Item, Integer> invertedMap = new HashMap<>();
        for (Map.Entry<Integer, Item> entry : itemMapping.entrySet()) {
            invertedMap.put(entry.getValue(), entry.getKey());
        }
        return invertedMap;
    }


    private static List<Rule> readRulesFromFile(String filename) {
        List<Rule> rules = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                Rule rule = parseRule(line);
                if (rule != null) {
                    rules.add(rule);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rules;
    }

    private static Rule parseRule(String line) {
        String[] parts = line.split(" ==> ");
        if (parts.length == 2) {
            String antecedentStr = parts[0];
            String consequentStr = parts[1].split(" ")[0];
            int support = new Integer(parts[1].split(" ")[2]);
            double confidence = new Double(parts[1].split(" ")[3].replace(',', '.'));

            Set<Item> antecedent = parseItems(antecedentStr);
            Set<Item> consequent = parseItems(consequentStr);

            return new Rule(antecedent, consequent, support, confidence); // Aggiungi i valori di supporto e confidence appropriati
        }
        return null;
    }

    private static Set<Item> parseItems(String itemsStr) {
        Set<Item> items = new HashSet<>();
        String[] itemStrs = itemsStr.split(",");
        for (String itemStr : itemStrs) {
            if (!Objects.equals(itemStr.split("_")[2], "None"))
                items.add(new Item(itemStr.split("_")[0], itemStr.split("_")[1], new Integer(itemStr.split("_")[2])));
            else
                items.add(new Item(itemStr.split("_")[0], itemStr.split("_")[1], null));


        }
        return items;
    }


    private static long dateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
