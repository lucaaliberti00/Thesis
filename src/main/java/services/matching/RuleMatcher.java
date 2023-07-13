package services.matching;

import commons.idea.Idea;
import commons.mining.model.Item;
import commons.mining.model.Rule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RuleMatcher {

    public static void main(String[] args) {
        String rulesFile = "data/rules/ruleDB"; // Percorso del file contenente le regole
        List<Rule> rules = readRulesFromFile(rulesFile);

        String inputFile = "data/sanitized/test.idea"; // Percorso del file contenente le idee
        List<Idea> ideas = Idea.readIdeasFromFormattedFile(new File(inputFile));
        ideas.sort(new IdeaSourceIpComparator());

        Map<Rule, List<Match>> matches = new HashMap<>();


        for (Rule rule : rules) {
            //cz.cesnet.tarpit_Recon.Scanning_8000 ==> cz.cesnet.tarpit_Recon.Scanning_8080
            Set<Item> initRule = rule.getAntecedent();//cz.cesnet.tarpit_Recon.Scanning_8000
            Set<Item> endRule = rule.getConsequent(); //cz.cesnet.tarpit_Recon.Scanning_8080

            for (int i = 0; i < ideas.size(); i++) {
                String sourceIP = ideas.get(i).getSource().get(0).getIP4().get(0);

                int counter = 0;


                counter = checkMatches(ideas, initRule, sourceIP, counter, i);

                if (counter != -1)
                    counter = checkMatches(ideas, endRule, sourceIP, counter, i);

                if (counter != -1)
                    System.out.println("MATCH");


            }
        }

    }

    private static int checkMatches(List<Idea> ideas, Set<Item> rule, String sourceIP, int counter, int index) {
        if (index + counter + rule.size() >= ideas.size())
            return -1;

        List<Item> sequences = new ArrayList<>(rule);
        int j = 0;
        Item item;

        while (j < sequences.size()) {
            item = sequences.get(j); // prendo la sequenza tipo cznert_Recon_8080

            try{
                if (!ideas.get(index + counter).getSource().get(0).getIP4().get(0).equals(sourceIP)) {
                    counter++;
                    continue;
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }


            if (!item.equals(new Item(ideas.get(index)))) {
                return -1;
            }
            counter++;
            j++;
        }
        return counter;
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