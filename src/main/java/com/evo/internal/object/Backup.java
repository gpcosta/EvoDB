package com.evo.internal.object;

import com.evo.exception.SimpleException;
import com.evo.internal.FilesUtils;
import com.evo.internal.object.Project;
import com.evo.internal.rdbms.GetRDBMS;
import com.evo.internal.rdbms.dump.IDumperPerRdbms;
import com.evo.internal.rdbms.exception.SqlDumperException;
import com.evo.internal.rdbms.operations.IOperationsPerRdbms;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Backup {
	
	private Project project;
	
	private Path backupPath;
	
	private Backup(Project project, Path backupPath) {
		this.project = project;
		this.backupPath = backupPath;
	}
	
	public void restore() throws SimpleException {
		try {
			GetRDBMS getRDBMS = new GetRDBMS(this.project.getDatabaseConnection(), this.project.getConfig());
			IOperationsPerRdbms operations = getRDBMS.getOperationsPerRdbms();
			
			List<Path> backupsPaths = FilesUtils.filterFilesRecursivelyByName(
					this.project.getBackupsDirPath(),
					"^backup_[0-9]{4}_[0-9]{2}_[0-9]{2}T[0-9]{2}_[0-9]{2}_[0-9]{2}\\.sql$"
			);
			
			Path mostUpdatedBackupPath = null;
			for (Path path : backupsPaths) {
				if (mostUpdatedBackupPath == null)
					mostUpdatedBackupPath = path;
				else if (path.getFileName().toString().compareTo(mostUpdatedBackupPath.getFileName().toString()) > 0)
					mostUpdatedBackupPath = path;
			}
			
			for (String schema : this.project.getConfig().getSchemas())
				operations.dropSchema(this.project.getDatabaseConnection(), schema);
			operations.loadSourceFile(this.project.getDatabaseConnection(), mostUpdatedBackupPath);
		} catch (SQLException | NotDirectoryException e) {
			throw new SimpleException("There was an error while restoring backup.", e);
		}
	}
	
	public static Backup build(Project project) throws SimpleException {
		try {
			GetRDBMS getRDBMS = new GetRDBMS(project.getDatabaseConnection(), project.getConfig());
			IDumperPerRdbms operations = getRDBMS.getDumperPerRdbms();
			String backupContent = operations.getBackupSchemasStatements(project.getConfig().getSchemas());
			
			String currentDateTime = java.time.LocalDateTime.now()
					.format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
			Path backupPath = Paths.get(project.getBackupsDirPath().toString(),
					"backup_" + currentDateTime + ".sql");
			Files.write(backupPath, backupContent.getBytes(StandardCharsets.UTF_8));
			return new Backup(project, backupPath);
		} catch (SqlDumperException | IOException e) {
			throw new SimpleException("There was an error while writing backup.", e);
		}
	}
	
	public static Backup loadLast(Project project) throws SimpleException {
		try {
			List<Path> backupsPaths = FilesUtils.filterFilesRecursivelyByName(project.getBackupsDirPath(),
					"^backup_[0-9]{4}_[0-9]{2}_[0-9]{2}_[0-9]{2}_[0-9]{2}_[0-9]{2}\\.sql$");
			Path lastBackupPath = null;
			for (Path path : backupsPaths) {
				if (lastBackupPath == null)
					lastBackupPath = path;
				else if (path.getFileName().toString().compareTo(lastBackupPath.getFileName().toString()) > 0)
					lastBackupPath = path;
			}
			if (lastBackupPath == null)
				return null;
			return new Backup(project, lastBackupPath);
		} catch (IOException e) {
			throw new SimpleException("There was an error while loading backup.", e);
		}
	}
}
