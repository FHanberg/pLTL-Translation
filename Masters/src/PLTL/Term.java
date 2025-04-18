package PLTL;

//e.g "p", "q"
public class Term extends PLTLExp{
    public String m_term;

    public Term(String term){
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
        if(!(obj instanceof Term)){
            return false;
        }
        if(!((Term) obj).m_term.equals(this.m_term)){
            return false;
        }
        return true;
    }
}
