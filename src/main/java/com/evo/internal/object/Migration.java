package com.evo.internal.object;

import com.evo.exception.SimpleException;
import com.evo.internal.FilesUtils;
import com.evo.internal.grammar.StatementsParsed;
import com.evo.internal.history.MigrationInfo;
import com.evo.internal.history.MigrationHistoryTable;
import com.evo.internal.rdbms.GetRDBMS;
import com.evo.internal.rdbms.dump.IDumperPerRdbms;
import com.evo.internal.rdbms.exception.SqlDumperException;
import com.evo.internal.rdbms.operations.IOperationsPerRdbms;
import com.evo.vcs.exception.StatementException;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.zip.CRC32;

public class Migration extends AbstractFileWithStatements implements IMigration {
	
	public static class Type {
		public final static String REGULAR = "V";
		public final static String UNDO = "U";
		public final static String BASELINE = "B";
		public final static String DEPRECATION = "D";
		public final static String STAGED = "S";
		
		private String type;
		
		private Type(String type) {
			this.type = type;
		}
		
		public static Type getValidType(String type) throws Exception {
			if (!Type.isValidType(type))
				throw new Exception("Invalid migration type.");
			return new Type(type);
		}
		
		public static String getTypeInFullName(String type) throws SimpleException {
			switch (type) {
				case Type.REGULAR:
					return "regular";
				case Type.UNDO:
					return "undo";
				case Type.BASELINE:
					return "baseline";
				case Type.DEPRECATION:
					return "deprecation";
				case Type.STAGED:
					return "staged";
				default:
					throw new SimpleException("There is no type '" + type + "'.");
			}
		}
		
		public static boolean isValidType(String type) {
			switch (type) {
				case Type.REGULAR:
				case Type.UNDO:
				case Type.BASELINE:
				case Type.DEPRECATION:
					return true;
				default:
					return false;
			}
		}
	}
	
	public static String FIRST_VERSION = "0";
	
	public static String DESCRIPTION_FIRST_VERSION = "create_initial_schemas";
	
	private String type;
	
	private Version version;
	
	private String description;
	
	Migration(Path fileAbsolutePath, List<Statement> statements, String type, Version version, String description) {
		super(fileAbsolutePath, statements);
		this.type = type;
		this.version = version;
		this.description = description;
	}
	
	public String getType() {
		return this.type;
	}
	
