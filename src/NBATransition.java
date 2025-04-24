import java.util.Comparator;
import java.util.HashSet;

public class NBATransition {
    int m_from;
    int m_to;
    HashSet<Integer> m_labels;

    public NBATransition(int from, int to){
        m_from = from;
        m_to = to;
        m_labels = new HashSet<>();
    }

    public NBATransition(int from, int to, HashSet<Integer> labels){
        m_from = from;
        m_to = to;
        m_labels = labels;
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
            if((this.m_to == (((NBATransition) obj).m_to))){
                return true;
            }
        }
        return false;
    }

    static public Comparator<NBATransition> comp = (o1, o2) -> {
        if(o1.m_from == o2.m_from){
            if(o1.m_to == o2.m_to){
                return 0;
            }
            if(o1.m_to < o2.m_to){
                return -1;
            }
            return 1;
        }
        if(o1.m_from < o2.m_from){
            return -1;
        }
        return 1;
    };
}
