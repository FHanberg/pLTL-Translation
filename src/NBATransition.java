import PLTL.PLTLExp;
import PLTL.True;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class NBATransition {
    int m_from;
    int m_to;
    LinkedHashSet<Integer> m_labels;
    LinkedHashSet<LinkedHashSet<String>> m_valuations;

    public NBATransition(int from, int to, LinkedHashSet<String> valuation){
        m_from = from;
        m_to = to;
        m_labels = new LinkedHashSet<>();
        m_valuations = new LinkedHashSet<>();
        m_valuations.add(valuation);
    }

    public NBATransition(int from, int to, LinkedHashSet<Integer> labels, LinkedHashSet<LinkedHashSet<String>> valuations){
        m_from = from;
        m_to = to;
        m_labels = labels;
        m_valuations = valuations;
    }

    public void addValuation(LinkedHashSet<String> valuation){
        boolean old = false;
        for (LinkedHashSet<String> v: m_valuations) {
            if (v.equals(valuation)) {
                old = true;
                break;
            }
        }
        if(!old)
            m_valuations.add(valuation);
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof NBATransition)) {
            return false;
        }

        if((this.m_from == (((NBATransition) obj).m_from))){
            return this.m_to == (((NBATransition) obj).m_to);
        }
        return false;
    }



    static public Comparator<NBATransition> comp = (o1, o2) -> {
        if(o1.m_from == o2.m_from){
            return Integer.compare(o1.m_to, o2.m_to);
        }
        if(o1.m_from < o2.m_from){
            return -1;
        }
        return 1;
    };
}
