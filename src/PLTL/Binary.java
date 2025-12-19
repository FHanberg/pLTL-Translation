package PLTL;

//General binary class to reduce repeat code
public abstract class Binary extends PLTLExp {
    PLTLExp m_left;
    PLTLExp m_right;

    public Binary(PLTLExp left, PLTLExp right){
        super();
        m_left = left;
        m_right = right;
    }

    public PLTLExp getLeft() {
        return m_left;
    }

    public PLTLExp getRight() {
        return m_right;
    }

    public void setLeft(PLTLExp left){
        m_left = left;
    }

    public void setRight(PLTLExp right){
        m_right = right;
    }

    public String getString(){
        return "(" + m_left.getString() + " " + getOp() + " " + m_right.getString() + ")";
    }

    public abstract String getOp();

    @Override
    public boolean dirtyEquals(PLTLExp target) {
        if(target instanceof Binary){
            if(!this.getOp().equals(((Binary) target).getOp()))
                return false;
            boolean al = hasAndOr(this.getLeft());
            boolean ar = hasAndOr(this.getRight());
            boolean bl = hasAndOr(((Binary) target).getLeft());
            boolean br = hasAndOr(((Binary) target).getRight());
            if(!(al == bl && ar == br)){
                return false;
            }
            if(this.getString().equals(target.getString())){
                return true;
            }
            if(al || ar){
                return this.getLeft().dirtyEquals(((Binary) target).getLeft()) && this.getRight().dirtyEquals(((Binary) target).getRight());
            }
        }
        return false;
    }
}
