import PLTL.*;
import PLTL.Future.*;
import PLTL.Past.*;

import java.util.*;

public class Translator {
    //counters for labeling purposes
    int pastLabels;
    int transitionLabels;

    int prelimEntries;

    LinkedList<PLTLExp> canBeWeakened;

    HashMap<Integer, PLTLExp> WCMap;
    HashMap<Integer, PLTLExp> WCPostVal;

    HashMap<Integer, PLTLExp> prelimInputMap;
    HashMap<Integer, HashMap<String, PLTLExp>> prelimOutputMap;

    //Current formulas to be weakened
    LinkedHashSet<Integer> toWeaken;
    boolean opt;

    public Translator(){
        pastLabels = 0;
        transitionLabels = 0;
        prelimEntries = 0;
        prelimInputMap = new HashMap<>();
        prelimOutputMap = new HashMap<>();
    }

    public int getTransitionLabels(){
        return transitionLabels;
    }

    public LinkedList<TranslationOutput> translationStep(NBAState base, LinkedList<String> atomList, boolean optimize){
        opt = optimize;
        pastLabels = 0;
        canBeWeakened = new LinkedList<>();
        LinkedList<TranslationOutput> result = new LinkedList<>();
        setupLabels(base.getExp());

        WCMap = new HashMap<>();
        setWC(base.getExp());

        LinkedList<PLTLExp> topLevel = topLevelConjunction(base.getExp());

        /*for (PLTLExp exp: topLevel) {
            exp.accept(new FormulaReader());
            System.out.println("");
        }*/

        LinkedHashSet<LinkedHashSet<String>> valuations = getAllValuations(atomList, 0, new LinkedHashSet<>());
        for (LinkedHashSet<String> valuation : valuations) {
             StringBuilder key = new StringBuilder();
                for (String string : valuation) {
                    key.append(string);
                }
            LinkedList<PLTLExp> postPreLim = new LinkedList<>();
            for (PLTLExp topExp: topLevel) {
                if(prelimInputMap.isEmpty()){
                    prelimInputMap.put(prelimEntries, topExp);
                    prelimEntries += 1;
                }
                boolean hasEntry = false;
                for (Integer entry: prelimInputMap.keySet()) {

                    if(topExp.equals(prelimInputMap.get(entry))){
                        hasEntry = true;
                        if(prelimOutputMap.containsKey(entry)) {
                            HashMap<String, PLTLExp> map = prelimOutputMap.get(entry);
                            if(map.containsKey(key.toString())){
                                postPreLim.add(map.get(key.toString()));
                            }else{
                                PLTLExp post = topExp.accept(new LocalAfter(), valuation);
                                map.put(key.toString(), post);
                                postPreLim.add(post);
                                prelimOutputMap.put(entry, map);
                            }
                        }
                        else{
                            HashMap<String, PLTLExp> map = new HashMap<>();
                            PLTLExp post = topExp.accept(new LocalAfter(), valuation);
                            map.put(key.toString(), post);
                            postPreLim.add(post);
                            prelimOutputMap.put(entry, map);
                        }
                        break;
                    }
                }
                if(!hasEntry){
                    prelimInputMap.put(prelimEntries, topExp);
                    PLTLExp post = topExp.accept(new LocalAfter(), valuation);
                    HashMap<String, PLTLExp> map = new HashMap<>();
                    map.put(key.toString(), post);
                    postPreLim.add(post);
                    prelimOutputMap.put(prelimEntries, map);
                    prelimEntries += 1;
                }

            }


            PLTLExp prelim = topLevelMerge(postPreLim);
            //PLTLExp prelim = base.getExp().accept(new LocalAfter(), valuation);
            if(prelim instanceof True || prelim instanceof False) {
                addResult(result, prelim, valuation);
                continue;
            }

            WCPostVal = new HashMap<>();
            for(Integer n : WCMap.keySet()){
                WCPostVal.put(n, WCMap.get(n).accept(new LocalAfter(), valuation));
            }
            for (LinkedHashSet<Integer> C : getAllSubsets(pastLabels, 0, new LinkedHashSet<>())) {
                toWeaken = C;
                boolean valid = true;
                PLTLExp wcActual = new True();
                if (optimize && !C.isEmpty()) {
                    for (Integer num : C) {
                        PLTLExp add = WCPostVal.get(num);
                        if (add instanceof False) {
                            valid = false;
                            break;
                        }
                        add = add.accept(new PostUpdateHandler(), valuation);
                        wcActual = new And(wcActual, add);
                    }
                }
                if(!valid){
                    addResult(result, new False(), valuation);
                    continue;
                }
                LinkedList<PLTLExp> list = new LinkedList<>();
                for (PLTLExp pre: postPreLim) {
                    list.add(pre.accept(new PostUpdateHandler(), valuation));
                }
                PLTLExp current = topLevelMerge(list);
                //PLTLExp current = prelim.accept(new PostUpdateHandler(), valuation);
                if (optimize) {
                    if(!(wcActual instanceof True))
                        current = new And(current, wcActual);
                }
                PLTLExp partTrim = trueFalseTrim(current);
                PLTLExp fullTrim = andTrim(partTrim);
                PLTLExp rewritten = fullTrim.accept(new DNF());

                LinkedHashSet<PLTLExp> implicants = primeImplicants(rewritten);
                for (PLTLExp exp : implicants) {
                    PLTLExp trimOne = trueFalseTrim(exp);
                    addResult(result, andTrim(trimOne), valuation);
                }
            }
        }
        return result;
    }

