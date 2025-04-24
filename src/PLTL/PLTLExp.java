package PLTL;

import PLTL.Future.*;
import PLTL.Past.*;

//Main abstract class for formulas
public abstract class PLTLExp {
    public int transitionLabel;
    public int label;

    public PLTLExp(){
        label = -1;
        transitionLabel = -1;
    }

    public abstract <R> R accept(Visitor<R> v);
    public interface Visitor <R> {
        R visit(And exp);
        R visit(Or exp);

        R visit(Term exp);
        R visit(NotTerm exp);

        R visit(True exp);
        R visit(False exp);

        R visit(Globally exp);
        R visit(Historically exp);

        R visit(Until exp);
        R visit(WUntil exp);
        R visit(M exp);

        R visit(Future exp);
        R visit(Once exp);

        R visit(Next exp);
        R visit(Yesterday exp);
        R visit(WYesterday exp);

        R visit(Release exp);

        R visit(Since exp);
        R visit(WSince exp);

        R visit(Before exp);
        R visit(WBefore exp);

        R visit(Not exp);
    }

}
