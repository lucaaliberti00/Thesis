package analysis;

import analysis.utils.RuleMatch;
import commons.idea.Idea;
import commons.mining.model.Item;
import commons.mining.model.Rule;
import services.matching.utils.SuccessRateComparator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static commons.idea.Idea.readIdeasFromRawFile;
import static services.matching.Matching.parseItems;

public class SimulationReader {

    public static List<RuleMatch> run(String filePred, String fileObs, String metricsPath) {
        List<Idea> predictions = readIdeasFromRawFile(new File(filePred));
        List<Idea> observations = readIdeasFromRawFile(new File(fileObs));

        HashMap<String, List<Idea>> predMap = ideasByRule(predictions);
        HashMap<String, List<Idea>> obsMap = ideasByRule(observations);

        List<RuleMatch> ruleMatches = new ArrayList<>();

        for (String rule : predMap.keySet()) {
            List<Idea> predList = predMap.get(rule);
            List<Idea> obsList = obsMap.getOrDefault(rule, new ArrayList<>());

            Set<Item> antecedent = parseItems(rule.split(" ==> ")[0].trim());
            Set<Item> consequent = parseItems(rule.split(" ==> ")[1].trim());
            Rule r = new Rule(antecedent, consequent, obsList.size(), (double) obsList.size()/ (double) predList.size());

            ruleMatches.add(new RuleMatch(rule, predList.size(), obsList.size(), r));
        }

        // Ordina la lista in base al rapporto (ratio) in ordine decrescente
        //ruleMatches.sort(new SuccessRateComparator());

        return ruleMatches;

    }

    private static HashMap<String, List<Idea>> ideasByRule(List<Idea> ideas) {
        HashMap<String, List<Idea>> mapByRule = new HashMap<>();
        for (Idea i : ideas) {
            String rule = i.getNote().trim();
            if (mapByRule.get(rule) == null) {
                ArrayList<Idea> list = new ArrayList<>();
                list.add(i);
                mapByRule.put(rule, list);
            } else
                mapByRule.get(rule).add(i);
        }
        return mapByRule;
    }


}



