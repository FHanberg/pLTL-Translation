import PLTL.*;
import PLTL.Future.*;
import PLTL.Past.*;

import java.util.HashSet;
import java.util.LinkedList;

public class MainLoop {


    public static String Process(PLTLExp input, boolean optimize){
        //Initialization
        int stateLabel = 0;

        //System.out.println("Provided formula:");
        //Initial conversion to NNF + console readout
        //input.accept(new FormulaReader());
        //System.out.println();
        PLTLExp newTest = input.accept(new NNFConverter());
        //System.out.println("Post-NNF formula:");
        //newTest.accept(new FormulaReader());
        //System.out.println();
        //System.out.println();

        LinkedList<String> atoms = atomRepeat(atomList(newTest));

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
            System.out.print("Set size: " + mainSet.size() + " Transition size: " + transitionSet.size() + "\n" );
            //Storage set used to contain the states which should be checked in the next step.
            HashSet<NBAState> nextSet = new HashSet<>();
            //For each "fresh" state
            System.out.println("Current Set size: " + currentSet.size());
            int z = 1;
            for (NBAState node: currentSet) {
                System.out.println("Progress: " + z);
                z++;
                int curLabel = node.m_label;
                //Perform a step in the main NBA-to-Büchi translation
                LinkedList<TranslationOutput> prospective = t.translationStep(node, atoms, optimize);
                System.out.println("Prospective Size: " + prospective.size());
                //For each primary implicant:
                for (TranslationOutput out: prospective) {
                    NBAState next = new NBAState(out.getTo());
                    //Determine if the main set already has an equal state, and return its label
                    int equalLabel = getLabelOfEqual(out.getTo(), mainSet);
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
                        transitionSet.add(new NBATransition(curLabel, stateLabel, transLabels, out.getVals()));
                        stateLabel += 1;

                        //If the state has been created before, add a new transition if necessary
                    }else{
                            if(!transitionRepeat(new NBATransition(curLabel, equalLabel, new HashSet<>()), transitionSet, out.getVals())){
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
                                transitionSet.add(new NBATransition(curLabel, equalLabel, transLabels, out.getVals()));

                            }

                    }
                }
            }
            //replace the (now old) set of fresh states with the next set
            currentSet = nextSet;
        }
        transitionSet.sort(NBATransition.comp);


        //Write information about the set to the console
        return Output.readout(mainSet, transitionSet, atoms, t.getTransitionLabels());
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
        public HashSet<Integer> visit(Mighty exp) {
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
    static public boolean transitionRepeat(NBATransition target, LinkedList<NBATransition> set, HashSet<HashSet<String>> valuations){
        for(NBATransition t : set){
            if(t.equals(target)) {
                for (HashSet<String> s: valuations) {
                    t.addValuation(s);
                }
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

    static private LinkedList<String> atomRepeat(LinkedList<String> list){
        LinkedList<String> result = new LinkedList<>();
        for (String x: list) {
            boolean rep = false;
            for (String y: result) {
                if (x.equals(y)) {
                    rep = true;
                    break;
                }
            }
            if(!rep)
                result.add(x);
        }
        return result;
    }

    /*
    Checks if a state already exists in a set, and returns its label if so.
    otherwise returns -1.
     */
    static public int getLabelOfEqual(PLTLExp target, LinkedList<NBAState> set){
        for(NBAState node : set){
            if(target.equals(node.m_exp))
                return node.getLabel();
        }
        return -1;
    }
}
