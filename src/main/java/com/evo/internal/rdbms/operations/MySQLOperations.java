package com.evo.internal.rdbms.operations;

import com.evo.exception.SimpleException;
import com.evo.internal.history.MigrationInfo;
import com.evo.internal.history.MigrationHistoryTable;
import com.evo.internal.object.Migration;
import com.evo.internal.object.Statement;
import com.evo.internal.object.Version;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQLOperations implements IOperationsPerRdbms {
	
	@Override
	public void loadSourceFile(Connection conn, Path filePath) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("SOURCE " + filePath.toString())) {
			stmt.execute();
		}
	}
	
	@Override
	public int getCurrentPort(Connection conn) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("SHOW VARIABLES WHERE Variable_name = 'port'");
		     ResultSet rs = stmt.executeQuery()) {
			if (rs.next())
				return Integer.parseInt(rs.getString(2));
			throw new SQLException("There was an error obtaining the current port.");
		}
	}
	
	@Override
	public void createSchema(Connection conn, String schema) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("CREATE SCHEMA IF NOT EXISTS " + schema)) {
			stmt.execute();
		}
	}
	
	@Override
	public void dropSchema(Connection conn, String schema) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("DROP SCHEMA IF EXISTS " + schema)) {
			stmt.execute();
		}
	}
	
	@Override
	public void changeToSchema(Connection conn, String schema) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("USE " + schema)) {
			stmt.execute();
		}
	}
	
	@Override
	public boolean existSchema(Connection conn, String schema) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement("SELECT SCHEMA_NAME FROM information_schema.schemata WHERE SCHEMA_NAME = ?");
			stmt.setString(1, schema);
			rs = stmt.executeQuery();
			return rs.next();
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}
	
	@Override
	public boolean existTable(Connection conn, String schema, String table) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement("SELECT TABLE_SCHEMA, TABLE_NAME " +
					"FROM information_schema.TABLES " +
					"WHERE TABLE_SCHEMA = ? " +
					"AND TABLE_NAME = ?");
			stmt.setString(1, schema);
			stmt.setString(2, table);
			rs = stmt.executeQuery();
			return rs.next();
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}
	
	@Override
	public boolean existEvoMigrationHistoryTable(Connection conn, String schema) throws SQLException {
		return this.existTable(conn, schema, "__evo_migration");
	}
		
		@Override
	public void createEvoMigrationHistoryTable(Connection conn, String schema) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " +
					schema + ".`" + MigrationHistoryTable.TABLE_NAME + "` (" +
				" `" + MigrationHistoryTable.COL_MIGRATION_TYPE + "` CHAR(1) NOT NULL, " +
				" `" + MigrationHistoryTable.COL_VERSION + "` VARCHAR(20) NOT NULL, " +
				" `" + MigrationHistoryTable.COL_DESCRIPTION + "` VARCHAR(255) NOT NULL DEFAULT '', " +
				" `" + MigrationHistoryTable.COL_FILENAME + "` VARCHAR(255) NOT NULL, " +
				" `" + MigrationHistoryTable.COL_CHECKSUM + "` INT(11) NOT NULL, " +
				" `" + MigrationHistoryTable.COL_EXECUTED_AT + "` DATETIME NULL DEFAULT NULL, " +
				" `" + MigrationHistoryTable.COL_IS_CURRENT + "` TINYINT(1) NOT NULL DEFAULT 0, " +
				" `" + MigrationHistoryTable.COL_IS_TARGET + "` TINYINT(1) NOT NULL DEFAULT 0, " +
				" `" + MigrationHistoryTable.COL_LAST_EXECUTED_STMT_POS + "` INT(11) NOT NULL DEFAULT 0, " +
				" `" + MigrationHistoryTable.COL_LAST_EXECUTED_STMT + "` VARCHAR(1000) NOT NULL DEFAULT '', " +
				" `" + MigrationHistoryTable.COL_LAST_EXECUTED_STMT_LINE + "` INT(11) NOT NULL DEFAULT 0, " +
				" `" + MigrationHistoryTable.COL_LAST_EXECUTED_STMT_FIRST_CHAR + "` INT(11) NOT NULL DEFAULT 0, " +
				" `" + MigrationHistoryTable.COL_SUCCESS + "` TINYINT(1) NOT NULL DEFAULT 0, " +
				" INDEX `migration_type_version` (" +
					"`" + MigrationHistoryTable.COL_MIGRATION_TYPE + "`," +
					"`" + MigrationHistoryTable.COL_VERSION + "`" +
				"));")
		) {
			stmt.execute();
		}
	}
	
	@Override
	public MigrationInfo getHeadMigrationInfo(Connection conn, String schema) throws SimpleException, SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement("SELECT " + MigrationHistoryTable.ALL_COLS + " " +
					"FROM " + schema + "." + MigrationHistoryTable.TABLE_NAME + " " +
					"WHERE " + MigrationHistoryTable.COL_MIGRATION_TYPE + " = ? " +
					"AND " + MigrationHistoryTable.COL_VERSION + " = (" +
						"SELECT MAX(" + MigrationHistoryTable.COL_VERSION + ") " +
						"FROM " + schema + "." + MigrationHistoryTable.TABLE_NAME +
					")");
		     stmt.setString(1, Migration.Type.REGULAR);
		     rs = stmt.executeQuery();
			return MigrationInfo.build(rs);
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}
	
	@Override
	public MigrationInfo getCurrentMigrationInfo(Connection conn, String schema) throws SimpleException, SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("SELECT " + MigrationHistoryTable.ALL_COLS + " " +
				"FROM " + schema + "." + MigrationHistoryTable.TABLE_NAME + " " +
				"WHERE " + MigrationHistoryTable.COL_IS_CURRENT + " = 1");
		     ResultSet rs = stmt.executeQuery()
		) {
			return MigrationInfo.build(rs);
		}
	}
	
	@Override
	public MigrationInfo getSpecificMigrationInfo(Connection conn, String schema, String migrationType,
	                                              Version version)
			throws SimpleException, SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement("SELECT " + MigrationHistoryTable.ALL_COLS + " " +
					"FROM " + schema + "." + MigrationHistoryTable.TABLE_NAME + " " +
					"WHERE " + MigrationHistoryTable.COL_MIGRATION_TYPE + " = ? " +
					"AND " + MigrationHistoryTable.COL_VERSION + " = ?");
			stmt.setString(1, migrationType);
			stmt.setString(2, version.getStandardizedVersion());
			rs = stmt.executeQuery();
			return MigrationInfo.build(rs);
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}
	
	@Override
	public List<MigrationInfo> getAllMigrationsInfo(Connection conn, String schema)
			throws SimpleException, SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("SELECT " + MigrationHistoryTable.ALL_COLS + " " +
				"FROM " + schema + "." + MigrationHistoryTable.TABLE_NAME);
		     ResultSet rs = stmt.executeQuery()
		) {
			List<MigrationInfo> result = new ArrayList<>();
			MigrationInfo migrationInfo;
			while ((migrationInfo = MigrationInfo.build(rs)) != null)
				result.add(migrationInfo);
			return result;
		}
	}
	
	@Override
	public List<MigrationInfo> getAllMigrationsInfoByType(Connection conn, String schema, String migrationType)
			throws SimpleException, SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement("SELECT " + MigrationHistoryTable.ALL_COLS + " " +
					"FROM " + schema + "." + MigrationHistoryTable.TABLE_NAME + " " +
					"WHERE " + MigrationHistoryTable.COL_MIGRATION_TYPE + " = ?");
			stmt.setString(1, migrationType);
			rs = stmt.executeQuery();
			List<MigrationInfo> result = new ArrayList<>();
			MigrationInfo migrationInfo;
			while ((migrationInfo = MigrationInfo.build(rs)) != null)
				result.add(migrationInfo);
			return result;
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}
	
	@Override
	public List<MigrationInfo> getMigrationsInfoBetween(Connection conn, String schema, Version versionFrom, Version versionTo) throws SimpleException, SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement("SELECT " + MigrationHistoryTable.ALL_COLS + " " +
					"FROM " + schema + "." + MigrationHistoryTable.TABLE_NAME +" " +
					"WHERE " + MigrationHistoryTable.COL_MIGRATION_TYPE + "= ? " +
					(versionFrom == null ? "" : "AND " + MigrationHistoryTable.COL_VERSION + " > ? ") +
					"AND " + MigrationHistoryTable.COL_VERSION + " <= ? " +
					"ORDER BY " + MigrationHistoryTable.COL_VERSION);
			int i = 1;
			stmt.setString(i++, Migration.Type.REGULAR);
			if (versionFrom != null)
				stmt.setString(i++, versionFrom.getStandardizedVersion());
			stmt.setString(i, versionTo.getStandardizedVersion());
			rs = stmt.executeQuery();
			List<MigrationInfo> result = new ArrayList<>();
			MigrationInfo migrationInfo;
			while ((migrationInfo = MigrationInfo.build(rs)) != null)
				result.add(migrationInfo);
			return result;
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}
	
	@Override
	public void addMigrationToHistory(Connection conn, String schema, Migration migration)
			throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + schema + "." + MigrationHistoryTable.TABLE_NAME +
				"(" +
					MigrationHistoryTable.COL_MIGRATION_TYPE + ", " +
					MigrationHistoryTable.COL_VERSION + ", " +
					MigrationHistoryTable.COL_DESCRIPTION + ", " +
					MigrationHistoryTable.COL_FILENAME + ", " +
					MigrationHistoryTable.COL_CHECKSUM +
				") VALUES (?, ?, ?, ?, ?)")) {
			stmt.setString(1, migration.getType());
			stmt.setString(2, migration.getVersion().getStandardizedVersion());
			stmt.setString(3, migration.getDescription());
			stmt.setString(4, migration.getFilename());
			stmt.setInt(5, migration.getChecksum());
			stmt.executeUpdate();
		}
	}
	
	@Override
	public void removeMigrationFromHistory(Connection conn, String schema, MigrationInfo migration)
			throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM " + schema + "." + MigrationHistoryTable.TABLE_NAME + " " +
				"WHERE " + MigrationHistoryTable.COL_MIGRATION_TYPE + " = ? " +
				"AND " + MigrationHistoryTable.COL_VERSION + " = ?")) {
			stmt.setString(1, migration.getMigrationType());
			stmt.setString(2, migration.getVersion().getStandardizedVersion());
			stmt.executeUpdate();
		}
	}
	
	@Override
	public void setNewInfoForSpecificVersion(Connection conn, String schema, Migration migration)
			throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("UPDATE " + schema + "." + MigrationHistoryTable.TABLE_NAME + " " +
				"SET " + MigrationHistoryTable.COL_DESCRIPTION + " = ?, " +
				MigrationHistoryTable.COL_FILENAME + " = ?, " +
				MigrationHistoryTable.COL_CHECKSUM + " = ?, " +
				"WHERE " + MigrationHistoryTable.COL_MIGRATION_TYPE + " = ? " +
				"AND " + MigrationHistoryTable.COL_VERSION + " = ?")) {
			stmt.setString(1, migration.getDescription());
			stmt.setString(2, migration.getFilename());
			stmt.setInt(3, migration.getChecksum());
			stmt.setString(4, migration.getType());
			stmt.setString(5, migration.getVersion().getStandardizedVersion());
			stmt.executeUpdate();
		}
	}
	
	@Override
	public void setMigrationAsCurrent(Connection conn, String schema, Migration migration)
			throws SQLException {
		// clean previous current Migrations
		try (PreparedStatement stmt = conn.prepareStatement("UPDATE " + schema + "." + MigrationHistoryTable.TABLE_NAME + " " +
				"SET " + MigrationHistoryTable.COL_IS_CURRENT + " = 0")) {
			stmt.executeUpdate();
		}
		
		try (PreparedStatement stmt = conn.prepareStatement("UPDATE " + schema + "." + MigrationHistoryTable.TABLE_NAME + " " +
				"SET " + MigrationHistoryTable.COL_EXECUTED_AT + " = CURRENT_TIMESTAMP, " +
				MigrationHistoryTable.COL_IS_CURRENT + " = 1, " +
				MigrationHistoryTable.COL_SUCCESS + " = 1 " +
				"WHERE " + MigrationHistoryTable.COL_MIGRATION_TYPE + " = ? " +
				"AND " + MigrationHistoryTable.COL_VERSION + " = ?")) {
			stmt.setString(1, migration.getType());
			stmt.setString(2, migration.getVersion().getStandardizedVersion());
			stmt.executeUpdate();
		}
	}
	
	@Override
	public void setMigrationAsTarget(Connection conn, String schema, Migration migration)
			throws SQLException {
		// clean previous targets Migrations
		try (PreparedStatement stmt = conn.prepareStatement("UPDATE " + schema + "." + MigrationHistoryTable.TABLE_NAME + " " +
				"SET " + MigrationHistoryTable.COL_IS_TARGET + " = 0")) {
			stmt.executeUpdate();
		}
		
		try (PreparedStatement stmt = conn.prepareStatement("UPDATE " + schema + "." + MigrationHistoryTable.TABLE_NAME + " " +
				"SET " + MigrationHistoryTable.COL_IS_TARGET + " = 1 " +
				"WHERE " + MigrationHistoryTable.COL_MIGRATION_TYPE + " = ? " +
				"AND " + MigrationHistoryTable.COL_VERSION + " = ?")) {
			stmt.setString(1, migration.getType());
			stmt.setString(2, migration.getVersion().getStandardizedVersion());
			stmt.executeUpdate();
		}
	}
	
	@Override
	public void setStatementAsLastExecuted(Connection conn, String schema, Migration migration, Statement statement)
			throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("UPDATE " + schema + "." + MigrationHistoryTable.TABLE_NAME + " " +
				"SET " + MigrationHistoryTable.COL_LAST_EXECUTED_STMT_POS + " = ?, " +
				MigrationHistoryTable.COL_LAST_EXECUTED_STMT + " = ?, " +
				MigrationHistoryTable.COL_LAST_EXECUTED_STMT_LINE + " = ?, " +
				MigrationHistoryTable.COL_LAST_EXECUTED_STMT_FIRST_CHAR + " = ? " +
				"WHERE " + MigrationHistoryTable.COL_MIGRATION_TYPE + " = ? " +
				"AND " + MigrationHistoryTable.COL_VERSION + " = ?")) {
			stmt.setInt(1, statement.getMigrationPosition());
			stmt.setString(2, statement.getRawStatement());
			stmt.setInt(3, statement.getLine());
			stmt.setInt(4, statement.getFirstCharPositionInLine());
			stmt.setString(5, migration.getType());
			stmt.setString(6, migration.getVersion().getStandardizedVersion());
			stmt.executeUpdate();
		}
	}
	
	@Override
	public void cleanTargetMigration(Connection conn, String schema, Migration migration) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("UPDATE " + schema + "." + MigrationHistoryTable.TABLE_NAME + " " +
				"SET " + MigrationHistoryTable.COL_IS_TARGET + " = 0 " +
				"WHERE " + MigrationHistoryTable.COL_MIGRATION_TYPE + " = ? " +
				"AND " + MigrationHistoryTable.COL_VERSION + " = ?")) {
			stmt.setString(1, migration.getType());
			stmt.setString(2, migration.getVersion().getStandardizedVersion());
			stmt.executeUpdate();
		}
	}
}