    private LinkedList<PLTLExp> topLevelConjunction(PLTLExp exp){
        LinkedList<PLTLExp> result = new LinkedList<>();
        if(exp instanceof And){
            result.addAll(topLevelConjunction(((And) exp).getLeft()));
            result.addAll(topLevelConjunction(((And) exp).getRight()));
        }else{
            result.add(exp);
        }
        return result;
    }

    private PLTLExp topLevelMerge(LinkedList<PLTLExp> list){
        PLTLExp result = list.get(0);
        if(list.size() > 1){
            for (int i = 1; i < list.size(); i++) {
                PLTLExp x = list.get(i);
                result = new And(result, x);
            }
        }
        return result;
    }

    private void addResult(LinkedList<TranslationOutput> target, PLTLExp exp, LinkedHashSet<String> val){
        for (TranslationOutput out: target) {
            if(out.getTo().equals(exp)) {
                out.addVal(val);
                return;
            }
        }
        target.add(new TranslationOutput(exp, val));
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
        if(exp instanceof Or){
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
        }if(exp instanceof Until){
            if(((Until) exp).getRight() instanceof Until right){
                if(((Until) exp).getLeft().equals(right.getLeft())){
                    return right;
                }
            }
        }


        return exp;
    }

    //Removes duplicate statements from conjunctions
    private PLTLExp andTrim(PLTLExp exp){
        if(!exp.getClass().equals(And.class)){
            return exp;
        }
        ArrayList<PLTLExp> trim = new ArrayList<>();
        ArrayList<PLTLExp> list = And.getAllAndProps((And) exp);
        for (int x = 0; x < list.size() ; x++) {
            PLTLExp cur = list.get(x);
            boolean match = false;
            for(int i = x+1; i < list.size(); i++){
                if (cur.equals(list.get(i))) {
                    match = true;
                    break;
                }
            }
            if(!match){
                trim.add(cur);
            }
        }
        if(trim.size() == 1)
            return trim.get(0);
        PLTLExp result = new And(trim.get(trim.size()-1), trim.get(trim.size()-2));
        if(trim.size() >= 3) {
            for (int x = trim.size() - 3; x >= 0; x--) {
                result = new And(trim.get(x), result);
            }
        }
        return result;
    }

    //Returns the (currently unoptimized) implicants of a formula
    LinkedHashSet<PLTLExp> primeImplicants(PLTLExp exp){
        LinkedHashSet<PLTLExp> result = new LinkedHashSet<>();

        if(exp instanceof Or){
            result.addAll(primeImplicants(((Or) exp).getLeft()));
            result.addAll(primeImplicants(((Or) exp).getRight()));
        }else{
            result.add(exp);
        }

        return result;
    }

    void pastLabeling(PLTLExp exp){
        if(!canBeWeakened.isEmpty()) {
            for (PLTLExp entry : canBeWeakened) {
                if (entry.equals(exp)) {
                    exp.pastLabel = entry.pastLabel;
                    return;
                }
            }
        }
        exp.pastLabel = pastLabels;
        pastLabels += 1;
        canBeWeakened.add(exp);
    }

