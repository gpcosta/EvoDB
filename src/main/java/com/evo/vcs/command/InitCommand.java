package com.evo.vcs.command;

import com.evo.exception.SimpleException;
import com.evo.internal.object.Migration;
import com.evo.internal.object.Project;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "init", description = "Initialize an Evo Project.")
public class InitCommand extends AbstractCommand implements Runnable {
	
	@Option(names = { "--project-name" }, description = "Evo Project Name", required = true)
	private String projectName;
	
	@Option(names = { "--schemas" }, description = "Schemas that should be controlled by Evo. " +
			"It should be provided a list of strings separated by comma (,). " +
			"The first is treated as the default schema where Migration History Table will be stored. " +
			"If default schema does not exist when this command is executed, the default schema will be created.",
			required = true)
	private String schemas;
	
	@Option(names = { "--jdbc-url" }, description = "JDBC url", required = true)
	private String jdbcUrl;
	
	@Option(names = { "--jdbc-driver" }, description = "JDBC driver (overrides value in evo.conf)", required = true)
	private String jdbcDriver;
	
	@Option(names = { "--db-user" }, description = "Database user", required = true)
	private String dbUser;
	
	@Option(names = { "--db-password" }, description = "Database password", required = true, interactive = true,
			arity = "0..1")
	private String dbPassword;
	
	@Option(names = { "--db-hostname" }, description = "Database hostname (default: localhost)",
			defaultValue = "localhost")
	private String dbHostname;
	
	@Override
	public void run() {
		try {
			this.project = Project.build(this.projectName, this.getUserDirPath(), this.schemas, this.jdbcUrl,
					this.jdbcDriver, this.dbUser, this.dbPassword, this.dbHostname,
					(String defaultSchema) -> System.out.println("Default schema (" + defaultSchema + ") was created."),
					(Migration migration) -> System.out.println("Initial Migration (" + migration.getFilename() +") " +
							"was created based on the current schemas."));
			System.out.println("Project initialized.");
		} catch (SimpleException e) {
			System.err.println(CommandLine.Help.Ansi.AUTO.string(e.getMessage()));
			System.err.println();
			e.printStackTrace();
		}
	}
}
