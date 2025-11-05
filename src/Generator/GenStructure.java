package Generator;

import java.util.Objects;

public abstract class GenStructure {
    private GenCodes m_type;

    public int localDepth;

    public int inPast;

    public GenStructure(GenCodes type){
        m_type = type;
    }

    public GenCodes getType() {
        return m_type;
    }

    public void setType(GenCodes m_type) {
        this.m_type = m_type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenStructure that = (GenStructure) o;
        return m_type == that.m_type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_type);
    }
}
