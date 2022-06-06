package com.evo.internal.rdbms.operations;

import com.evo.exception.SimpleException;
import com.evo.internal.history.MigrationInfo;
import com.evo.internal.object.Migration;
import com.evo.internal.object.Statement;
import com.evo.internal.object.Version;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IOperationsPerRdbms {
	
	void loadSourceFile(Connection conn, Path filePath) throws SQLException;
	
	int getCurrentPort(Connection conn) throws SQLException;
	
	void createSchema(Connection conn, String schema) throws SQLException;
	
	void dropSchema(Connection conn, String schema) throws SQLException;
	
	void changeToSchema(Connection conn, String schema) throws SQLException;
	
	boolean existSchema(Connection conn, String schema) throws SQLException;
	
	boolean existTable(Connection conn, String schema, String table) throws SQLException;
	
	boolean existEvoMigrationHistoryTable(Connection conn, String schema) throws SQLException;
	
	void createEvoMigrationHistoryTable(Connection conn, String schema) throws SQLException;
	
	MigrationInfo getHeadMigrationInfo(Connection conn, String schema) throws SimpleException, SQLException;
	
	MigrationInfo getCurrentMigrationInfo(Connection conn, String schema) throws SimpleException, SQLException;
	
	MigrationInfo getSpecificMigrationInfo(Connection conn, String schema, String migrationType, Version version)
			throws SimpleException, SQLException;
	
	List<MigrationInfo> getAllMigrationsInfo(Connection conn, String schema) throws SimpleException, SQLException;
	
	List<MigrationInfo> getAllMigrationsInfoByType(Connection conn, String schema, String migrationType)
			throws SimpleException, SQLException;
	
	List<MigrationInfo> getMigrationsInfoBetween(Connection conn, String schema, Version versionFrom, Version versionTo)
			throws SimpleException, SQLException;
	
	void addMigrationToHistory(Connection conn, String schema, Migration migration)
			throws SQLException;
	
	void removeMigrationFromHistory(Connection conn, String schema, MigrationInfo migration)
			throws SQLException;
	
	void setNewInfoForSpecificVersion(Connection conn, String schema, Migration migration)
			throws SQLException;
	
	void setMigrationAsCurrent(Connection conn, String schema, Migration migration)
			throws SQLException;
	
	void setMigrationAsTarget(Connection conn, String schema, Migration migration)
			throws SQLException;
	
	void setStatementAsLastExecuted(Connection conn, String schema, Migration migration, Statement statement)
			throws SQLException;
	
	void cleanTargetMigration(Connection conn, String schema, Migration migration)
			throws SQLException;
}
