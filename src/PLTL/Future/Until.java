package PLTL.Future;

import PLTL.Binary;
import PLTL.PLTLExp;

public class Until extends Binary {
    public Until(PLTLExp left, PLTLExp right) {
        super(left, right);
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
        if(!(obj instanceof Until)){
            return false;
        }
        if(!((Until) obj).getLeft().equals(this.getLeft())){
            return false;
        }
        if(!((Until) obj).getRight().equals(this.getRight())){
            return false;
        }
        return true;
    }
}
