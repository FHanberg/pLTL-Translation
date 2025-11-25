import PLTL.PLTLExp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class TranslationOutput {

    private PLTLExp m_to;
    private LinkedHashSet<LinkedHashSet<String>> m_valuations;
    private LinkedHashSet<Integer> m_obligations;

    public TranslationOutput(PLTLExp to, LinkedHashSet<String> first_val){
        m_to = to;
        m_valuations = new LinkedHashSet<>();
        m_valuations.add(first_val);
        m_obligations = new LinkedHashSet<>();
    }

    public PLTLExp getTo(){
        return m_to;
    }

    public LinkedHashSet<LinkedHashSet<String>> getVals(){
        return m_valuations;
    }

    public void addVal(LinkedHashSet<String> val){
        boolean fresh = true;
        for (LinkedHashSet<String> set: m_valuations) {
            if(val.size() == set.size()){
                if(set.containsAll(val)){
                    fresh = false;
                }
            }

        }
        if(fresh)
            m_valuations.add(val);
    }

    public void addObs(LinkedHashSet<Integer> obs){
        m_obligations.addAll(obs);
    }

    public LinkedHashSet<Integer> getObs(){
        return m_obligations;
    }
}
