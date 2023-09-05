package commons.mining.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Handler;

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
        String r = antecedent + " ==> " + consequent;
        r = r.replaceAll("\\[|\\]", "").trim();
        return r;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        Set<String> rA= new HashSet<>();
        for (Item i : ((Rule) o).getAntecedent()){
            rA.add(i.toString().replaceAll(" ", ""));
        }
        Set<String> rC= new HashSet<>();
        for (Item i : ((Rule) o).getConsequent()){
            rC.add(i.toString().replaceAll(" ", ""));
        }

        Set<String> thisA= new HashSet<>();
        Set<String> thisC= new HashSet<>();

        for (Item i : (antecedent)){
            thisA.add(i.toString().replaceAll(" ", ""));
        }
        for (Item i : consequent){
            thisC.add(i.toString().replaceAll(" ", ""));
        }

        return rA.equals(thisA) && rC.equals(thisC);
    }

    @Override
    public int hashCode() {

        Set<String> thisA= new HashSet<>();
        Set<String> thisC= new HashSet<>();

        for (Item i : (antecedent)){
            thisA.add(i.toString().replaceAll(" ", ""));
        }
        for (Item i : consequent){
            thisC.add(i.toString().replaceAll(" ", ""));
        }

        return Objects.hash(thisA, thisC);
    }
}
