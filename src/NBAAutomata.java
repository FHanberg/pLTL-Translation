import java.util.HashMap;
import java.util.LinkedList;

public class NBAAutomata {
    private LinkedList<NBAState> m_states;
    private HashMap<Integer, HashMap<String, LinkedList<NBATransition>>> m_transitions;
    private LinkedList<String> m_atoms;
    private int m_labels;

    public NBAAutomata(LinkedList<NBAState> states, HashMap<Integer, HashMap<String,LinkedList<NBATransition>>> transitions, LinkedList<String> atoms, int labels){
        m_states = states;
        m_transitions = transitions;
        m_atoms = atoms;
        m_labels = labels;
    }

    public LinkedList<NBAState> getM_states() {
        return m_states;
    }

    public HashMap<Integer, HashMap<String,LinkedList<NBATransition>>> getM_transitions() {
        return m_transitions;
    }

    public LinkedList<String> getM_atoms() {
        return m_atoms;
    }

    public int getM_labels() {
        return m_labels;
    }

    public boolean simpleOutcome(){
        if(m_states.size() <= 3){
            return true;
        }else{
            for (int i = 1; i < m_states.size(); i++) {
                boolean isFalse = false;
                boolean reachesSelf = false;

                HashMap<String, LinkedList<NBATransition>> map = m_transitions.get(i);
                for (LinkedList<NBATransition> tr : map.values()) {
                    for (NBATransition t : tr) {
                        if (t.m_to == i) {
                            if (tr.size() == 1)
                                if (t.m_labels.size() == 0) {
                                    if (!reachesSelf)
                                        isFalse = true;
                                }else{
                                    isFalse = false;
                                }
                            reachesSelf = true;
                        }
                        if (t.m_to != 0) {
                            if (t.m_labels.size() < m_labels)
                                return false;
                        }
                    }
                }
                if(isFalse)
                    return false;
            }
            return true;
        }
    }
}