	public Version getVersion() {
		return this.version;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public String getFilename() {
		return this.getFilename(true);
	}
	
	public String getFilenameWithoutExtension() {
		return this.getFilename(false);
	}
	
	private String getFilename(boolean withExtension) {
		String description = Migration.getValidDescription(this.description);
		return this.type + this.version.getVersion() + (description.isEmpty() ? "" : "__" + description) + (withExtension ? ".sql" : "");
	}
	
	public int getChecksum() {
		final CRC32 crc32 = new CRC32();
		crc32.update(this.getType().getBytes(StandardCharsets.UTF_8));
		crc32.update(this.getVersion().getVersion().getBytes(StandardCharsets.UTF_8));
		crc32.update(this.getDescription().getBytes(StandardCharsets.UTF_8));
		for (Statement stmt : this.getStatements())
			crc32.update(stmt.getRawStatement().getBytes(StandardCharsets.UTF_8));
		return (int) crc32.getValue();
	}
	
	public void execute(Connection conn, MigrationHistoryTable migrationHistoryTable,
	                    Consumer<Statement> successEachStatementCallback,
	                    BiConsumer<Statement, StatementException> errorEachStatementCallback)
			throws StatementException {
		for (Statement stmt : this.getStatements()) {
			try {
				if (migrationHistoryTable != null) {
					// It can happen that a DDL statement is executed, but it is not
					// stored as last statement executed. However, DDL statements we can check in the schema to know
					// if it was performed or not. There is no way to check if a DML statement was executed or not,
					// so it is important to be sure that if a DML statement was executed, it was stored as
					// the last statement executed.
					// This allows the developer to set the state of things correctly so the deployment can resume
					// exactly where it failed
					boolean isDML = stmt.isDML();
					if (isDML)
						conn.setAutoCommit(false);
					stmt.execute(conn);
					migrationHistoryTable.setStatementAsLastExecuted(this, stmt);
					if (isDML)
						conn.setAutoCommit(true);
				} else {
					stmt.execute(conn);
				}
				
				if (successEachStatementCallback != null)
					successEachStatementCallback.accept(stmt);
			} catch (StatementException e) {
				if (errorEachStatementCallback != null)
					errorEachStatementCallback.accept(stmt, e);
				throw e;
			} catch (SimpleException | SQLException e) {
				String errorCause = e instanceof SimpleException ?
						StatementException.ERROR_CAUSE_EVO :
						StatementException.ERROR_CAUSE_TRANSACTION;
				StatementException statementException = new StatementException(stmt, e, errorCause);
				if (errorEachStatementCallback != null)
					errorEachStatementCallback.accept(stmt, statementException);
				throw statementException;
			}
		}
	}
	
	public static String getFilenamePattern(String type) throws SimpleException {
		if (!Type.isValidType(type))
			throw new SimpleException("There is no type '" + type + "'.");
		if (type.equals(Type.DEPRECATION))
			return "^" + type + "(?:" + Version.versionPattern + ")(?:__[0-9]{4}-[0-9]{2}-[0-9]{2})(?:__(.*)){0,1}\\.sql$";
		else
			return "^" + type + "(?:" + Version.versionPattern + ")(?:__(.*)){0,1}\\.sql$";
	}
	
	public static String getFilenamePattern(String type, Version version) throws SimpleException {
		if (!Type.isValidType(type))
			throw new SimpleException("There is no type '" + type + "'.");
		if (type.equals(Type.DEPRECATION))
			return "^" + type + version.getVersion() + "(?:__[0-9]{4}-[0-9]{2}-[0-9]{2})(?:__(.*)){0,1}\\.sql$";
		else
			return "^" + type + version.getVersion() + "(?:__(.*)){0,1}\\.sql$";
	}
	
	private static boolean isValidFilename(String filename) throws SimpleException {
		return filename.matches(Migration.getFilenamePattern(Migration.getMigrationTypeByFilename(filename)));
	}
	
	private static String getValidDescription(String description) {
		if (description == null)
			return "";
		return StringUtils.stripAccents(description).replaceAll(" ", "_");
	}
	
	private static Date getValidDate(String date) throws SimpleException {
		if (date == null)
			throw new SimpleException("");
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			df.setLenient(false);
			return df.parse(date);
		} catch (ParseException e) {
			throw new SimpleException("", e);
		}
	}
	
	private static String getMigrationTypeByFilename(String filename) throws SimpleException {
		if (filename.equals(Project.STAGED_FILE_NAME))
			return Type.STAGED;
		String type = filename.substring(0, 1);
		if (Type.isValidType(type))
			return type;
		throw new SimpleException("'" + filename + "' is not a valid name for a migration file. " +
				"First character is not valid.");
	}
	
	protected static Version getVersionByFilename(String filename) throws SimpleException {
		String version = filename.split("__", 2)[0].substring(1);
		if (version.endsWith(".sql"))
			version = version.substring(0, version.length() - 4);
		return new Version(version);
	}
	
	private static String getDescriptionByFilename(String filename) throws SimpleException {
		int numberOfParts = 2;
		if (Migration.getMigrationTypeByFilename(filename).equals(Type.DEPRECATION))
			numberOfParts = 3;
		
		String[] filenameParts = filename.split("__", numberOfParts);
		if (filenameParts.length < numberOfParts)
			return "";
		
		String description = filenameParts[numberOfParts - 1];
		if (description.endsWith(".sql"))
			description = description.substring(0, description.length() - 4);
		return description;
	}
	
	private static Date getDateByFilename(String filename) throws SimpleException {
		String[] filenameParts = filename.split("__", 2);
		if (filenameParts.length < 2 && Migration.getMigrationTypeByFilename(filename).equals(Type.DEPRECATION))
			throw new SimpleException("There is no date associated for this migration file '" + filename + "'.");
		return Migration.getValidDate(filenameParts[1]);
	}
	
	private static String getStringDateByFilename(String filename) throws SimpleException {
		return new SimpleDateFormat("yyyy-MM-dd").format(Migration.getDateByFilename(filename));
	}
	
	public static Migration buildRegular(Project project, StagedMigration stagedMigration, String version,
	                                     String description) throws SimpleException {
		return Migration.build(project, stagedMigration, version, description, Type.REGULAR);
	}
	
