package com.evo.vcs.exception;

import com.evo.internal.object.Statement;

public class StatementException extends Exception {
	
	// error caused by start/end of transaction involving this statement and other Evo's auxiliary statements
	public static final String ERROR_CAUSE_TRANSACTION = "Transaction";
	// error caused by the statement itself
	public static final String ERROR_CAUSE_STATEMENT = "Statement";
	// error caused by statement made by Evo before or after the real statement
	public static final String ERROR_CAUSE_EVO = "Evo";
	
	private Statement statement;
	
	private String errorCause;
	
	public StatementException() {
		super();
	}
	
	public StatementException(Statement statement) {
		this(statement, null, StatementException.ERROR_CAUSE_STATEMENT);
	}
	
	public StatementException(Statement statement, Throwable cause) {
		this(statement, cause, StatementException.ERROR_CAUSE_STATEMENT);
	}
	
	public StatementException(Statement statement, Throwable cause, String errorCause) {
		super(cause);
		this.statement = statement;
		switch (errorCause) {
			case StatementException.ERROR_CAUSE_TRANSACTION:
			case StatementException.ERROR_CAUSE_STATEMENT:
			case StatementException.ERROR_CAUSE_EVO:
				this.errorCause = errorCause;
				break;
			default:
				this.errorCause = StatementException.ERROR_CAUSE_STATEMENT;
				break;
		}
	}
	
	public Statement getStatement() {
		return this.statement;
	}
	
	/**
	 * @return the cause of the statement having failed. Can be any constant ERROR_CAUSE_*
	 */
	public String getErrorCause() {
		return this.errorCause;
	}
}
