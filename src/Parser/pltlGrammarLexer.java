// Generated from pltlGrammar.g4 by ANTLR 4.13.2
package Parser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class pltlGrammarLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, ATOMIC=5, UNTIL=6, GLOBALLY=7, FUTURE=8, 
		NEXT=9, HISTORICALLY=10, ONCE=11, YESTERDAY=12, WYESTERDAY=13, WEAK=14, 
		RELEASE=15, MIGHTY=16, BEFORE=17, WBEFORE=18, SINCE=19, WSINCE=20, IMPLICATION=21, 
		AND=22, OR=23, NOT=24, WS=25;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "ATOMIC", "UNTIL", "GLOBALLY", "FUTURE", 
			"NEXT", "HISTORICALLY", "ONCE", "YESTERDAY", "WYESTERDAY", "WEAK", "RELEASE", 
			"MIGHTY", "BEFORE", "WBEFORE", "SINCE", "WSINCE", "IMPLICATION", "AND", 
			"OR", "NOT", "WS"
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


	public pltlGrammarLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "pltlGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\u0019x\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b"+
		"\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002"+
		"\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002"+
		"\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002"+
		"\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002"+
		"\u0018\u0007\u0018\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0004\u0004"+
		"\u0004D\b\u0004\u000b\u0004\f\u0004E\u0001\u0005\u0001\u0005\u0001\u0006"+
		"\u0001\u0006\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\t\u0001\t\u0001"+
		"\n\u0001\n\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\r\u0001"+
		"\r\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0015"+
		"\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0018"+
		"\u0004\u0018s\b\u0018\u000b\u0018\f\u0018t\u0001\u0018\u0001\u0018\u0000"+
		"\u0000\u0019\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b"+
		"\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b"+
		"\u000e\u001d\u000f\u001f\u0010!\u0011#\u0012%\u0013\'\u0014)\u0015+\u0016"+
		"-\u0017/\u00181\u0019\u0001\u0000\u0002\u0001\u0000az\u0003\u0000\t\n"+
		"\r\r  y\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000"+
		"\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000"+
		"\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000"+
		"\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000"+
		"\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000"+
		"\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000"+
		"\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000"+
		"\u001d\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000"+
		"!\u0001\u0000\u0000\u0000\u0000#\u0001\u0000\u0000\u0000\u0000%\u0001"+
		"\u0000\u0000\u0000\u0000\'\u0001\u0000\u0000\u0000\u0000)\u0001\u0000"+
		"\u0000\u0000\u0000+\u0001\u0000\u0000\u0000\u0000-\u0001\u0000\u0000\u0000"+
		"\u0000/\u0001\u0000\u0000\u0000\u00001\u0001\u0000\u0000\u0000\u00013"+
		"\u0001\u0000\u0000\u0000\u00038\u0001\u0000\u0000\u0000\u0005>\u0001\u0000"+
		"\u0000\u0000\u0007@\u0001\u0000\u0000\u0000\tC\u0001\u0000\u0000\u0000"+
		"\u000bG\u0001\u0000\u0000\u0000\rI\u0001\u0000\u0000\u0000\u000fK\u0001"+
		"\u0000\u0000\u0000\u0011M\u0001\u0000\u0000\u0000\u0013O\u0001\u0000\u0000"+
		"\u0000\u0015Q\u0001\u0000\u0000\u0000\u0017S\u0001\u0000\u0000\u0000\u0019"+
		"U\u0001\u0000\u0000\u0000\u001bX\u0001\u0000\u0000\u0000\u001dZ\u0001"+
		"\u0000\u0000\u0000\u001f\\\u0001\u0000\u0000\u0000!^\u0001\u0000\u0000"+
		"\u0000#`\u0001\u0000\u0000\u0000%c\u0001\u0000\u0000\u0000\'e\u0001\u0000"+
		"\u0000\u0000)h\u0001\u0000\u0000\u0000+k\u0001\u0000\u0000\u0000-m\u0001"+
		"\u0000\u0000\u0000/o\u0001\u0000\u0000\u00001r\u0001\u0000\u0000\u0000"+
		"34\u0005t\u0000\u000045\u0005r\u0000\u000056\u0005u\u0000\u000067\u0005"+
		"e\u0000\u00007\u0002\u0001\u0000\u0000\u000089\u0005f\u0000\u00009:\u0005"+
		"a\u0000\u0000:;\u0005l\u0000\u0000;<\u0005s\u0000\u0000<=\u0005e\u0000"+
		"\u0000=\u0004\u0001\u0000\u0000\u0000>?\u0005(\u0000\u0000?\u0006\u0001"+
		"\u0000\u0000\u0000@A\u0005)\u0000\u0000A\b\u0001\u0000\u0000\u0000BD\u0007"+
		"\u0000\u0000\u0000CB\u0001\u0000\u0000\u0000DE\u0001\u0000\u0000\u0000"+
		"EC\u0001\u0000\u0000\u0000EF\u0001\u0000\u0000\u0000F\n\u0001\u0000\u0000"+
		"\u0000GH\u0005U\u0000\u0000H\f\u0001\u0000\u0000\u0000IJ\u0005G\u0000"+
		"\u0000J\u000e\u0001\u0000\u0000\u0000KL\u0005F\u0000\u0000L\u0010\u0001"+
		"\u0000\u0000\u0000MN\u0005X\u0000\u0000N\u0012\u0001\u0000\u0000\u0000"+
		"OP\u0005H\u0000\u0000P\u0014\u0001\u0000\u0000\u0000QR\u0005O\u0000\u0000"+
		"R\u0016\u0001\u0000\u0000\u0000ST\u0005Y\u0000\u0000T\u0018\u0001\u0000"+
		"\u0000\u0000UV\u0005~\u0000\u0000VW\u0005Y\u0000\u0000W\u001a\u0001\u0000"+
		"\u0000\u0000XY\u0005W\u0000\u0000Y\u001c\u0001\u0000\u0000\u0000Z[\u0005"+
		"R\u0000\u0000[\u001e\u0001\u0000\u0000\u0000\\]\u0005M\u0000\u0000] \u0001"+
		"\u0000\u0000\u0000^_\u0005B\u0000\u0000_\"\u0001\u0000\u0000\u0000`a\u0005"+
		"~\u0000\u0000ab\u0005B\u0000\u0000b$\u0001\u0000\u0000\u0000cd\u0005S"+
		"\u0000\u0000d&\u0001\u0000\u0000\u0000ef\u0005~\u0000\u0000fg\u0005S\u0000"+
		"\u0000g(\u0001\u0000\u0000\u0000hi\u0005-\u0000\u0000ij\u0005>\u0000\u0000"+
		"j*\u0001\u0000\u0000\u0000kl\u0005&\u0000\u0000l,\u0001\u0000\u0000\u0000"+
		"mn\u0005|\u0000\u0000n.\u0001\u0000\u0000\u0000op\u0005!\u0000\u0000p"+
		"0\u0001\u0000\u0000\u0000qs\u0007\u0001\u0000\u0000rq\u0001\u0000\u0000"+
		"\u0000st\u0001\u0000\u0000\u0000tr\u0001\u0000\u0000\u0000tu\u0001\u0000"+
		"\u0000\u0000uv\u0001\u0000\u0000\u0000vw\u0006\u0018\u0000\u0000w2\u0001"+
		"\u0000\u0000\u0000\u0003\u0000Et\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}