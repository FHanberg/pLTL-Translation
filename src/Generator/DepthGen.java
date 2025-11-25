package Generator;

import PLTL.Unary;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

import static Generator.GenCodes.*;

public class DepthGen {
    private int m_remainingLen;

    private int m_remainingDepth;

    private int m_requestedDepth;

    private int m_greatestDepth;

    private int m_maxVar;

    private int m_curVar;

    private int m_prob; //0 - 100 span

    private boolean past;

    public DepthGen(){
        m_remainingLen = -1;
        m_remainingDepth = 0;
        m_maxVar = 0;
        m_curVar = 0;
        m_prob = 50;

    }

    public String Generate(int depth, int variables){
        m_remainingLen = -1;
        m_remainingDepth = depth;
        m_maxVar = variables;

        GenStructure init = InitGen();
        GenStructure result = InteriorGenerate(init);
        return Convert(result);
    }

    public String Generate(int depth, int variables, int length, int prob){
        m_remainingLen = length;
        m_requestedDepth = depth;
        m_greatestDepth = 0;
        m_maxVar = variables;
        m_curVar = 0;
        m_prob = prob;

        GenStructure top = InitGen();

        ArrayDeque<GenStructure> queue = new ArrayDeque<>();
        queue.addLast(top);

        boolean rollPast;
        boolean rollFuture; //need both if top-level is non-temporal
        boolean rollTerminal;
        boolean rollLogic;
        boolean depthCrunch = false; //ensures binary nodes contain at least 1 terminal

        while(!queue.isEmpty()){
            GenStructure current = queue.pop();
            rollPast = false;
            rollFuture = false;
            rollTerminal = false;
            rollLogic = false;
            boolean swap = false;
            if(m_remainingLen <= 0){
                rollTerminal = true;
            }
            else if(current.localDepth == m_requestedDepth){
                if(current.inPast == 0){
                    rollFuture = true;
                }else{
                    rollPast = true;
                }
                rollLogic = true;
            }
            else if(m_remainingLen <= m_requestedDepth - m_greatestDepth){
                if(current.localDepth != m_greatestDepth){
                    if(current instanceof GenUnary u){
                        u.setTarget(TerminalGen());
                    }
                    if(current instanceof GenBinary b){
                        b.setLeft(TerminalGen());
                        b.setRight(TerminalGen());
                    }
                    continue;
                }

                depthCrunch = true;
                swap = true;
            }
            else{
                if(current.inPast != -1) {
                    int rand = Random(1, 100);
                    if (rand <= m_prob) {
                        swap = true;
                    if (current.inPast == 0)
                        rollPast = true;
                    else
                        rollFuture = true;
                    }else{
                        rollLogic = true;
                        //if(!queue.isEmpty() && current.localDepth != m_greatestDepth)
                            //rollTerminal = true;
                        if(current.inPast == 1)
                            rollPast = true;
                        else
                            rollFuture = true;
                    }
                }else{
                    rollLogic = true;
                    //if(!queue.isEmpty() && current.localDepth != m_greatestDepth)
                        //rollTerminal = true;
                    rollFuture = true;
                    rollPast = true;
                }

            }
            if(current instanceof GenBinary b){
                GenStructure left = GenFull(rollLogic,rollTerminal,rollPast,rollFuture);
                GenStructure right;
                if(depthCrunch)
                    right = TerminalGen();
                else
                    right = GenFull(rollLogic,rollTerminal,rollPast,rollFuture);
                b.setLeft(left);
                b.setRight(right);
                left.localDepth = b.localDepth;
                right.localDepth = b.localDepth;
                if(swap){
                    if(current.inPast == 0){
                        left.inPast = 1;
                        right.inPast = 1;
                    }else{
                        left.inPast = 0;
                        right.inPast = 0;
                    }
                    left.localDepth = b.localDepth +1;
                    right.localDepth = b.localDepth +1;
                    if(left.localDepth > m_greatestDepth)
                        m_greatestDepth = left.localDepth;
                }else{
                    if(current.inPast == -1){
                        left.inPast = IsPast(left.getType());
                        right.inPast = IsPast(right.getType());
                    }
                    else{
                        left.inPast = current.inPast;
                        right.inPast = current.inPast;
                    }
                }
                if(!(left instanceof GenTerminal)){
                    m_remainingLen -= 1;
                    queue.addLast(left);
                }
                if(!(right instanceof GenTerminal)){
                    m_remainingLen -= 1;
                    queue.addLast(right);
                }
            }
            if(current instanceof GenUnary u){
                GenStructure target = GenFull(rollLogic,rollTerminal,rollPast,rollFuture);
                u.setTarget(target);
                target.localDepth = u.localDepth;
                if(swap){
                    if(current.inPast == 0)
                        target.inPast = 1;
                    else
                        target.inPast = 0;
                    target.localDepth = u.localDepth +1;
                    if(target.localDepth > m_greatestDepth)
                        m_greatestDepth = target.localDepth;
                }else{
                    if(current.inPast == -1){
                        target.inPast = IsPast(target.getType());
                    }
                    else{
                        target.inPast = current.inPast;
                    }
                }
                if(!(target instanceof GenTerminal)){
                    queue.addLast(target);
                    m_remainingLen-=1;
                }
            }
        }

        return Convert(top);
    }

    private int IsPast(GenCodes code){
        return switch (code) {
            case IMPLIES, AND, NOT, TRUE, FALSE, TERM, OR -> -1;
            case WS, WB, S, B, WY, Y, O, H -> 1;
            default -> 0;
        };
    }

