package PLTL.Future;

import PLTL.PLTLExp;

public class Globally extends PLTL.Unary {
    public Globally(PLTLExp target) {
        super(target);
    }

    @Override
    public String getOp() {
        return "G";
    }

    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }

    @Override
    public <R, A> R accept(AltVisitor<R, A> v, A args) {
        return v.visit(this, args);
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(!(obj instanceof Globally)){
            return false;
        }
        if(!((Globally) obj).getTarget().equals(this.getTarget())){
            return false;
        }
        return true;
    }
}
