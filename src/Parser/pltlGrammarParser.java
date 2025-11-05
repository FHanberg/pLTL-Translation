// Generated from pltlGrammar.g4 by ANTLR 4.13.2
package Parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class pltlGrammarParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, ATOMIC=5, UNTIL=6, GLOBALLY=7, FUTURE=8, 
		NEXT=9, HISTORICALLY=10, ONCE=11, YESTERDAY=12, WYESTERDAY=13, WEAK=14, 
		RELEASE=15, MIGHTY=16, BEFORE=17, WBEFORE=18, SINCE=19, WSINCE=20, IMPLICATION=21, 
		AND=22, OR=23, NOT=24, WS=25;
	public static final int
		RULE_file_ = 0, RULE_formula = 1;
	private static String[] makeRuleNames() {
		return new String[] {
			"file_", "formula"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'true'", "'false'", "'('", "')'", null, "'U'", "'G'", "'F'", "'X'", 
			"'H'", "'O'", "'Y'", "'~Y'", "'W'", "'R'", "'M'", "'B'", "'~B'", "'S'", 
			"'~S'", "'->'", "'&'", "'|'", "'!'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, "ATOMIC", "UNTIL", "GLOBALLY", "FUTURE", 
			"NEXT", "HISTORICALLY", "ONCE", "YESTERDAY", "WYESTERDAY", "WEAK", "RELEASE", 
			"MIGHTY", "BEFORE", "WBEFORE", "SINCE", "WSINCE", "IMPLICATION", "AND", 
			"OR", "NOT", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "pltlGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public pltlGrammarParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class File_Context extends ParserRuleContext {
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public TerminalNode EOF() { return getToken(pltlGrammarParser.EOF, 0); }
		public File_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof pltlGrammarListener ) ((pltlGrammarListener)listener).enterFile_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof pltlGrammarListener ) ((pltlGrammarListener)listener).exitFile_(this);
		}
	}

	public final File_Context file_() throws RecognitionException {
		File_Context _localctx = new File_Context(_ctx, getState());
		enterRule(_localctx, 0, RULE_file_);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(4);
			formula(0);
			setState(5);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FormulaContext extends ParserRuleContext {
		public TerminalNode ATOMIC() { return getToken(pltlGrammarParser.ATOMIC, 0); }
		public List<FormulaContext> formula() {
			return getRuleContexts(FormulaContext.class);
		}
		public FormulaContext formula(int i) {
			return getRuleContext(FormulaContext.class,i);
		}
		public TerminalNode NOT() { return getToken(pltlGrammarParser.NOT, 0); }
		public TerminalNode GLOBALLY() { return getToken(pltlGrammarParser.GLOBALLY, 0); }
		public TerminalNode FUTURE() { return getToken(pltlGrammarParser.FUTURE, 0); }
		public TerminalNode NEXT() { return getToken(pltlGrammarParser.NEXT, 0); }
		public TerminalNode HISTORICALLY() { return getToken(pltlGrammarParser.HISTORICALLY, 0); }
		public TerminalNode ONCE() { return getToken(pltlGrammarParser.ONCE, 0); }
		public TerminalNode YESTERDAY() { return getToken(pltlGrammarParser.YESTERDAY, 0); }
		public TerminalNode WYESTERDAY() { return getToken(pltlGrammarParser.WYESTERDAY, 0); }
		public TerminalNode AND() { return getToken(pltlGrammarParser.AND, 0); }
		public TerminalNode OR() { return getToken(pltlGrammarParser.OR, 0); }
		public TerminalNode IMPLICATION() { return getToken(pltlGrammarParser.IMPLICATION, 0); }
		public TerminalNode UNTIL() { return getToken(pltlGrammarParser.UNTIL, 0); }
		public TerminalNode WEAK() { return getToken(pltlGrammarParser.WEAK, 0); }
		public TerminalNode RELEASE() { return getToken(pltlGrammarParser.RELEASE, 0); }
		public TerminalNode MIGHTY() { return getToken(pltlGrammarParser.MIGHTY, 0); }
		public TerminalNode BEFORE() { return getToken(pltlGrammarParser.BEFORE, 0); }
		public TerminalNode WBEFORE() { return getToken(pltlGrammarParser.WBEFORE, 0); }
		public TerminalNode SINCE() { return getToken(pltlGrammarParser.SINCE, 0); }
		public TerminalNode WSINCE() { return getToken(pltlGrammarParser.WSINCE, 0); }
		public FormulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof pltlGrammarListener ) ((pltlGrammarListener)listener).enterFormula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof pltlGrammarListener ) ((pltlGrammarListener)listener).exitFormula(this);
		}
	}

	public final FormulaContext formula() throws RecognitionException {
		return formula(0);
	}

	private FormulaContext formula(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		FormulaContext _localctx = new FormulaContext(_ctx, _parentState);
		FormulaContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_formula, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(21);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				{
				setState(8);
				match(T__0);
				}
				break;
			case T__1:
				{
				setState(9);
				match(T__1);
				}
				break;
			case ATOMIC:
				{
				setState(10);
				match(ATOMIC);
				}
				break;
			case T__2:
				{
				setState(11);
				match(T__2);
				setState(12);
				formula(0);
				setState(13);
				match(T__3);
				}
				break;
			case NOT:
				{
				setState(15);
				match(NOT);
				setState(16);
				formula(5);
				}
				break;
			case GLOBALLY:
			case FUTURE:
			case NEXT:
				{
				setState(17);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 896L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(18);
				formula(4);
				}
				break;
			case HISTORICALLY:
			case ONCE:
			case YESTERDAY:
			case WYESTERDAY:
				{
				setState(19);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 15360L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(20);
				formula(3);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(34);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(32);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
					case 1:
						{
						_localctx = new FormulaContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_formula);
						setState(23);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(24);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 14680064L) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(25);
						formula(7);
						}
						break;
					case 2:
						{
						_localctx = new FormulaContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_formula);
						setState(26);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(27);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 114752L) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(28);
						formula(3);
						}
						break;
					case 3:
						{
						_localctx = new FormulaContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_formula);
						setState(29);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(30);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 1966080L) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(31);
						formula(2);
						}
						break;
					}
					} 
				}
				setState(36);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1:
			return formula_sempred((FormulaContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean formula_sempred(FormulaContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 6);
		case 1:
			return precpred(_ctx, 2);
		case 2:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u0019&\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001\u0016"+
		"\b\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0005\u0001!\b\u0001\n\u0001"+
		"\f\u0001$\t\u0001\u0001\u0001\u0000\u0001\u0002\u0002\u0000\u0002\u0000"+
		"\u0005\u0001\u0000\u0007\t\u0001\u0000\n\r\u0001\u0000\u0015\u0017\u0002"+
		"\u0000\u0006\u0006\u000e\u0010\u0001\u0000\u0011\u0014,\u0000\u0004\u0001"+
		"\u0000\u0000\u0000\u0002\u0015\u0001\u0000\u0000\u0000\u0004\u0005\u0003"+
		"\u0002\u0001\u0000\u0005\u0006\u0005\u0000\u0000\u0001\u0006\u0001\u0001"+
		"\u0000\u0000\u0000\u0007\b\u0006\u0001\uffff\uffff\u0000\b\u0016\u0005"+
		"\u0001\u0000\u0000\t\u0016\u0005\u0002\u0000\u0000\n\u0016\u0005\u0005"+
		"\u0000\u0000\u000b\f\u0005\u0003\u0000\u0000\f\r\u0003\u0002\u0001\u0000"+
		"\r\u000e\u0005\u0004\u0000\u0000\u000e\u0016\u0001\u0000\u0000\u0000\u000f"+
		"\u0010\u0005\u0018\u0000\u0000\u0010\u0016\u0003\u0002\u0001\u0005\u0011"+
		"\u0012\u0007\u0000\u0000\u0000\u0012\u0016\u0003\u0002\u0001\u0004\u0013"+
		"\u0014\u0007\u0001\u0000\u0000\u0014\u0016\u0003\u0002\u0001\u0003\u0015"+
		"\u0007\u0001\u0000\u0000\u0000\u0015\t\u0001\u0000\u0000\u0000\u0015\n"+
		"\u0001\u0000\u0000\u0000\u0015\u000b\u0001\u0000\u0000\u0000\u0015\u000f"+
		"\u0001\u0000\u0000\u0000\u0015\u0011\u0001\u0000\u0000\u0000\u0015\u0013"+
		"\u0001\u0000\u0000\u0000\u0016\"\u0001\u0000\u0000\u0000\u0017\u0018\n"+
		"\u0006\u0000\u0000\u0018\u0019\u0007\u0002\u0000\u0000\u0019!\u0003\u0002"+
		"\u0001\u0007\u001a\u001b\n\u0002\u0000\u0000\u001b\u001c\u0007\u0003\u0000"+
		"\u0000\u001c!\u0003\u0002\u0001\u0003\u001d\u001e\n\u0001\u0000\u0000"+
		"\u001e\u001f\u0007\u0004\u0000\u0000\u001f!\u0003\u0002\u0001\u0002 \u0017"+
		"\u0001\u0000\u0000\u0000 \u001a\u0001\u0000\u0000\u0000 \u001d\u0001\u0000"+
		"\u0000\u0000!$\u0001\u0000\u0000\u0000\" \u0001\u0000\u0000\u0000\"#\u0001"+
		"\u0000\u0000\u0000#\u0003\u0001\u0000\u0000\u0000$\"\u0001\u0000\u0000"+
		"\u0000\u0003\u0015 \"";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}