package PLTL.Future;

import PLTL.Binary;
import PLTL.PLTLExp;

//Yes, this is operator "W"
public class WUntil extends Binary {
    public WUntil(PLTLExp left, PLTLExp right) {
        super(left, right);
    }

    @Override
    public String getOp() {
        return "W";
    }

    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }

    @Override
    public <R, A> R accept(AltVisitor<R, A> v, A args) {
        return v.visit(this, args);
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
