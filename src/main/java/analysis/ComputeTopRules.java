package analysis;

import analysis.utils.RuleMatch;
import commons.mining.model.Rule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static analysis.SimulationRulesAnalysis.saveToCSV;
import static services.matching.Matching.readRulesFromFile;
import static services.matching.Matching.run;
import static services.mining.Mining.writeRuleToFile;

public class ComputeTopRules {

    public static void main(String[] args) throws IOException {

        List<Rule> uniqueRules = new ArrayList<>();

        String rulesFiles = "data/rules/FullSimulation/TopSeqRules/ruleDB_";

        String dirSim = "C:\\Users\\lucaa\\Desktop\\FullSimulation\\";

        ArrayList<String> days = new ArrayList<>();
        days.add("2019-03-12");
        days.add("2019-03-13");
        days.add("2019-03-14");
        days.add("2019-03-15");
        days.add("2019-03-16");
        days.add("2019-03-17");

        List<Rule> rules = readRulesFromFile(rulesFiles + "2019-03-11");
        uniqueRules.addAll(rules);


        for (String day : days) {

            rules = readRulesFromFile(rulesFiles + day);
            boolean contained = false;
            List<Rule> toAdd = new ArrayList<>();

            for (Rule r : rules) {
                for(Rule ur : uniqueRules){
                    if(ur.getAntecedent().equals(r.getAntecedent()) && ur.getConsequent().equals(r.getConsequent())) {
                        contained = true;
                    }
                }
                if(!contained)
                    toAdd.add(r);
            }
            uniqueRules.addAll(toAdd);
        }

        for (Rule r : uniqueRules)
            System.out.println(r);

        writeRuleToFile(uniqueRules, "data/rules/FullSimulation/TopSeqRules/topRules");


    }

}