	public static Migration buildBaseline(Project project, String version, String description)
			throws SimpleException {
		Version thisVersion = new Version(version);
		description = Migration.getValidDescription(description);
		String filename = Type.BASELINE + version + (description.isEmpty() ? "" : "__" + description) + ".sql";
		
		GetRDBMS getRdbms = new GetRDBMS(project.getDatabaseConnection(), project.getConfig());
		IDumperPerRdbms dumper = getRdbms.getDumperPerRdbms();
		IOperationsPerRdbms operations = getRdbms.getOperationsPerRdbms();
		
		try {
			List<String> existingSchemas = new ArrayList<>();
			for (String schema : project.getConfig().getSchemas()) {
				if (operations.existSchema(project.getDatabaseConnection(), schema))
					existingSchemas.add(schema);
			}
			String content = dumper.getCreateSchemasStatements(existingSchemas);
			
			Files.write(Paths.get(project.getMigrationsDirPath().toString(), filename),
					content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW);
			
			StatementsParsed statementsParsed = new StatementsParsed(content);
			return new Migration(Paths.get(project.getMigrationsDirPath().toString(), filename),
					statementsParsed.getStatementsOrComments(), Type.BASELINE, thisVersion, description);
		} catch (IOException | SQLException | SqlDumperException e) {
			throw new SimpleException("There was an error while creating a migration.", e);
		}
	}
	
	public static Migration buildDeprecation(Project project, StagedMigration stagedMigration, String version,
	                                         String deprecationDate, String description) throws SimpleException {
		// if this fails, it means that deprecationDate is not valid
		Migration.getValidDate(deprecationDate);
		description = Migration.getValidDescription(description);
		String deprecationDescription = deprecationDate + (description.equals("") ? "" : "__" + description);
		return Migration.build(project, stagedMigration, version, deprecationDescription, Type.DEPRECATION);
	}
	
	private static Migration build(Project project, StagedMigration stagedMigration, String version,
	                               String description, String migrationType) throws SimpleException {
		Version thisVersion = new Version(version);
		if (stagedMigration.getStatements().size() == 0)
			throw new SimpleException("Staged migration has no statement.");
		
		description = Migration.getValidDescription(description);
		String filename = migrationType + version + (description.isEmpty() ? "" : "__" + description) + ".sql";
		
		if (Migration.loadByVersion(project, thisVersion, migrationType) != null)
			throw new SimpleException("There is already a migration with the version provided.");
		try {
			Files.copy(
					stagedMigration.getFileAbsolutePath(),
					Paths.get(project.getMigrationsDirPath().toString(), filename)
			);
			
			return new Migration(Paths.get(project.getMigrationsDirPath().toString(), filename),
					stagedMigration.getStatements(), migrationType, thisVersion, description);
		} catch (IOException e) {
			throw new SimpleException("There was an error while creating a migration.", e);
		}
	}
	
	public static Migration load(Project project, String filename) throws SimpleException {
		return Migration.load(Paths.get(project.getMigrationsDirPath().toString(), filename));
	}
	
	public static Migration load(Path fileAbsolutePath) throws SimpleException {
		String filename = fileAbsolutePath.getFileName().toString();
		if (!Migration.isValidFilename(filename))
			throw new SimpleException("'" + filename + "' is not a valid filename for a migration.");
		
		if (!fileAbsolutePath.toFile().exists() || !fileAbsolutePath.toFile().isFile())
			return null;
		try {
			StatementsParsed statementsParsed = new StatementsParsed(fileAbsolutePath);
			String type = Migration.getMigrationTypeByFilename(filename);
			String description = (type.equals(Type.DEPRECATION) ? Migration.getStringDateByFilename(filename) + "__" : "") +
					Migration.getDescriptionByFilename(filename);
			return new Migration(fileAbsolutePath, statementsParsed.getStatementsOrComments(),
					Migration.getMigrationTypeByFilename(filename), Migration.getVersionByFilename(filename),
					description);
		} catch (IOException e) {
			throw new SimpleException("'" + filename + "' is not a valid migration file.", e);
		}
	}
	
	public static Migration loadRegularByVersion(Project project, Version version) throws SimpleException {
		return Migration.loadByVersion(project, version, Type.REGULAR);
	}
	
	public static Migration loadLatestRegular(Project project) throws SimpleException {
		return Migration.loadLatest(project, Type.REGULAR);
	}
	
	public static Migration loadLatestBaseline(Project project) throws SimpleException {
		return Migration.loadLatest(project, Type.BASELINE);
	}
	
