import PLTL.*;
import PLTL.Future.*;
import PLTL.Past.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

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

    public int getTransitionLabels(){
        return transitionLabels;
    }

    public HashSet<NBAState> translationStep(NBAState base, LinkedList<String> atomList){
        pastLabels = 0;
        HashSet<NBAState> result = new HashSet<>();
        setupLabels(base.getExp());
        HashSet<HashSet<String>> valuations = getAllValuations(atomList, 0, new HashSet<>());
        for(HashSet<Integer> C : getAllSubsets(pastLabels, 0, new HashSet<>())) {
            WC = new HashSet<>();
            toWeaken = C;
            setWC(base.getExp(), C);
            for (HashSet<String> valuation : valuations) {
                PLTLExp current = base.getExp().accept(new LocalAfter(), valuation);
                PLTLExp rewritten = current.accept(new DNF());
                HashSet<PLTLExp> implicants = primeImplicants(rewritten);
                for (PLTLExp exp : implicants) {
                    result.add(new NBAState(trueFalseTrim(exp), valuation));
                }
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

        if(exp instanceof Or){
            result.addAll(primeImplicants(((Or) exp).getLeft()));
            result.addAll(primeImplicants(((Or) exp).getRight()));
        }else{
            result.add(exp);
        }

        return result;
    }


    void setupLabels(PLTLExp exp){

        if(exp instanceof Unary){
            if(exp instanceof Yesterday || exp instanceof WYesterday){
                exp.label = pastLabels;
                pastLabels +=1;
                setupLabels(((Unary) exp).getTarget());
            }else{
                exp.label = -1;
                setupLabels(((Unary) exp).getTarget());
            }

        }else if(exp instanceof Binary){
            if (exp instanceof Since || exp instanceof WSince || exp instanceof Before || exp instanceof WBefore) {
                exp.label = pastLabels;
                pastLabels +=1;
            } else if(exp instanceof Until || exp instanceof M){
                if(exp.transitionLabel == -1){
                    exp.transitionLabel = transitionLabels;
                    transitionLabels += 1;
                }
            }else{
                exp.label = -1;
            }
            setupLabels(((Binary)exp).getLeft());
            setupLabels(((Binary)exp).getRight());
        }

    }

    public HashSet<HashSet<String>> getAllValuations(LinkedList<String> atoms, int index, HashSet<String> current){
        HashSet<HashSet<String>> result = new HashSet<>();
        if(index != atoms.size()){
            HashSet<String> add = new HashSet<>(current);
            add.add(atoms.get(index));
            result.addAll(getAllValuations(atoms, index + 1, add));
            HashSet<String> stay = new HashSet<>(current);
            result.addAll(getAllValuations(atoms, index + 1, stay));
        }
        result.add(current);
        return result;
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

    class LocalAfter implements PLTLExp.AltVisitor<PLTLExp, HashSet<String>>{

        @Override
        public PLTLExp visit(And exp, HashSet<String> args) {
            PLTLExp left = exp.getLeft().accept(new LocalAfter(), args);
            PLTLExp right = exp.getRight().accept(new LocalAfter(), args);

            return new And(left, right);
        }

        @Override
        public PLTLExp visit(Or exp, HashSet<String> args) {

            PLTLExp left = exp.getLeft().accept(new LocalAfter(), args);
            PLTLExp right = exp.getRight().accept(new LocalAfter(), args);

            return new Or(left, right);
        }


        @Override
        public PLTLExp visit(Term exp, HashSet<String> args) {
            if(args.contains(exp.m_term))
                return new True();
            return new False();
        }

        @Override
        public PLTLExp visit(NotTerm exp, HashSet<String> args) {
            if(args.contains(exp.m_term))
                return new False();
            return new True();
        }


        @Override
        public PLTLExp visit(True exp, HashSet<String> args) {
            return exp;
        }

        @Override
        public PLTLExp visit(False exp, HashSet<String> args) {
            return exp;
        }


        @Override
        public PLTLExp visit(Globally exp, HashSet<String> args) {
            return null;
        }

        @Override
        public PLTLExp visit(Historically exp, HashSet<String> args) {
            return null;
        } //Should not be reached


        @Override
        public PLTLExp visit(Until exp, HashSet<String> args) {
            PLTLExp right = exp.getRight().accept(new LocalAfter(), args);
            return new Or(right, new And(exp.getLeft().accept(new LocalAfter(), args), postUpdate(exp, args)));
        }
        @Override
        public PLTLExp visit(WUntil exp, HashSet<String> args) {
            return new Or(exp.getRight().accept(new LocalAfter(), args),
                    new And(exp.getLeft().accept(new LocalAfter(), args), postUpdate(exp, args)));
        }

        @Override
        public PLTLExp visit(M exp, HashSet<String> args) {
            return new And(exp.getRight().accept(new LocalAfter(), args),
                    new Or(exp.getLeft().accept(new LocalAfter(), args), postUpdate(exp, args)));
        }


        @Override
        public PLTLExp visit(Future exp, HashSet<String> args) {
            return null;
        }

        @Override
        public PLTLExp visit(Once exp, HashSet<String> args) {
            return null;
        }


        @Override
        public PLTLExp visit(Next exp, HashSet<String> args) {
            return postUpdate(exp.getTarget(), args);
        }

        @Override
        public PLTLExp visit(Yesterday exp, HashSet<String> args) {
            return new False();
        }

        @Override
        public PLTLExp visit(WYesterday exp, HashSet<String> args) {
            return new True();
        }


        @Override
        public PLTLExp visit(Release exp, HashSet<String> args) {
            return new And(exp.getRight().accept(new LocalAfter(), args),
                    new Or(exp.getLeft().accept(new LocalAfter(), args), postUpdate(exp, args)));
        }


        @Override
        public PLTLExp visit(Since exp, HashSet<String> args) {
            return exp.getRight().accept(new LocalAfter(), args);
        }

        @Override
        public PLTLExp visit(WSince exp, HashSet<String> args) {
            return new Or(exp.getLeft(), exp.getRight()).accept(new LocalAfter(), args);
        }


        @Override
        public PLTLExp visit(Before exp, HashSet<String> args) {
            return new And(exp.getLeft(), exp.getRight()).accept(new LocalAfter(), args);
        }

        @Override
        public PLTLExp visit(WBefore exp, HashSet<String> args) {
            return exp.getRight().accept(new LocalAfter(), args);
        }


        @Override
        public PLTLExp visit(Not exp, HashSet<String> args) {
            return null;
        }

        PLTLExp postUpdate(PLTLExp target, HashSet<String> args){
            PLTLExp result = target.accept(new Weakening());

            for (PLTLExp C: WC) {
                result = new And(C.accept(new LocalAfter(), args), result);
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
            newExp.transitionLabel = exp.transitionLabel;
            return newExp;
        }

        @Override
        public PLTLExp visit(WUntil exp) {
            return new WUntil(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
        }

        @Override
        public PLTLExp visit(M exp) {
            M newExp = new M(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
            newExp.transitionLabel = exp.transitionLabel;
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
