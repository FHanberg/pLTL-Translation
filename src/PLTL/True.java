package PLTL;

public class True extends PLTLExp{
    @Override
    public <R> R accept(PLTLExp.Visitor<R> v) {
        return v.visit(this);
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
