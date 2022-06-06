package com.evo.vcs.command;

import com.evo.exception.SimpleException;
import com.evo.internal.history.MigrationInfo;
import com.evo.internal.object.Migration;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.List;

@Command(name = "status", description = "Provide the status of the Evo Project.")
public class StatusCommand extends AbstractWithDbConnCommand implements Runnable {
	
	@Override
	public void run() {
		super.loadProjectWithTempConfig();
		try {
			// identify current migration
			MigrationInfo currentMigrationInfo = this.getProject().getMigrationHistoryTable().getCurrentMigrationInfo();
			if (currentMigrationInfo == null) {
				System.out.println("Current Migration: (none)");
			} else {
				System.out.println("Current Migration: " + currentMigrationInfo.getFilename());
				if (currentMigrationInfo.getVersion().isStaged()) {
					System.out.println(CommandLine.Help.Ansi.AUTO.string(
							"With " + currentMigrationInfo.getFilename() + " as current migration, " +
									"it is impossible to provide a relevant status. " +
									"Please, execute the teardown command (or equivalent) before."
					));
					System.exit(1);
				}
			}
			
			System.out.println();
			System.out.println("Conflicts:");
			List<Migration> conflicts = Migration.getConflicts(this.getProject());
			if (conflicts.size() == 0) {
				System.out.println("No conflicts found!");
			} else {
				for (Migration conflict : conflicts)
					System.out.println(CommandLine.Help.Ansi.AUTO.string(
							"@|fg(red) - " + conflict.getFilename() + "|@"
					));
			}
			
			System.out.println();
			System.out.println("New Past Migrations:");
			List<Migration> newPastMigrations = Migration.getNewPastMigrations(this.getProject());
			if (newPastMigrations.size() == 0) {
				System.out.println("No new past migrations found!");
			} else {
				for (Migration newPastMigration : newPastMigrations)
					System.out.println(CommandLine.Help.Ansi.AUTO.string(
							"@|fg(red) - " + newPastMigration.getFilename() + "|@"
					));
			}
			
			System.out.println();
			System.out.println("Lost Past Migrations:");
			List<MigrationInfo> lostPastMigrations = Migration.getLostPastMigrations(this.getProject());
			if (lostPastMigrations.size() == 0) {
				System.out.println("No lost past migrations found!");
			} else {
				for (MigrationInfo lostPastMigration : lostPastMigrations)
					System.out.println(CommandLine.Help.Ansi.AUTO.string(
							"@|fg(red) - " + lostPastMigration.getFilename() + "|@"
					));
			}
			
			System.out.println();
			System.out.println("New Future Migrations:");
			List<Migration> newFutureMigrations = Migration.getNewFutureMigrations(this.getProject());
			if (newFutureMigrations.size() == 0) {
				System.out.println("No new future migrations found!");
			} else {
				for (Migration newFutureMigration : newFutureMigrations)
					System.out.println(CommandLine.Help.Ansi.AUTO.string(
							"@|fg(green) - " + newFutureMigration.getFilename() + "|@"
					));
			}
			
			System.out.println();
			System.out.println("Lost Future Migrations:");
			List<MigrationInfo> lostFutureMigrations = Migration.getLostFutureMigrations(this.getProject());
			if (lostPastMigrations.size() == 0) {
				System.out.println("No lost future migrations found!");
			} else {
				for (MigrationInfo lostFutureMigration : lostFutureMigrations)
					System.out.println(CommandLine.Help.Ansi.AUTO.string(
							"@|fg(red) - " + lostFutureMigration.getFilename() + "|@"
					));
			}
			
			System.out.println();
			System.out.println("Deprecation migrations that should have already been executed:");
			List<Migration> deprecationMigrationsToExecute = Migration.getDeprecationMigrationsToExecute(
					this.getProject(), currentMigrationInfo == null ? null : currentMigrationInfo.getVersion()
			);
			if (deprecationMigrationsToExecute.size() == 0) {
				System.out.println("No deprecation migration that should have already been executed found!");
			} else {
				for (Migration deprecationMigration : deprecationMigrationsToExecute)
					System.out.println(CommandLine.Help.Ansi.AUTO.string(
							"@|fg(red) - " + deprecationMigration.getFilename() + "|@"
					));
			}
			
			System.out.println();
			System.out.println("Deprecation migrations in the future:");
			List<Migration> deprecationMigrationsNotToExecute =
					Migration.getDeprecationMigrationsNotToExecute(this.getProject(),
							currentMigrationInfo == null ? null : currentMigrationInfo.getVersion());
			if (deprecationMigrationsNotToExecute.size() == 0) {
				System.out.println("No deprecation migration in the future found!");
			} else {
				for (Migration deprecationMigration : deprecationMigrationsNotToExecute)
					System.out.println(CommandLine.Help.Ansi.AUTO.string(
							"@|fg(green) - " + deprecationMigration.getFilename() + "|@"
					));
			}
		} catch (SimpleException e) {
			System.err.println(CommandLine.Help.Ansi.AUTO.string(e.getMessage()));
			System.err.println();
			e.printStackTrace();
		}
	}
}
