package PLTL.Future;

import PLTL.Binary;
import PLTL.PLTLExp;

public class WUntil extends Binary {
    public WUntil(PLTLExp left, PLTLExp right) {
        super(left, right);
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
        if(!(obj instanceof WUntil)){
            return false;
        }
        if(!((WUntil) obj).getLeft().equals(this.getLeft())){
            return false;
        }
        if(!((WUntil) obj).getRight().equals(this.getRight())){
            return false;
        }
        return true;
    }
}
