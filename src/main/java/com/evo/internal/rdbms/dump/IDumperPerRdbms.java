package com.evo.internal.rdbms.dump;

import com.evo.internal.rdbms.exception.SqlDumperException;

import java.util.List;

public interface IDumperPerRdbms {
	String getBackupSchemasStatements(List<String> schemas) throws SqlDumperException;
	
	String getCreateSchemasStatements(List<String> schemas) throws SqlDumperException;
	
	String getCreateTableStatement(String schema, String table) throws SqlDumperException;
}
