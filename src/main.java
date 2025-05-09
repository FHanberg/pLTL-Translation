import PLTL.*;
import PLTL.Future.*;
import PLTL.Past.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class main {

    public static void main(String[] args) {
        //Initialization
        int stateLabel = 0;

        //The PLTL statement to be translated
        PLTLExp test = new Until(new Term("p"), new Term("q"));

        System.out.println("Provided formula:");
        //Initial conversion to NNF + console readout
        test.accept(new readFormula());
        System.out.println("");
        PLTLExp newTest = test.accept(new NNFConverter());
        System.out.println("Post-NNF formula:");
        newTest.accept(new readFormula());
        System.out.println("");
        System.out.println("");

        LinkedList<String> atoms = atomList(newTest);

        //Primary state output
        LinkedList<NBAState> mainSet = new LinkedList<>();
        //Primary transition output
        LinkedList<NBATransition> transitionSet = new LinkedList<>();
        //marks current states to be translated
        HashSet<NBAState> currentSet = new HashSet<>();

        //Create initial state
        NBAState firstState = new NBAState(newTest, stateLabel);

        stateLabel += 1;
        mainSet.add(firstState);
        currentSet.add(firstState);

        //Translator needs to be non-static or return the tuple (states + next U/M label)
        Translator t = new Translator();
        //Main loop of full translation
        while(!currentSet.isEmpty()){
            //Storage set used to contain the states which should be checked in the next step.
            HashSet<NBAState> nextSet = new HashSet<>();
            //For each "fresh" state
            for (NBAState node: currentSet) {
                int curLabel = node.m_label;
                //Perform a step in the main NBA-to-Büchi translation
                HashSet<NBAState> prospective = t.translationStep(node, atoms);
                //For each primary implicant:
                for (NBAState next: prospective) {
                    //Determine if the main set already has an equal state, and return its label
                    int equalLabel = getLabelOfEqual(next, mainSet);
                    //If the state is wholly new, label and add it to both the main set and
                    // the list of upcoming states to check
                    if(equalLabel == -1){
                        next.setLabel(stateLabel);
                        nextSet.add(next);
                        mainSet.add(next);

                        //Labels are added if their corresponding U/M are NOT present
                        HashSet<Integer> antiLabels = next.m_exp.accept(new transitionLabel());
                        HashSet<Integer> transLabels = new HashSet<>();
                        if(!(next.m_exp instanceof False)) {
                            for (int i = 0; i < t.getTransitionLabels(); i++) {
                                if (!antiLabels.contains(i)) {
                                    transLabels.add(i);
                                }
                            }
                        }
                        transitionSet.add(new NBATransition(curLabel, stateLabel, transLabels, next.m_valuation));
                        stateLabel += 1;

                        //If the state has been created before, add a new transition if necessary
                    }else if(!transitionRepeat(new NBATransition(curLabel, equalLabel, new HashSet<>()), transitionSet, next.m_valuation)){
                            //Labels are added if their corresponding U/M are NOT present
                            HashSet<Integer> antiLabels = next.m_exp.accept(new transitionLabel());
                            HashSet<Integer> transLabels = new HashSet<>();
                            if(!(next.m_exp instanceof False)) {
                                for (int i = 0; i < t.getTransitionLabels(); i++) {
                                    if (!antiLabels.contains(i)) {
                                        transLabels.add(i);
                                    }
                                }
                            }
                            transitionSet.add(new NBATransition(curLabel, equalLabel, transLabels, next.m_valuation));

                    }
                }
            }
            //replace the (now old) set of fresh states with the next set
            currentSet = nextSet;
        }
        transitionSet.sort(NBATransition.comp);


        //Write information about the set to the console
        readout(mainSet, transitionSet, atoms, t.getTransitionLabels());
    }


    /*
    Print information about a completed Büchi-automata
    */
    static public void readout(LinkedList<NBAState> states, LinkedList<NBATransition> trans, LinkedList<String> atoms, int tLabels){
        System.out.println("HOA: v1");
        System.out.println("States: " + states.size());
        System.out.println("Start: 0");
        System.out.println("acc-name: Buchi");
        System.out.print("Acceptance: " + tLabels);
        for(int i = 0 ; i < tLabels ; i++){
            if(i > 0)
                System.out.print(" &");
            System.out.print(" Inf(" + i + ")");
        }
        System.out.println();
        System.out.print("AP: "+ atoms.size());
        for(String atom : atoms){
            System.out.print(" \"" + atom + "\"");
        }
        System.out.println();
        System.out.println("--BODY--");
        for(NBAState state : states){
            System.out.println("State: " + state.getLabel());
            transitionReadout(state.m_label, trans, atoms);
        }
        System.out.println("--END--");
        /*System.out.println("");
        System.out.println("total number of transitions: " + trans.size());
        for(NBATransition t : trans){
            StringBuilder labels = new StringBuilder("");
            labels.append(", valuations:");
            for (HashSet<String> set: t.m_valuations) {
                labels.append(" [");
                if(set.isEmpty()){
                    labels.append(" ");
                }
                else {
                    for (String s : set) {
                        labels.append(s).append(" ");
                    }
                }
                labels.append("] ");
            }
            if(!t.m_labels.isEmpty()) {
                labels.append(", labels:");
                for (Integer i : t.m_labels) {
                    labels.append(" ").append(i);
                }
            }
            System.out.println(t.m_from + " -> " + t.m_to + labels);*/
        //}
    }

    static private void transitionReadout(int state, LinkedList<NBATransition> trans, LinkedList<String> atoms){
        LinkedList<NBATransition> relevant = getStateTransitions(state, trans);
        HashMap<HashSet<String>, LinkedList<Integer>> transitionMap = new HashMap<>();
        HashMap<HashSet<String>, LinkedList<Integer>> labelMap = new HashMap<>();
        for (NBATransition t: relevant) {
            HashSet<HashSet<String>> actual = transitionTrim(t.m_valuations, atoms);
            for(HashSet<String> valuation : actual){
                if(!transitionMap.containsKey(valuation)){
                    LinkedList<Integer> tr = new LinkedList<>();
                    tr.add(t.m_to);
                    transitionMap.put(valuation, tr);
                    LinkedList<Integer> lb = new LinkedList<>(t.m_labels);
                    labelMap.put(valuation, lb);
                }else{
                    LinkedList<Integer> vList = transitionMap.get(valuation);
                    LinkedList<Integer> lList = labelMap.get(valuation);
                    if(!vList.contains(t.m_to))
                        vList.add(t.m_to);
                    for (Integer i: t.m_labels) {
                        if(!lList.contains(i))
                            lList.add(i);
                    }
                }
            }
        }

        //Actually do the readout
        for (HashSet<String> set: transitionMap.keySet()) {
            System.out.print("  [");
            if(set.contains("t")){
                System.out.print("t]");
            }else{
                for (int i = 0; i < atoms.size(); i++) {
                    if(i != 0){
                        System.out.print("&");
                    }
                    if(set.contains(String.valueOf(i))){
                        System.out.print(i);
                    }else{
                        System.out.print("!" + i);
                    }
                }
                System.out.print("]");

            }
            for (Integer i: transitionMap.get(set)) {
                System.out.print(" " + i);
            }
            LinkedList<Integer> labelList = labelMap.get(set);
            if(!labelList.isEmpty()){
                boolean first = true;
                System.out.print(" {");
                for (Integer i: labelList) {
                    if(!first)
                        System.out.print(" ");
                    System.out.print(i);
                    first = false;
                }
                System.out.print("}");
            }
            System.out.println();
        }
    }

    static private LinkedList<NBATransition> getStateTransitions(int state, LinkedList<NBATransition> trans){
        LinkedList<NBATransition> result = new LinkedList<>();
        for (NBATransition t: trans) {
            if(t.m_from == state)
                result.add(t);
        }
        return result;
    }

    static private HashSet<HashSet<String>> transitionTrim(HashSet<HashSet<String>> target, LinkedList<String> atoms){
        HashSet<HashSet<String>> result = new HashSet<>();
        //Convert to HOA-format for valuations (0,1,2...)
        HashSet<HashSet<String>> converted = valuationConversion(target, atoms);
        // All valuations are valid, so replace with "true"
        if(converted.size() == Math.pow(2, atoms.size())){
            HashSet<String> a = new HashSet<>();
            a.add("t");
            result.add(a);
        }else if(converted.size() == 1){
            //There's only one valuation, so return it
            return converted;
        }else{
            for (HashSet<String> set: converted) {
                if(set.isEmpty()){
                    result = new HashSet<>();
                    result.add(new HashSet<>());
                    break;
                }
                result.add(set);
            }
        }
        return result;
    }

    private static HashSet<HashSet<String>> valuationConversion(HashSet<HashSet<String>> target, LinkedList<String> atoms){
        HashSet<HashSet<String>> result = new HashSet<>();
        for (HashSet<String> set: target) {
            HashSet<String> replacement = new HashSet<>();
            for (String atom: atoms) {
                if(set.contains(atom))
                    replacement.add(String.valueOf(atoms.indexOf(atom)));
            }
            result.add(replacement);
        }
        return result;
    }

    /*
    Return all labels to not be applied to a transition.
     */
    public static class transitionLabel implements PLTLExp.Visitor<HashSet<Integer>>{


        @Override
        public HashSet<Integer> visit(And exp) {
            HashSet<Integer> left = exp.getLeft().accept(new transitionLabel());
            HashSet<Integer> right = exp.getRight().accept(new transitionLabel());
            left.addAll(right);
            return left;
        }

        @Override
        public HashSet<Integer> visit(Or exp) {
            HashSet<Integer> left = exp.getLeft().accept(new transitionLabel());
            HashSet<Integer> right = exp.getRight().accept(new transitionLabel());
            left.addAll(right);
            return left;
        }

        @Override
        public HashSet<Integer> visit(Term exp) {
            return new HashSet<>();
        }

        @Override
        public HashSet<Integer> visit(NotTerm exp) {
            return new HashSet<>();
        }

        @Override
        public HashSet<Integer> visit(True exp) {
            return new HashSet<>();
        }

        @Override
        public HashSet<Integer> visit(False exp) {
            return new HashSet<>();
        }

        @Override
        public HashSet<Integer> visit(Globally exp) {
            return exp.getTarget().accept(new transitionLabel());
        }

        @Override
        public HashSet<Integer> visit(Historically exp) {
            return exp.getTarget().accept(new transitionLabel());
        }

        @Override
        public HashSet<Integer> visit(Until exp) {
            HashSet<Integer> left = exp.getLeft().accept(new transitionLabel());
            HashSet<Integer> right = exp.getRight().accept(new transitionLabel());
            left.addAll(right);
            left.add(exp.transitionLabel);
            return left;
        }

        @Override
        public HashSet<Integer> visit(WUntil exp) {
            HashSet<Integer> left = exp.getLeft().accept(new transitionLabel());
            HashSet<Integer> right = exp.getRight().accept(new transitionLabel());
            left.addAll(right);
            return left;
        }

        @Override
        public HashSet<Integer> visit(M exp) {
            HashSet<Integer> left = exp.getLeft().accept(new transitionLabel());
            HashSet<Integer> right = exp.getRight().accept(new transitionLabel());
            left.addAll(right);
            left.add(exp.transitionLabel);
            return left;
        }

        @Override
        public HashSet<Integer> visit(Future exp) {
            return exp.getTarget().accept(new transitionLabel());
        }

        @Override
        public HashSet<Integer> visit(Once exp) {
            return exp.getTarget().accept(new transitionLabel());
        }

        @Override
        public HashSet<Integer> visit(Next exp) {
            return exp.getTarget().accept(new transitionLabel());
        }

        @Override
        public HashSet<Integer> visit(Yesterday exp) {
            return exp.getTarget().accept(new transitionLabel());
        }

        @Override
        public HashSet<Integer> visit(WYesterday exp) {
            return exp.getTarget().accept(new transitionLabel());
        }

        @Override
        public HashSet<Integer> visit(Release exp) {
            HashSet<Integer> left = exp.getLeft().accept(new transitionLabel());
            HashSet<Integer> right = exp.getRight().accept(new transitionLabel());
            left.addAll(right);
            return left;
        }

        @Override
        public HashSet<Integer> visit(Since exp) {
            HashSet<Integer> left = exp.getLeft().accept(new transitionLabel());
            HashSet<Integer> right = exp.getRight().accept(new transitionLabel());
            left.addAll(right);
            return left;
        }

        @Override
        public HashSet<Integer> visit(WSince exp) {
            HashSet<Integer> left = exp.getLeft().accept(new transitionLabel());
            HashSet<Integer> right = exp.getRight().accept(new transitionLabel());
            left.addAll(right);
            return left;
        }

        @Override
        public HashSet<Integer> visit(Before exp) {
            HashSet<Integer> left = exp.getLeft().accept(new transitionLabel());
            HashSet<Integer> right = exp.getRight().accept(new transitionLabel());
            left.addAll(right);
            return left;
        }

        @Override
        public HashSet<Integer> visit(WBefore exp) {
            HashSet<Integer> left = exp.getLeft().accept(new transitionLabel());
            HashSet<Integer> right = exp.getRight().accept(new transitionLabel());
            left.addAll(right);
            return left;
        }

        @Override
        public HashSet<Integer> visit(Not exp) {
            return null;
        }
    }

    /*
    Check if a transition already exists in a given set.
     */
    static public boolean transitionRepeat(NBATransition target, LinkedList<NBATransition> set, HashSet<String> valuation){
        for(NBATransition t : set){
            if(t.equals(target)) {
                t.addValuation(valuation);
                return true;
            }
        }
        return false;
    }

    static public LinkedList<String> atomList(PLTLExp exp){
        LinkedList<String> result = new LinkedList<>();
        if(exp instanceof Binary){
            result.addAll(atomList(((Binary) exp).getLeft()));
            result.addAll(atomList(((Binary) exp).getRight()));
        }
        else if(exp instanceof Unary){
            result.addAll(atomList(((Unary) exp).getTarget()));
        }else if(exp instanceof Term){
            result.add(((Term) exp).m_term);
        }else if(exp instanceof NotTerm){
            result.add(((NotTerm) exp).m_term);
        }

        return result;
    }

    /*
    Checks if a state already exists in a set, and returns its label if so.
    otherwise returns -1.
     */
    static public int getLabelOfEqual(NBAState target, LinkedList<NBAState> set){
        for(NBAState node : set){
            if(target.equals(node))
                return node.getLabel();
        }
        return -1;
    }

    /*
    A visitor-implementation which writes out a PLTL formula to the console
     */
    public static class readFormula implements PLTLExp.Visitor<PLTLExp>{

        @Override
        public PLTLExp visit(And exp) {
            System.out.print("(");
            exp.getLeft().accept(new readFormula());
            System.out.print(" and ");
            exp.getRight().accept(new readFormula());
            System.out.print(")");
            return exp;
        }

        @Override
        public PLTLExp visit(Or exp) {
            System.out.print("(");
            exp.getLeft().accept(new readFormula());
            System.out.print(" or ");
            exp.getRight().accept(new readFormula());
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
            exp.getTarget().accept(new readFormula());
            return exp;
        }

        @Override
        public PLTLExp visit(WUntil exp) {
            System.out.print("(");
            exp.getLeft().accept(new readFormula());
            System.out.print(" WUntil ");
            exp.getRight().accept(new readFormula());
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
            exp.getTarget().accept(new readFormula());
            return exp;
        }

        @Override
        public PLTLExp visit(Globally exp) {
            System.out.print("globally ");
            exp.getTarget().accept(new readFormula());
            return exp;
        }

        @Override
        public PLTLExp visit(M exp) {
            System.out.print("(");
            exp.getLeft().accept(new readFormula());
            System.out.print(" M ");
            exp.getRight().accept(new readFormula());
            System.out.print(")");
            return exp;
        }

        @Override
        public PLTLExp visit(Next exp) {
            System.out.print("next ");
            exp.getTarget().accept(new readFormula());
            return exp;
        }

        @Override
        public PLTLExp visit(Release exp) {
            System.out.print("(");
            exp.getLeft().accept(new readFormula());
            System.out.print(" Releases ");
            exp.getRight().accept(new readFormula());
            System.out.print(")");
            return exp;
        }

        @Override
        public PLTLExp visit(Until exp) {
            System.out.print("(");
            exp.getLeft().accept(new readFormula());
            System.out.print(" Until ");
            exp.getRight().accept(new readFormula());
            System.out.print(")");
            return exp;
        }

        @Override
        public PLTLExp visit(Before exp) {
            System.out.print("(");
            exp.getLeft().accept(new readFormula());
            System.out.print(" Before ");
            exp.getRight().accept(new readFormula());
            System.out.print(")");
            return exp;
        }

        @Override
        public PLTLExp visit(Once exp) {
            System.out.print("once ");
            exp.getTarget().accept(new readFormula());
            return exp;
        }

        @Override
        public PLTLExp visit(Since exp) {
            System.out.print("(");
            exp.getLeft().accept(new readFormula());
            System.out.print(" Since ");
            exp.getRight().accept(new readFormula());
            System.out.print(")");
            return exp;
        }

        @Override
        public PLTLExp visit(WBefore exp) {
            System.out.print("(");
            exp.getLeft().accept(new readFormula());
            System.out.print(" WBefore ");
            exp.getRight().accept(new readFormula());
            System.out.print(")");
            return exp;
        }

        @Override
        public PLTLExp visit(WSince exp) {
            System.out.print("(");
            exp.getLeft().accept(new readFormula());
            System.out.print(" WSince ");
            exp.getRight().accept(new readFormula());
            System.out.print(")");
            return exp;
        }

        @Override
        public PLTLExp visit(WYesterday exp) {
            System.out.print("Wyesterday ");
            exp.getTarget().accept(new readFormula());
            return exp;
        }

        @Override
        public PLTLExp visit(Yesterday exp) {
            System.out.print("yesterday ");
            exp.getTarget().accept(new readFormula());
            return exp;
        }

        @Override
        public PLTLExp visit(Not exp) {
            System.out.print("not ");
            exp.getTarget().accept(new readFormula());
            return exp;
        }
    }

}
