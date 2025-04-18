package PLTL.Past;

import PLTL.PLTLExp;
import PLTL.Unary;

public class Historically extends Unary {
    public Historically(PLTLExp target) {
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
        if(!(obj instanceof Historically)){
            return false;
        }
        if(!((Historically) obj).getTarget().equals(this.getTarget())){
            return false;
        }
        return true;
    }
}
