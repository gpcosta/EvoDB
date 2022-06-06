package com.evo.internal;

import com.evo.exception.SimpleException;
import com.evo.internal.history.MigrationInfo;
import com.evo.internal.rdbms.GetRDBMS;
import com.evo.internal.rdbms.operations.IOperationsPerRdbms;
import com.evo.vcs.exception.StatementException;
import com.evo.internal.object.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Database {
	
	private Project project;
	
	private IOperationsPerRdbms operations;
	
	public Database(Project project) throws SimpleException, SQLException {
		this.project = project;
		this.operations = new GetRDBMS(this.project.getDatabaseConnection(), this.project.getConfig())
				.getOperationsPerRdbms();
	}
	
	/**
	 * Executes all migrations between executeFrom, exclusively, and executeTo, inclusively
	 *
	 * @param executeFrom
	 * @param executeTo
	 * @param successEachMigrationCallback
	 * @param errorEachMigrationCallback
	 * @throws SimpleException
	 * @throws StatementException
	 */
	public void deployRegularMigrations(Migration executeFrom, Migration executeTo,
	                                    Consumer<Migration> successEachMigrationCallback,
	                                    BiConsumer<Migration, StatementException> errorEachMigrationCallback,
	                                    Consumer<Statement> successEachStatementCallback,
	                                    BiConsumer<Statement, StatementException> errorEachStatementCallback)
			throws SimpleException, StatementException {
		this.project.getMigrationHistoryTable().setMigrationAsTarget(executeTo);
		for (Migration migration : this.getRegularMigrationsToExecute(executeFrom, executeTo)) {
			try {
				migration.execute(this.project.getDatabaseConnection(), this.project.getMigrationHistoryTable(),
						successEachStatementCallback, errorEachStatementCallback);
				this.project.getMigrationHistoryTable().setMigrationAsCurrent(migration);
				if (successEachMigrationCallback != null)
					successEachMigrationCallback.accept(migration);
			} catch (StatementException e) {
				if (errorEachMigrationCallback != null)
					errorEachMigrationCallback.accept(migration, e);
				throw e;
			}
		}
		this.project.getMigrationHistoryTable().cleanTargetMigration(executeTo);
	}
	
	/**
	 * Obtains all migrations between executeFrom, exclusively, and executeTo, inclusively
	 *
	 * @param executeFrom
	 * @param executeTo
	 * @return
	 * @throws SimpleException
	 */
	private List<Migration> getRegularMigrationsToExecute(Migration executeFrom, Migration executeTo)
			throws SimpleException {
		List<MigrationInfo> migrationsInfo = this.project.getMigrationHistoryTable()
				.getMigrationsInfoBetween(
						executeFrom == null ? null : executeFrom.getVersion(),
						executeTo.getVersion()
				);
		List<Migration> result = new ArrayList<>();
		for (MigrationInfo migrationInfo : migrationsInfo) {
			if (!migrationInfo.getMigrationType().equals(Migration.Type.REGULAR))
				continue;
			if (migrationInfo.getVersion().isEqualThan(executeTo.getVersion()))
				result.add(executeTo);
			else
				result.add(Migration.load(this.project, migrationInfo.getFilename()));
		}
		return result;
	}
}
