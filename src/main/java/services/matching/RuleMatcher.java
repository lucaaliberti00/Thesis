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

public class RuleMatcher {

    public static void main(String[] args) {
        String rulesFile = "data/rules/ruleDB"; // Percorso del file contenente le regole
        List<Rule> rules = readRulesFromFile(rulesFile);

        String inputFile = "data/sanitized/test.idea"; // Percorso del file contenente le idee
        List<Idea> ideas = Idea.readIdeasFromFormattedFile(new File(inputFile));
        Map<Rule, List<Match>> matches = new HashMap<>();
        for (Rule rule : rules) {

            Set<Item> initRule = rule.getAntecedent();
            Set<Item> endRule = rule.getConsequent();

            for (int i = 0; i < ideas.size(); i++) {
                String sourceIP = ideas.get(i).getSource().get(0).getIP4().get(0);

                boolean matched = true;
                int counter = 0;

                if (i + initRule.size() < ideas.size()) {
                    for (Item item : initRule) {
                        if (!item.equals(new Item(ideas.get(i + counter)))) {
                            matched = false;
                            break;
                        }
                        counter++;
                    }

                } else {
                    matched = false;
                }

                if (matched && (i + counter + endRule.size() < ideas.size())) {
                    for (Item item : endRule) {
                        if (!item.equals(new Item(ideas.get(i + counter)))) {
                            matched = false;
                            break;
                        }
                        counter++;

                    }
                } else {
                    matched = false;
                }

                if (matched)
                    System.out.println("MATCH");


            }
        }

    }

    public static Rule matchIdeaWithRules(String[] idea, List<Rule> rules) {
        for (Rule rule : rules) {
            Set<String> antecedentItems = convertItemsToStringSet(rule.getAntecedent());
            boolean match = true;
            for (String item : antecedentItems) {
                if (!containsItem(idea, item)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return rule;
            }
        }
        return null;
    }

    private static Set<String> convertItemsToStringSet(Set<Item> items) {
        Set<String> itemSet = new HashSet<>();
        for (Item item : items) {
            itemSet.add(item.getNodeName());
        }
        return itemSet;
    }

    private static boolean containsItem(String[] idea, String item) {
        for (String field : idea) {
            if (field.equals(item)) {
                return true;
            }
        }
        return false;
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

    private static String[] parseIdeaFields(Idea idea) {
        // Effettua il parsing del formato IDEA e restituisci un array di stringhe con i valori rilevanti
        // Adatta questa funzione in base alla struttura effettiva del formato IDEA
        // Esempio: return new String[]{idea.getField1(), idea.getField2(), ...};
        return new String[0];
    }

    private static long dateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