    void setupLabels(PLTLExp exp){

        if(exp instanceof Unary){
            if(exp instanceof Yesterday || exp instanceof WYesterday){
                pastLabeling(exp);
            }else{
                exp.pastLabel = -1;
            }
            setupLabels(((Unary) exp).getTarget());

        }else if(exp instanceof Binary){
            if (exp instanceof Since || exp instanceof WSince || exp instanceof Before || exp instanceof WBefore) {
                pastLabeling(exp);
            } else if(exp instanceof Until || exp instanceof Mighty){
                if(exp.transitionLabel == -1){
                    exp.transitionLabel = transitionLabels;
                    transitionLabels += 1;
                }
            }else{
                exp.pastLabel = -1;
            }
            setupLabels(((Binary)exp).getLeft());
            setupLabels(((Binary)exp).getRight());
        }

    }

    public LinkedHashSet<LinkedHashSet<String>> getAllValuations(LinkedList<String> atoms, int index, LinkedHashSet<String> current){
        LinkedHashSet<LinkedHashSet<String>> result = new LinkedHashSet<>();
        if(index != atoms.size()){
            LinkedHashSet<String> add = new LinkedHashSet<>(current);
            add.add(atoms.get(index));
            result.addAll(getAllValuations(atoms, index + 1, add));
            LinkedHashSet<String> stay = new LinkedHashSet<>(current);
            result.addAll(getAllValuations(atoms, index + 1, stay));
        }
        result.add(current);
        return result;
    }

    public LinkedHashSet<LinkedHashSet<Integer>> getAllSubsets(int length, int index, LinkedHashSet<Integer> current){
        LinkedHashSet<LinkedHashSet<Integer>> result = new LinkedHashSet<>();
        if(index != length){
            LinkedHashSet<Integer> add = new LinkedHashSet<>(current);
            add.add(index);
            result.addAll(getAllSubsets(length, index + 1, add));
            LinkedHashSet<Integer> stay = new LinkedHashSet<>(current);
            result.addAll(getAllSubsets(length, index + 1, stay));
        }
        result.add(current);
        return result;
    }

    void setWC(PLTLExp exp){
        if(exp instanceof Unary){
            if(exp instanceof Yesterday || exp instanceof WYesterday){
                WCMap.put(exp.pastLabel, ((Unary)exp).getTarget());
            }
            setWC(((Unary) exp).getTarget());
        }else if(exp instanceof Binary){
                if (exp instanceof Since) {
                    WCMap.put(exp.pastLabel, ((Since)exp).getRight());
                } else if (exp instanceof WSince) {
                    WCMap.put(exp.pastLabel, (new Or(((WSince) exp).getLeft(),((WSince) exp).getRight())));
                } else if (exp instanceof Before) {
                    WCMap.put(exp.pastLabel, (new And(((Before) exp).getLeft(),((Before) exp).getRight())));
                } else if (exp instanceof WBefore) {
                    WCMap.put(exp.pastLabel, ((WBefore) exp).getRight());
                }
            setWC(((Binary)exp).getLeft());
            setWC(((Binary)exp).getRight());
        }
    }

    class LocalAfter implements PLTLExp.AltVisitor<PLTLExp, LinkedHashSet<String>>{

        @Override
        public PLTLExp visit(And exp, LinkedHashSet<String> args) {
            PLTLExp left = exp.getLeft().accept(new LocalAfter(), args);
            if(left instanceof False)
                return new False();
            PLTLExp right = exp.getRight().accept(new LocalAfter(), args);
            if(right instanceof False){
                return new False();
            }
            boolean leftTrue = left instanceof True;
            boolean rightTrue = right instanceof True;
            if(leftTrue && rightTrue)
                return new True();
            if(leftTrue)
                return right;
            if(rightTrue)
                return left;
            return new And(left, right);
        }

        @Override
        public PLTLExp visit(Or exp, LinkedHashSet<String> args) {
            PLTLExp left = exp.getLeft().accept(new LocalAfter(), args);
            if(left instanceof True)
                return new True();
            PLTLExp right = exp.getRight().accept(new LocalAfter(), args);
            if(right instanceof True){
                return new True();
            }
            boolean leftFalse = left instanceof False;
            boolean rightFalse = right instanceof False;
            if(leftFalse && rightFalse)
                return new False();
            if(leftFalse)
                return right;
            if(rightFalse)
                return right;
            return new Or(left, right);
        }

        @Override
        public PLTLExp visit(Term exp, LinkedHashSet<String> args) {
            if(args.contains(exp.m_term))
                return new True();
            return new False();
        }

