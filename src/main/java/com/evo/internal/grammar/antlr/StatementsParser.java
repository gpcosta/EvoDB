// Generated from .\Statements.g4 by ANTLR 4.9.3
package com.evo.internal.grammar.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class StatementsParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		WHITE_SPACE=1, COMMENT_MULTILINE=2, COMMENT_SINGLE_LINE=3, COLON=4, SEMICOLON=5, 
		SINGLE_QUOTE=6, SINGLE_QUOTE_ESC=7, DOUBLE_QUOTE=8, DOUBLE_QUOTE_ESC=9, 
		BACK_TICK=10, BACK_TICK_ESC=11, PHRASE=12, NEW_LINE=13, WORD=14;
	public static final int
		RULE_statements = 0, RULE_statementOrComment = 1, RULE_statement = 2, 
		RULE_comment = 3;
	private static String[] makeRuleNames() {
		return new String[] {
			"statements", "statementOrComment", "statement", "comment"
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

	@Override
	public String getGrammarFileName() { return "Statements.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public StatementsParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class StatementsContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(StatementsParser.EOF, 0); }
		public List<StatementOrCommentContext> statementOrComment() {
			return getRuleContexts(StatementOrCommentContext.class);
		}
		public StatementOrCommentContext statementOrComment(int i) {
			return getRuleContext(StatementOrCommentContext.class,i);
		}
		public StatementsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statements; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StatementsListener ) ((StatementsListener)listener).enterStatements(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StatementsListener ) ((StatementsListener)listener).exitStatements(this);
		}
	}

	public final StatementsContext statements() throws RecognitionException {
		StatementsContext _localctx = new StatementsContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_statements);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(11);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << COMMENT_MULTILINE) | (1L << COMMENT_SINGLE_LINE) | (1L << PHRASE) | (1L << WORD))) != 0)) {
				{
				{
				setState(8);
				statementOrComment();
				}
				}
				setState(13);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(14);
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

	public static class StatementOrCommentContext extends ParserRuleContext {
		public CommentContext comment() {
			return getRuleContext(CommentContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public StatementOrCommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statementOrComment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StatementsListener ) ((StatementsListener)listener).enterStatementOrComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StatementsListener ) ((StatementsListener)listener).exitStatementOrComment(this);
		}
	}

	public final StatementOrCommentContext statementOrComment() throws RecognitionException {
		StatementOrCommentContext _localctx = new StatementOrCommentContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_statementOrComment);
		try {
			setState(18);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(16);
				comment();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(17);
				statement();
				}
				break;
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

	public static class StatementContext extends ParserRuleContext {
		public TerminalNode SEMICOLON() { return getToken(StatementsParser.SEMICOLON, 0); }
		public List<TerminalNode> WORD() { return getTokens(StatementsParser.WORD); }
		public TerminalNode WORD(int i) {
			return getToken(StatementsParser.WORD, i);
		}
		public List<TerminalNode> PHRASE() { return getTokens(StatementsParser.PHRASE); }
		public TerminalNode PHRASE(int i) {
			return getToken(StatementsParser.PHRASE, i);
		}
		public List<TerminalNode> COMMENT_MULTILINE() { return getTokens(StatementsParser.COMMENT_MULTILINE); }
		public TerminalNode COMMENT_MULTILINE(int i) {
			return getToken(StatementsParser.COMMENT_MULTILINE, i);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StatementsListener ) ((StatementsListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StatementsListener ) ((StatementsListener)listener).exitStatement(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_statement);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(21); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(20);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << COMMENT_MULTILINE) | (1L << PHRASE) | (1L << WORD))) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(23); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(26);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMICOLON) {
				{
				setState(25);
				match(SEMICOLON);
				}
			}

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

	public static class CommentContext extends ParserRuleContext {
		public TerminalNode COMMENT_MULTILINE() { return getToken(StatementsParser.COMMENT_MULTILINE, 0); }
		public TerminalNode SEMICOLON() { return getToken(StatementsParser.SEMICOLON, 0); }
		public TerminalNode COMMENT_SINGLE_LINE() { return getToken(StatementsParser.COMMENT_SINGLE_LINE, 0); }
		public CommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StatementsListener ) ((StatementsListener)listener).enterComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StatementsListener ) ((StatementsListener)listener).exitComment(this);
		}
	}

	public final CommentContext comment() throws RecognitionException {
		CommentContext _localctx = new CommentContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_comment);
		int _la;
		try {
			setState(36);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COMMENT_MULTILINE:
				enterOuterAlt(_localctx, 1);
				{
				setState(28);
				match(COMMENT_MULTILINE);
				setState(30);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEMICOLON) {
					{
					setState(29);
					match(SEMICOLON);
					}
				}

				}
				break;
			case COMMENT_SINGLE_LINE:
				enterOuterAlt(_localctx, 2);
				{
				setState(32);
				match(COMMENT_SINGLE_LINE);
				setState(34);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEMICOLON) {
					{
					setState(33);
					match(SEMICOLON);
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\20)\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\3\2\7\2\f\n\2\f\2\16\2\17\13\2\3\2\3\2\3\3\3\3\5\3"+
		"\25\n\3\3\4\6\4\30\n\4\r\4\16\4\31\3\4\5\4\35\n\4\3\5\3\5\5\5!\n\5\3\5"+
		"\3\5\5\5%\n\5\5\5\'\n\5\3\5\2\2\6\2\4\6\b\2\3\5\2\4\4\16\16\20\20\2+\2"+
		"\r\3\2\2\2\4\24\3\2\2\2\6\27\3\2\2\2\b&\3\2\2\2\n\f\5\4\3\2\13\n\3\2\2"+
		"\2\f\17\3\2\2\2\r\13\3\2\2\2\r\16\3\2\2\2\16\20\3\2\2\2\17\r\3\2\2\2\20"+
		"\21\7\2\2\3\21\3\3\2\2\2\22\25\5\b\5\2\23\25\5\6\4\2\24\22\3\2\2\2\24"+
		"\23\3\2\2\2\25\5\3\2\2\2\26\30\t\2\2\2\27\26\3\2\2\2\30\31\3\2\2\2\31"+
		"\27\3\2\2\2\31\32\3\2\2\2\32\34\3\2\2\2\33\35\7\7\2\2\34\33\3\2\2\2\34"+
		"\35\3\2\2\2\35\7\3\2\2\2\36 \7\4\2\2\37!\7\7\2\2 \37\3\2\2\2 !\3\2\2\2"+
		"!\'\3\2\2\2\"$\7\5\2\2#%\7\7\2\2$#\3\2\2\2$%\3\2\2\2%\'\3\2\2\2&\36\3"+
		"\2\2\2&\"\3\2\2\2\'\t\3\2\2\2\t\r\24\31\34 $&";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}