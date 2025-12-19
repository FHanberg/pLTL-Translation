package PLTL;

//only used in pre-NNF formulas, replaced by NotTerm after conversion
public class Not extends Unary {
    public Not(PLTLExp target) {
        super(target);
    }

    @Override
    public String getOp() {
        return "!";
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
        if(!(obj instanceof Not)){
            return false;
        }
        if(!((Not) obj).m_target.equals(this.m_target)){
            return false;
        }
        return true;
    }
}
