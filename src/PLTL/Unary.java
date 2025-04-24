package PLTL;


//General unary class to reduce repeat code
public abstract class Unary extends PLTLExp{
    PLTLExp m_target;

    public Unary(PLTLExp target){
        super();
        m_target = target;
    }

    public PLTLExp getTarget(){
        return m_target;
    }

}
