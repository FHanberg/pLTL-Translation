import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/*
    Print information about a completed Büchi-automata
    */
public class Output {

    static public String readout(String name, LinkedList<NBAState> states, HashMap<Integer,HashMap<String, LinkedList<NBATransition>>> trans, LinkedList<String> atoms, int tLabels){
        StringBuilder result = new StringBuilder();
        result.append("HOA: v1\n");
        result.append("States: ").append(states.size()).append("\n");
        result.append("Start: 0\n");
        result.append("acc-name: Buchi\n");
        result.append("Acceptance: ").append(tLabels);
        for(int i = 0 ; i < tLabels ; i++){
            if(i > 0)
                result.append(" &");
            result.append(" Inf(").append(i).append(")");
        }
        result.append("\n");
        result.append("AP: ").append(atoms.size());
        for(String atom : atoms){
            result.append(" \"").append(atom).append("\"");
        }
        result.append("\n");
        result.append("--BODY--\n");
        for(NBAState state : states){
            result.append("State: ").append(state.getLabel()).append(" \n");
            result.append(transitionReadout(state.m_label,states.size(), trans, atoms));
        }
        result.append("--END--");
        return result.toString();
    }

    static private String transitionReadout(int state, int statecount, HashMap<Integer,HashMap<String,LinkedList<NBATransition>>> trans, LinkedList<String> atoms){
        StringBuilder result = new StringBuilder();
        HashMap<String, LinkedList<NBATransition>> transitions = trans.get(state);
        HashMap<HashSet<String>, LinkedList<Integer>> transitionMap = new HashMap<>();
        HashMap<HashSet<String>, LinkedList<Integer>> labelMap = new HashMap<>();
        LinkedList<ReadoutTransition> relevant = new LinkedList<>();
        for(int i = 0; i < statecount ; i++){
            ReadoutTransition readout = new ReadoutTransition(state, i);
            for (LinkedList<NBATransition> list: transitions.values()) {
                for (NBATransition t: list) {
                    if(t.m_to == i){
                        readout.m_labels.addAll(t.m_labels);
                        readout.m_valuations.add(t.m_valuation);
                    }
                }
            }
            if(!readout.m_valuations.isEmpty())
                relevant.add(readout);
        }
        for (ReadoutTransition t: relevant) {

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
            for (Integer x: transitionMap.get(set)) {
                result.append("  [");
                if (set.contains("t")) {
                    result.append("t]");
                } else {
                    for (int i = 0; i < atoms.size(); i++) {
                        if (i != 0) {
                            result.append("&");
                        }
                        if (set.contains(String.valueOf(i))) {
                            result.append(i);
                        } else {
                            result.append("!").append(i);
                        }
                    }
                    result.append("]");

                }
                result.append(" ").append(x);
                LinkedList<Integer> labelList = labelMap.get(set);
                if (!labelList.isEmpty()) {
                    boolean first = true;
                    result.append(" {");
                    for (Integer i : labelList) {
                        if (!first)
                            result.append(" ");
                        result.append(i);
                        first = false;
                    }
                    result.append("}");
                }
                result.append("\n");
            }
        }
        return result.toString();
    }

    static private LinkedList<NBATransition> getStateTransitions(int state, LinkedList<NBATransition> trans){
        LinkedList<NBATransition> result = new LinkedList<>();
        for (NBATransition t: trans) {
            if(t.m_from == state)
                result.add(t);
        }
        return result;
    }

    static private HashSet<HashSet<String>> transitionTrim(LinkedHashSet<LinkedHashSet<String>> target, LinkedList<String> atoms){
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
        }else if(converted.isEmpty()){
            result = new HashSet<>();
            result.add(new HashSet<>());
            return result;
        }else{
            return converted;
        }
        return result;
    }

    private static HashSet<HashSet<String>> valuationConversion(LinkedHashSet<LinkedHashSet<String>> target, LinkedList<String> atoms){
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

    static public LinkedHashSet<LinkedHashSet<String>> getAllValuations(LinkedList<String> atoms, int index, LinkedHashSet<String> current){
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


}


