package com.evo.internal.object;

import com.evo.exception.SimpleException;
import com.evo.internal.history.MigrationInfo;
import com.evo.internal.history.MigrationHistoryTable;
import com.evo.internal.rdbms.GetRDBMS;
import com.evo.internal.rdbms.operations.IOperationsPerRdbms;
import com.evo.jdbc.Driver;
import com.evo.vcs.exception.StatementException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.function.Consumer;

public class Project {
	
	public static String DB_MIGRATIONS_FOLDER_NAME = "db_migrations";
	public static String DB_TESTS_FOLDER_NAME = "db_tests";
	public static String STAGED_FOLDER_NAME = "staged";
	public static String STAGED_FILE_NAME = "staged.sql";
	public static String STAGED_TESTS_FOLDER_NAME = "tests";
	public static String FILTERS_FILE_NAME = "filters.sql";
	public static String CONFIG_FILE_NAME = "evo.conf";
	public static String BACKUP_FOLDER_NAME = "backups";
	
	private Path root;
	
	private Config config;
	
	private StagedMigration stagedMigration;
	
	private Filters filters;
	
	private MigrationHistoryTable migrationHistoryTable;
	
	private Connection conn;
	
	private Project(Path root) {
		this(root, null, null, null, null);
	}
	
	private Project(Path root, Config config, StagedMigration stagedMigration, Filters filters,
	                MigrationHistoryTable migrationHistoryTable) {
		this.root = root;
		this.config = config;
		this.stagedMigration = stagedMigration;
		this.filters = filters;
		this.migrationHistoryTable = migrationHistoryTable;
	}
	
	public Path getRoot() {
		return this.root;
	}
	
	public Config getConfig() {
		return this.config;
	}
	
	private void setConfig(Config config) {
		this.config = config;
	}
	
	public StagedMigration getStagedMigration() {
		return this.stagedMigration;
	}
	
	private void setStagedMigration(StagedMigration stagedMigration) {
		this.stagedMigration = stagedMigration;
	}
	
	public Filters getFilters() {
		return this.filters;
	}
	
	private void setFilters(Filters filters) {
		this.filters = filters;
	}
	
	public MigrationHistoryTable getMigrationHistoryTable() {
		return this.migrationHistoryTable;
	}
	
	private void setMigrationHistoryTable(MigrationHistoryTable migrationHistoryTable) {
		this.migrationHistoryTable = migrationHistoryTable;
	}
	
	public Connection getDatabaseConnection() throws SimpleException {
		try {
			if (this.conn != null && !this.conn.isClosed())
				return this.conn;
			
			Properties properties = new Properties();
			properties.setProperty("user", this.getConfig().getDbUser());
			properties.setProperty("password", this.getConfig().getDbPassword());
			this.conn = DriverManager.getConnection(this.getConfig().getJdbcUrl(), properties);
			
			return this.conn;
		} catch (SQLException e) {
			throw new SimpleException("There was an error while connecting to the database.", e);
		}
	}
	
	public Path getMigrationsDirPath() {
		return Paths.get(this.root.toString(), Project.DB_MIGRATIONS_FOLDER_NAME);
	}
	
	public Path getTestsDirPath(Migration migration) {
		return Paths.get(this.root.toString(), Project.DB_TESTS_FOLDER_NAME,
				migration.getFilenameWithoutExtension());
	}
	
	public Path getStagedMigrationPath() {
		return Paths.get(this.root.toString(), Project.STAGED_FOLDER_NAME, Project.STAGED_FILE_NAME);
	}
	
	public Path getStagedTestsDirPath() {
		return Paths.get(this.root.toString(), Project.STAGED_FOLDER_NAME, Project.STAGED_TESTS_FOLDER_NAME);
	}
	
	public Path getFiltersPath() {
		return Paths.get(this.root.toString(), Project.FILTERS_FILE_NAME);
	}
	
	public Path getConfigPath() {
		return Paths.get(this.root.toString(), Project.CONFIG_FILE_NAME);
	}
	
	public Path getBackupsDirPath() {
		return Paths.get(this.root.toString(), Project.BACKUP_FOLDER_NAME);
	}
	
