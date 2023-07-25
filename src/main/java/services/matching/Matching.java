package services.matching;

import ca.pfv.spmf.input.sequence_database_array_integers.Sequence;
import commons.mining.model.Item;
import commons.mining.model.KeyType;
import commons.mining.model.Rule;

import services.mining.spmf.IdeaSequenceDatabase;
import services.mining.spmf.SequenceDatabases;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Matching {

    public static void main(String[] args) {
        ArrayList<Double> Top10Confidence = new ArrayList<>();

        String rulesFile = "data/rules/FullSimulation/TopSeqRules/ruleDB_2019-03-17"; // Percorso del file contenente le regole
        List<Rule> rules = readRulesFromFile(rulesFile);

        String inputFile = "C:\\Users\\lucaa\\Desktop\\FullSimulation\\2019-03-17\\aggregated_2019-03-17.json"; // Percorso del file contenente le idee

        IdeaSequenceDatabase sequenceDb = SequenceDatabases.fromFile(inputFile, KeyType.SRC_IPV4);
        Map<Item, Integer> invertedMap = inverter(sequenceDb.getItemMapping());

        List<Sequence> seqs = sequenceDb.getDatabase().getSequences();

        for (Rule r : rules) {

            //r = rules.get(4);
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
            double confidence = (double) cons_count / ant_count;
            System.out.println(r);
            System.out.println("Supporto: " + seq_ok);
            System.out.println("Mining Supporto: " + r.getSupport());
            System.out.println("Confidenza: " + confidence);
            System.out.println("Mining Confidenza: " + r.getConfidence());
            System.out.println("\t----\t");

            Top10Confidence.add(confidence);
        }

        Top10Confidence.sort(Collections.reverseOrder());

        int numElementsToAverage = Math.min(10, Top10Confidence.size());
        double sumOfFirst10 = 0.0;

        for (int i = 0; i < numElementsToAverage; i++) {
            sumOfFirst10 += Top10Confidence.get(i);
        }

        double averageOfFirst10 = sumOfFirst10 / numElementsToAverage;

        System.out.println("Top 10 Confidence Mean: " + averageOfFirst10);

    }

    public static List<Rule> run(String rulesFile, String inputFile, boolean verbose){

        return null;
    }

    private static int getLastMatch(List<Integer> firstList, List<Integer> secondList) {

        List<Integer> temp = new ArrayList<>(secondList);

        int index = -1;

        for (int i = 0; i < firstList.size(); i++) {
            if (temp.size() == 0)
                return index;

            int item = firstList.get(i);

            if (temp.contains(item)) {
                try {
                    temp.remove(Integer.valueOf(item));
                }catch (Exception e){
                    e.printStackTrace();
                }
                index = i;
            }

        }
        if (temp.size() != 0)
            return -1;
        else
            return index;
    }

    public static List<Integer> itemToInt(List<Item> list, Map<Item, Integer> invertedMap) {
        List<Integer> listInt = new ArrayList<>();
        for (Item i : list) {
            if (invertedMap.get(i) == null) {
                i.setNodeName(i.getNodeName().replaceAll(" ", ""));
                i.setNodeName(i.getCategory().replaceAll(" ", ""));
            }
            listInt.add(invertedMap.get(i));
        }

        return listInt;
    }

    public static Map<Item, Integer> inverter(Map<Integer, Item> itemMapping) {
        Map<Item, Integer> invertedMap = new HashMap<>();
        for (Map.Entry<Integer, Item> entry : itemMapping.entrySet()) {
            invertedMap.put(entry.getValue(), entry.getKey());
        }
        return invertedMap;
    }


    public static List<Rule> readRulesFromFile(String filename) {
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
            int support = new Integer(parts[1].split(" ")[1]);
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
            String[] splitted = itemStr.split("_");
            String node = "";
            String alert = "";
            String port = "";
            if(splitted.length > 3){
                node = splitted[0];
                for(int i=1; i<splitted.length-1; i++)
                    if (Character.isUpperCase(splitted[i].charAt(0)))
                        alert = alert + "_" + splitted[i];
                    else
                        node = node + "_" + splitted[i];
            }else{
                node = splitted[0];
                alert = splitted[1];
            }
            port = splitted[splitted.length -1];

            if (alert.startsWith("_"))
                alert = alert.replace("_", " ");

            if (!Objects.equals(port, "None"))
                items.add(new Item(node, alert, new Integer(port)));
            else
                items.add(new Item(node, alert, null));


        }
        return items;
    }


    private static long dateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
