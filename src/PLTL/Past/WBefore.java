package PLTL.Past;

import PLTL.Binary;
import PLTL.PLTLExp;

public class WBefore extends Binary {
    public WBefore(PLTLExp left, PLTLExp right) {
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
        if(!(obj instanceof WBefore)){
            return false;
        }
        if(!((WBefore) obj).getLeft().equals(this.getLeft())){
            return false;
        }
        if(!((WBefore) obj).getRight().equals(this.getRight())){
            return false;
        }
        return true;
    }
}
