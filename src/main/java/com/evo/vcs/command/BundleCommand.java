package com.evo.vcs.command;

import com.evo.exception.SimpleException;
import com.evo.internal.object.*;
import com.evo.vcs.exception.StatementException;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "bundle", description = "Generate new migration and add it to db_migration folder.")
public class BundleCommand extends AbstractWithDbConnCommand implements Runnable {
	
	@Option(names = { "--version" }, description = "Migration's version", required = true)
	private String migrationVersion;
	
	@Option(names = { "--description" }, description = "Migration description", defaultValue = "")
	private String migrationDescription;
	
	@Option(names = { "--after-version" }, description = "The migration should be tested as if is after this version.",
			defaultValue = "")
	private String afterVersion;
	
	@Option(names = { "--deprecation-date" }, description = "The date when this migration should be executed. " +
			"Date format: yyyy-MM-dd.", defaultValue = "")
	private String deprecationDate;
	
	@Override
	public void run() {
		super.loadProjectWithTempConfig();
		try {
			StagedMigration stagedMigration = StagedMigration.load(this.getProject().getStagedMigrationPath());
			if (stagedMigration.getStatements().size() == 0)
				throw new SimpleException("Staged migration has no statement.");
			
			ProjectHelper.teardownProject(this.getProject());
			super.loadProjectWithTempConfig();
			
			System.out.println("Project is being deployed up to the after version.");
			ProjectHelper.deployProject(this.getProject(), new Version("staged"), false,
					false, false, false, true,
					null, null, null,
					null);
			
			System.out.println("Migration is being generated.");
			Migration migration;
			if (this.deprecationDate.equals(""))
				migration = Migration.buildRegular(this.getProject(), stagedMigration, this.migrationVersion,
						this.migrationDescription);
			else
				migration = Migration.buildDeprecation(this.getProject(), stagedMigration, this.migrationVersion,
						this.deprecationDate, this.migrationDescription);
			
			System.out.println("Migration tests are being generated.");
			Test.build(this.getProject(), migration);
			
			StagedMigration.truncate(this.getProject().getStagedMigrationPath());
			System.out.println("Staged Migration is now cleaned.");
			System.out.println(CommandLine.Help.Ansi.AUTO.string(
					"@|fg(green) Migration was bundled with success.|@"
			));
			
			ProjectHelper.teardownProject(this.getProject());
			super.loadProjectWithTempConfig();
			ProjectHelper.deployProject(this.getProject(), migration.getVersion(), false,
					false, false, false, true,
					null, null, null,
					null);
		} catch (SimpleException e) {
			System.err.println(CommandLine.Help.Ansi.AUTO.string(e.getMessage()));
			System.err.println();
			e.printStackTrace();
		}
	}
}
