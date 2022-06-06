package com.evo.vcs.command;

import com.evo.exception.SimpleException;
import com.evo.internal.object.*;
import picocli.CommandLine;
import picocli.CommandLine.Option;

abstract public class AbstractWithDbConnCommand extends AbstractCommand {
	
	@Option(names = { "--jdbc-url" }, description = "JDBC url (overrides value in evo.conf)", defaultValue = "")
	private String jdbcUrl;
	
	@Option(names = { "--jdbc-driver" }, description = "JDBC driver (overrides value in evo.conf)",
			defaultValue = "")
	private String jdbcDriver;
	
	@Option(names = { "--db-user" }, description = "Database user (overrides value in evo.conf)", defaultValue = "")
	private String dbUser;
	
	@Option(names = { "--db-password" }, description = "Database password (overrides value in evo.conf)",
			interactive = true, arity = "0..1", defaultValue = "")
	private String dbPassword;
	
	@Option(names = { "--db-hostname" }, description = "Database hostname (overrides value in evo.conf)",
			defaultValue = "localhost")
	private String dbHostname;
	
	void loadProjectWithTempConfig() {
		try {
			if (Project.exist(this.getUserDirPath()))
				this.project = Project.load(this.getUserDirPath());
			else
				throw new SimpleException("No Evo Project was found in this folder.");
			
			if (!this.jdbcUrl.equals(""))
				this.project.getConfig().setTempJdbcUrl(this.jdbcUrl);
			if (!this.jdbcDriver.equals(""))
				this.project.getConfig().setTempJdbcDriver(this.jdbcDriver);
			if (!this.dbUser.equals(""))
				this.project.getConfig().setTempDbUser(this.dbUser);
			if (!this.dbPassword.equals(""))
				this.project.getConfig().setTempDbPassword(this.dbPassword);
			if (!this.dbHostname.equals(""))
				this.project.getConfig().setTempDbHostname(this.dbHostname);
		} catch (SimpleException e) {
			System.err.println(CommandLine.Help.Ansi.AUTO.string(e.getMessage()));
			System.err.println();
			e.printStackTrace();
			System.exit(1);
		}
	}
}
