package com.evo.jdbc;

import com.evo.jdbc.info.PreparedStatementInfo;
import com.evo.jdbc.info.StatementInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EventListenerImpl extends EventListener {
	
	private Driver driver;
	
	protected EventListenerImpl(Driver driver) {
		this.driver = driver;
	}
	
	@Override
	public Result onAfterExecuteSuccess(StatementInfo statementInfo, boolean executeResult) throws SQLException {
		this.driver.storeStatementsIntoStagedMigration(statementInfo);
		return null;
	}
	
	@Override
	public Result onAfterExecuteSuccess(PreparedStatementInfo preparedStatementInfo, boolean executeResult) throws SQLException {
		this.driver.storeStatementsIntoStagedMigration(preparedStatementInfo);
		return null;
	}
	
	@Override
	public Result onAfterExecuteQuerySuccess(StatementInfo statementInfo, ResultSet executeResult) throws SQLException {
		this.driver.storeStatementsIntoStagedMigration(statementInfo);
		return null;
	}
	
	@Override
	public Result onAfterExecuteQuerySuccess(PreparedStatementInfo preparedStatementInfo, ResultSet executeResult) throws SQLException {
		this.driver.storeStatementsIntoStagedMigration(preparedStatementInfo);
		return null;
	}
	
	@Override
	public Result onAfterExecuteUpdateSuccess(StatementInfo statementInfo, int executeResult) throws SQLException {
		this.driver.storeStatementsIntoStagedMigration(statementInfo);
		return null;
	}
	
	@Override
	public Result onAfterExecuteUpdateSuccess(PreparedStatementInfo preparedStatementInfo, int executeResult) throws SQLException {
		this.driver.storeStatementsIntoStagedMigration(preparedStatementInfo);
		return null;
	}
	
	@Override
	public List<Result> onAfterExecuteBatchSuccess(List<? extends StatementInfo> statementsInfo, int[] executeResult) throws SQLException {
		for (StatementInfo statementInfo : statementsInfo)
			this.driver.storeStatementsIntoStagedMigration(statementInfo);
		return null;
	}
}
