package com.evo.internal.rdbms;

import com.evo.exception.SimpleException;
import com.evo.internal.object.Config;
import com.evo.internal.rdbms.dump.IDumperPerRdbms;
import com.evo.internal.rdbms.dump.MySQLDumper;
import com.evo.internal.rdbms.dump.PostgreSQLDumper;
import com.evo.internal.rdbms.operations.IOperationsPerRdbms;
import com.evo.internal.rdbms.operations.MySQLOperations;
import com.evo.internal.rdbms.operations.PostgreSQLOperations;

import java.sql.Connection;
import java.sql.SQLException;

public class GetRDBMS {
	
	public static final String RDBMS_MYSQL = "MySQL";
	public static final String RDBMS_MARIADB = "MariaDB";
	public static final String RDBMS_POSTGRESQL = "PostgreSQL";
	
	public static final int RDBMS_MYSQL_DEFAULT_PORT = 3306;
	public static final int RDBMS_MARIADB_DEFAULT_PORT = 3306;
	public static final int RDBMS_POSTGRESQL_DEFAULT_PORT = 5432;
	
	private static final String MESSAGE_NOT_SUPPORTED_DATABASE = "Database used is not a MySQL, Maria DB or PostgreSQL.";
	
	private Connection conn;
	
	private Config projectConfig;
	
	private String rdbmsName;
	
	private int rdbmsPort;
	
	public GetRDBMS(Connection conn, Config projectConfig) throws SimpleException {
		try {
			this.conn = conn;
			this.projectConfig = projectConfig;
			this.rdbmsName = this.conn.getMetaData().getDatabaseProductName();
			this.rdbmsPort = this.getOperationsPerRdbms().getCurrentPort(this.conn);
			if (!this.isMySQL() && !this.isMariaDB() && !this.isPostgreSQL())
				throw new SimpleException(GetRDBMS.MESSAGE_NOT_SUPPORTED_DATABASE);
		} catch (SQLException e) {
			throw new SimpleException("There was an error while contacting the database.", e);
		}
	}
	
	public IDumperPerRdbms getDumperPerRdbms() throws SimpleException {
		if (this.isMySQL() || this.isMariaDB())
			return new MySQLDumper(
					this.projectConfig.getDbUser(),
					this.projectConfig.getDbPassword(),
					this.rdbmsPort
			);
		if (this.isPostgreSQL())
			return new PostgreSQLDumper(
					this.projectConfig.getDbUser(),
					this.projectConfig.getDbPassword(),
					this.rdbmsPort
			);
		throw new SimpleException(GetRDBMS.MESSAGE_NOT_SUPPORTED_DATABASE);
	}
	
	public IOperationsPerRdbms getOperationsPerRdbms() throws SimpleException {
		if (this.isMySQL() || this.isMariaDB())
			return new MySQLOperations();
		if (this.isPostgreSQL())
			return new PostgreSQLOperations();
		throw new SimpleException(GetRDBMS.MESSAGE_NOT_SUPPORTED_DATABASE);
	}
	
	/**
	 * @return any Database.RDBMS_* const
	 */
	public String getRdbmsName() {
		return this.rdbmsName;
	}
	
	public int getRdbmsPort() {
		return this.rdbmsPort;
	}
	
	public boolean isMySQL() {
		return this.rdbmsName.equals(GetRDBMS.RDBMS_MYSQL);
	}
	
	public boolean isMariaDB() {
		return this.rdbmsName.equals(GetRDBMS.RDBMS_MARIADB);
	}
	
	public boolean isPostgreSQL() {
		return this.rdbmsName.equals(GetRDBMS.RDBMS_POSTGRESQL);
	}
}
