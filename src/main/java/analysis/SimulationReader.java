package analysis;

import analysis.utils.RuleMatch;
import commons.idea.Idea;
import services.matching.utils.ConfidenceComparator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static commons.idea.Idea.readIdeasFromRawFile;
import static services.matching.Matching.computeTopKRate;

public class SimulationReader {

    public static List<RuleMatch> run(String filePred, String fileObs){
        List<Idea> predictions = readIdeasFromRawFile(new File(filePred));
        List<Idea> observations = readIdeasFromRawFile(new File(fileObs));

        HashMap<String,List<Idea>> predMap = ideasByRule(predictions);
        HashMap<String,List<Idea>> obsMap = ideasByRule(observations);

        List<RuleMatch> ruleMatches = new ArrayList<>();

        for (String rule : predMap.keySet()) {
            List<Idea> predList = predMap.get(rule);
            List<Idea> obsList = obsMap.getOrDefault(rule, new ArrayList<>());

            ruleMatches.add(new RuleMatch(rule, predList.size(), obsList.size(), null));
        }

        // Ordina la lista in base al rapporto (ratio) in ordine decrescente
        ruleMatches.sort(new ConfidenceComparator());

        return ruleMatches;

    }


    private static HashMap<String,List<Idea>> ideasByRule(List<Idea> ideas){
        HashMap<String,List<Idea>> mapByRule = new HashMap<>();
        for (Idea i : ideas){
            String rule = i.getNote();
            if (mapByRule.get(rule) == null) {
                ArrayList<Idea> list = new ArrayList<>();
                list.add(i);
                mapByRule.put(rule, list);
            }else
                mapByRule.get(rule).add(i);
        }
        return mapByRule;
    }






    }