	public static Migration loadDeprecationByVersion(Project project, Version version) throws SimpleException {
		return Migration.loadByVersion(project, version, Type.DEPRECATION);
	}
	
	private static Migration loadByVersion(Project project, Version version, String migrationType)
			throws SimpleException {
		try {
			List<Path> migrationsPaths = FilesUtils.filterFilesRecursivelyByName(project.getMigrationsDirPath(),
					Migration.getFilenamePattern(migrationType, version));
			if (migrationsPaths.size() == 0)
				return null;
			if (migrationsPaths.size() > 1)
				throw new SimpleException("There is conflict with the following Migration version: '" + version + "'.");
			return Migration.load(migrationsPaths.get(0));
		} catch (NotDirectoryException e) {
			throw new SimpleException("There is no migrations path for Evo project.", e);
		}
	}
	
	public static List<Migration> loadMigrationsToExecute(Project project, Version targetVersion)
			throws SimpleException {
		try {
			List<Path> migrationsPaths = FilesUtils.filterFilesRecursivelyByName(project.getMigrationsDirPath(),
					Migration.getFilenamePattern(Migration.Type.REGULAR));
			
			List<Migration> migrationsToExecute = new ArrayList<>();
			MigrationInfo currentMigrationInfo = project.getMigrationHistoryTable().getCurrentMigrationInfo();
			for (Path path : migrationsPaths) {
				Version iterationVersion = Migration.getVersionByFilename(path.getFileName().toString());
				if (currentMigrationInfo.getVersion().isHigherOrEqualThan(iterationVersion))
					continue;
				if (targetVersion.isLowerThan(iterationVersion))
					continue;
				
				migrationsToExecute.add(Migration.load(project, path.getFileName().toString()));
			}
			
			Collections.sort(migrationsToExecute, (left, right) -> {
				if (left.version.isLowerThan(right.getVersion()))
					return -1;
				if (left.version.isHigherThan(right.getVersion()))
					return 1;
				return 0;
			});
			
			migrationsToExecute.addAll(Migration.getDeprecationMigrationsToExecute(project, targetVersion));
			return migrationsToExecute;
		} catch (NotDirectoryException e) {
			throw new SimpleException("There is no migrations path for Evo project.", e);
		}
	}
	
	private static Migration loadLatest(Project project, String migrationType) throws SimpleException {
		try {
			List<Path> migrationsPaths = FilesUtils.filterFilesRecursivelyByName(project.getMigrationsDirPath(),
					Migration.getFilenamePattern(migrationType));
			
			Path latestPath = null;
			for (Path path : migrationsPaths) {
				if (latestPath == null)
					latestPath = path;
				else if (path.getFileName().toString().compareTo(latestPath.getFileName().toString()) > 0)
					latestPath = path;
			}
			if (latestPath == null)
				return null;
			return Migration.load(latestPath);
		} catch (NotDirectoryException e) {
			throw new SimpleException("There is no migrations path for Evo project.", e);
		}
	}
	
	public static List<Migration> getConflicts(Project project) throws SimpleException {
		List<Migration> conflicts = Migration.getConflictsByMigrationType(project, Type.REGULAR);
		//conflicts.addAll(Migration.getConflictsByMigrationType(project, Type.UNDO));
		conflicts.addAll(Migration.getConflictsByMigrationType(project, Type.BASELINE));
		conflicts.addAll(Migration.getConflictsByMigrationType(project, Type.DEPRECATION));
		return conflicts;
	}
	
	private static List<Migration> getConflictsByMigrationType(Project project, String migrationType) throws SimpleException {
		List<Migration> conflicts = new ArrayList<>();
		List<String> migrationsFilenames = Migration.getMigrationsFilenames(project, migrationType);
		for (String migrationFilename : migrationsFilenames) {
			MigrationInfo migrationInHistory = project.getMigrationHistoryTable()
					.getSpecificMigrationInfo(
							Migration.getMigrationTypeByFilename(migrationFilename),
							Migration.getVersionByFilename(migrationFilename)
					);
			// it means that there is no conflict
			if (migrationInHistory == null)
				continue;
			Migration migrationInFile = Migration.load(project, migrationFilename);
			if (migrationInHistory.getChecksum() != migrationInFile.getChecksum())
				conflicts.add(migrationInFile);
		}
		return conflicts;
	}
	
