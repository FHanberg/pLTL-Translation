import PLTL.*;
import PLTL.Future.*;
import PLTL.Past.*;


/*
    A visitor-implementation which writes out a PLTL formula to the console
     */
public class FormulaReader implements PLTLExp.Visitor<PLTLExp>{

    @Override
    public PLTLExp visit(And exp) {
        System.out.print("(");
        exp.getLeft().accept(new FormulaReader());
        System.out.print(" and ");
        exp.getRight().accept(new FormulaReader());
        System.out.print(")");
        return exp;
    }

    @Override
    public PLTLExp visit(Or exp) {
        System.out.print("(");
        exp.getLeft().accept(new FormulaReader());
        System.out.print(" or ");
        exp.getRight().accept(new FormulaReader());
        System.out.print(")");
        return exp;
    }

    @Override
    public PLTLExp visit(Term exp) {
        System.out.print(exp.m_term);
        return exp;
    }

    @Override
    public PLTLExp visit(NotTerm exp) {
        System.out.print("not ");
        System.out.print(exp.m_term);
        return exp;
    }

    @Override
    public PLTLExp visit(False exp) {
        System.out.print("false");
        return exp;
    }

    @Override
    public PLTLExp visit(Historically exp) {
        System.out.print("historically ");
        exp.getTarget().accept(new FormulaReader());
        return exp;
    }

    @Override
    public PLTLExp visit(WUntil exp) {
        System.out.print("(");
        exp.getLeft().accept(new FormulaReader());
        System.out.print(" WUntil ");
        exp.getRight().accept(new FormulaReader());
        System.out.print(")");
        return exp;
    }

    @Override
    public PLTLExp visit(True exp) {
        System.out.print("true");
        return exp;
    }

    @Override
    public PLTLExp visit(Future exp) {
        System.out.print("future ");
        exp.getTarget().accept(new FormulaReader());
        return exp;
    }

    @Override
    public PLTLExp visit(Globally exp) {
        System.out.print("globally ");
        exp.getTarget().accept(new FormulaReader());
        return exp;
    }

    @Override
    public PLTLExp visit(Mighty exp) {
        System.out.print("(");
        exp.getLeft().accept(new FormulaReader());
        System.out.print(" M ");
        exp.getRight().accept(new FormulaReader());
        System.out.print(")");
        return exp;
    }

    @Override
    public PLTLExp visit(Next exp) {
        System.out.print("next ");
        exp.getTarget().accept(new FormulaReader());
        return exp;
    }

    @Override
    public PLTLExp visit(Release exp) {
        System.out.print("(");
        exp.getLeft().accept(new FormulaReader());
        System.out.print(" Releases ");
        exp.getRight().accept(new FormulaReader());
        System.out.print(")");
        return exp;
    }

    @Override
    public PLTLExp visit(Until exp) {
        System.out.print("(");
        exp.getLeft().accept(new FormulaReader());
        System.out.print(" Until ");
        exp.getRight().accept(new FormulaReader());
        System.out.print(")");
        return exp;
    }

    @Override
    public PLTLExp visit(Before exp) {
        System.out.print("(");
        exp.getLeft().accept(new FormulaReader());
        System.out.print(" Before ");
        exp.getRight().accept(new FormulaReader());
        System.out.print(")");
        return exp;
    }

    @Override
    public PLTLExp visit(Once exp) {
        System.out.print("once ");
        exp.getTarget().accept(new FormulaReader());
        return exp;
    }

    @Override
    public PLTLExp visit(Since exp) {
        System.out.print("(");
        exp.getLeft().accept(new FormulaReader());
        System.out.print(" Since ");
        exp.getRight().accept(new FormulaReader());
        System.out.print(")");
        return exp;
    }

    @Override
    public PLTLExp visit(WBefore exp) {
        System.out.print("(");
        exp.getLeft().accept(new FormulaReader());
        System.out.print(" WBefore ");
        exp.getRight().accept(new FormulaReader());
        System.out.print(")");
        return exp;
    }

    @Override
    public PLTLExp visit(WSince exp) {
        System.out.print("(");
        exp.getLeft().accept(new FormulaReader());
        System.out.print(" WSince ");
        exp.getRight().accept(new FormulaReader());
        System.out.print(")");
        return exp;
    }

    @Override
    public PLTLExp visit(WYesterday exp) {
        System.out.print("Wyesterday ");
        exp.getTarget().accept(new FormulaReader());
        return exp;
    }

    @Override
    public PLTLExp visit(Yesterday exp) {
        System.out.print("yesterday ");
        exp.getTarget().accept(new FormulaReader());
        return exp;
    }

    @Override
    public PLTLExp visit(Not exp) {
        System.out.print("not ");
        exp.getTarget().accept(new FormulaReader());
        return exp;
    }
}
