package com.evo.internal.object;

import com.evo.vcs.exception.StatementException;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

abstract public class AbstractFileWithStatements implements IFileWithStatements {
	
	private List<Statement> statements;
	
	private Path fileAbsolutePath;
	
	public AbstractFileWithStatements(Path fileAbsolutePath, List<Statement> statements) {
		this.statements = statements;
		this.fileAbsolutePath = fileAbsolutePath;
	}
	
	public List<Statement> getStatements() {
		return this.statements;
	}
	
	protected void appendStatement(Statement statement) {
		this.statements.add(statement);
	}
	
	public String getFilename() {
		return this.fileAbsolutePath.getFileName().toString();
	}
	
	public Path getFileAbsolutePath() {
		return this.fileAbsolutePath;
	}
	
	public void execute(Connection conn, Consumer<Statement> successEachStatementCallback,
	                    BiConsumer<Statement, StatementException> errorEachStatementCallback) throws StatementException {
		for (Statement stmt : this.getStatements()) {
			try {
				stmt.execute(conn);
				if (successEachStatementCallback != null)
					successEachStatementCallback.accept(stmt);
			} catch (StatementException e) {
				if (errorEachStatementCallback != null)
					errorEachStatementCallback.accept(stmt, e);
				throw e;
			}
		}
	}
}
