package com.evo.jdbc.info;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Information about PreparedStatement
 */
public class PreparedStatementInfo extends StatementInfo {
	
	private Map<Integer, Value> parameters;
	
	public PreparedStatementInfo(Connection connection, String query, Map<Integer, Value> parameters) {
		super(connection, query);
		this.parameters = parameters;
	}
	
	public PreparedStatementInfo(Connection connection, String query) {
		this(connection, query, new HashMap<>());
	}
	
	public Map<Integer, Value> getParameters() {
		return this.parameters;
	}
	
	/**
	 * Set a value for the parameter in the given position
	 * @param position the position of the parameter (starts in 1 and not in 0, as a parameter in PreparedStatement)
	 * @param parameterValue the value of the parameter
	 * @return this
	 */
	public PreparedStatementInfo addParameter(final int position, Value parameterValue) {
		this.parameters.put(position, parameterValue);
		return this;
	}
	
	/**
	 * Set all parameters values
	 * @param parametersValues map with all the parameters values
	 * @return this
	 */
	public PreparedStatementInfo setParameters(Map<Integer, Value> parametersValues) {
		this.parameters = parametersValues;
		return this;
	}
	
	public String getCommandWithValues() throws SQLException {
		final StringBuilder sb = new StringBuilder();
		final String command = getStatement();
		
		int currentParam = 1;
		for (char c : command.toCharArray()) {
			if (c == '?') {
				Value value = this.parameters.get(currentParam);
				if (value == null)
					throw new SQLException("There is no parameter for position " + currentParam);
				
				sb.append(value); // value.toString() is called
				currentParam++;
			} else {
				sb.append(c);
			}
		}
		
		return sb.toString();
	}
}
