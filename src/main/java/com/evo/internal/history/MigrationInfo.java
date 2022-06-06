package com.evo.internal.history;

import com.evo.exception.SimpleException;
import com.evo.internal.object.Migration;
import com.evo.internal.object.Version;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MigrationInfo {
	
	private String migrationType;
	
	private Version version;
	
	private String description;
	
	private String filename;
	
	private int checksum;
	
	private String executedAt;
	
	private boolean isCurrent;
	
	private boolean isTarget;
	
	private int lastExecutedStmtPos;
	
	private String lastExecutedStmt;
	
	private int lastExecutedStmtLine;
	
	private int lastExecutedStmtFirstChar;
	
	private boolean success;
	
	private MigrationInfo(String migrationType, Version version, String description, String filename, int checksum,
	                      String executedAt, boolean isCurrent, boolean isTarget, int lastExecutedStmtPos,
	                      String lastExecutedStmt, int lastExecutedStmtLine, int lastExecutedStmtFirstChar,
	                      boolean success) {
		this.migrationType = migrationType;
		this.version = version;
		this.description = description;
		this.filename = filename;
		this.checksum = checksum;
		this.executedAt = executedAt;
		this.isCurrent = isCurrent;
		this.isTarget = isTarget;
		this.lastExecutedStmtPos = lastExecutedStmtPos;
		this.lastExecutedStmt = lastExecutedStmt;
		this.lastExecutedStmtLine = lastExecutedStmtLine;
		this.lastExecutedStmtFirstChar = lastExecutedStmtFirstChar;
		this.success = success;
	}
	
	public String getMigrationType() {
		return this.migrationType;
	}
	
	public Version getVersion() {
		return this.version;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public String getFilename() {
		return this.filename;
	}
	
	public int getChecksum() {
		return this.checksum;
	}
	
	public String getExecutedAt() {
		return this.executedAt;
	}
	
	public boolean isCurrent() {
		return this.isCurrent;
	}
	
	public boolean isTarget() {
		return this.isTarget;
	}
	
	public int getLastExecutedStmtPos() {
		return this.lastExecutedStmtPos;
	}
	
	public String getLastExecutedStmt() {
		return this.lastExecutedStmt;
	}
	
	public int getLastExecutedStmtLine() {
		return this.lastExecutedStmtLine;
	}
	
	public int getLastExecutedStmtFirstChar() {
		return this.lastExecutedStmtFirstChar;
	}
	
	public boolean isSuccessful() {
		return this.success;
	}
	
	public static MigrationInfo build(ResultSet rs) throws SimpleException {
		try {
			if (!rs.next())
				return null;
			
			return new MigrationInfo(
					rs.getString(MigrationHistoryTable.COL_MIGRATION_TYPE),
					new Version(rs.getString(MigrationHistoryTable.COL_VERSION)),
					rs.getString(MigrationHistoryTable.COL_DESCRIPTION),
					rs.getString(MigrationHistoryTable.COL_FILENAME),
					rs.getInt(MigrationHistoryTable.COL_CHECKSUM),
					rs.getString(MigrationHistoryTable.COL_EXECUTED_AT),
					rs.getBoolean(MigrationHistoryTable.COL_IS_CURRENT),
					rs.getBoolean(MigrationHistoryTable.COL_IS_TARGET),
					rs.getInt(MigrationHistoryTable.COL_LAST_EXECUTED_STMT_POS),
					rs.getString(MigrationHistoryTable.COL_LAST_EXECUTED_STMT),
					rs.getInt(MigrationHistoryTable.COL_LAST_EXECUTED_STMT_LINE),
					rs.getInt(MigrationHistoryTable.COL_LAST_EXECUTED_STMT_FIRST_CHAR),
					rs.getBoolean(MigrationHistoryTable.COL_SUCCESS)
			);
		} catch (SQLException e) {
			throw new SimpleException("There was an error while loading migration info from " +
					"the Migration History Table.", e);
		}
	}
	
	public static MigrationInfo build(Migration migration) throws SimpleException {
		return new MigrationInfo(
				migration.getType(),
				migration.getVersion(),
				migration.getDescription(),
				migration.getFilename(),
				migration.getChecksum(),
				java.time.LocalDateTime.now().toString(),
				false,
				false,
				0,
				"",
				0,
				0,
				false
		);
	}
}
