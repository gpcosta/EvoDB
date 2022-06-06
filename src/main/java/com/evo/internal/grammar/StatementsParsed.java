package com.evo.internal.grammar;

import com.evo.internal.grammar.antlr.StatementsLexer;
import com.evo.internal.grammar.antlr.StatementsParser;
import com.evo.internal.object.Statement;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class StatementsParsed {
	
	private CodePointCharStream charStream;
	
	private StatementsLexer lexer;
	
	private CommonTokenStream tokenStream;
	
	private StatementsParser parser;
	
	private StatementsParser.StatementsContext topParserRule;
	
	public StatementsParsed(Path statementsPath) throws IOException {
		this(new String(Files.readAllBytes(statementsPath), StandardCharsets.UTF_8));
	}
	
	public StatementsParsed(String statements) {
		this.charStream = CharStreams.fromString(statements);
		this.lexer = new StatementsLexer(this.getCharStream());
		this.lexer.removeErrorListeners();
		this.lexer.addErrorListener(new BaseErrorListener() {
			@Override
			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) throws ParseCancellationException {
				throw new ParseCancellationException(msg);
			}
		});
		
		this.tokenStream = new CommonTokenStream(this.getLexer());
		this.parser = new StatementsParser(this.getTokenStream());
		this.parser.removeErrorListeners();
		this.parser.addErrorListener(new BaseErrorListener() {
			@Override
			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) throws ParseCancellationException {
				throw new ParseCancellationException(msg);
			}
		});
		
		this.topParserRule = this.getParser().statements();
	}
	
	protected StatementsParsed setCharStream(CodePointCharStream charStream) {
		this.charStream = charStream;
		return this;
	}
	
	public CodePointCharStream getCharStream() {
		return this.charStream;
	}
	
	protected StatementsParsed setLexer(StatementsLexer lexer) {
		this.lexer = lexer;
		return this;
	}
	public StatementsLexer getLexer() {
		return this.lexer;
	}
	
	public CommonTokenStream getTokenStream() {
		return this.tokenStream;
	}
	
	protected StatementsParsed setTokenStream(CommonTokenStream tokenStream) {
		this.tokenStream = tokenStream;
		return this;
	}
	
	protected StatementsParsed setParser(StatementsParser parser) {
		this.parser = parser;
		return this;
	}
	
	public StatementsParser getParser() {
		return this.parser;
	}
	
	protected StatementsParsed setTopParserRule(StatementsParser.StatementsContext parserRuleContext) {
		this.topParserRule = parserRuleContext;
		return this;
	}
	
	public StatementsParser.StatementsContext getTopParserRule() {
		return this.topParserRule;
	}
	
	public String getOriginalStatement(ParserRuleContext statementContext) {
		return this.charStream.getText(new Interval(
				statementContext.getStart().getStartIndex(),
				statementContext.getStop().getStopIndex()
		));
	}
	
	public int getLine(ParserRuleContext statementContext) {
		return statementContext.getStart().getLine();
	}
	
	public int getFirstCharPositionInLine(ParserRuleContext statementContext) {
		return statementContext.getStart().getCharPositionInLine();
	}
	
	public List<Statement> getStatementsOrComments() {
		List<Statement> result = new ArrayList<>();
		List<StatementsParser.StatementOrCommentContext> statementsOrComments =
				this.getTopParserRule().statementOrComment();
		for (int i = 0, len = statementsOrComments.size(); i < len;) {
			StatementsParser.StatementOrCommentContext context = statementsOrComments.get(i);
			i++;
			result.add(new Statement(context.statement() == null, this.getOriginalStatement(context),
					i, this.getLine(context), this.getFirstCharPositionInLine(context)));
		}
		return result;
	}
}
