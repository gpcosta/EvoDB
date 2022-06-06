package com.evo.internal.rdbms.exception;

public class SqlDumperException extends Exception {
	
	public SqlDumperException() {
		this(null);
	}
	
	public SqlDumperException(Throwable cause) {
		super(
				"There was an error while reading the database dump.",
				cause
		);
	}
}