	public static List<Migration> getNewPastMigrations(Project project) throws SimpleException {
		List<Migration> newPastMigrations = new ArrayList<>();
		Version currentVersion = project.getMigrationHistoryTable().getCurrentMigrationInfo().getVersion();
		List<String> migrationsFilenames = Migration.getMigrationsFilenames(project, Type.REGULAR);
		for (String filename : migrationsFilenames) {
			Version iterationVersion = Migration.getVersionByFilename(filename);
			// current migration has version higher than iterationVersion (iterationVersion is in the past)
			// and migration of iterationVersion is not in Migration History Table (new migration)
			if (
					currentVersion.isHigherThan(iterationVersion) &&
					!project.getMigrationHistoryTable().thereIsThisVersionInHistory(Type.REGULAR, iterationVersion)
			) {
				newPastMigrations.add(Migration.load(project, filename));
			}
		}
		return newPastMigrations;
	}
	
	public static List<MigrationInfo> getLostPastMigrations(Project project) throws SimpleException {
		List<MigrationInfo> lostPastMigrations = new ArrayList<>();
		Version currentVersion = project.getMigrationHistoryTable().getCurrentMigrationInfo().getVersion();
		
		List<MigrationInfo> baselineMigrations = project.getMigrationHistoryTable().getAllMigrationsInfoByType(Type.BASELINE);
		// there is only one baseline
		// and first migration should always be a baseline
		MigrationInfo baselineMigrationInfo = baselineMigrations.size() > 0 ? baselineMigrations.get(0) : null;
		
		List<MigrationInfo> migrationsInfo = project.getMigrationHistoryTable().getAllMigrationsInfoByType(Type.REGULAR);
		for (MigrationInfo migrationInfo : migrationsInfo) {
			Version iterationVersion = migrationInfo.getVersion();
			Migration iterationMigration = Migration.load(project, migrationInfo.getFilename());
			// current migration has version higher than iterationVersion (iterationVersion is in the past)
			// and migration of iterationVersion is not a file (lost migration)
			if (
					iterationVersion.isLowerThan(currentVersion) &&
					(
							// if there is a baseline
							baselineMigrationInfo != null &&
							iterationVersion.isHigherOrEqualThan(baselineMigrationInfo.getVersion())
					) &&
					iterationMigration == null
			) {
				lostPastMigrations.add(migrationInfo);
			}
		}
		return lostPastMigrations;
	}
	
	public static List<Migration> getNewFutureMigrations(Project project) throws SimpleException {
		List<Migration> newFutureMigrations = new ArrayList<>();
		Version currentVersion = project.getMigrationHistoryTable().getCurrentMigrationInfo().getVersion();
		List<String> migrationsFilenames = Migration.getMigrationsFilenames(project, Type.REGULAR);
		for (String filename : migrationsFilenames) {
			Version iterationVersion = Migration.getVersionByFilename(filename);
			// current migration has version lower than iterationVersion (iterationVersion is in the future)
			// and migration of iterationVersion is not in Migration History Table (new migration)
			if (
					currentVersion.isLowerThan(iterationVersion) &&
					!project.getMigrationHistoryTable().thereIsThisVersionInHistory(Type.REGULAR, iterationVersion)
			) {
				newFutureMigrations.add(Migration.load(project, filename));
			}
		}
		return newFutureMigrations;
	}
	
	public static List<MigrationInfo> getLostFutureMigrations(Project project) throws SimpleException {
		List<MigrationInfo> lostFutureMigrations = new ArrayList<>();
		Version currentVersion = project.getMigrationHistoryTable().getCurrentMigrationInfo().getVersion();
		List<MigrationInfo> migrationsInfo = project.getMigrationHistoryTable().getAllMigrationsInfo();
		for (MigrationInfo migrationInfo : migrationsInfo) {
			Version iterationVersion = migrationInfo.getVersion();
			Migration iterationMigration = Migration.load(project, migrationInfo.getFilename());
			// current migration has version higher than iterationVersion (iterationVersion is in the past)
			// and migration of iterationVersion is not a file (lost migration)
			if (
					currentVersion.isLowerThan(iterationVersion) &&
					iterationMigration == null
			) {
				lostFutureMigrations.add(migrationInfo);
			}
		}
		return lostFutureMigrations;
	}
	
