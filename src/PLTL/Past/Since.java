package PLTL.Past;

import PLTL.Binary;
import PLTL.PLTLExp;

public class Since extends Binary {
    public Since(PLTLExp left, PLTLExp right) {
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
        if(!(obj instanceof Since)){
            return false;
        }
        if(!((Since) obj).getLeft().equals(this.getLeft())){
            return false;
        }
        if(!((Since) obj).getRight().equals(this.getRight())){
            return false;
        }
        return true;
    }
}
