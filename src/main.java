import PLTL.*;
import PLTL.Future.*;
import PLTL.Past.*;

import java.util.HashSet;

public class main {

    public static void main(String[] args) {
        //Initialization
        int stateLabel = 0;

        //The PLTL statement to be translated
        PLTLExp test = new Next(new Until(new Not(new Term("0")), new Term("1")));

        System.out.println("Provided formula:");
        //Initial conversion to NNF + console readout
        test.accept(new readFormula());
        System.out.println("");
        PLTLExp newTest = test.accept(new NNFConverter());
        System.out.println("Post-NNF formula:");
        newTest.accept(new readFormula());
        System.out.println("");
        System.out.println("");

        //Primary state output
        HashSet<NBAState> mainSet = new HashSet<>();
        //Primary transition output
        HashSet<NBATransition> transitionSet = new HashSet<>();
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
                HashSet<NBAState> prospective = t.translationStep(node);
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
                        HashSet<Integer> transLabels = next.m_exp.accept(new transitionLabel());
                        transitionSet.add(new NBATransition(curLabel, stateLabel, transLabels));
                        stateLabel += 1;
                        //If the state has been created before, add a new transition if necessary
                    }else if(!transitionRepeat(new NBATransition(curLabel, equalLabel), transitionSet)){
                        if(curLabel == equalLabel){
                            HashSet<Integer> transLabels = next.m_exp.accept(new transitionLabel());
                            transitionSet.add(new NBATransition(curLabel, equalLabel, transLabels));
                        }
                    }
                }
            }
            //replace the (now old) set of fresh states with the next set
            currentSet = nextSet;
        }
        //Write information about the set to the console
        readout(mainSet, transitionSet);
    }


    /*
    Print information about a completed Büchi-automata
    */
    static public void readout(HashSet<NBAState> states, HashSet<NBATransition> trans){
        System.out.println("total number of states: " + states.size());
        for(NBAState state : states){
            System.out.print("State " + state.getLabel() + ": ");
            state.m_exp.accept(new readFormula());
            System.out.println("");
        }
        System.out.println("");
        System.out.println("total number of transitions: " + trans.size());
        for(NBATransition t : trans){
            StringBuilder labels = new StringBuilder("");
            if(!t.m_labels.isEmpty()) {
                labels.append(", labels:");
                for (Integer i : t.m_labels) {
                    labels.append(" ").append(i);
                }
            }
            System.out.println(t.m_from + " -> " + t.m_to + labels.toString());
        }
    }

    /*
    Return all labels to be applied to a transition.
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
            left.add(exp.label);
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
            left.add(exp.label);
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
    static public boolean transitionRepeat(NBATransition target, HashSet<NBATransition> set){
        for(NBATransition t : set){
            if(t.equals(target))
                return true;
        }
        return false;
    }

    /*
    Checks if a state already exists in a set, and returns its label if so.
    otherwise returns -1.
     */
    static public int getLabelOfEqual(NBAState target, HashSet<NBAState> set){
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
