package PLTL;

//General binary class to reduce repeat code
public abstract class Binary extends PLTLExp {
    PLTLExp m_left;
    PLTLExp m_right;

    public Binary(PLTLExp left, PLTLExp right){
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
}
