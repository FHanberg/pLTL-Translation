package PLTL;

//Used in post-NNF-conversion formulas to represent "not [term]"
public class NotTerm extends PLTLExp {
    public String m_term;

    public NotTerm(String term){
        m_term = term;
    }
    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(!(obj instanceof NotTerm)){
            return false;
        }
        if(!((NotTerm) obj).m_term.equals(this.m_term)){
            return false;
        }
        return true;
    }
}