        @Override
        public PLTLExp visit(NotTerm exp, LinkedHashSet<String> args) {
            if(args.contains(exp.m_term))
                return new False();
            return new True();
        }


        @Override
        public PLTLExp visit(True exp, LinkedHashSet<String> args) {
            return exp;
        }

        @Override
        public PLTLExp visit(False exp, LinkedHashSet<String> args) {
            return exp;
        }


        @Override
        public PLTLExp visit(Globally exp, LinkedHashSet<String> args) {
            return null;
        }

        @Override
        public PLTLExp visit(Historically exp, LinkedHashSet<String> args) {
            return null;
        } //Should not be reached


        @Override
        public PLTLExp visit(Until exp, LinkedHashSet<String> args) {
            PLTLExp right = exp.getRight().accept(new LocalAfter(), args);
            return new Or(right, new And(exp.getLeft().accept(new LocalAfter(), args), exp));
        }
        @Override
        public PLTLExp visit(WUntil exp, LinkedHashSet<String> args) {
            return new Or(exp.getRight().accept(new LocalAfter(), args),
                    new And(exp.getLeft().accept(new LocalAfter(), args), exp));
        }

        @Override
        public PLTLExp visit(Mighty exp, LinkedHashSet<String> args) {
            return new And(exp.getRight().accept(new LocalAfter(), args),
                    new Or(exp.getLeft().accept(new LocalAfter(), args), exp));
        }


        @Override
        public PLTLExp visit(Future exp, LinkedHashSet<String> args) {
            return null;
        }

        @Override
        public PLTLExp visit(Once exp, LinkedHashSet<String> args) {
            return null;
        }


        @Override
        public PLTLExp visit(Next exp, LinkedHashSet<String> args) {
            return exp;
        }

        @Override
        public PLTLExp visit(Yesterday exp, LinkedHashSet<String> args) {
            return new False();
        }

        @Override
        public PLTLExp visit(WYesterday exp, LinkedHashSet<String> args) {
            return new True();
        }


        @Override
        public PLTLExp visit(Release exp, LinkedHashSet<String> args) {
            return new And(exp.getRight().accept(new LocalAfter(), args),
                    new Or(exp.getLeft().accept(new LocalAfter(), args), exp));
        }


        @Override
        public PLTLExp visit(Since exp, LinkedHashSet<String> args) {
            return exp.getRight().accept(new LocalAfter(), args);
        }

        @Override
        public PLTLExp visit(WSince exp, LinkedHashSet<String> args) {
            if(exp.getLeft() instanceof True)
                return new True();
            return new Or(exp.getLeft(), exp.getRight()).accept(new LocalAfter(), args);
        }


        @Override
        public PLTLExp visit(Before exp, LinkedHashSet<String> args) {
            return new And(exp.getLeft(), exp.getRight()).accept(new LocalAfter(), args);
        }

        @Override
        public PLTLExp visit(WBefore exp, LinkedHashSet<String> args) {
            return exp.getRight().accept(new LocalAfter(), args);
        }


        @Override
        public PLTLExp visit(Not exp, LinkedHashSet<String> args) {
            return null;
        }
    }

    class PostUpdateHandler implements PLTLExp.AltVisitor<PLTLExp, LinkedHashSet<String>>{

        @Override
        public PLTLExp visit(And exp, LinkedHashSet<String> args) {
            PLTLExp left = exp.getLeft().accept(new PostUpdateHandler(), args);
            if(left instanceof False)
                return new False();
            PLTLExp right = exp.getRight().accept(new PostUpdateHandler(), args);
            if(right instanceof False){
                return new False();
            }
            boolean leftTrue = left instanceof True;
            boolean rightTrue = right instanceof True;
            if(leftTrue && rightTrue)
                return new True();
            if(leftTrue)
                return right;
            if(rightTrue)
                return left;
            return new And(left, right);
        }

        @Override
        public PLTLExp visit(Or exp, LinkedHashSet<String> args) {
            PLTLExp left = exp.getLeft().accept(new PostUpdateHandler(), args);
            if(left instanceof True)
                return new True();
            PLTLExp right = exp.getRight().accept(new PostUpdateHandler(), args);
            if(right instanceof True){
                return new True();
            }
            boolean leftFalse = left instanceof False;
            boolean rightFalse = right instanceof False;
            if(leftFalse && rightFalse)
                return new False();
            if(leftFalse)
                return right;
            if(rightFalse)
                return right;
            return new Or(left, right);
        }


