import PLTL.*;
import PLTL.Future.*;
import PLTL.Past.*;

import java.util.HashSet;

public class Translator {
    //counters for labeling purposes
    int pastLabels;
    int transitionLabels;
    //Current weakening conditions
    HashSet<PLTLExp> WC;
    //Current formulas to be weakened
    HashSet<Integer> toWeaken;

    public Translator(){
        pastLabels = 0;
        transitionLabels = 0;
    }

    public HashSet<NBAState> translationStep(NBAState base){
        pastLabels = 0;
        HashSet<NBAState> result = new HashSet<>();
        setupLabels(base.getExp());
        HashSet<HashSet<Integer>> subsets = getAllSubsets(pastLabels, 0, new HashSet<>());
        for (HashSet<Integer> C: subsets) {
            WC = new HashSet<>();
            toWeaken = C;
            setWC(base.getExp(), C);
            PLTLExp current = base.getExp().accept(new LocalAfter());
            PLTLExp rewritten = current.accept(new DNF());
            HashSet<PLTLExp> implicants = primeImplicants(rewritten);
            for (PLTLExp exp: implicants) {
                result.add(new NBAState(trueFalseTrim(exp)));
            }
        }

        return result;
    }

    //Checks And statements for True/False and trims accordingly
    private PLTLExp trueFalseTrim(PLTLExp exp){
        if(exp instanceof And){
            PLTLExp left = trueFalseTrim(((And) exp).getLeft());
            PLTLExp right = trueFalseTrim(((And) exp).getRight());
            if(left instanceof True){
                return right;
            }
            if(right instanceof True){
                return left;
            }
            if(left instanceof False){
                return new False();
            }
            if(right instanceof False){
                return new False();
            }
            return new And(left, right);
        }
        if(exp instanceof Or){//Might never be true in practice at the moment
            PLTLExp left = trueFalseTrim(((Or) exp).getLeft());
            PLTLExp right = trueFalseTrim(((Or) exp).getRight());
            if(left instanceof True){
                return new True();
            }
            if(right instanceof True){
                return new True();
            }
            if(left instanceof False){
                return right;
            }
            if(right instanceof False){
                return left;
            }
            return new Or(left, right);
        }

        return exp;
    }

    //Returns the (currently unoptimized) implicants of a formula
    HashSet<PLTLExp> primeImplicants(PLTLExp exp){
        HashSet<PLTLExp> result = new HashSet<>();

        if(exp instanceof And){
            result.add(((And) exp).getLeft());
            result.add(((And) exp).getRight());
        }else if(exp instanceof Or){
            result.addAll(primeImplicants(((Or) exp).getLeft()));
            result.addAll(primeImplicants(((Or) exp).getRight()));
        }else{
            result.add(exp);
        }

        return result;
    }

    //Not set up as a visitor since
    void setupLabels(PLTLExp exp){

        if(exp instanceof Unary){
            if(exp instanceof Yesterday || exp instanceof WYesterday){
                exp.label = pastLabels;
                pastLabels +=1;
            }else{
                exp.label = -1;
            }
            setupLabels(((Unary) exp).getTarget());
        }else if(exp instanceof Binary){
            if(exp instanceof Since || exp instanceof WSince|| exp instanceof Before || exp instanceof WBefore){
                exp.label = pastLabels;
                pastLabels +=1;
            }else if(exp instanceof Until || exp instanceof M){
                if(exp.label == -1){
                    exp.label = transitionLabels;
                    transitionLabels += 1;
                }
            }else{
                exp.label = -1;
            }
            setupLabels(((Binary)exp).getLeft());
            setupLabels(((Binary)exp).getRight());
        }

    }

    public HashSet<HashSet<Integer>> getAllSubsets(int length, int index, HashSet<Integer> current){
        HashSet<HashSet<Integer>> result = new HashSet<>();
        if(index != length){
            HashSet<Integer> add = new HashSet<>(current);
            add.add(index);
            result.addAll(getAllSubsets(length, index + 1, add));
            HashSet<Integer> stay = new HashSet<>(current);
            result.addAll(getAllSubsets(length, index + 1, stay));
        }
        result.add(current);
        return result;
    }

