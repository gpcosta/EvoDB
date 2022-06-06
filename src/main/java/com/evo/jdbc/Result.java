package com.evo.jdbc;

import java.sql.ResultSet;

/**
 * Wrapper for the result returned. Important when we want to abstract the results that came from the database
 * Result can be:
 *      - ResultSet - rows that came from the execution of a query
 *      - UpdatedCount - number of rows that were manipulated (can come from INSERT, UPDATE or DELETE)
 *      - Null - when a result has nothing to return. It is important because there is a return of
 *               an object similar to a NullObject and different a simple null
 */
public class Result {
	
	private boolean isNull;
	
	private boolean isResultSet;
	
	private boolean isUpdatedCount;
	
	private ResultSet resultSet;
	
	private int updatedCount;
	
	protected Result(boolean isNull, boolean isResultSet, boolean isUpdatedCount, ResultSet resultSet,
	                 int updatedCount) {
		this.isNull = isNull;
		this.isResultSet = isResultSet;
		this.isUpdatedCount = isUpdatedCount;
		this.resultSet = resultSet;
		this.updatedCount = updatedCount;
	}
	
	public static Result generateNullResult() {
		return new Result(true, false, false, null, 0);
	}
	
	public static Result generateResultSetResult(ResultSet resultSet) {
		return new Result(false, true, false, resultSet, 0);
	}
	
	public static Result generateUpdatedCountResult(int updatedCount) {
		return new Result(false, false, true, null, updatedCount);
	}
	
	public boolean isNull() {
		return this.isNull;
	}
	
	public boolean isResultSet() {
		return this.isResultSet;
	}
	
	public boolean isUpdatedCount() {
		return this.isUpdatedCount;
	}
	
	public ResultSet getResultSet() {
		if (!this.isResultSet())
			throw new UnsupportedOperationException("There is no result set.");
		return this.resultSet;
	}
	
	public int getUpdatedCount() {
		if (!this.isUpdatedCount())
			throw new UnsupportedOperationException("There is no updated count.");
		return this.updatedCount;
	}
}
