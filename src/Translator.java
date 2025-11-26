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
    HashMap<Integer, HashMap<String, HashMap<LinkedHashSet<Integer>, PLTLExp>>> WCPrelims;
    HashMap<PLTLExp, LinkedHashSet<PLTLExp>> ImplicantMap;
    HashMap<Integer, Boolean> currentlyWeak;

    HashMap<Integer, PLTLExp> prelimInputMap;
    HashMap<Integer, HashMap<String, PLTLExp>> prelimOutputMap;
    LinkedHashSet<LinkedHashSet<String>> allVals;
    LinkedHashSet<LinkedHashSet<Integer>> allWC;

    String key;

    //Current formulas to be weakened
    LinkedHashSet<Integer> toWeaken;

    LinkedHashSet<Integer> oldObligations;
    LinkedHashSet<Integer> newObligations;
    boolean opt;

    public Translator(){
        pastLabels = 0;
        transitionLabels = 0;
        prelimEntries = 0;
        prelimInputMap = new HashMap<>();
        prelimOutputMap = new HashMap<>();
        WCPrelims = new HashMap<>();
        allVals = new LinkedHashSet<>();
        allWC = new LinkedHashSet<>();
        ImplicantMap = new HashMap<>();
    }

    public void setup(NBAState base, LinkedList<String> atomList){
        canBeWeakened = new LinkedList<>();
        allVals = getAllValuations(atomList, 0, new LinkedHashSet<>());
        setupLabels(base.getExp());
        allWC = getAllSubsets(pastLabels, 0, new LinkedHashSet<>());
        setWC(base.getExp());
        currentlyWeak = new HashMap<>();
        currentWeak(base.getExp());
    }

    public int getTransitionLabels(){
        return transitionLabels;
    }

    public LinkedList<TranslationOutput> translationStep(NBAState base, LinkedList<String> atomList, boolean optimize){
        opt = optimize;
        oldObligations = obligationFinder(base.getExp());
        LinkedList<TranslationOutput> result = new LinkedList<>();
        currentWeak(base.getExp());
        LinkedHashSet<Integer> currentWeakSubs = new LinkedHashSet<>();
        for (Integer i: currentlyWeak.keySet()) {
            if(currentlyWeak.get(i))
                currentWeakSubs.add(i);
        }
        LinkedList<PLTLExp> topLevel = topLevelConjunction(base.getExp());


        for (LinkedHashSet<String> valuation : allVals) {
            newObligations = new LinkedHashSet<>();
             StringBuilder keyBuild = new StringBuilder();
                for (String string : valuation) {
                    keyBuild.append(string);
                }
                key = keyBuild.toString();
            LinkedList<PLTLExp> postPreLim = new LinkedList<>();
            boolean earlyEnd = false;
            for (PLTLExp topExp: topLevel) {
                if(prelimInputMap.isEmpty()){
                    prelimInputMap.put(prelimEntries, topExp);
                    prelimEntries += 1;
                }
                PLTLExp post = translationActual(topExp, valuation);
                if(post instanceof True || post instanceof False){
                    addResult(result, post, valuation);
                    earlyEnd = true;
                    break;
                }
                postPreLim.add(post);

            }
            if(earlyEnd)
                continue;

            for (LinkedHashSet<Integer> C : allWC) {
                toWeaken = C;
                boolean valid = true;
                PLTLExp wcActual = new True();
                if (optimize && !C.isEmpty()) {
                    for (Integer num : C) {
                        PLTLExp add = WCPrelims.get(num).get(key).get(currentWeakSubs);
                        if (add instanceof False) {
                            valid = false;
                            break;
                        }
                        add = add.accept(new PostUpdateHandler(), valuation);
                        wcActual = new And(wcActual, add);
                    }
                }
                if(!valid){
                    //addResult(result, new False(), valuation);
                    continue;
                }
                LinkedList<PLTLExp> list = new LinkedList<>();
                for (PLTLExp pre: postPreLim) {
                    list.add(pre.accept(new PostUpdateHandler(), valuation));
                }
                PLTLExp current = topLevelMerge(list);
                if (optimize) {
                    if(!(wcActual instanceof True))
                        current = new And(current, wcActual);
                }
                PLTLExp partTrim = trueFalseTrim(current);
                PLTLExp fullTrim = andTrim(partTrim);
                LinkedHashSet<PLTLExp> implicants;
                if(!ImplicantMap.containsKey(fullTrim)) {
                    PLTLExp rewritten = fullTrim.accept(new DNF());
                    implicants = primeImplicants(rewritten);
                    LinkedHashSet<PLTLExp> trimmed = new LinkedHashSet<>();
                    for (PLTLExp exp: implicants) {
                        PLTLExp trimOne = trueFalseTrim(exp);
                        trimmed.add(andTrim(trimOne));
                    }
                    ImplicantMap.put(fullTrim, trimmed);
                }
                implicants = ImplicantMap.get(fullTrim);
                for (PLTLExp exp : implicants) {
                    addResult(result, exp, valuation);
                }
            }
        }
        return result;
    }

    private PLTLExp translationActual(PLTLExp exp, LinkedHashSet<String> valuation){
        for (Integer entry: prelimInputMap.keySet()) {

            if(exp.equals(prelimInputMap.get(entry))){
                if(prelimOutputMap.containsKey(entry)) {
                    HashMap<String, PLTLExp> map = prelimOutputMap.get(entry);
                    if(map.containsKey(key)){
                        PLTLExp post = map.get(key);
                        if(!(post instanceof True) && !(post instanceof False))
                            post.obligation = exp.obligation;
                        else
                            post.obligation = -1;
                        return post;
                    }else{
                        PLTLExp post = exp.accept(new LocalAfter(), valuation);
                        if(!(post instanceof True) && !(post instanceof False))
                            post.obligation = exp.obligation;
                        else
                            post.obligation = -1;
                        map.put(key, post);
                        prelimOutputMap.put(entry, map);
                        return post;
                    }
                }
                else{
                    HashMap<String, PLTLExp> map = new HashMap<>();
                    PLTLExp post = exp.accept(new LocalAfter(), valuation);
                    if(!(post instanceof True) && !(post instanceof False))
                        post.obligation = exp.obligation;
                    else
                        post.obligation = -1;
                    map.put(key, post);
                    prelimOutputMap.put(entry, map);
                    return post;
                }
            }
        }
        prelimInputMap.put(prelimEntries, exp);
        PLTLExp post = exp.accept(new LocalAfter(), valuation);
        if(!(post instanceof True) && !(post instanceof False))
            post.obligation = exp.obligation;
        else
            post.obligation = -1;
        HashMap<String, PLTLExp> map = new HashMap<>();
        map.put(key, post);
        prelimOutputMap.put(prelimEntries, map);
        prelimEntries += 1;
        return post;
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
        LinkedHashSet<Integer> remainingObs = obligationFinder(exp);
        LinkedHashSet<Integer> clearedObs = new LinkedHashSet<>();
        if(!(exp instanceof False)) {
            for (Integer x : oldObligations) {
                if (!remainingObs.contains(x))
                    clearedObs.add(x);
            }
            for (Integer x : newObligations) {
                if (!remainingObs.contains(x))
                    clearedObs.add(x);
            }
        }
        for (TranslationOutput out: target) {
            if(out.getTo().equals(exp)) {
                out.addVal(val);
                out.addObs(clearedObs);
                return;
            }
        }
        TranslationOutput result = new TranslationOutput(exp, val);
        result.addObs(clearedObs);
        target.add(result);
    }

    public static LinkedHashSet<Integer> obligationFinder (PLTLExp exp){
        LinkedHashSet<Integer> result = new LinkedHashSet<>();
        if(exp.obligation != -1){
            result.add(exp.obligation);
        }
        if(exp instanceof Binary){
            result.addAll(obligationFinder(((Binary) exp).getLeft()));
            result.addAll(obligationFinder(((Binary) exp).getRight()));
        }
        if(exp instanceof Unary){
            result.addAll(obligationFinder(((Unary) exp).getTarget()));
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
                setupWC(exp.pastLabel,((Unary)exp).getTarget(),((Unary)exp).getTarget());
            }
            setWC(((Unary) exp).getTarget());
        }else if(exp instanceof Binary){
                if (exp instanceof Since) {
                    setupWC(exp.pastLabel, (new Or(((Since) exp).getLeft(),((Since) exp).getRight())), ((Since)exp).getRight());
                } else if (exp instanceof WSince) {
                    setupWC(exp.pastLabel, (new Or(((WSince) exp).getLeft(),((WSince) exp).getRight())), ((WSince)exp).getRight());
                } else if (exp instanceof Before) {
                    setupWC(exp.pastLabel, ((Before) exp).getRight(), (new And(((Before) exp).getLeft(),((Before) exp).getRight())));
                } else if (exp instanceof WBefore) {
                    setupWC(exp.pastLabel, ((WBefore) exp).getRight(), (new And(((WBefore) exp).getLeft(),((WBefore) exp).getRight())));
                }
            setWC(((Binary)exp).getLeft());
            setWC(((Binary)exp).getRight());
        }
    }

    void currentWeak(PLTLExp exp){
        if(exp instanceof Unary){
            if(exp instanceof Yesterday){
                currentlyWeak.put(exp.pastLabel, false);
            }else if(exp instanceof  WYesterday)
                currentlyWeak.put(exp.pastLabel, true);
            currentWeak(((Unary) exp).getTarget());
        }else if(exp instanceof Binary){
            if (exp instanceof Since) {
                currentlyWeak.put(exp.pastLabel, false);
            } else if (exp instanceof WSince) {
                currentlyWeak.put(exp.pastLabel, true);
            } else if (exp instanceof Before) {
                currentlyWeak.put(exp.pastLabel, false);
            } else if (exp instanceof WBefore) {
                currentlyWeak.put(exp.pastLabel, true);
            }
            currentWeak(((Binary)exp).getLeft());
            currentWeak(((Binary)exp).getRight());
        }
    }

    void setupWC(int label, PLTLExp weak, PLTLExp strong){
        if(!WCPrelims.containsKey(label)){
            HashMap<String, HashMap<LinkedHashSet<Integer>, PLTLExp>> map = new HashMap<>();
            for (LinkedHashSet<String> valuation: allVals) {
                StringBuilder keyBuild = new StringBuilder();
                for (String string : valuation) {
                    keyBuild.append(string);
                }
                key = keyBuild.toString();
                HashMap<LinkedHashSet<Integer>, PLTLExp> inner = new HashMap<>();
                for(LinkedHashSet<Integer> C : allWC){
                    toWeaken = C;
                    PLTLExp postupdate;
                    if(C.contains(label)){
                        postupdate = weak.accept(new PostUpdateHandler(), valuation);
                    }else{
                        postupdate = strong.accept(new PostUpdateHandler(), valuation);
                    }
                    PLTLExp eval = translationActual(postupdate, valuation);
                    inner.put(C, eval);
                }
                map.put(key,inner);
            }
            WCPrelims.put(label, map);
        }
    }

    class LocalAfter implements PLTLExp.AltVisitor<PLTLExp, LinkedHashSet<String>>{

        @Override
        public PLTLExp visit(And exp, LinkedHashSet<String> args) {
            PLTLExp left = translationActual(exp.getLeft(), args);
            if(left instanceof False)
                return new False();
            PLTLExp right = translationActual(exp.getRight(), args);
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
            PLTLExp left = translationActual(exp.getLeft(), args);
            if(left instanceof True)
                return new True();
            PLTLExp right = translationActual(exp.getRight(), args);
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
            newObligations.add(exp.transitionLabel);
            PLTLExp left = translationActual(exp.getLeft(), args);
            PLTLExp right = translationActual(exp.getRight(), args);
            PLTLExp rCopy = right.accept(new DeepCopy());
            rCopy.obligation = exp.transitionLabel;
            return new Or(rCopy, new And(left, exp));
        }
        @Override
        public PLTLExp visit(WUntil exp, LinkedHashSet<String> args) {
            PLTLExp left = translationActual(exp.getLeft(), args);
            PLTLExp right = translationActual(exp.getRight(), args);
            PLTLExp rCopy = right.accept(new DeepCopy());
            return new Or(rCopy,
                    new And(left, exp));
        }

        @Override
        public PLTLExp visit(Mighty exp, LinkedHashSet<String> args) {
            newObligations.add(exp.transitionLabel);
            PLTLExp left = translationActual(exp.getLeft(), args);
            PLTLExp lCopy = left.accept(new DeepCopy());
            lCopy.obligation = exp.transitionLabel;
            PLTLExp right = translationActual(exp.getRight(), args);
            return new And(right,
                    new Or(lCopy, exp));
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
            PLTLExp left = translationActual(exp.getLeft(), args);
            PLTLExp lCopy = left.accept(new DeepCopy());
            PLTLExp right = translationActual(exp.getRight(), args);
            return new And(right,
                    new Or(lCopy, exp));
        }


        @Override
        public PLTLExp visit(Since exp, LinkedHashSet<String> args) {
            return translationActual(exp.getRight(), args);
        }

        @Override
        public PLTLExp visit(WSince exp, LinkedHashSet<String> args) {
            if(exp.getLeft() instanceof True)
                return new True();
            return translationActual(new Or(exp.getLeft(), exp.getRight()), args);
        }


        @Override
        public PLTLExp visit(Before exp, LinkedHashSet<String> args) {
            return translationActual(new And(exp.getLeft(), exp.getRight()), args);
        }

        @Override
        public PLTLExp visit(WBefore exp, LinkedHashSet<String> args) {
            return translationActual(exp.getRight(), args);
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
                    PLTLExp add = WCPrelims.get(n).get(key).get(toWeaken);
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
            PLTLExp ret;
            if(toWeaken.contains(exp.pastLabel)) {
                ret = new WYesterday(exp.getTarget().accept(new Weakening()));
                ret.pastLabel = exp.pastLabel;
                return ret;
            }
            ret = new Yesterday(exp.getTarget().accept(new Weakening()));
            ret.pastLabel = exp.pastLabel;
            return ret;
        }

        @Override
        public PLTLExp visit(WYesterday exp) {
            PLTLExp ret;
            if(toWeaken.contains(exp.pastLabel)) {
                ret = new WYesterday(exp.getTarget().accept(new Weakening()));
                ret.pastLabel = exp.pastLabel;
                return ret;
            }
            ret = new Yesterday(exp.getTarget().accept(new Weakening()));
            ret.pastLabel = exp.pastLabel;
            return ret;
        }



        @Override
        public PLTLExp visit(Release exp) {
            return new Release(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
        }


        @Override
        public PLTLExp visit(Since exp) {
            PLTLExp ret;
            if(toWeaken.contains(exp.pastLabel)) {
                ret = new WSince(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
                ret.pastLabel = exp.pastLabel;
                return ret;
            }
            ret = new Since(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
            ret.pastLabel = exp.pastLabel;
            return ret;
        }

        @Override
        public PLTLExp visit(WSince exp) {
            PLTLExp ret;
            if(toWeaken.contains(exp.pastLabel)) {
                ret = new WSince(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
                ret.pastLabel = exp.pastLabel;
                return ret;
            }
            ret = new Since(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
            ret.pastLabel = exp.pastLabel;
            return ret;
        }



        @Override
        public PLTLExp visit(Before exp) {
            PLTLExp ret;
            if(toWeaken.contains(exp.pastLabel)) {
                ret = new WBefore(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
                ret.pastLabel = exp.pastLabel;
                return ret;
            }
            ret = new Before(exp.getLeft().accept(new Weakening()),exp.getRight().accept(new Weakening()));
            ret.pastLabel = exp.pastLabel;
            return ret;
        }



        @Override
        public PLTLExp visit(WBefore exp) {
            PLTLExp ret;
            if(toWeaken.contains(exp.pastLabel)) {
                ret = new WBefore(exp.getLeft().accept(new Weakening()), exp.getRight().accept(new Weakening()));
                ret.pastLabel = exp.pastLabel;
                return ret;
            }
            ret = new Before(exp.getLeft().accept(new Weakening()),exp.getRight().accept(new Weakening()));
            ret.pastLabel = exp.pastLabel;
            return ret;
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

    class DeepCopy implements PLTLExp.Visitor<PLTLExp>{
        public void generalCopy(PLTLExp copy, PLTLExp of){
            copy.obligation = of.obligation;
            copy.pastLabel = of.pastLabel;
            copy.transitionLabel = of.transitionLabel;
        }

        @Override
        public PLTLExp visit(And exp) {
            PLTLExp left = exp.getLeft().accept(new DeepCopy());
            PLTLExp right = exp.getRight().accept(new DeepCopy());
            And copy = new And(left,right);
            generalCopy(copy, exp);
            return copy;
        }

        @Override
        public PLTLExp visit(Or exp) {
            PLTLExp left = exp.getLeft().accept(new DeepCopy());
            PLTLExp right = exp.getRight().accept(new DeepCopy());
            Or copy = new Or(left,right);
            generalCopy(copy, exp);
            return copy;
        }

        @Override
        public PLTLExp visit(Term exp) {
            Term copy = new Term(exp.m_term);
            generalCopy(copy, exp);
            return copy;
        }

        @Override
        public PLTLExp visit(NotTerm exp) {
            NotTerm copy = new NotTerm(exp.m_term);
            generalCopy(copy, exp);
            return copy;
        }

        @Override
        public PLTLExp visit(True exp) {
            True copy = new True();
            generalCopy(copy, exp);
            return copy;
        }

        @Override
        public PLTLExp visit(False exp) {
            False copy = new False();
            generalCopy(copy, exp);
            return copy;
        }

        @Override
        public PLTLExp visit(Globally exp) {
            Globally copy = new Globally(exp.getTarget().accept(new DeepCopy()));
            generalCopy(copy, exp);
            return copy;
        }

        @Override
        public PLTLExp visit(Historically exp) {
            Historically copy = new Historically(exp.getTarget().accept(new DeepCopy()));
            generalCopy(copy, exp);
            return copy;
        }

        @Override
        public PLTLExp visit(Until exp) {
            PLTLExp left = exp.getLeft().accept(new DeepCopy());
            PLTLExp right = exp.getRight().accept(new DeepCopy());
            Until copy = new Until(left,right);
            generalCopy(copy, exp);
            return copy;
        }

        @Override
        public PLTLExp visit(WUntil exp) {
            PLTLExp left = exp.getLeft().accept(new DeepCopy());
            PLTLExp right = exp.getRight().accept(new DeepCopy());
            WUntil copy = new WUntil(left,right);
            generalCopy(copy, exp);
            return copy;
        }

        @Override
        public PLTLExp visit(Mighty exp) {
            PLTLExp left = exp.getLeft().accept(new DeepCopy());
            PLTLExp right = exp.getRight().accept(new DeepCopy());
            Mighty copy = new Mighty(left,right);
            generalCopy(copy, exp);
            return copy;
        }

        @Override
        public PLTLExp visit(Future exp) {
            Future copy = new Future(exp.getTarget().accept(new DeepCopy()));
            generalCopy(copy, exp);
            return copy;
        }

        @Override
        public PLTLExp visit(Once exp) {
            Once copy = new Once(exp.getTarget().accept(new DeepCopy()));
            generalCopy(copy, exp);
            return copy;
        }

        @Override
        public PLTLExp visit(Next exp) {
            Next copy = new Next(exp.getTarget().accept(new DeepCopy()));
            generalCopy(copy, exp);
            return copy;
        }

        @Override
        public PLTLExp visit(Yesterday exp) {
            Yesterday copy = new Yesterday(exp.getTarget().accept(new DeepCopy()));
            generalCopy(copy, exp);
            return copy;
        }

        @Override
        public PLTLExp visit(WYesterday exp) {
            WYesterday copy = new WYesterday(exp.getTarget().accept(new DeepCopy()));
            generalCopy(copy, exp);
            return copy;
        }

        @Override
        public PLTLExp visit(Release exp) {
            PLTLExp left = exp.getLeft().accept(new DeepCopy());
            PLTLExp right = exp.getRight().accept(new DeepCopy());
            Release copy = new Release(left,right);
            generalCopy(copy, exp);
            return copy;
        }

        @Override
        public PLTLExp visit(Since exp) {
            PLTLExp left = exp.getLeft().accept(new DeepCopy());
            PLTLExp right = exp.getRight().accept(new DeepCopy());
            Since copy = new Since(left,right);
            generalCopy(copy, exp);
            return copy;
        }

        @Override
        public PLTLExp visit(WSince exp) {
            PLTLExp left = exp.getLeft().accept(new DeepCopy());
            PLTLExp right = exp.getRight().accept(new DeepCopy());
            WSince copy = new WSince(left,right);
            generalCopy(copy, exp);
            return copy;
        }

        @Override
        public PLTLExp visit(Before exp) {
            PLTLExp left = exp.getLeft().accept(new DeepCopy());
            PLTLExp right = exp.getRight().accept(new DeepCopy());
            Before copy = new Before(left,right);
            generalCopy(copy, exp);
            return copy;
        }

        @Override
        public PLTLExp visit(WBefore exp) {
            PLTLExp left = exp.getLeft().accept(new DeepCopy());
            PLTLExp right = exp.getRight().accept(new DeepCopy());
            WBefore copy = new WBefore(left,right);
            generalCopy(copy, exp);
            return copy;
        }

        @Override
        public PLTLExp visit(Not exp) {
            Not copy = new Not(exp.getTarget().accept(new DeepCopy()));
            generalCopy(copy, exp);
            return copy;
        }
    }



}