    void setWC(PLTLExp exp, HashSet<Integer> target){
        if(exp instanceof Unary){
            if(exp instanceof Yesterday || exp instanceof WYesterday){
                if(target.contains(exp.label)){
                    WC.add(((Unary)exp).getTarget());
                }
            }
            setWC(((Unary) exp).getTarget(), target);
        }else if(exp instanceof Binary){
            if(target.contains(exp.label)) {
                if (exp instanceof Since) {
                    WC.add(((Since) exp).getRight());
                } else if (exp instanceof WSince) {
                    WC.add(new Or(((WSince) exp).getLeft(),((WSince) exp).getRight()));
                } else if (exp instanceof Before) {
                    WC.add(new And(((Before) exp).getLeft(),((Before) exp).getRight()));
                } else if (exp instanceof WBefore) {
                    WC.add(((WBefore) exp).getRight());
                }
            }
            setWC(((Binary)exp).getLeft(), target);
            setWC(((Binary)exp).getRight(), target);
        }
    }

    class LocalAfter implements PLTLExp.Visitor<PLTLExp>{

        @Override
        public PLTLExp visit(And exp) {
            PLTLExp left = exp.getLeft().accept(new LocalAfter());
            PLTLExp right = exp.getRight().accept(new LocalAfter());

            return new And(left, right);
        }

        @Override
        public PLTLExp visit(Or exp) {

            PLTLExp left = exp.getLeft().accept(new LocalAfter());
            PLTLExp right = exp.getRight().accept(new LocalAfter());

            return new Or(left, right);
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
            return null;
        }

        @Override
        public PLTLExp visit(Historically exp) {
            return null;
        } //Should not be reached


        @Override
        public PLTLExp visit(Until exp) {
            return new Or(exp.getRight().accept(new LocalAfter()), new And(exp.getLeft().accept(new LocalAfter()), postUpdate(exp)));
        }
        @Override
        public PLTLExp visit(WUntil exp) {
            return new Or(exp.getRight().accept(new LocalAfter()), new And(exp.getLeft().accept(new LocalAfter()), postUpdate(exp)));
        }

        @Override
        public PLTLExp visit(M exp) {
            return new And(exp.getRight().accept(new LocalAfter()), new Or(exp.getLeft().accept(new LocalAfter()), postUpdate(exp)));
        }


        @Override
        public PLTLExp visit(Future exp) {
            return null;
        }

        @Override
        public PLTLExp visit(Once exp) {
            return null;
        }


        @Override
        public PLTLExp visit(Next exp) {
            return postUpdate(exp.getTarget());
        }

        @Override
        public PLTLExp visit(Yesterday exp) {
            return new False();
        }

        @Override
        public PLTLExp visit(WYesterday exp) {
            return new True();
        }


        @Override
        public PLTLExp visit(Release exp) {
            return new And(exp.getRight().accept(new LocalAfter()), new Or(exp.getLeft().accept(new LocalAfter()), postUpdate(exp)));
        }


        @Override
        public PLTLExp visit(Since exp) {
            return exp.getRight().accept(new LocalAfter());
        }

        @Override
        public PLTLExp visit(WSince exp) {
            return new Or(exp.getLeft(), exp.getRight()).accept(new LocalAfter());
        }


        @Override
        public PLTLExp visit(Before exp) {
            return new And(exp.getLeft(), exp.getRight()).accept(new LocalAfter());
        }

        @Override
        public PLTLExp visit(WBefore exp) {
            return exp.getRight().accept(new LocalAfter());
        }


        @Override
        public PLTLExp visit(Not exp) {
            return null;
        }

        PLTLExp postUpdate(PLTLExp target){
            PLTLExp result = target.accept(new Weakening());

            for (PLTLExp C: WC) {
                result = new And(C.accept(new LocalAfter()), result);
            }

            return result;
        }
    }

    class Weakening implements PLTLExp.Visitor<PLTLExp>{

