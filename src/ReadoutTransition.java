import java.util.LinkedHashSet;

public class ReadoutTransition{
    int m_from;
    int m_to;
    LinkedHashSet<Integer> m_labels;
    LinkedHashSet<LinkedHashSet<String>> m_valuations;

    public ReadoutTransition(int f, int t){
        m_from = f;
        m_to = t;
        m_labels = new LinkedHashSet<>();
        m_valuations = new LinkedHashSet<>();
    }
}