	public static List<Migration> getDeprecationMigrationsToExecute(Project project, Version compareVersion)
			throws SimpleException {
		List<Migration> deprecationMigrationsToExecute = new ArrayList<>();
		if (compareVersion == null)
			return deprecationMigrationsToExecute;
		
		List<String> migrationsFilenames = Migration.getMigrationsFilenames(project, Type.DEPRECATION);
		
		Calendar calCurrent = Calendar.getInstance();
		calCurrent.setTime(new Date());
		int currentDayOfYear = calCurrent.get(Calendar.DAY_OF_YEAR);
		int currentYear = calCurrent.get(Calendar.YEAR);
		
		Calendar calIterationDay = Calendar.getInstance();
		for (String filename : migrationsFilenames) {
			Version iterationVersion = Migration.getVersionByFilename(filename);
			Date iterationDate = Migration.getDateByFilename(filename);
			calIterationDay.setTime(iterationDate);
			int iterationDayOfYear = calIterationDay.get(Calendar.DAY_OF_YEAR);
			int iterationYear = calIterationDay.get(Calendar.YEAR);
			
			// iterationVersion is in the "past" or "present"
			// iterationDate is in the past or present
			// and migration of iterationVersion is not in Migration History Table (new migration)
			if (
					iterationVersion.isLowerOrEqualThan(compareVersion) &&
					(iterationYear < currentYear || (iterationYear == currentYear && iterationDayOfYear <= currentDayOfYear)) &&
					!project.getMigrationHistoryTable().thereIsThisVersionInHistory(Type.DEPRECATION, iterationVersion)
			) {
				deprecationMigrationsToExecute.add(Migration.load(project, filename));
			}
		}
		return deprecationMigrationsToExecute;
	}
	
	public static List<Migration> getDeprecationMigrationsNotToExecute(Project project, Version compareVersion)
			throws SimpleException {
		List<Migration> deprecationMigrationsNotToExecute = new ArrayList<>();
		List<String> migrationsFilenames = Migration.getMigrationsFilenames(project, Type.DEPRECATION);
		if (compareVersion == null) {
			for (String filename : migrationsFilenames)
				deprecationMigrationsNotToExecute.add(Migration.load(project, filename));
			return deprecationMigrationsNotToExecute;
		}
		
		Calendar calToday = Calendar.getInstance();
		calToday.setTime(new Date());
		int currentDayOfYear = calToday.get(Calendar.DAY_OF_YEAR);
		int currentYear = calToday.get(Calendar.YEAR);
		
		Calendar calIterationDay = Calendar.getInstance();
		for (String filename : migrationsFilenames) {
			Version iterationVersion = Migration.getVersionByFilename(filename);
			Date iterationDate = Migration.getDateByFilename(filename);
			calIterationDay.setTime(iterationDate);
			int iterationDayOfYear = calIterationDay.get(Calendar.DAY_OF_YEAR);
			int iterationYear = calIterationDay.get(Calendar.YEAR);
			
			// iterationVersion is in the "future"
			// iterationDate is in the future
			// and migration of iterationVersion is not in Migration History Table (new migration)
			if (
					iterationVersion.isHigherThan(compareVersion) &&
					((iterationYear == currentYear && iterationDayOfYear > currentDayOfYear) || iterationYear > currentYear) &&
					!project.getMigrationHistoryTable().thereIsThisVersionInHistory(Type.DEPRECATION, iterationVersion)
			) {
				deprecationMigrationsNotToExecute.add(Migration.load(project, filename));
			}
		}
		return deprecationMigrationsNotToExecute;
	}
	
	private static List<Path> getMigrationsPaths(Project project, String migrationType) throws SimpleException {
		try {
			return FilesUtils.filterFilesRecursivelyByName(project.getMigrationsDirPath(),
					Migration.getFilenamePattern(migrationType));
		} catch (NotDirectoryException e) {
			throw new SimpleException("There is no migrations path for Evo project.", e);
		}
	}
	
	private static List<String> getMigrationsFilenames(Project project, String migrationType) throws SimpleException {
		List<String> migrationsFilenames = new ArrayList<>();
		for (Path migrationPath : Migration.getMigrationsPaths(project, migrationType))
			migrationsFilenames.add(migrationPath.getFileName().toString());
		return migrationsFilenames;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Migration)) return false;
		Migration migration = (Migration) o;
		return getChecksum() == migration.getChecksum();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getChecksum());
	}
}
