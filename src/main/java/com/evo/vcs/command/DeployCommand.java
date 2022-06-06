package com.evo.vcs.command;

import com.evo.exception.SimpleException;
import com.evo.internal.object.StagedMigration;
import com.evo.internal.object.Version;
import com.evo.vcs.exception.StatementException;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "deploy", description = "Deploy the current database into the Target Migration's version.")
public class DeployCommand extends AbstractWithDbConnCommand implements Runnable {
	
	@Option(names = { "--target-version" }, description = "Target Migration's version.", defaultValue = "")
	private String targetMigrationVersion;
	
	@Option(names = { "--backup" }, description = "Flag to do a backup before migration process.")
	private boolean backupFlag;
	
	@Option(names = { "--restore-backup" }, description = "Flag to restore backup if something went wrong.")
	private boolean restoreBackupFlag;
	
	@Option(names = { "--test-before" }, description = "Flag to test before migration process starts.")
	private boolean testBeforeFlag;
	
	@Option(names = { "--test-after" }, description = "Flag to test after migration process starts.")
	private boolean testAfterFlag;
	
	@Option(names = { "--deprecate" }, description = "Flag to apply deprecation migrations.")
	private boolean deprecateFlag;
	
	@Override
	public void run() {
		super.loadProjectWithTempConfig();
		try {
			ProjectHelper.deployProject(this.getProject(),
					this.targetMigrationVersion.equals("") ? null : new Version(this.targetMigrationVersion),
					this.backupFlag, this.restoreBackupFlag, this.testBeforeFlag, this.testAfterFlag,
					this.deprecateFlag, this.getSuccessCallbackForEachMigration(),
					this.getErrorCallbackForEachMigration(), this.getSuccessCallbackForEachTest(),
					this.getErrorCallbackForEachTest());
		} catch (SimpleException e) {
			System.err.println(CommandLine.Help.Ansi.AUTO.string(e.getMessage()));
			System.err.println();
			e.printStackTrace();
		}
	}
}
