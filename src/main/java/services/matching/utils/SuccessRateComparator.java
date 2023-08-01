package services.matching.utils;

import analysis.utils.RuleMatch;

import java.util.Comparator;

public class SuccessRateComparator implements Comparator<RuleMatch> {
    @Override
    public int compare(RuleMatch r1, RuleMatch r2) {
        // Tratta i valori NaN come maggiori di tutti gli altri valori

        Double d1 = r1.getSuccessRate();
        Double d2 = r2.getSuccessRate();
        if (d1.isNaN() && d2.isNaN()) {
            return 0;
        } else if (d1.isNaN()) {
            return 1;
        } else if (d2.isNaN()) {
            return -1;
        } else {
            // Ordina i valori normalmente (dal più grande al più piccolo)
            return Double.compare(d2, d1);
        }
    }
}
