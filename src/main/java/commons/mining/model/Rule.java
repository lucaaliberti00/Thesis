package commons.mining.model;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class Rule {

    private Set<Item> antecedent;
    private Set<Item> consequent;
    private int support;
    private double confidence;

    public Rule(Set<Item> antecedent, Set<Item> consequent, int support, double confidence) {
        this.antecedent = antecedent;
        this.consequent = consequent;
        this.support = support;
        this.confidence = confidence;
    }

    public Set<Item> getAntecedent() {
        return Collections.unmodifiableSet(antecedent);
    }

    public Set<Item> getConsequent() {
        return Collections.unmodifiableSet(consequent);
    }

    public double getConfidence() {
        return confidence;
    }

    public int getSupport() {
        return support;
    }

    public void setSupport(int support) {
        this.support = support;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "antecedent=" + antecedent +
                ", consequent=" + consequent +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return Objects.equals(antecedent, rule.antecedent) && Objects.equals(consequent, rule.consequent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(antecedent, consequent);
    }
}
