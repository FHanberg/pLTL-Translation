package PLTL.Future;

import PLTL.PLTLExp;

public class Globally extends PLTL.Unary {
    public Globally(PLTLExp target) {
        super(target);
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
        if(!(obj instanceof Globally)){
            return false;
        }
        if(!((Globally) obj).getTarget().equals(this.getTarget())){
            return false;
        }
        return true;
    }
}
