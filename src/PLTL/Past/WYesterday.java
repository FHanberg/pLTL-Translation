package PLTL.Past;

import PLTL.PLTLExp;
import PLTL.Unary;

public class WYesterday extends Unary {
    public WYesterday(PLTLExp target) {
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
        if(!(obj instanceof WYesterday)){
            return false;
        }
        if(!((WYesterday) obj).getTarget().equals(this.getTarget())){
            return false;
        }
        return true;
    }
}
