package Parser;

import PLTL.*;
import PLTL.Future.*;
import PLTL.Past.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import Parser.pltlGrammarParser.FormulaContext;

import java.util.ArrayList;

/*
Converts ANTLR context into an actual PLTL expression
 */
public class ContextConverter {

    public static PLTLExp Conversion(ParseTree context){
        switch(context.getChildCount()){
            //Atom/True/False
            case 1:
                String terminal = context.getChild(0).getText();
                if(terminal.equals("true"))
                    return new True();
                if(terminal.equals("false"))
                    return new False();
                return new Term(terminal);
            //Unary
            case 2:
                String op = context.getChild(0).getText();
                ParseTree target = context.getChild(1);
                return switch (op) {
                    case "!" -> new Not(Conversion(target));
                    case "F" -> new Future(Conversion(target));
                    case "G" -> new Globally(Conversion(target));
                    case "X" -> new Next(Conversion(target));
                    case "H" -> new Historically(Conversion(target));
                    case "O" -> new Once(Conversion(target));
                    case "Y" -> new Yesterday(Conversion(target));
                    case "~Y" -> new WYesterday(Conversion(target));
                    default -> throw new IllegalStateException("Unexpected value: " + op);
                };
            //Binary/Parenthesis
            case 3:
                //Parenthesis has a unique format, so that's checked first
                if(context.getChild(0).getClass().equals(TerminalNodeImpl.class)){
                    return Conversion(context.getChild(1));
                }
                PLTLExp left = Conversion(context.getChild(0));
                PLTLExp right = Conversion(context.getChild(2));
                String oop = context.getChild(1).getText();
                return switch(oop){
                    case "&" -> new And(left, right);
                    case "|" -> new Or(left, right);
                    case "->" -> new Or(new Not(left), right);
                    case "U" -> new Until(left, right);
                    case "W" -> new WUntil(left, right);
                    case "R" -> new Release(left, right);
                    case "M" -> new Mighty(left, right);
                    case "B" -> new Before(left, right);
                    case "~B" -> new WBefore(left, right);
                    case "S" -> new Since(left, right);
                    case "~S" -> new WSince(left, right);
                    default -> throw new IllegalStateException("Unexpected value: " + oop);
                };
            default:
                return null;
        }
    }
}
