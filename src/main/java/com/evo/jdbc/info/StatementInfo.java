package com.evo.jdbc.info;

import java.sql.Connection;

/**
 * Information about Statement
 */
public class StatementInfo {
	
	private final Connection connection;
	
	private String statement;
	
	public StatementInfo(Connection connection, String statement) {
		this.connection = connection;
		this.statement = statement;
	}
	
	public Connection getConnection() {
		return this.connection;
	}
	
	public String getStatement() {
		return this.statement;
	}
}
