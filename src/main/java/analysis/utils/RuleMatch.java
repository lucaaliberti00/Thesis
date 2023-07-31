package analysis.utils;

import commons.mining.model.Rule;

public class RuleMatch {

    private String rule;
    private int partialMatches;
    private int fullMatches;
    private double successRate;

    private Rule completeRule;

    public RuleMatch(String rule, int partialMatches, int fullMatches, Rule completeRule) {
        this.rule = rule.replaceAll("\\[", "").replaceAll("]", "");
        this.partialMatches = partialMatches;
        this.fullMatches = fullMatches;
        this.successRate = (double)fullMatches/partialMatches;
        this.completeRule = completeRule;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public int getPartialMatches() {
        return partialMatches;
    }

    public void setPartialMatches(int partialMatches) {
        this.partialMatches = partialMatches;
    }

    public int getFullMatches() {
        return fullMatches;
    }

    public void setFullMatches(int fullMatches) {
        this.fullMatches = fullMatches;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }

    @Override
    public String toString() {
        return "RuleMatch{" +
                "rule='" + rule + '\'' +
                ", partialMatches=" + partialMatches +
                ", fullMatches=" + fullMatches +
                ", successRate=" + successRate +
                '}';
    }

    public String toCSV(String date){
        return date + ";" + rule + ";" + partialMatches + ";" + fullMatches + ";" + successRate;
    }

    public Rule getCompleteRule() {
        return completeRule;
    }

    public void setCompleteRule(Rule completeRule) {
        this.completeRule = completeRule;
    }
}
