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

    public String getString(){
        return "(" + getOp() + " " + m_target.getString() + ")";
    }

    public abstract String getOp();

    @Override
    public boolean dirtyEquals(PLTLExp exp) {
        if(exp instanceof Unary){
            if(!this.getOp().equals(((Unary) exp).getOp()))
                return false;
            boolean a = hasAndOr(this.m_target);
            boolean b = hasAndOr(((Unary) exp).getTarget());
            if(a != b)
                return false;
            if(this.getString().equals(exp.getString()))
                return true;
            if(a){
                return this.m_target.dirtyEquals(((Unary) exp).getTarget());
            }
        }
        return false;
    }
}
