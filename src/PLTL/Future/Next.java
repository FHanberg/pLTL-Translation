package PLTL.Future;

import PLTL.PLTLExp;

public class Next extends PLTL.Unary {
    public Next(PLTLExp target) {
        super(target);
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
        if(!(obj instanceof Next)){
            return false;
        }
        if(!((Next) obj).getTarget().equals(this.getTarget())){
            return false;
        }
        return true;
    }
}
