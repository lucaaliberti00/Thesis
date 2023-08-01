package services.matching;

import analysis.utils.RuleMatch;
import ca.pfv.spmf.input.sequence_database_array_integers.Sequence;
import commons.mining.model.Item;
import commons.mining.model.KeyType;
import commons.mining.model.Rule;

import services.matching.utils.SuccessRateComparator;
import services.mining.spmf.IdeaSequenceDatabase;
import services.mining.spmf.SequenceDatabases;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class Matching {

    public static void main(String[] args) {

        String rulesFile = "data/rules/FullSimulation/TopSeqRules/ruleDB_";
        String dirSim = "C:\\Users\\lucaa\\Desktop\\FullSimulation\\";

        HashMap<String, HashMap<String, Double>> statsDays = new HashMap<>();
        HashMap<Rule, List<Double>> supportXrule = new HashMap<>();
        HashMap<Rule, List<Double>> confidenceXrule = new HashMap<>();
        HashMap<String, HashMap<Rule, Double>> statsSuccRate = new HashMap<>();

        ArrayList<String> days = new ArrayList<>();
        days.add("2019-03-12");
        days.add("2019-03-13");
        days.add("2019-03-14");
        days.add("2019-03-15");
        days.add("2019-03-16");
        days.add("2019-03-17");

        List<Rule> rules = readRulesFromFile(rulesFile + "2019-03-11");

        String inputFile = dirSim + "2019-03-11" + "\\aggregated_" + "2019-03-11" + ".json";
        List<RuleMatch> ruleMatches = run(rules, inputFile);
        System.out.println("\t\tDAY " + "2019-03-11" + "\t");
        computeStatsDays(statsDays, ruleMatches,"2019-03-11",true);
        computeSupportRules(supportXrule, rules);
        computeConfidenceRules(confidenceXrule, rules);
        computeStatsSuccRate(statsSuccRate, ruleMatches, "2019-03-11");

        HashSet<Rule> uniqueRules = new HashSet<>(rules);
        System.out.println("Rules in List at 2019-03-11: " + uniqueRules.size());

        for (String day : days) {
            inputFile = dirSim + day + "\\aggregated_" + day + ".json";
            ruleMatches = run(new ArrayList<>(uniqueRules), inputFile);
            System.out.println("\t\tDAY " + day + "\t");

            computeStatsDays(statsDays, ruleMatches,day,true);
            computeStatsSuccRate(statsSuccRate, ruleMatches, "2019-03-11");

            rules = readRulesFromFile(rulesFile + day);

            computeSupportRules(supportXrule, rules);
            computeConfidenceRules(confidenceXrule, rules);

            uniqueRules.addAll(rules);
            System.out.println("Rules in List at " + day + ": " + uniqueRules.size());

        }

        System.out.println(statsDays);
        System.out.println(supportXrule);
        System.out.println(confidenceXrule);
        System.out.println(statsSuccRate);



    }


    public static List<RuleMatch> run(List<Rule> rules, String inputFile) {
        //List<Rule> rules = readRulesFromFile(rulesFile);
        IdeaSequenceDatabase sequenceDb = SequenceDatabases.fromFile(inputFile, KeyType.SRC_IPV4);
        Map<Item, Integer> invertedMap = inverter(sequenceDb.getItemMapping());

        List<Sequence> seqs = sequenceDb.getDatabase().getSequences();

        List<RuleMatch> ruleMatches = new ArrayList<>();
        for (Rule r : rules) {
            List<Item> antecedent = new ArrayList<>(r.getAntecedent());
            List<Integer> antecedentInt = itemToInt(antecedent, invertedMap);

            List<Item> consequent = new ArrayList<>(r.getConsequent());
            List<Integer> consequentInt = itemToInt(consequent, invertedMap);

            int ant_count = 0;
            int cons_count = 0;

            boolean matched;

            for (Sequence s : seqs) {
                matched = false;
                List<Integer> combinedList = new ArrayList<>();
                for (Integer[] array : s.getItemsets()) {
                    combinedList.addAll(Arrays.asList(array));
                }

                int index = getLastMatch(combinedList, antecedentInt);
                if (index != -1) {
                    combinedList = combinedList.subList(index, combinedList.size());
                    matched = new HashSet<>(combinedList).containsAll(consequentInt);
                    ant_count++;
                }
                if (matched) {
                    cons_count++;
                }

            }

            ruleMatches.add(new RuleMatch(r.getAntecedent() + " ==> " + r.getConsequent(), ant_count, cons_count, r));

        }

        ruleMatches.sort(new SuccessRateComparator());

        return ruleMatches;
    }


    public static void computeStatsDays(HashMap<String, HashMap<String, Double>> statsDays, List<RuleMatch> ruleMatches, String day, boolean verbose) {
        int predAlerts = 0;
        int succPreds = 0;

        //Se vuoi considerare la Top10
        //ruleMatches.sort(new SuccessRateComparator());
        //int numTopRules = Math.min(10, ruleMatches.size());


        for (int i = 0 ; i< ruleMatches.size(); i++) {
            predAlerts += ruleMatches.get(i).getPartialMatches();
            succPreds += ruleMatches.get(i).getFullMatches();
        }
        if (!statsDays.containsKey(day)) {
            statsDays.put(day, new HashMap<>());
        }
        statsDays.get(day).put("Predicted Alerts", (double) predAlerts);
        statsDays.get(day).put("Successfull Predictions", (double) succPreds);
        statsDays.get(day).put("Success Rate", (double) (double) succPreds / predAlerts * 100);
        if (verbose) {
            System.out.println("Predicted Alerts: " + predAlerts);
            System.out.println("Successful Predictions: " + succPreds);
            System.out.println("Success Rate: " + (double) succPreds / predAlerts * 100 + "%");
        }

    }
    public static void computeSupportRules(HashMap<Rule, List<Double>> supportXrule, List<Rule> rules){
        for(Rule r : rules) {
            if (!supportXrule.containsKey(r))
                supportXrule.put(r, new ArrayList<>());
            supportXrule.get(r).add((double) r.getSupport());
        }
    }
    public static void computeConfidenceRules(HashMap<Rule, List<Double>> confidenceXrule, List<Rule> rules){
        for(Rule r : rules) {
            if (!confidenceXrule.containsKey(r))
                confidenceXrule.put(r, new ArrayList<>());
            confidenceXrule.get(r).add(r.getConfidence());
        }
    }
    public static void computeStatsSuccRate(HashMap<String, HashMap<Rule, Double>> statsSuccRate, List<RuleMatch> ruleMatches, String day){
        if(!statsSuccRate.containsKey(day))
            statsSuccRate.put(day, new HashMap<>());

        for(RuleMatch rm : ruleMatches){
            if(statsSuccRate.get(day).containsKey(rm.getCompleteRule()))
                System.err.println(" ATTENZIONE ERRORE ");
            statsSuccRate.get(day).put(rm.getCompleteRule(), rm.getSuccessRate());
        }
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
                } catch (Exception e) {
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
                i.setCategory(i.getCategory().replaceAll(" ", ""));
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
            String antecedentStr = parts[0].replaceAll(" ", "");
            String consequentStr = parts[1].split(" ")[0].replaceAll(" ", "");
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
            if (splitted.length > 3) {
                node = splitted[0];
                for (int i = 1; i < splitted.length - 1; i++)
                    if (Character.isUpperCase(splitted[i].charAt(0)))
                        alert = alert + "_" + splitted[i];
                    else
                        node = node + "_" + splitted[i];
            } else {
                node = splitted[0];
                alert = splitted[1];
            }
            port = splitted[splitted.length - 1];

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

    public static double calcolaMedia(List<Double> listaValori) {
        if (listaValori == null || listaValori.isEmpty()) {
            throw new IllegalArgumentException("La lista non può essere vuota o nulla.");
        }

        double somma = 0.0;
        for (Double valore : listaValori) {
            somma += valore;
        }

        return somma / listaValori.size();
    }
}
