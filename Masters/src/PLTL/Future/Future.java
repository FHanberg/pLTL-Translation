package PLTL.Future;

import PLTL.Not;
import PLTL.PLTLExp;

public class Future extends PLTL.Unary {
    public Future(PLTLExp target) {
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
        if(!(obj instanceof Future)){
            return false;
        }
        if(!((Future) obj).getTarget().equals(this.getTarget())){
            return false;
        }
        return true;
    }
}
