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
        R visit(Mighty exp);

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

    public abstract <R, A> R accept(AltVisitor<R,A> v, A args);
    public interface AltVisitor <R,A>{
        R visit(And exp, A args);
        R visit(Or exp, A args);

        R visit(Term exp, A args);
        R visit(NotTerm exp, A args);

        R visit(True exp, A args);
        R visit(False exp, A args);

        R visit(Globally exp, A args);
        R visit(Historically exp, A args);

        R visit(Until exp, A args);
        R visit(WUntil exp, A args);
        R visit(Mighty exp, A args);

        R visit(Future exp, A args);
        R visit(Once exp, A args);

        R visit(Next exp, A args);
        R visit(Yesterday exp, A args);
        R visit(WYesterday exp, A args);

        R visit(Release exp, A args);

        R visit(Since exp, A args);
        R visit(WSince exp, A args);

        R visit(Before exp, A args);
        R visit(WBefore exp, A args);

        R visit(Not exp, A args);
    }

}