    private GenStructure GenFull(boolean logic, boolean terminal, boolean past, boolean future){
        int max = 1;
        if(logic)
            max += 4;
        if(terminal)
            max += 3;
        if(past)
            max += 8;
        if(future)
            max += 7;
        int rand = Random(1,max);
        if(logic){
            if(rand <= 4) {
                return LogicGen();
            }else{
                rand -= 4;
            }
        }
        if(terminal){
            if(rand <= 3) {
                return TerminalGen();
            }else{
                rand -= 3;
            }
        }
        if(past){
            if(rand <= 8)
                return PastGen();
        }
        if(future){
            return FutureGen();
        }
        return TerminalGen();
    }

    private GenStructure InitGen(){
        past = false;
        m_remainingDepth -=1;
        if(m_remainingLen <= 0){
            return PastGen();
        }

        GenStructure result;
        int rand = Random(1,19);
        if(rand <= 8){
            result = PastGen();
            result.inPast = 1;
        }else if(rand <= 15){
            result = FutureGen();
            result.inPast = 0;
        }else{
            result = LogicGen();
            result.inPast = -1;
        }
        result.localDepth = 0;

        m_remainingLen -= 1;

        return result;
    }

    private GenStructure InteriorGenerate(GenStructure current){
        if(m_remainingLen <= 0){
            if(m_remainingDepth <= 0){
                if(current instanceof GenTerminal){
                    return current;
                }else if(current instanceof GenUnary){
                    ((GenUnary) current).setTarget(TerminalGen());
                    return current;
                }else if(current instanceof GenBinary){
                    ((GenBinary) current).setLeft(TerminalGen());
                    ((GenBinary) current).setRight(TerminalGen());
                    return current;
                }
            }
            m_remainingDepth -=1;
            GenStructure next;
            if(past){
                next = PastGen();
            }else{
                next = FutureGen();
            }
            past = !past;
            if(current instanceof GenUnary){
                ((GenUnary) current).setTarget(next);

            }else if(current instanceof GenBinary){
                ((GenBinary) current).setLeft(next);
                ((GenBinary) current).setRight(TerminalGen());
            }
            InteriorGenerate(next);
            return current;
        }

        return null;
    }

    private int Random(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, max+1);
    }

    public static char getChar(int i) {
        return i<0 || i>25 ? '?' : (char)('a' + i);
    }

    private GenStructure PastGen(){
        int rand = Random(0, 5);
        return switch (rand) {
            case 0 -> new GenBinary(GenCodes.B);
            case 1 -> new GenUnary(GenCodes.H);
            case 2 -> new GenUnary(GenCodes.O);
            case 3 -> new GenBinary(GenCodes.S);
            case 4 -> new GenBinary(GenCodes.WB);
            default -> new GenBinary(GenCodes.WS);
        };

    }

    private GenStructure FutureGen(){
        int rand = Random(0,6);
        return switch (rand) {
            case 0 -> new GenUnary(GenCodes.F);
            case 1 -> new GenUnary(GenCodes.G);
            case 2 -> new GenBinary(GenCodes.M);
            case 3 -> new GenUnary(GenCodes.X);
            case 4 -> new GenBinary(GenCodes.R);
            case 5 -> new GenBinary(GenCodes.U);
            default -> new GenBinary(GenCodes.W);
        };
    }

    private GenStructure TerminalGen(){
        int rand = Random(2, 4);
        switch (rand) {
            case 0 -> {
                return new GenTerminal(FALSE);
            }
            case 1 -> {
                return new GenTerminal(GenCodes.TRUE);
            }
            default -> {
                GenTerminal term = new GenTerminal(GenCodes.TERM);
                term.term = getChar(m_curVar);
                m_curVar += 1;
                if (m_curVar >= m_maxVar)
                    m_curVar = 0;
                return term;
            }
        }
    }

    private GenStructure LogicGen(){
        int rand = Random(0,3);
        return switch (rand){
            case 0 -> new GenUnary(NOT);
            case 1 -> new GenBinary(IMPLIES);
            case 2 -> new GenBinary(AND);
            default -> new GenBinary(OR);
        };
    }

    private String Convert(GenStructure s){
        if(s instanceof GenTerminal){
            switch (s.getType()){
                case FALSE:
                    return "false";
                case TRUE:
                    return "true";
                case TERM:
                    return "" + ((GenTerminal) s).term;
            }
        }else if(s instanceof GenUnary){
            String target = Convert(((GenUnary) s).getTarget());
            String op = "";
            switch (s.getType()){
                case F -> op = "F";
                case G -> op = "G";
                case H -> op = "H";
                case O -> op = "O";
                case Y -> op = "Y";
                case X -> op = "X";
                case WY -> op = "~Y";
                case NOT -> op = "!";
            }
            return op + " ( " + target + " ) ";
        }else if(s instanceof GenBinary){
            String left = Convert(((GenBinary) s).getLeft());
            String right = Convert(((GenBinary) s).getRight());
            String op = "";
            switch (s.getType()){
                case W -> op = "W";
                case B -> op = "B";
                case M -> op = "M";
                case R -> op = "R";
                case S -> op = "S";
                case U -> op = "U";
                case OR -> op = "|";
                case WB -> op = "~B";
                case WS -> op = "~S";
                case AND -> op = "&";
                case IMPLIES -> op = "->";
            }
            return " ( " + left + " " + op + " " + right + " ) ";
        }
        return null;
    }
}
