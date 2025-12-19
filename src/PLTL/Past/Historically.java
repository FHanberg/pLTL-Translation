package PLTL.Past;

import PLTL.PLTLExp;
import PLTL.Unary;

public class Historically extends Unary {
    public Historically(PLTLExp target) {
        super(target);
    }

    @Override
    public String getOp() {
        return "H";
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
        if(!(obj instanceof Historically)){
            return false;
        }
        if(!((Historically) obj).getTarget().equals(this.getTarget())){
            return false;
        }
        return true;
    }
}
