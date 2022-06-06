package com.evo.jdbc;

import com.evo.jdbc.info.PreparedStatementInfo;
import com.evo.jdbc.info.StatementInfo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Class that allows to put callbacks at the several moments of the Driver working flow
 */
public abstract class EventListener {
	public void onBeforeGetConnection() throws SQLException {}
	public void onAfterGetConnectionSuccess(Connection connection) throws SQLException {}
	public void onAfterGetConnectionError(SQLException e) {}
	
	public void onBeforePreparedStatementSet(PreparedStatementInfo preparedStatementInfo, int parameterIndex, Object value) throws SQLException {}
	public void onAfterPreparedStatementSetSuccess(PreparedStatementInfo preparedStatementInfo, int parameterIndex, Object value) throws SQLException {}
	public void onAfterPreparedStatementSetError(PreparedStatementInfo preparedStatementInfo, int parameterIndex, Object value, SQLException e) {}
	
	public Result onBeforeExecute(StatementInfo statementInfo) throws SQLException { return null; }
	public Result onAfterExecuteSuccess(StatementInfo statementInfo, boolean executeResult) throws SQLException { return null; }
	public Result onAfterExecuteError(StatementInfo statementInfo, SQLException e) { return null; }
	
	public Result onBeforeExecute(PreparedStatementInfo preparedStatementInfo) throws SQLException { return null; }
	public Result onAfterExecuteSuccess(PreparedStatementInfo preparedStatementInfo, boolean executeResult) throws SQLException { return null; }
	public Result onAfterExecuteError(PreparedStatementInfo preparedStatementInfo, SQLException e) { return null; }
	
	public Result onBeforeExecuteQuery(StatementInfo statementInfo) throws SQLException { return null; }
	public Result onAfterExecuteQuerySuccess(StatementInfo statementInfo, ResultSet executeResult) throws SQLException { return null; }
	public Result onAfterExecuteQueryError(StatementInfo statementInfo, SQLException e) { return null; }
	
	public Result onBeforeExecuteQuery(PreparedStatementInfo preparedStatementInfo) throws SQLException { return null; }
	public Result onAfterExecuteQuerySuccess(PreparedStatementInfo preparedStatementInfo, ResultSet executeResult) throws SQLException { return null; }
	public Result onAfterExecuteQueryError(PreparedStatementInfo preparedStatementInfo, SQLException e) { return null; }
	
	public Result onBeforeExecuteUpdate(StatementInfo statementInfo) throws SQLException { return null; }
	public Result onAfterExecuteUpdateSuccess(StatementInfo statementInfo, int executeResult) throws SQLException { return null; }
	public Result onAfterExecuteUpdateError(StatementInfo statementInfo, SQLException e) { return null; }
	
	public Result onBeforeExecuteUpdate(PreparedStatementInfo preparedStatementInfo) throws SQLException { return null; }
	public Result onAfterExecuteUpdateSuccess(PreparedStatementInfo preparedStatementInfo, int executeResult) throws SQLException { return null; }
	public Result onAfterExecuteUpdateError(PreparedStatementInfo preparedStatementInfo, SQLException e) { return null; }
	
	public List<Result> onBeforeExecuteBatch(List<? extends StatementInfo> statementsInfo) throws SQLException { return null; }
	public List<Result> onAfterExecuteBatchSuccess(List<? extends StatementInfo> statementsInfo, int[] executeResult) throws SQLException { return null; }
	public List<Result> onAfterExecuteBatchError(List<? extends StatementInfo> statementsInfo, SQLException e) { return null; }
}
