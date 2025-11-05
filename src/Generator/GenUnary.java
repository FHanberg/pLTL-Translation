package Generator;

public class GenUnary extends GenStructure{
    GenStructure m_target;
    public GenUnary(GenCodes type) {
        super(type);
    }

    public void setTarget(GenStructure m_target) {
        this.m_target = m_target;
    }

    public GenStructure getTarget() {
        return m_target;
    }
}
