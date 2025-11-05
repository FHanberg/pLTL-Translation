package Generator;

public class GenBinary extends GenStructure{
    GenStructure m_left;
    GenStructure m_right;
    public GenBinary(GenCodes type) {
        super(type);
    }

    public GenStructure getLeft() {
        return m_left;
    }

    public GenStructure getRight() {
        return m_right;
    }

    public void setLeft(GenStructure left) {
        this.m_left = left;
    }

    public void setRight(GenStructure right) {
        this.m_right = right;
    }
}
