// Generated from pltlGrammar.g4 by ANTLR 4.13.2
package Parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link pltlGrammarParser}.
 */
public interface pltlGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link pltlGrammarParser#file_}.
	 * @param ctx the parse tree
	 */
	void enterFile_(pltlGrammarParser.File_Context ctx);
	/**
	 * Exit a parse tree produced by {@link pltlGrammarParser#file_}.
	 * @param ctx the parse tree
	 */
	void exitFile_(pltlGrammarParser.File_Context ctx);
	/**
	 * Enter a parse tree produced by {@link pltlGrammarParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterFormula(pltlGrammarParser.FormulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link pltlGrammarParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitFormula(pltlGrammarParser.FormulaContext ctx);
}