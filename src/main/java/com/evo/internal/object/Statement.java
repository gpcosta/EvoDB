package com.evo.internal.object;

import com.evo.vcs.exception.StatementException;
import org.antlr.v4.runtime.ParserRuleContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;

public class Statement {
	
	private boolean isComment;
	
	private String stmt;
	
	/**
	 * To use internally - it is result of the iteration of all statements and comments, but starts at 1 and not 0
	 */
	private int migrationPosition;
	
	/**
	 * To provide to the developer in order to tell which statement really is inside migration
	 */
	private int line;
	
	/**
	 * To provide to the developer in order to tell which statement really is inside migration
	 */
	private int firstCharPositionInLine;
	
	public Statement(boolean isComment, String stmt, int migrationPosition, int line, int firstCharPositionInLine) {
		this.isComment = isComment;
		this.stmt = stmt.trim();
		this.migrationPosition = migrationPosition;
		this.line = line;
		this.firstCharPositionInLine = firstCharPositionInLine;
	}
	
	public boolean isComment() {
		return this.isComment;
	}
	
	public String getStatement() {
		if (!this.isComment && !this.stmt.endsWith(";"))
			return this.stmt + ";";
		return this.stmt;
	}
	
	public String getRawStatement() {
		return this.stmt;
	}
	
	public int getMigrationPosition() {
		return this.migrationPosition;
	}
	
	public int getLine() {
		return this.line;
	}
	
	public int getFirstCharPositionInLine() {
		return this.firstCharPositionInLine;
	}
	
	public boolean isDML() {
		String upperCaseStmt = this.stmt.toUpperCase(Locale.ROOT);
		return
				upperCaseStmt.startsWith("INSERT") ||
				upperCaseStmt.startsWith("SELECT") ||
				upperCaseStmt.startsWith("UPDATE") ||
				upperCaseStmt.startsWith("DELETE");
	}
	
	public void execute(Connection conn) throws StatementException {
		try (PreparedStatement stmt = conn.prepareStatement(this.stmt)) {
			stmt.execute();
		} catch (SQLException e){
			throw new StatementException(this, e);
		}
	}
	
	@Override
	public String toString() {
		return this.getStatement();
	}
}
