package com.evo.internal.rdbms.dump;

import com.evo.internal.rdbms.exception.SqlDumperException;

import java.util.Collections;
import java.util.List;

public class PostgreSQLDumper extends AbstractDump implements IDumperPerRdbms {
	
	private String dbUser;
	
	private String dbPassword;
	
	private int dbPort;
	
	public PostgreSQLDumper(String dbUser, String dbPassword, int dbPort) {
		this.dbUser = dbUser;
		this.dbPassword = dbPassword;
		this.dbPort = dbPort;
	}
	
	@Override
	public String getBackupSchemasStatements(List<String> schemas) throws SqlDumperException {
		return this.dumpSchemas(schemas, true);
	}
	
	@Override
	public String getCreateSchemasStatements(List<String> schemas) throws SqlDumperException {
		return this.dumpSchemas(schemas, false);
	}
	
	@Override
	public String getCreateTableStatement(String schema, String table) throws SqlDumperException {
		return this.dumpTables(schema, Collections.singletonList(table), false);
	}
	
	private String dumpSchemas(List<String> schemas, boolean withData) throws SqlDumperException {
		return super.dump(
				"pg_dump" +
						" --user=" + this.dbUser +
						" --password=" + this.dbPassword +
						" --port=" + this.dbPort +
						(withData ? " " : " --no-data") +
						// select multiple schemas and get also CREATE DATABASE statement
						" --databases " + String.join(" ", schemas) +
						" --triggers" + // dump also triggers
						" --routines" + // dump also routines
						" --events" + // dump also events
						""
		);
	}
	
	private String dumpTables(String schema, List<String> tables, boolean withData) throws SqlDumperException {
		return super.dump(
				"mysqldump" +
						" --user=" + this.dbUser +
						" --password=" + this.dbPassword +
						" --port=" + this.dbPort +
						(withData ? " " : " --no-data") +
						// select specific tables inside a specific schema
						" " + schema + " " + String.join(" ", tables) +
						" --triggers" + // dump also triggers
						" --routines" + // dump also routines
						" --events" // dump also events
		);
	}
}
