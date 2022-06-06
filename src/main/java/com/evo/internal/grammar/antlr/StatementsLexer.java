// Generated from .\Statements.g4 by ANTLR 4.9.3
package com.evo.internal.grammar.antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class StatementsLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		WHITE_SPACE=1, COMMENT_MULTILINE=2, COMMENT_SINGLE_LINE=3, COLON=4, SEMICOLON=5, 
		SINGLE_QUOTE=6, SINGLE_QUOTE_ESC=7, DOUBLE_QUOTE=8, DOUBLE_QUOTE_ESC=9, 
		BACK_TICK=10, BACK_TICK_ESC=11, PHRASE=12, NEW_LINE=13, WORD=14;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"WHITE_SPACE", "COMMENT_MULTILINE", "COMMENT_SINGLE_LINE", "COLON", "SEMICOLON", 
			"SINGLE_QUOTE", "SINGLE_QUOTE_ESC", "DOUBLE_QUOTE", "DOUBLE_QUOTE_ESC", 
			"BACK_TICK", "BACK_TICK_ESC", "PHRASE", "NEW_LINE", "WORD", "A", "B", 
			"C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", 
			"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "WHITE_SPACE", "COMMENT_MULTILINE", "COMMENT_SINGLE_LINE", "COLON", 
			"SEMICOLON", "SINGLE_QUOTE", "SINGLE_QUOTE_ESC", "DOUBLE_QUOTE", "DOUBLE_QUOTE_ESC", 
			"BACK_TICK", "BACK_TICK_ESC", "PHRASE", "NEW_LINE", "WORD"
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


	public StatementsLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Statements.g4"; }

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

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 11:
			PHRASE_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void PHRASE_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:
			 setText(getText().substring(1, getText().length()-1)); 
			break;
		case 1:
			 setText(getText().substring(1, getText().length()-1)); 
			break;
		case 2:
			 setText(getText().substring(1, getText().length()-1)); 
			break;
		}
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\20\u00f9\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\3\2\6\2U\n"+
		"\2\r\2\16\2V\3\2\3\2\3\3\3\3\3\3\3\3\7\3_\n\3\f\3\16\3b\13\3\3\3\3\3\3"+
		"\3\3\4\3\4\3\4\3\4\3\4\5\4l\n\4\3\4\7\4o\n\4\f\4\16\4r\13\4\3\4\5\4u\n"+
		"\4\3\4\3\4\5\4y\n\4\3\4\3\4\3\4\3\4\5\4\177\n\4\3\4\3\4\5\4\u0083\n\4"+
		"\5\4\u0085\n\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3\n"+
		"\3\13\3\13\3\f\3\f\3\f\3\r\3\r\7\r\u009c\n\r\f\r\16\r\u009f\13\r\3\r\3"+
		"\r\3\r\3\r\3\r\7\r\u00a6\n\r\f\r\16\r\u00a9\13\r\3\r\3\r\3\r\3\r\3\r\7"+
		"\r\u00b0\n\r\f\r\16\r\u00b3\13\r\3\r\3\r\3\r\5\r\u00b8\n\r\3\16\5\16\u00bb"+
		"\n\16\3\16\3\16\5\16\u00bf\n\16\3\17\6\17\u00c2\n\17\r\17\16\17\u00c3"+
		"\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26"+
		"\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35"+
		"\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3"+
		"\'\3(\3(\3)\3)\3`\2*\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27"+
		"\r\31\16\33\17\35\20\37\2!\2#\2%\2\'\2)\2+\2-\2/\2\61\2\63\2\65\2\67\2"+
		"9\2;\2=\2?\2A\2C\2E\2G\2I\2K\2M\2O\2Q\2\3\2%\5\2\13\f\17\17\"\"\4\2\13"+
		"\13\"\"\4\2\f\f\17\17\3\2<<\3\2==\3\2))\3\2$$\3\2bb\6\2\13\f\17\17\"\""+
		"<=\4\2CCcc\4\2DDdd\4\2EEee\4\2FFff\4\2GGgg\4\2HHhh\4\2IIii\4\2JJjj\4\2"+
		"KKkk\4\2LLll\4\2MMmm\4\2NNnn\4\2OOoo\4\2PPpp\4\2QQqq\4\2RRrr\4\2SSss\4"+
		"\2TTtt\4\2UUuu\4\2VVvv\4\2WWww\4\2XXxx\4\2YYyy\4\2ZZzz\4\2[[{{\4\2\\\\"+
		"||\2\u00ef\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2"+
		"\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27"+
		"\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\3T\3\2\2\2\5Z\3\2\2\2"+
		"\7\u0084\3\2\2\2\t\u0086\3\2\2\2\13\u0088\3\2\2\2\r\u008a\3\2\2\2\17\u008c"+
		"\3\2\2\2\21\u008f\3\2\2\2\23\u0091\3\2\2\2\25\u0094\3\2\2\2\27\u0096\3"+
		"\2\2\2\31\u00b7\3\2\2\2\33\u00be\3\2\2\2\35\u00c1\3\2\2\2\37\u00c5\3\2"+
		"\2\2!\u00c7\3\2\2\2#\u00c9\3\2\2\2%\u00cb\3\2\2\2\'\u00cd\3\2\2\2)\u00cf"+
		"\3\2\2\2+\u00d1\3\2\2\2-\u00d3\3\2\2\2/\u00d5\3\2\2\2\61\u00d7\3\2\2\2"+
		"\63\u00d9\3\2\2\2\65\u00db\3\2\2\2\67\u00dd\3\2\2\29\u00df\3\2\2\2;\u00e1"+
		"\3\2\2\2=\u00e3\3\2\2\2?\u00e5\3\2\2\2A\u00e7\3\2\2\2C\u00e9\3\2\2\2E"+
		"\u00eb\3\2\2\2G\u00ed\3\2\2\2I\u00ef\3\2\2\2K\u00f1\3\2\2\2M\u00f3\3\2"+
		"\2\2O\u00f5\3\2\2\2Q\u00f7\3\2\2\2SU\t\2\2\2TS\3\2\2\2UV\3\2\2\2VT\3\2"+
		"\2\2VW\3\2\2\2WX\3\2\2\2XY\b\2\2\2Y\4\3\2\2\2Z[\7\61\2\2[\\\7,\2\2\\`"+
		"\3\2\2\2]_\13\2\2\2^]\3\2\2\2_b\3\2\2\2`a\3\2\2\2`^\3\2\2\2ac\3\2\2\2"+
		"b`\3\2\2\2cd\7,\2\2de\7\61\2\2e\6\3\2\2\2fg\7/\2\2gh\7/\2\2hi\3\2\2\2"+
		"il\t\3\2\2jl\7%\2\2kf\3\2\2\2kj\3\2\2\2lp\3\2\2\2mo\n\4\2\2nm\3\2\2\2"+
		"or\3\2\2\2pn\3\2\2\2pq\3\2\2\2qx\3\2\2\2rp\3\2\2\2su\7\17\2\2ts\3\2\2"+
		"\2tu\3\2\2\2uv\3\2\2\2vy\7\f\2\2wy\7\2\2\3xt\3\2\2\2xw\3\2\2\2y\u0085"+
		"\3\2\2\2z{\7/\2\2{|\7/\2\2|\u0082\3\2\2\2}\177\7\17\2\2~}\3\2\2\2~\177"+
		"\3\2\2\2\177\u0080\3\2\2\2\u0080\u0083\7\f\2\2\u0081\u0083\7\2\2\3\u0082"+
		"~\3\2\2\2\u0082\u0081\3\2\2\2\u0083\u0085\3\2\2\2\u0084k\3\2\2\2\u0084"+
		"z\3\2\2\2\u0085\b\3\2\2\2\u0086\u0087\t\5\2\2\u0087\n\3\2\2\2\u0088\u0089"+
		"\t\6\2\2\u0089\f\3\2\2\2\u008a\u008b\t\7\2\2\u008b\16\3\2\2\2\u008c\u008d"+
		"\7^\2\2\u008d\u008e\5\r\7\2\u008e\20\3\2\2\2\u008f\u0090\t\b\2\2\u0090"+
		"\22\3\2\2\2\u0091\u0092\7^\2\2\u0092\u0093\5\21\t\2\u0093\24\3\2\2\2\u0094"+
		"\u0095\t\t\2\2\u0095\26\3\2\2\2\u0096\u0097\7^\2\2\u0097\u0098\5\25\13"+
		"\2\u0098\30\3\2\2\2\u0099\u009d\5\r\7\2\u009a\u009c\n\7\2\2\u009b\u009a"+
		"\3\2\2\2\u009c\u009f\3\2\2\2\u009d\u009b\3\2\2\2\u009d\u009e\3\2\2\2\u009e"+
		"\u00a0\3\2\2\2\u009f\u009d\3\2\2\2\u00a0\u00a1\5\r\7\2\u00a1\u00a2\b\r"+
		"\3\2\u00a2\u00b8\3\2\2\2\u00a3\u00a7\5\21\t\2\u00a4\u00a6\n\b\2\2\u00a5"+
		"\u00a4\3\2\2\2\u00a6\u00a9\3\2\2\2\u00a7\u00a5\3\2\2\2\u00a7\u00a8\3\2"+
		"\2\2\u00a8\u00aa\3\2\2\2\u00a9\u00a7\3\2\2\2\u00aa\u00ab\5\21\t\2\u00ab"+
		"\u00ac\b\r\4\2\u00ac\u00b8\3\2\2\2\u00ad\u00b1\5\25\13\2\u00ae\u00b0\n"+
		"\t\2\2\u00af\u00ae\3\2\2\2\u00b0\u00b3\3\2\2\2\u00b1\u00af\3\2\2\2\u00b1"+
		"\u00b2\3\2\2\2\u00b2\u00b4\3\2\2\2\u00b3\u00b1\3\2\2\2\u00b4\u00b5\5\25"+
		"\13\2\u00b5\u00b6\b\r\5\2\u00b6\u00b8\3\2\2\2\u00b7\u0099\3\2\2\2\u00b7"+
		"\u00a3\3\2\2\2\u00b7\u00ad\3\2\2\2\u00b8\32\3\2\2\2\u00b9\u00bb\7\17\2"+
		"\2\u00ba\u00b9\3\2\2\2\u00ba\u00bb\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc\u00bf"+
		"\7\f\2\2\u00bd\u00bf\7\17\2\2\u00be\u00ba\3\2\2\2\u00be\u00bd\3\2\2\2"+
		"\u00bf\34\3\2\2\2\u00c0\u00c2\n\n\2\2\u00c1\u00c0\3\2\2\2\u00c2\u00c3"+
		"\3\2\2\2\u00c3\u00c1\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4\36\3\2\2\2\u00c5"+
		"\u00c6\t\13\2\2\u00c6 \3\2\2\2\u00c7\u00c8\t\f\2\2\u00c8\"\3\2\2\2\u00c9"+
		"\u00ca\t\r\2\2\u00ca$\3\2\2\2\u00cb\u00cc\t\16\2\2\u00cc&\3\2\2\2\u00cd"+
		"\u00ce\t\17\2\2\u00ce(\3\2\2\2\u00cf\u00d0\t\20\2\2\u00d0*\3\2\2\2\u00d1"+
		"\u00d2\t\21\2\2\u00d2,\3\2\2\2\u00d3\u00d4\t\22\2\2\u00d4.\3\2\2\2\u00d5"+
		"\u00d6\t\23\2\2\u00d6\60\3\2\2\2\u00d7\u00d8\t\24\2\2\u00d8\62\3\2\2\2"+
		"\u00d9\u00da\t\25\2\2\u00da\64\3\2\2\2\u00db\u00dc\t\26\2\2\u00dc\66\3"+
		"\2\2\2\u00dd\u00de\t\27\2\2\u00de8\3\2\2\2\u00df\u00e0\t\30\2\2\u00e0"+
		":\3\2\2\2\u00e1\u00e2\t\31\2\2\u00e2<\3\2\2\2\u00e3\u00e4\t\32\2\2\u00e4"+
		">\3\2\2\2\u00e5\u00e6\t\33\2\2\u00e6@\3\2\2\2\u00e7\u00e8\t\34\2\2\u00e8"+
		"B\3\2\2\2\u00e9\u00ea\t\35\2\2\u00eaD\3\2\2\2\u00eb\u00ec\t\36\2\2\u00ec"+
		"F\3\2\2\2\u00ed\u00ee\t\37\2\2\u00eeH\3\2\2\2\u00ef\u00f0\t \2\2\u00f0"+
		"J\3\2\2\2\u00f1\u00f2\t!\2\2\u00f2L\3\2\2\2\u00f3\u00f4\t\"\2\2\u00f4"+
		"N\3\2\2\2\u00f5\u00f6\t#\2\2\u00f6P\3\2\2\2\u00f7\u00f8\t$\2\2\u00f8R"+
		"\3\2\2\2\23\2V`kptx~\u0082\u0084\u009d\u00a7\u00b1\u00b7\u00ba\u00be\u00c3"+
		"\6\b\2\2\3\r\2\3\r\3\3\r\4";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}