        @Override
        public PLTLExp visit(Term exp, LinkedHashSet<String> args) {
            return exp;
        }

        @Override
        public PLTLExp visit(NotTerm exp, LinkedHashSet<String> args) {
            return exp;
        }


        @Override
        public PLTLExp visit(True exp, LinkedHashSet<String> args) {
            return exp;
        }

        @Override
        public PLTLExp visit(False exp, LinkedHashSet<String> args) {
            return exp;
        }


        @Override
        public PLTLExp visit(Globally exp, LinkedHashSet<String> args) {
            return null;
        }

        @Override
        public PLTLExp visit(Historically exp, LinkedHashSet<String> args) {
            return null;
        } //Should not be reached


        @Override
        public PLTLExp visit(Until exp, LinkedHashSet<String> args) {
            return postUpdate(exp, args);
        }
        @Override
        public PLTLExp visit(WUntil exp, LinkedHashSet<String> args) {
            return postUpdate(exp, args);
        }

        @Override
        public PLTLExp visit(Mighty exp, LinkedHashSet<String> args) {
            return postUpdate(exp, args);
        }


        @Override
        public PLTLExp visit(Future exp, LinkedHashSet<String> args) {
            return null;
        }

        @Override
        public PLTLExp visit(Once exp, LinkedHashSet<String> args) {
            return null;
        }


        @Override
        public PLTLExp visit(Next exp, LinkedHashSet<String> args) {
            return postUpdate(exp.getTarget(), args);
        }

        @Override
        public PLTLExp visit(Yesterday exp, LinkedHashSet<String> args) {
            return new False();
        }

        @Override
        public PLTLExp visit(WYesterday exp, LinkedHashSet<String> args) {
            return new True();
        }


        @Override
        public PLTLExp visit(Release exp, LinkedHashSet<String> args) {
            return postUpdate(exp, args);
        }


        @Override
        public PLTLExp visit(Since exp, LinkedHashSet<String> args) {
            return exp;
        }

        @Override
        public PLTLExp visit(WSince exp, LinkedHashSet<String> args) {
            return exp;
        }


        @Override
        public PLTLExp visit(Before exp, LinkedHashSet<String> args) {
            return exp;
        }

        @Override
        public PLTLExp visit(WBefore exp, LinkedHashSet<String> args) {
            return exp;
        }


        @Override
        public PLTLExp visit(Not exp, LinkedHashSet<String> args) {
            return null;
        }

        PLTLExp postUpdate(PLTLExp target, LinkedHashSet<String> args){
            PLTLExp result = target.accept(new Weakening());

            if(!opt) {
                for (Integer n : toWeaken) {
                    PLTLExp add = WCPostVal.get(n);
                    if(add instanceof False)
                        return new False();
                    add = add.accept(new PostUpdateHandler(), args);
                    result = new And(add, result);
                }
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
        public PLTLExp visit(Mighty exp) {
            Mighty newExp = new Mighty(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
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
            if(toWeaken.contains(exp.pastLabel))
                return new WYesterday(exp.getTarget().accept(new Weakening()));
            return new Yesterday(exp.getTarget().accept(new Weakening()));
        }

        @Override
        public PLTLExp visit(WYesterday exp) {
            if(toWeaken.contains(exp.pastLabel))
                return new WYesterday(exp.getTarget().accept(new Weakening()));
            return new Yesterday(exp.getTarget().accept(new Weakening()));
        }



        @Override
        public PLTLExp visit(Release exp) {
            return new Release(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
        }


        @Override
        public PLTLExp visit(Since exp) {
            if(toWeaken.contains(exp.pastLabel))
                return new WSince(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
            return new Since(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
        }

        @Override
        public PLTLExp visit(WSince exp) {
            if(toWeaken.contains(exp.pastLabel))
                return new WSince(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
            return new Since(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
        }



        @Override
        public PLTLExp visit(Before exp) {
            if(toWeaken.contains(exp.pastLabel))
                return new WBefore(exp.getLeft().accept(new Weakening()),exp.getRight().accept(new Weakening()));
            return new Before(exp.getLeft().accept(new Weakening()),exp.getRight().accept(new Weakening()));
        }



        @Override
        public PLTLExp visit(WBefore exp) {
            if(toWeaken.contains(exp.pastLabel))
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
        public PLTLExp visit(Mighty exp) {
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
