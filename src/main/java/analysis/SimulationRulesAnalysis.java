package analysis;

import analysis.utils.RuleMatch;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.idea.Idea;
import commons.mining.model.Item;
import commons.mining.model.Rule;

import java.io.*;
import java.util.*;

import static analysis.SimulationReader.run;
import static services.matching.Matching.parseItems;
import static services.matching.utils.StatisticsUtils.*;

public class SimulationRulesAnalysis {

    public static void main(String[] args) throws IOException {
        String dirSim = "C:\\Users\\lucaa\\Desktop\\FullSimulation\\";
        String dirCSV = "simulation/TopSeqRules/";

        HashMap<String, HashMap<String, Double>> statsDays = new HashMap<>();
        HashMap<String, HashMap<Rule, Double>> statsSuccRate = new HashMap<>();
        HashMap<Rule, List<Double>> statsMitigationTime = new HashMap<>();
        HashMap<String, List<Double>> statsMitigationTimexDay = new HashMap<>();


        ArrayList<String> days = new ArrayList<>();
        days.add("2019-03-11");
        days.add("2019-03-12");
        days.add("2019-03-13");
        days.add("2019-03-14");
        days.add("2019-03-15");
        days.add("2019-03-16");
        days.add("2019-03-17");

        for (String day : days) {
            String fileObs = dirSim + day + "\\observations_" + day + ".json";
            String filePred = dirSim + day + "\\predictions_" + day + ".json";
            String fileAggr = dirSim + day + "\\aggregated_" + day + ".json";


            System.out.println("\t\tDAY " + day + "\t");
            List<RuleMatch> ruleMatches = run(filePred, fileObs, null);

            computeStatsDays(statsDays, ruleMatches, day, countLines(fileAggr), true);
            computeStatsSuccRate(statsSuccRate, ruleMatches, day);
            statsMitigationTimexDay.put(day, computeMitigationTime(statsMitigationTime, fileObs));

        }

        printStatsDays(statsDays, true, dirCSV + "StatsDays.csv");
        printSuccRateRules(computeSucRateXRule(statsSuccRate), true, dirCSV + "StatsSuccessRate.csv");
        printMitigationTimexRule(statsMitigationTime, dirCSV + "StatsMitigationTimexRule.csv");
        printMitigationTimexDay(statsMitigationTimexDay, dirCSV + "StatsMitigationTimexDay.csv");
    }

    public static List<Double> computeMitigationTime(HashMap<Rule, List<Double>> mitigationTimexRule, String fileObs) {
        List<Double> meanMitigationTime = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileObs))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JsonNode jsonNode = objectMapper.readTree(line);
                String rule = jsonNode.get("Note").asText();


                Set<Item> antecedent = parseItems(rule.split(" ==> ")[0].trim());
                Set<Item> consequent = parseItems(rule.split(" ==> ")[1].trim());
                Rule r = new Rule(antecedent, consequent, 0, 0);

                if (jsonNode.get("mitigationTime").asDouble() > 0) {
                    meanMitigationTime.add(jsonNode.get("mitigationTime").asDouble());
                    if (!mitigationTimexRule.containsKey(r))
                        mitigationTimexRule.put(r, new ArrayList<>());
                    mitigationTimexRule.get(r).add(jsonNode.get("mitigationTime").asDouble());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return meanMitigationTime;

    }

    public static void printMitigationTimexRule(HashMap<Rule, List<Double>> mitigationTimexRule, String filePath) {
        saveMeanAndDev(mitigationTimexRule, filePath, "Mitigation Time");
    }

    public static void printMitigationTimexDay(HashMap<String, List<Double>> mitigationTimexDay, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("Rule;Mean ").append("Mitigation Time").append(";Dev ").append("Mitigation Time").append("\n");

            for (Map.Entry<String, List<Double>> e : mitigationTimexDay.entrySet()) {
                writer.append(String.valueOf(e.getKey()).trim()).append(";").append(String.valueOf(calculateMean(e.getValue()))).append(";").append(String.valueOf(calculateStandardDeviation(e.getValue())));
                writer.append("\n");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static int countLines(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            int lines = 0;
            while (reader.readLine() != null) {
                lines++;
            }
            return lines;
        }
    }


}


