package PLTL.Past;

import PLTL.Binary;
import PLTL.PLTLExp;

public class WSince extends Binary {
    public WSince(PLTLExp left, PLTLExp right) {
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
        if(!(obj instanceof WSince)){
            return false;
        }
        if(!((WSince) obj).getLeft().equals(this.getLeft())){
            return false;
        }
        if(!((WSince) obj).getRight().equals(this.getRight())){
            return false;
        }
        return true;
    }
}
