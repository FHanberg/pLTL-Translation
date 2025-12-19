import PLTL.PLTLExp;

import java.util.LinkedHashSet;

public class TranslationOutput {

    private PLTLExp m_to;
    private LinkedHashSet<String> m_valuation;
    private String m_key;
    private LinkedHashSet<Integer> m_obligations;

    public TranslationOutput(PLTLExp to, LinkedHashSet<String> val, String key){
        m_to = to;
        m_valuation = val;
        m_obligations = new LinkedHashSet<>();
        m_key = key;
    }

    public PLTLExp getTo(){
        return m_to;
    }

    public LinkedHashSet<String> getVal(){
        return m_valuation;
    }

    public String getKey(){
        return m_key;
    }

    public void addObs(LinkedHashSet<Integer> obs){
        m_obligations.addAll(obs);
    }

    public LinkedHashSet<Integer> getObs(){
        return m_obligations;
    }
}
