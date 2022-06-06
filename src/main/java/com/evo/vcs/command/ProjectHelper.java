package com.evo.vcs.command;

import com.evo.exception.SimpleException;
import com.evo.internal.history.MigrationInfo;
import com.evo.internal.object.*;
import com.evo.internal.rdbms.GetRDBMS;
import com.evo.internal.rdbms.operations.IOperationsPerRdbms;
import com.evo.vcs.exception.StatementException;
import picocli.CommandLine;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ProjectHelper {
	
	/**
	 *
	 * @param project
	 * @param targetMigrationVersion null if it is to deploy to last version
	 * @param backupFlag
	 * @param restoreBackupFlag
	 * @param testBeforeFlag
	 * @param testAfterFlag
	 * @param deprecateFlag
	 * @param successEachMigrationCallback
	 * @param errorEachMigrationCallback
	 * @param successCallbackForEachTest
	 * @param errorCallbackForEachTest
	 * @throws SimpleException
	 */
	public static void deployProject(Project project, Version targetMigrationVersion, boolean backupFlag,
	                                 boolean restoreBackupFlag, boolean testBeforeFlag, boolean testAfterFlag,
	                                 boolean deprecateFlag, Consumer<Migration> successEachMigrationCallback,
	                                 BiConsumer<Migration, StatementException> errorEachMigrationCallback,
	                                 Consumer<ITest> successCallbackForEachTest,
	                                 Consumer<ITest> errorCallbackForEachTest)
			throws SimpleException {
		try {
			StagedMigration stagedMigration = null;
			if (targetMigrationVersion != null && targetMigrationVersion.isStaged()) {
				targetMigrationVersion = null;
				stagedMigration = StagedMigration.load(project.getStagedMigrationPath());
			}
			
			// identify current migration
			MigrationInfo currentMigrationInfo = project.getMigrationHistoryTable().getCurrentMigrationInfo();
			Migration currentMigration = null;
			if (currentMigrationInfo == null) {
				System.out.println("Current Migration: (none)");
				System.out.println(CommandLine.Help.Ansi.AUTO.string(
						"@|fg(red) You must execute first the init or load command.|@"
				));
				System.exit(1);
			} else if (currentMigrationInfo.getVersion().isStaged()) {
				System.out.println("Current Migration: " + currentMigrationInfo.getFilename());
				System.out.println(CommandLine.Help.Ansi.AUTO.string(
						"@|fg(red) With " + currentMigrationInfo.getFilename() + " as current migration, " +
								"you must execute the teardown command (or equivalent) before.|@"
				));
				System.exit(1);
			} else {
				currentMigration = Migration.load(project, currentMigrationInfo.getFilename());
				System.out.println("Current Migration: " + currentMigrationInfo.getFilename());
			}
			
			// identify target migration
			Migration targetMigration;
			if (targetMigrationVersion == null)
				targetMigration = Migration.loadLatestRegular(project);
			else
				targetMigration = Migration.loadRegularByVersion(project, targetMigrationVersion);
			
			if (stagedMigration != null) {
				System.out.println("Target Migration: " + stagedMigration.getFilename());
			} else if (targetMigration == null) {
				System.out.println("Target Migration: (none)");
				System.out.println(CommandLine.Help.Ansi.AUTO.string(
						"@|fg(red) It is impossible to proceed without a target migration.|@"
				));
				System.exit(1);
			} else {
				System.out.println("Target Migration: " + targetMigration.getFilename());
			}
			
			if (stagedMigration == null) {
				if (currentMigration.getVersion().isHigherThan(targetMigration.getVersion())) {
					System.out.println();
					System.out.println(CommandLine.Help.Ansi.AUTO.string(
							"@|fg(red) Deploy from the current version " +
									"(" + currentMigration.getVersion().getVersion() + ") " +
									"to a lower target version (" + targetMigration.getVersion().getVersion() + ") " +
									"is impossible.|@"
					));
					System.exit(1);
				}
				if (currentMigration.getVersion().isEqualThan(targetMigration.getVersion())) {
					System.out.println();
					System.out.println(CommandLine.Help.Ansi.AUTO.string(
							"@|fg(green) The database is already in the target version " +
									"(" + targetMigration.getVersion().getVersion() + ").|@"
					));
					System.exit(1);
				}
			}
			
			System.out.println();
			System.out.println("Verifying conflicts...");
			List<Migration> conflicts = Migration.getConflicts(project);
			if (conflicts.size() == 0) {
				System.out.println("No conflicts found!");
			} else {
				System.out.println("Conflicts found:");
				for (Migration conflict : conflicts)
					System.out.println(CommandLine.Help.Ansi.AUTO.string(
							"@|fg(red) - " + conflict.getFilename() + "|@"
					));
				System.out.println(CommandLine.Help.Ansi.AUTO.string(
						"@|fg(red) Fix the conflicts in order to deploy to run.|@"
				));
				System.exit(1);
			}
			
			System.out.println();
			System.out.println("Verifying new past migrations...");
			List<Migration> newPastMigrations = Migration.getNewPastMigrations(project);
			if (newPastMigrations.size() == 0) {
				System.out.println("No new past migrations found!");
			} else {
				System.out.println("New past migrations found:");
				for (Migration newPastMigration : newPastMigrations)
					System.out.println(CommandLine.Help.Ansi.AUTO.string(
							"@|fg(red) - " + newPastMigration.getFilename() + "|@"
					));
				System.out.println(CommandLine.Help.Ansi.AUTO.string(
						"@|fg(red) Deploy do not run with new past migrations.|@"
				));
				System.exit(1);
			}
			
			System.out.println();
			System.out.println("Verifying lost past migrations...");
			List<MigrationInfo> lostPastMigrations = Migration.getLostPastMigrations(project);
			if (lostPastMigrations.size() == 0) {
				System.out.println("No lost past migrations found!");
			} else {
				System.out.println("Lost past migrations found:");
				for (MigrationInfo lostPastMigration : lostPastMigrations)
					System.out.println(CommandLine.Help.Ansi.AUTO.string(
							"@|fg(red) - " + lostPastMigration.getFilename() + "|@"
					));
				System.out.println(CommandLine.Help.Ansi.AUTO.string(
						"@|fg(red) Deploy do not run with lost past migrations.|@"
				));
				System.exit(1);
			}
			
			System.out.println();
			System.out.println("Looking for new future migrations...");
			List<Migration> newFutureMigrations = Migration.getNewFutureMigrations(project);
			if (newFutureMigrations.size() == 0) {
				System.out.println("No new future migrations found!");
			} else {
				System.out.println("New future migrations found:");
				for (Migration newFutureMigration : newFutureMigrations) {
					project.getMigrationHistoryTable().addMigrationToHistory(newFutureMigration);
					System.out.println(CommandLine.Help.Ansi.AUTO.string(
							"@|fg(green) - " + newFutureMigration.getFilename() + "|@"
					));
				}
			}
			
			System.out.println();
			System.out.println("Looking for lost future migrations...");
			List<MigrationInfo> lostFutureMigrations = Migration.getLostFutureMigrations(project);
			if (lostFutureMigrations.size() == 0) {
				System.out.println("No new future migrations found!");
			} else {
				System.out.println("Lost future migrations found:");
				for (MigrationInfo lostFutureMigration : lostFutureMigrations) {
					project.getMigrationHistoryTable().removeMigrationFromHistory(lostFutureMigration);
					System.out.println(CommandLine.Help.Ansi.AUTO.string(
							"@|fg(green) - " + lostFutureMigration.getFilename() + "|@"
					));
				}
			}
			
			if (deprecateFlag) {
				System.out.println();
				System.out.println("Looking for deprecation migrations to be executed...");
				List<Migration> deprecationMigrationsToExecute = Migration.getDeprecationMigrationsToExecute(project,
						targetMigration.getVersion());
				if (deprecationMigrationsToExecute.size() == 0) {
					System.out.println("No deprecation migrations to be executed found!");
				} else {
					System.out.println("Deprecation migrations to be execute found:");
					for (Migration deprecationMigration : deprecationMigrationsToExecute) {
						project.getMigrationHistoryTable().addMigrationToHistory(deprecationMigration);
						System.out.println(CommandLine.Help.Ansi.AUTO.string(
								"@|fg(green) - " + deprecationMigration.getFilename() + "|@"
						));
					}
				}
			}
			
			if (backupFlag) {
				System.out.println();
				System.out.println("Creating backup...");
				Backup.build(project);
			}
			
			if (currentMigration != null && testBeforeFlag) {
				System.out.println();
				System.out.println("Testing the current migration...");
				Test.executeAll(project, currentMigration, successCallbackForEachTest, errorCallbackForEachTest);
			}
			
			System.out.println();
			System.out.println("Deployment process is about to start!");
			
			List<Migration> migrationsToExecute = targetMigration == null ?
					new ArrayList<>() :
					Migration.loadMigrationsToExecute(project, targetMigration.getVersion());
			
			if (stagedMigration != null) {
				migrationsToExecute.add(stagedMigration);
				project.getMigrationHistoryTable().addMigrationToHistory(stagedMigration);
				project.getMigrationHistoryTable().setMigrationAsTarget(stagedMigration);
			} else {
				project.getMigrationHistoryTable().setMigrationAsTarget(targetMigration);
			}
			
			for (Migration migration : migrationsToExecute) {
				try {
					migration.execute(project.getDatabaseConnection(), project.getMigrationHistoryTable(),
							null, null);
					project.getMigrationHistoryTable().setMigrationAsCurrent(migration);
					if (successEachMigrationCallback != null)
						successEachMigrationCallback.accept(migration);
				} catch (StatementException e) {
					if (errorEachMigrationCallback != null)
						errorEachMigrationCallback.accept(migration, e);
					throw e;
				}
			}
			
			if (stagedMigration != null)
				project.getMigrationHistoryTable().cleanTargetMigration(stagedMigration);
			else
				project.getMigrationHistoryTable().cleanTargetMigration(targetMigration);
			
			System.out.println();
			System.out.println(CommandLine.Help.Ansi.AUTO.string(
					"@|fg(green) Deployment process is over and it was successful!|@"
			));
			
			if (testAfterFlag) {
				System.out.println();
				System.out.println("Testing the target migration...");
				if (stagedMigration != null)
					StagedTest.executeAll(project, successCallbackForEachTest, errorCallbackForEachTest);
				else
					Test.executeAll(project, targetMigration, successCallbackForEachTest, errorCallbackForEachTest);
			}
		} catch (SimpleException | StatementException e) {
			if (restoreBackupFlag) {
				System.out.println("Restoring backup...");
				Backup backup = Backup.loadLast(project);
				if (backup != null)
					backup.restore();
			}
			
			throw new SimpleException(e.getMessage(), e);
		}
	}
	
	public static void teardownProject(Project project) throws SimpleException {
		try {
			IOperationsPerRdbms operations = new GetRDBMS(project.getDatabaseConnection(),
					project.getConfig()).getOperationsPerRdbms();
			for (String schema : project.getConfig().getSchemas()) {
				if (operations.existSchema(project.getDatabaseConnection(), schema)) {
					operations.dropSchema(project.getDatabaseConnection(), schema);
					System.out.println("Schema '" + schema + "' was dropped.");
				} else {
					System.out.println("Schema '" + schema + "' does not exist.");
				}
			}
		} catch (SQLException e) {
			throw new SimpleException(e.getMessage(), e);
		}
	}
}
