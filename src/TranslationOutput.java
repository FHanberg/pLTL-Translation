import PLTL.PLTLExp;

import java.util.HashSet;
import java.util.LinkedList;

public class TranslationOutput {
    private NBAState m_from;
    private PLTLExp m_to;
    private HashSet<HashSet<String>> m_valuations;

    public TranslationOutput(NBAState from, PLTLExp to, HashSet<String> first_val){
        m_from = from;
        m_to = to;
        m_valuations = new HashSet<>();
        m_valuations.add(first_val);
    }

    public NBAState getFrom(){
        return m_from;
    }

    public PLTLExp getTo(){
        return m_to;
    }

    public HashSet<HashSet<String>> getVals(){
        return m_valuations;
    }

    public void addVal(HashSet<String> val){
        boolean fresh = true;
        for (HashSet<String> set: m_valuations) {
            if(val.size() == set.size()){
                if(set.containsAll(val)){
                    fresh = false;
                }
            }

        }
        if(fresh)
            m_valuations.add(val);
    }
}
