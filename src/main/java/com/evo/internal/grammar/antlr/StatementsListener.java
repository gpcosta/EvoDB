// Generated from .\Statements.g4 by ANTLR 4.9.3
package com.evo.internal.grammar.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link StatementsParser}.
 */
public interface StatementsListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link StatementsParser#statements}.
	 * @param ctx the parse tree
	 */
	void enterStatements(StatementsParser.StatementsContext ctx);
	/**
	 * Exit a parse tree produced by {@link StatementsParser#statements}.
	 * @param ctx the parse tree
	 */
	void exitStatements(StatementsParser.StatementsContext ctx);
	/**
	 * Enter a parse tree produced by {@link StatementsParser#statementOrComment}.
	 * @param ctx the parse tree
	 */
	void enterStatementOrComment(StatementsParser.StatementOrCommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link StatementsParser#statementOrComment}.
	 * @param ctx the parse tree
	 */
	void exitStatementOrComment(StatementsParser.StatementOrCommentContext ctx);
	/**
	 * Enter a parse tree produced by {@link StatementsParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(StatementsParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link StatementsParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(StatementsParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link StatementsParser#comment}.
	 * @param ctx the parse tree
	 */
	void enterComment(StatementsParser.CommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link StatementsParser#comment}.
	 * @param ctx the parse tree
	 */
	void exitComment(StatementsParser.CommentContext ctx);
}