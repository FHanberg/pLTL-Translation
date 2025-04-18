import PLTL.*;
import PLTL.Future.*;
import PLTL.Past.*;

public class NNFConverter implements PLTLExp.Visitor<PLTLExp> {

    @Override
    public PLTLExp visit(And exp) {
        PLTLExp newLeft = exp.getLeft().accept(new NNFConverter());
        PLTLExp newRight = exp.getRight().accept(new NNFConverter());
        return new And(newLeft, newRight);
    }

    @Override
    public PLTLExp visit(Or exp) {
        PLTLExp newLeft = exp.getLeft().accept(new NNFConverter());
        PLTLExp newRight = exp.getRight().accept(new NNFConverter());
        return new Or(newLeft, newRight);
    }


    @Override
    public PLTLExp visit(Term exp) {
        return exp;
    }

    @Override
    public PLTLExp visit(NotTerm exp) {
        return exp;
    }


    @Override
    public PLTLExp visit(True exp) {
        return exp;
    }

    @Override
    public PLTLExp visit(False exp) {
        return exp;
    }


    @Override
    public PLTLExp visit(Globally exp) {
        Not newExp = new Not(new Future(new Not(exp.getTarget())));
        return newExp.accept(new NNFConverter());
    }

    @Override
    public PLTLExp visit(Historically exp) {
        Not newExp = new Not(new Once(new Not(exp.getTarget())));
        return newExp.accept(new NNFConverter());
    }


    @Override
    public PLTLExp visit(Until exp) {
        PLTLExp newLeft = exp.getLeft().accept(new NNFConverter());
        PLTLExp newRight = exp.getRight().accept(new NNFConverter());
        return new Until(newLeft, newRight);
    }

    @Override
    public PLTLExp visit(WUntil exp) {
        PLTLExp newLeft = exp.getLeft().accept(new NNFConverter());
        PLTLExp newRight = exp.getRight().accept(new NNFConverter());
        return new WUntil(newLeft, newRight);
    }

    @Override
    public PLTLExp visit(M exp) {
        PLTLExp newLeft = exp.getLeft().accept(new NNFConverter());
        PLTLExp newRight = exp.getRight().accept(new NNFConverter());
        return new M(newLeft, newRight);
    }


    @Override
    public PLTLExp visit(Future exp) {
        return new Until(new True(), exp.getTarget().accept(new NNFConverter()));
    }

    @Override
    public PLTLExp visit(Once exp) {
        return new Since(new True(), exp.getTarget().accept(new NNFConverter()));
    }


    @Override
    public PLTLExp visit(Next exp) {
        return new Next(exp.getTarget().accept(new NNFConverter()));
    }

    @Override
    public PLTLExp visit(Yesterday exp) {
        return new Yesterday(exp.getTarget().accept(new NNFConverter()));
    }

    @Override
    public PLTLExp visit(WYesterday exp) {
        return new WYesterday(exp.getTarget().accept(new NNFConverter()));
    }


    @Override
    public PLTLExp visit(Release exp) {
        PLTLExp newLeft = exp.getLeft().accept(new NNFConverter());
        PLTLExp newRight = exp.getRight().accept(new NNFConverter());
        return new Release(newLeft, newRight);
    }


    @Override
    public PLTLExp visit(Since exp) {
        PLTLExp newLeft = exp.getLeft().accept(new NNFConverter());
        PLTLExp newRight = exp.getRight().accept(new NNFConverter());
        return new Since(newLeft, newRight);
    }

    @Override
    public PLTLExp visit(WSince exp) {
        PLTLExp newLeft = exp.getLeft().accept(new NNFConverter());
        PLTLExp newRight = exp.getRight().accept(new NNFConverter());
        return new WSince(newLeft, newRight);
    }


    @Override
    public PLTLExp visit(Before exp) {
        PLTLExp newLeft = exp.getLeft().accept(new NNFConverter());
        PLTLExp newRight = exp.getRight().accept(new NNFConverter());
        return new Before(newLeft, newRight);
    }

    @Override
    public PLTLExp visit(WBefore exp) {
        PLTLExp newLeft = exp.getLeft().accept(new NNFConverter());
        PLTLExp newRight = exp.getRight().accept(new NNFConverter());
        return new WBefore(newLeft, newRight);
    }


    //The "meat" of NNF-conversion
    @Override
    public PLTLExp visit(Not exp) {
        PLTLExp target = exp.getTarget();
        if(target instanceof True)
            return new False();
        if(target instanceof False){
            return new True();
        }
        if(target instanceof Term){
            return new NotTerm(((Term) target).m_term);
        }
        if(target instanceof NotTerm){
            return new Term(((NotTerm) target).m_term);
        }
        if(target instanceof Binary){
            PLTLExp left = ((Binary) target).getLeft();
            PLTLExp right = ((Binary) target).getRight();
            if(target instanceof And){
                //Calling accept on the full Or instead of left/right individually might be mildly inefficient,
                //but I'd say the code is less cluttered.
                return new Or(new Not(left), new Not(right)).accept(new NNFConverter());
            }
            if(target instanceof Or){
                return new And(new Not(left), new Not(right)).accept(new NNFConverter());
            }
            if(target instanceof Until){
                return new Release(new Not(left), new Not(right)).accept(new NNFConverter());
            }
            if(target instanceof Release){
                return new Until(new Not(left), new Not(right)).accept(new NNFConverter());
            }
            if(target instanceof WUntil){
                return new M(new Not(left), new Not(right)).accept(new NNFConverter());
            }
            if(target instanceof M){
                return new WUntil(new Not(left), new Not(right)).accept(new NNFConverter());
            }
            if(target instanceof Since){
                return new WBefore(new Not(left), new Not(right)).accept(new NNFConverter());
            }
            if(target instanceof WBefore){
                return new Since(new Not(left), new Not(right)).accept(new NNFConverter());
            }
            if(target instanceof WSince){
                return new Before(new Not(left), new Not(right)).accept(new NNFConverter());
            }
            if(target instanceof Before){
                return new WSince(new Not(left), new Not(right)).accept(new NNFConverter());
            }
        }
        if(target instanceof Unary){
            PLTLExp inExp = ((Unary) target).getTarget();
            if(target instanceof Yesterday){
                return new WYesterday(new Not(inExp).accept(new NNFConverter()));
            }
            if(target instanceof WYesterday){
                return new Yesterday(new Not(inExp).accept(new NNFConverter()));
            }
            if(target instanceof Next){
                return new Next(new Not(inExp).accept(new NNFConverter()));
            }
            if(target instanceof Not){
                return inExp.accept(new NNFConverter());
            }
            if(target instanceof Future){
                return new Not(new Until(new True(), inExp)).accept(new NNFConverter());
            }
            if(target instanceof Once){
                return new Not(new Since(new True(), inExp)).accept(new NNFConverter());
            }
            if(target instanceof Globally){
                return new Future(new Not(inExp)).accept(new NNFConverter());
            }
            if(target instanceof Historically){
                return new Once(new Not(inExp)).accept(new NNFConverter());
            }
        }


        return null;
    }
}
