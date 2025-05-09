package PLTL;

public class True extends PLTLExp{
    @Override
    public <R> R accept(PLTLExp.Visitor<R> v) {
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
        if(!(obj instanceof True)){
            return false;
        }
        return true;
    }
}