        @Override
        public PLTLExp visit(And exp) {
            return new And(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
        }

        @Override
        public PLTLExp visit(Or exp) {
            return new Or(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
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
            return null;
        }

        @Override
        public PLTLExp visit(Historically exp) {
            return null;
        }


        @Override
        public PLTLExp visit(Until exp) {
            Until newExp = new Until(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
            newExp.label = exp.label;
            return newExp;
        }

        @Override
        public PLTLExp visit(WUntil exp) {
            return new WUntil(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
        }

        @Override
        public PLTLExp visit(M exp) {
            M newExp = new M(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
            newExp.label = exp.label;
            return newExp;
        }


        @Override
        public PLTLExp visit(Future exp) {
            return null;
        }

        @Override
        public PLTLExp visit(Once exp) {
            return null;
        }


        @Override
        public PLTLExp visit(Next exp) {
            return new Next(exp.getTarget().accept(new Weakening()));
        }

        @Override
        public PLTLExp visit(Yesterday exp) {
            if(toWeaken.contains(exp.label))
                return new WYesterday(exp.getTarget().accept(new Weakening()));
            return new Yesterday(exp.getTarget().accept(new Weakening()));
        }

        @Override
        public PLTLExp visit(WYesterday exp) {
            if(toWeaken.contains(exp.label))
                return new WYesterday(exp.getTarget().accept(new Weakening()));
            return new Yesterday(exp.getTarget().accept(new Weakening()));
        }



        @Override
        public PLTLExp visit(Release exp) {
            return new Release(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
        }


        @Override
        public PLTLExp visit(Since exp) {
            if(toWeaken.contains(exp.label))
                return new WSince(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
            return new Since(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
        }

        @Override
        public PLTLExp visit(WSince exp) {
            if(toWeaken.contains(exp.label))
                return new WSince(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
            return new Since(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
        }



        @Override
        public PLTLExp visit(Before exp) {
            if(toWeaken.contains(exp.label))
                return new WBefore(exp.getLeft().accept(new Weakening()),exp.getRight().accept(new Weakening()));
            return new Before(exp.getLeft().accept(new Weakening()),exp.getRight().accept(new Weakening()));
        }



        @Override
        public PLTLExp visit(WBefore exp) {
            if(toWeaken.contains(exp.label))
                return new WBefore(exp.getLeft().accept(new Weakening()),exp.getRight().accept(new Weakening()));
            return new Before(exp.getLeft().accept(new Weakening()),exp.getRight().accept(new Weakening()));
        }





        @Override
        public PLTLExp visit(Not exp) {
            return null;
        }


    }

    class DNF implements PLTLExp.Visitor<PLTLExp>{

        @Override
        public PLTLExp visit(And exp) {
            PLTLExp left = exp.getLeft().accept(new DNF());
            PLTLExp right = exp.getRight().accept(new DNF());

            if(left instanceof Or){
                return new Or(new And(((Or) left).getLeft(), right).accept(new DNF())
                        ,new And(((Or) left).getRight(), right).accept(new DNF()));
            }
            if(right instanceof Or){
                return new Or(new And(left, ((Or) right).getLeft()).accept(new DNF())
                        ,new And(left,((Or) right).getRight()).accept(new DNF()));
            }

            return exp;
        }

        @Override
        public PLTLExp visit(Or exp) {
            PLTLExp left = exp.getLeft().accept(new DNF());
            PLTLExp right = exp.getRight().accept(new DNF());

            return new Or(left, right);
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
        public PLTLExp visit(False exp) {
            return exp;
        }

        @Override
        public PLTLExp visit(Historically exp) {
            return null;
        }

        @Override
        public PLTLExp visit(WUntil exp) {
            return exp;
        }

        @Override
        public PLTLExp visit(True exp) {
            return exp;
        }

        @Override
        public PLTLExp visit(Future exp) {
            return null;
        }

        @Override
        public PLTLExp visit(Globally exp) {
            return null;
        }

        @Override
        public PLTLExp visit(M exp) {
            return exp;
        }

        @Override
        public PLTLExp visit(Next exp) {
            return exp;
        }

        @Override
        public PLTLExp visit(Release exp) {
            return exp;
        }

        @Override
        public PLTLExp visit(Until exp) {
            return exp;
        }

        @Override
        public PLTLExp visit(Before exp) {
            return exp;
        }

        @Override
        public PLTLExp visit(Once exp) {
            return null;
        }

        @Override
        public PLTLExp visit(Since exp) {
            return exp;
        }

        @Override
        public PLTLExp visit(WBefore exp) {
            return exp;
        }

        @Override
        public PLTLExp visit(WSince exp) {
            return exp;
        }

        @Override
        public PLTLExp visit(WYesterday exp) {
            return exp;
        }

        @Override
        public PLTLExp visit(Yesterday exp) {
            return exp;
        }

        @Override
        public PLTLExp visit(Not exp) {
            return null;
        }
    }



}
