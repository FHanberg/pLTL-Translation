package PLTL.Future;

import PLTL.Binary;
import PLTL.PLTLExp;

//Not actually sure what this operator is shorthand for, honestly
public class Mighty extends Binary {
    public Mighty(PLTLExp left, PLTLExp right) {
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
        if(!(obj instanceof Mighty)){
            return false;
        }
        if(!((Mighty) obj).getLeft().equals(this.getLeft())){
            return false;
        }
        return ((Mighty) obj).getRight().equals(this.getRight());
    }
}
