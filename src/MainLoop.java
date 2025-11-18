import PLTL.*;
import PLTL.Future.*;
import PLTL.Past.*;

import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class MainLoop {


    public static String Process(PLTLExp input, boolean optimize){
        //Initialization
        int stateLabel = 0;


        PLTLExp newTest = input.accept(new NNFConverter());

        LinkedList<String> atoms = atomRepeat(atomList(newTest));

        //Primary state output
        LinkedList<NBAState> mainSet = new LinkedList<>();
        //Primary transition output
        LinkedList<NBATransition> transitionSet = new LinkedList<>();
        //marks current states to be translated
        ArrayDeque<NBAState> toBeChecked = new ArrayDeque<>();

        //Create initial state
        NBAState firstState = new NBAState(newTest, stateLabel);

        stateLabel += 1;
        mainSet.add(firstState);
        toBeChecked.addLast(firstState);

        //Translator needs to be non-static or return the tuple (states + next U/M label)
        Translator t = new Translator();
        //Main loop of full translation
            while (!toBeChecked.isEmpty()) {
                System.out.println("Current Set size: " + toBeChecked.size());
                NBAState node = toBeChecked.pop();
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
                        toBeChecked.addLast(next);
                        mainSet.add(next);

                        //Labels are added if their corresponding U/M are NOT present
                        LinkedHashSet<Integer> antiLabels = next.m_exp.accept(new transitionLabel());
                        LinkedHashSet<Integer> transLabels = new LinkedHashSet<>();
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
                            if(!transitionRepeat(new NBATransition(curLabel, equalLabel, new LinkedHashSet<>()), transitionSet, out.getVals())){
                                //Labels are added if their corresponding U/M are NOT present
                                LinkedHashSet<Integer> antiLabels = next.m_exp.accept(new transitionLabel());
                                LinkedHashSet<Integer> transLabels = new LinkedHashSet<>();
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
                System.out.println("step done");
            }
        transitionSet.sort(NBATransition.comp);


        //Write information about the set to the console
        return Output.readout(mainSet, transitionSet, atoms, t.getTransitionLabels());
    }

    /*
    Return all labels to not be applied to a transition.
     */
    public static class transitionLabel implements PLTLExp.Visitor<LinkedHashSet<Integer>>{


        @Override
        public LinkedHashSet<Integer> visit(And exp) {
            LinkedHashSet<Integer> left = exp.getLeft().accept(new transitionLabel());
            LinkedHashSet<Integer> right = exp.getRight().accept(new transitionLabel());
            left.addAll(right);
            return left;
        }

        @Override
        public LinkedHashSet<Integer> visit(Or exp) {
            LinkedHashSet<Integer> left = exp.getLeft().accept(new transitionLabel());
            LinkedHashSet<Integer> right = exp.getRight().accept(new transitionLabel());
            left.addAll(right);
            return left;
        }

        @Override
        public LinkedHashSet<Integer> visit(Term exp) {
            return new LinkedHashSet<>();
        }

        @Override
        public LinkedHashSet<Integer> visit(NotTerm exp) {
            return new LinkedHashSet<>();
        }

        @Override
        public LinkedHashSet<Integer> visit(True exp) {
            return new LinkedHashSet<>();
        }

        @Override
        public LinkedHashSet<Integer> visit(False exp) {
            return new LinkedHashSet<>();
        }

        @Override
        public LinkedHashSet<Integer> visit(Globally exp) {
            return exp.getTarget().accept(new transitionLabel());
        }

        @Override
        public LinkedHashSet<Integer> visit(Historically exp) {
            return exp.getTarget().accept(new transitionLabel());
        }

        @Override
        public LinkedHashSet<Integer> visit(Until exp) {
            LinkedHashSet<Integer> left = exp.getLeft().accept(new transitionLabel());
            LinkedHashSet<Integer> right = exp.getRight().accept(new transitionLabel());
            left.addAll(right);
            left.add(exp.transitionLabel);
            return left;
        }

        @Override
        public LinkedHashSet<Integer> visit(WUntil exp) {
            LinkedHashSet<Integer> left = exp.getLeft().accept(new transitionLabel());
            LinkedHashSet<Integer> right = exp.getRight().accept(new transitionLabel());
            left.addAll(right);
            return left;
        }

        @Override
        public LinkedHashSet<Integer> visit(Mighty exp) {
            LinkedHashSet<Integer> left = exp.getLeft().accept(new transitionLabel());
            LinkedHashSet<Integer> right = exp.getRight().accept(new transitionLabel());
            left.addAll(right);
            left.add(exp.transitionLabel);
            return left;
        }

        @Override
        public LinkedHashSet<Integer> visit(Future exp) {
            return exp.getTarget().accept(new transitionLabel());
        }

        @Override
        public LinkedHashSet<Integer> visit(Once exp) {
            return exp.getTarget().accept(new transitionLabel());
        }

        @Override
        public LinkedHashSet<Integer> visit(Next exp) {
            return exp.getTarget().accept(new transitionLabel());
        }

        @Override
        public LinkedHashSet<Integer> visit(Yesterday exp) {
            return exp.getTarget().accept(new transitionLabel());
        }

        @Override
        public LinkedHashSet<Integer> visit(WYesterday exp) {
            return exp.getTarget().accept(new transitionLabel());
        }

        @Override
        public LinkedHashSet<Integer> visit(Release exp) {
            LinkedHashSet<Integer> left = exp.getLeft().accept(new transitionLabel());
            LinkedHashSet<Integer> right = exp.getRight().accept(new transitionLabel());
            left.addAll(right);
            return left;
        }

        @Override
        public LinkedHashSet<Integer> visit(Since exp) {
            LinkedHashSet<Integer> left = exp.getLeft().accept(new transitionLabel());
            LinkedHashSet<Integer> right = exp.getRight().accept(new transitionLabel());
            left.addAll(right);
            return left;
        }

        @Override
        public LinkedHashSet<Integer> visit(WSince exp) {
            LinkedHashSet<Integer> left = exp.getLeft().accept(new transitionLabel());
            LinkedHashSet<Integer> right = exp.getRight().accept(new transitionLabel());
            left.addAll(right);
            return left;
        }

        @Override
        public LinkedHashSet<Integer> visit(Before exp) {
            LinkedHashSet<Integer> left = exp.getLeft().accept(new transitionLabel());
            LinkedHashSet<Integer> right = exp.getRight().accept(new transitionLabel());
            left.addAll(right);
            return left;
        }

        @Override
        public LinkedHashSet<Integer> visit(WBefore exp) {
            LinkedHashSet<Integer> left = exp.getLeft().accept(new transitionLabel());
            LinkedHashSet<Integer> right = exp.getRight().accept(new transitionLabel());
            left.addAll(right);
            return left;
        }

        @Override
        public LinkedHashSet<Integer> visit(Not exp) {
            return null;
        }
    }

    /*
    Check if a transition already exists in a given set.
     */
    static public boolean transitionRepeat(NBATransition target, LinkedList<NBATransition> set, LinkedHashSet<LinkedHashSet<String>> valuations){
        for(NBATransition t : set){
            if(t.equals(target)) {
                for (LinkedHashSet<String> s: valuations) {
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
