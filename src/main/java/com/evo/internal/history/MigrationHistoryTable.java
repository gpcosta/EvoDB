package com.evo.internal.history;

import com.evo.exception.SimpleException;
import com.evo.internal.object.Config;
import com.evo.internal.object.Migration;
import com.evo.internal.object.Statement;
import com.evo.internal.object.Version;
import com.evo.internal.rdbms.GetRDBMS;
import com.evo.internal.rdbms.operations.IOperationsPerRdbms;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MigrationHistoryTable {
	
	public static String TABLE_NAME = "__evo_migration";
	
	public static String COL_MIGRATION_TYPE = "migration_type";
	public static String COL_VERSION = "version";
	public static String COL_DESCRIPTION = "description";
	public static String COL_FILENAME = "filename";
	public static String COL_CHECKSUM = "checksum";
	public static String COL_EXECUTED_AT = "executed_at";
	public static String COL_IS_CURRENT = "is_current";
	public static String COL_IS_TARGET = "is_target";
	public static String COL_LAST_EXECUTED_STMT_POS = "last_executed_stmt_pos";
	public static String COL_LAST_EXECUTED_STMT = "last_executed_stmt";
	public static String COL_LAST_EXECUTED_STMT_LINE = "last_executed_stmt_line";
	public static String COL_LAST_EXECUTED_STMT_FIRST_CHAR = "last_executed_stmt_first_char";
	public static String COL_SUCCESS = "success";
	
	public static String ALL_COLS =
			COL_MIGRATION_TYPE + ", " +
			COL_VERSION + ", " +
			COL_DESCRIPTION + ", " +
			COL_FILENAME + ", " +
			COL_CHECKSUM + ", " +
			COL_EXECUTED_AT + ", " +
			COL_IS_CURRENT + ", " +
			COL_IS_TARGET + ", " +
			COL_LAST_EXECUTED_STMT_POS + ", " +
			COL_LAST_EXECUTED_STMT_LINE + ", " +
			COL_LAST_EXECUTED_STMT_FIRST_CHAR + ", " +
			COL_LAST_EXECUTED_STMT + ", " +
			COL_SUCCESS;
	
	private Connection conn;
	
	private IOperationsPerRdbms operations;
	
	private String defaultSchema;
	
	private MigrationHistoryTable(Connection conn, IOperationsPerRdbms operations, String defaultSchema) {
		this.conn = conn;
		this.operations = operations;
		this.defaultSchema = defaultSchema;
	}
	
	public MigrationInfo getHeadMigrationInfo() throws SimpleException {
		try {
			return this.operations.getHeadMigrationInfo(this.conn, this.defaultSchema);
		} catch (SQLException e) {
			throw new SimpleException("There was an error while getting current migration from " +
					"Migration History Table.", e);
		}
	}
	
	public MigrationInfo getCurrentMigrationInfo() throws SimpleException {
		try {
			return this.operations.getCurrentMigrationInfo(this.conn, this.defaultSchema);
		} catch (SQLException e) {
			throw new SimpleException("There was an error while getting current migration from " +
					"Migration History Table.", e);
		}
	}
	
	public MigrationInfo getSpecificMigrationInfo(String migrationType, Version version) throws SimpleException {
		try {
			return this.operations.getSpecificMigrationInfo(this.conn, this.defaultSchema, migrationType, version);
		} catch (SQLException e) {
			throw new SimpleException("There was an error while getting migration from " +
					"Migration History Table with version '" + version + "'.", e);
		}
	}
	
	public List<MigrationInfo> getAllMigrationsInfo() throws SimpleException {
		try {
			return this.operations.getAllMigrationsInfo(this.conn, this.defaultSchema);
		} catch (SQLException e) {
			throw new SimpleException("There was an error while getting all migrations from " +
					"Migration History Table.", e);
		}
	}
	
	public List<MigrationInfo> getAllMigrationsInfoByType(String migrationType) throws SimpleException {
		try {
			return this.operations.getAllMigrationsInfoByType(this.conn, this.defaultSchema, migrationType);
		} catch (SQLException e) {
			throw new SimpleException("There was an error while getting all migrations from " +
					"Migration History Table.", e);
		}
	}
	
	/**
	 * Get MigrationInfos between migration with versionFrom, exclusively, to migration with versionTo, inclusively
	 *
	 * @param versionFrom
	 * @param versionTo
	 * @return
	 * @throws SimpleException
	 */
	public List<MigrationInfo> getMigrationsInfoBetween(Version versionFrom, Version versionTo)
			throws SimpleException {
		try {
			return this.operations.getMigrationsInfoBetween(this.conn, this.defaultSchema, versionFrom, versionTo);
		} catch (SQLException e) {
			throw new SimpleException("There was an error while getting migrations between " +
					"version '" + versionFrom + "' (exclusively) to version '" + versionTo + "' (inclusively) from " +
					"Migration History Table.", e);
		}
	}
	
	public MigrationInfo addMigrationToHistory(Migration migration) throws SimpleException {
		try {
			this.operations.addMigrationToHistory(this.conn, this.defaultSchema, migration);
			return MigrationInfo.build(migration);
		} catch (SQLException e) {
			throw new SimpleException("There was an error while getting migration from " +
					"Migration History Table with version '" + migration.getVersion() + "'.", e);
		}
	}
	
	public void removeMigrationFromHistory(MigrationInfo migration) throws SimpleException {
		try {
			this.operations.removeMigrationFromHistory(this.conn, this.defaultSchema, migration);
		} catch (SQLException e) {
			throw new SimpleException("There was an error while getting migration with version " +
					"'" + migration.getVersion() + "' from Migration History Table.", e);
		}
	}
	
	public void setNewInfoForSpecificVersion(Migration migration) throws SimpleException {
		try {
			this.operations.setNewInfoForSpecificVersion(this.conn, this.defaultSchema, migration);
		} catch (SQLException e) {
			throw new SimpleException("There was an error while updating info of migration with version " +
					"'" + migration.getVersion() + "' in Migration History Table.", e);
		}
	}
	
	public void setMigrationAsCurrent(Migration migration) throws SimpleException {
		try {
			this.operations.setMigrationAsCurrent(this.conn, this.defaultSchema, migration);
		} catch (SQLException e) {
			throw new SimpleException("There was an error while updating migration with version " +
					"'" + migration.getVersion().getVersion() + "' as current migration in Migration History Table.", e);
		}
	}
	
	public void setMigrationAsTarget(Migration migration) throws SimpleException {
		try {
			this.operations.setMigrationAsTarget(this.conn, this.defaultSchema, migration);
		} catch (SQLException e) {
			throw new SimpleException("There was an error while updating migration with version " +
					"'" + migration.getVersion().getVersion() + "' as target migration in Migration History Table.", e);
		}
	}
	
	public void setStatementAsLastExecuted(Migration migration, Statement statement) throws SimpleException {
		try {
			this.operations.setStatementAsLastExecuted(this.conn, this.defaultSchema, migration, statement);
		} catch (SQLException e) {
			throw new SimpleException("There was an error while updating migration with version " +
					"'" + migration.getVersion().getVersion() + "' with the last executed statement " +
					"(" + statement.getLine() + ":" + statement.getFirstCharPositionInLine() + "): " +
					statement.getRawStatement(), e);
		}
	}
	
	public void cleanTargetMigration(Migration migration) throws SimpleException {
		try {
			this.operations.cleanTargetMigration(this.conn, this.defaultSchema, migration);
		} catch (SQLException e) {
			throw new SimpleException("There was an error while updating migration with version " +
					"'" + migration.getVersion().getVersion() + "' from Migration History Table.", e);
		}
	}
	
	public boolean thereIsThisVersionInHistory(String migrationType, Version version) throws SimpleException {
		return this.getSpecificMigrationInfo(migrationType, version) != null;
	}
	
	public static MigrationHistoryTable build(Connection conn, Config config) throws SimpleException {
		try {
			IOperationsPerRdbms operations = new GetRDBMS(conn, config).getOperationsPerRdbms();
			operations.createEvoMigrationHistoryTable(conn, config.getDefaultSchema());
			return new MigrationHistoryTable(conn, operations, config.getDefaultSchema());
		} catch (SQLException e) {
			throw new SimpleException("There was an error while creating Migration History Table.", e);
		}
	}
	
	public static MigrationHistoryTable load(Connection conn, Config config) throws SimpleException {
		return new MigrationHistoryTable(
				conn,
				new GetRDBMS(conn, config).getOperationsPerRdbms(),
				config.getDefaultSchema()
		);
	}
	
	public static boolean exist(Connection conn, Config config) throws SimpleException {
		try {
			return new GetRDBMS(conn, config).getOperationsPerRdbms().existEvoMigrationHistoryTable(conn,
					config.getDefaultSchema());
		} catch (SQLException e) {
			throw new SimpleException("There was an error while connecting to the database.", e);
		}
	}
}
