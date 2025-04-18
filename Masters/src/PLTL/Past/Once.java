package PLTL.Past;

import PLTL.Future.Future;
import PLTL.PLTLExp;
import PLTL.Unary;

public class Once extends Unary {
    public Once(PLTLExp target) {
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
        if(!(obj instanceof Once)){
            return false;
        }
        if(!((Once) obj).getTarget().equals(this.getTarget())){
            return false;
        }
        return true;
    }
}
