package services.matching;

import commons.idea.Idea;

import java.util.Comparator;
import java.util.Date;

public class IdeaSourceIpComparator implements Comparator<Idea> {

    @Override
    public int compare(Idea idea1, Idea idea2) {
        String sourceIp1 = idea1.getSource().get(0).getIP4().get(0);
        String sourceIp2 = idea2.getSource().get(0).getIP4().get(0);

        // Ordinamento per indirizzo IP
        int ipComparison = sourceIp1.compareTo(sourceIp2);
        if (ipComparison != 0) {
            return ipComparison;
        }

        // Ordinamento per detectTime (meno recente al più recente)
        Date detectTime1 = idea1.getDetectTime();
        Date detectTime2 = idea2.getDetectTime();

        return detectTime1.compareTo(detectTime2);
    }
}
