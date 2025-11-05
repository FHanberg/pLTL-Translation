import java.util.LinkedList;

public class TNGBA {
    private LinkedList<NBAState> m_stateList;
    private LinkedList<NBATransition> m_transitionList;
    private LinkedList<String> m_atomList;
    private int m_labelCount;

    public TNGBA(LinkedList<NBAState> stateList, LinkedList<NBATransition> transitionList, LinkedList<String> atomList, int labels){
        m_stateList = stateList;
        m_transitionList = transitionList;
        m_atomList = atomList;
        m_labelCount = labels;
    }
}