	/**
	 * Build a new Project. It is idempotent.
	 *
	 * @param projectName
	 * @param root
	 * @param schemas
	 * @param jdbcUrl
	 * @param jdbcDriver
	 * @param dbUser
	 * @param dbPassword
	 * @param dbHostname
	 * @param successCreateDefaultSchemaCallback
	 * @param successCreateFirstMigrationCallback
	 * @return
	 * @throws SimpleException
	 */
	public static Project build(String projectName, Path root, String schemas, String jdbcUrl, String jdbcDriver,
	                            String dbUser, String dbPassword, String dbHostname,
	                            Consumer<String> successCreateDefaultSchemaCallback,
	                            Consumer<Migration> successCreateFirstMigrationCallback)
			throws SimpleException {
		if (projectName == null || projectName.equals(""))
			throw new SimpleException("Project must have at least a Project Name.");
		if (schemas == null || schemas.equals(""))
			throw new SimpleException("Project must have at least a Schema.");
		if (jdbcUrl == null || jdbcUrl.equals(""))
			throw new SimpleException("Project must have a JDBC URL.");
		if (jdbcDriver == null || jdbcDriver.equals(""))
			throw new SimpleException("Project must have a JDBC Driver.");
		if (dbUser == null || dbUser.equals(""))
			throw new SimpleException("Project must have a Database User.");
		if (dbPassword == null)
			throw new SimpleException("Project must have a Database Password.");
		if (dbHostname == null || dbHostname.equals(""))
			throw new SimpleException("Project must have a Database Hostname.");
		
		try {
			Project project = new Project(root);
			
			Files.createDirectories(Paths.get(root.toString(), Project.DB_MIGRATIONS_FOLDER_NAME));
			Files.createDirectories(Paths.get(root.toString(), Project.DB_TESTS_FOLDER_NAME));
			Files.createDirectories(Paths.get(root.toString(), Project.STAGED_FOLDER_NAME));
			Files.createDirectories(Paths.get(root.toString(), Project.STAGED_FOLDER_NAME,
					Project.STAGED_TESTS_FOLDER_NAME));
			
			StagedMigration stagedMigration = StagedMigration.build(Paths.get(root.toString(),
					Project.STAGED_FOLDER_NAME, Project.STAGED_FILE_NAME));
			project.setStagedMigration(stagedMigration);
			
			Config config = Config.build(Paths.get(root.toString(), Project.CONFIG_FILE_NAME), projectName, schemas,
					jdbcUrl, jdbcDriver, dbUser, dbPassword, dbHostname);
			project.setConfig(config);
			
			Filters filters = Filters.build(Paths.get(root.toString(), Project.FILTERS_FILE_NAME));
			project.setFilters(filters);
			
			GetRDBMS getRdbms = new GetRDBMS(project.getDatabaseConnection(), config);
			
			// create default schema if it not exists
			IOperationsPerRdbms operations = getRdbms.getOperationsPerRdbms();
			if (!operations.existSchema(project.getDatabaseConnection(), project.getConfig().getDefaultSchema())) {
				operations.createSchema(project.getDatabaseConnection(), project.getConfig().getDefaultSchema());
				successCreateDefaultSchemaCallback.accept(project.getConfig().getDefaultSchema());
			}
			
			MigrationHistoryTable migrationHistoryTable;
			if (MigrationHistoryTable.exist(project.getDatabaseConnection(), project.getConfig()))
				migrationHistoryTable = MigrationHistoryTable.load(project.getDatabaseConnection(), project.getConfig());
			else
				migrationHistoryTable = MigrationHistoryTable.build(
						DriverManager.getConnection(jdbcUrl, dbUser, dbPassword),
						project.getConfig()
				);
			project.setMigrationHistoryTable(migrationHistoryTable);
			
			Migration firstMigration = Migration.loadLatestBaseline(project);
			if (firstMigration == null) {
				firstMigration = Migration.buildBaseline(project, Migration.FIRST_VERSION,
						Migration.DESCRIPTION_FIRST_VERSION);
				successCreateFirstMigrationCallback.accept(firstMigration);
			}
			
			MigrationInfo firstMigrationInfo = project.migrationHistoryTable.getSpecificMigrationInfo(
					Migration.Type.BASELINE,
					new Version(Migration.FIRST_VERSION)
			);
			if (firstMigrationInfo == null)
				project.migrationHistoryTable.addMigrationToHistory(firstMigration);
			project.migrationHistoryTable.setMigrationAsCurrent(firstMigration);
			
			Driver.setNewProject(config.getProjectName(), root);
			return project;
		} catch (IOException | SQLException e) {
			throw new SimpleException("There was an error while creating the Evo project.", e);
		}
	}
	
	public static Project load(Path root) throws SimpleException {
		try {
			Config config = Config.load(Paths.get(root.toString(), Project.CONFIG_FILE_NAME));
			Project project = new Project(
					root,
					config,
					StagedMigration.load(Paths.get(root.toString(), Project.STAGED_FOLDER_NAME,
							Project.STAGED_FILE_NAME)),
					Filters.load(Paths.get(root.toString(), Project.FILTERS_FILE_NAME)),
					null
			);
			
			Connection conn = DriverManager.getConnection(config.getJdbcUrl(), config.getDbUser(),
					config.getDbPassword());
			MigrationHistoryTable migrationHistoryTable;
			if (MigrationHistoryTable.exist(conn, config)) {
				migrationHistoryTable = MigrationHistoryTable.load(conn, config);
			} else {
				Migration migration = Migration.loadLatestBaseline(project);
				if (migration == null)
					throw new SimpleException("There is no Baseline Migration in this Evo Project.");
				migration.execute(conn, null, null, null);
				migrationHistoryTable = MigrationHistoryTable.build(conn, config);
				migrationHistoryTable.addMigrationToHistory(migration);
				migrationHistoryTable.setMigrationAsCurrent(migration);
			}
			
			project.setMigrationHistoryTable(migrationHistoryTable);
			Driver.setNewProject(config.getProjectName(), root);
			return project;
		} catch (SQLException e) {
			throw new SimpleException("There was an error while connecting to the database.", e);
		} catch (StatementException e) {
			throw new SimpleException("There was an error while executing the first migration.", e);
		}
	}
	
	public static boolean exist(Path root) throws SimpleException {
		File rootFolder = new File(root.toString());
		if (!rootFolder.exists() || !rootFolder.isDirectory()
				|| !Config.exist(Paths.get(root.toString(), Project.CONFIG_FILE_NAME)))
			return false;
		
		Config config = Config.load(Paths.get(root.toString(), Project.CONFIG_FILE_NAME));
		return StagedMigration.exist(Paths.get(root.toString(), Project.STAGED_FOLDER_NAME,
						Project.STAGED_FILE_NAME)) &&
				Filters.exist(Paths.get(root.toString(), Project.FILTERS_FILE_NAME));
	}
}
