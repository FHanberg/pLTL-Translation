import PLTL.PLTLExp;

import java.util.HashSet;

public class NBAState {
    PLTLExp m_exp;
    int m_label;
    HashSet<String> m_valuation;


    public NBAState(PLTLExp exp){
        m_exp = exp;
        m_label = -1;
    }
    public NBAState(PLTLExp exp, HashSet<String> valuation){
        m_exp = exp;
        m_label = -1;
        m_valuation = valuation;
    }

    public NBAState(PLTLExp exp, int label){
        m_exp = exp;
        m_label = label;
        m_valuation = new HashSet<>();
    }

    public int getLabel() {
        return m_label;
    }

    public void setLabel(int m_label) {
        this.m_label = m_label;
    }

    public PLTLExp getExp() {
        return m_exp;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof NBAState)) {
            return false;
        }

        if((this.m_exp.equals(((NBAState) obj).m_exp))){
            return true;
        }
        return false;
    }
}
