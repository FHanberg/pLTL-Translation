package PLTL.Past;

import PLTL.Future.Globally;
import PLTL.PLTLExp;
import PLTL.Unary;

public class Yesterday extends Unary {
    public Yesterday(PLTLExp target) {
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
        if(!(obj instanceof Yesterday)){
            return false;
        }
        if(!((Yesterday) obj).getTarget().equals(this.getTarget())){
            return false;
        }
        return true;
    }
}
