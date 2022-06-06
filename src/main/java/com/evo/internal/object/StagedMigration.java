package com.evo.internal.object;

import com.evo.exception.SimpleException;
import com.evo.internal.grammar.StatementsParsed;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class StagedMigration extends Migration implements IMigration {
	
	private StagedMigration(Path path, List<Statement> statements, Version version) {
		super(path, statements, Type.STAGED, version, "staged");
	}
	
	@Override
	public String getFilename() {
		return Project.STAGED_FILE_NAME;
	}
	
	public void append(Statement statement) throws SimpleException {
		String stmt = statement.getStatement();
		if (!stmt.endsWith(System.getProperty("line.separator")))
			stmt += System.getProperty("line.separator");
		try {
			Files.write(this.getFileAbsolutePath(), stmt.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
			super.appendStatement(statement);
		} catch (IOException e) {
			throw new SimpleException("There was an error while appending one statement.", e);
		}
	}
	
	public static StagedMigration build(Path path) throws SimpleException {
		File file = new File(path.toString());
		try {
			Files.createDirectories(file.getParentFile().toPath());
			file.createNewFile();
			return new StagedMigration(path, new ArrayList<>(), new Version("staged"));
		} catch (IOException e) {
			throw new SimpleException("There was an error while creating '" + file.getName() + "'.", e);
		}
	}
	
	public static StagedMigration load(Path path) throws SimpleException {
		File file = new File(path.toString());
		if (!file.exists())
			throw new SimpleException("There is no valid staged migration file.");
		
		try {
			StatementsParsed statementsParsed = new StatementsParsed(path);
			return new StagedMigration(path, statementsParsed.getStatementsOrComments(), new Version("staged"));
		} catch (IOException e) {
			throw new SimpleException("'" + path.getFileName().toString() + "' is not a valid staged migration file.", e);
		}
	}
	
	public static boolean exist(Path path) throws SimpleException {
		File file = new File(path.toString());
		return file.exists() && file.isFile();
	}
	
	public static void truncate(Path path) throws SimpleException {
		try {
			Files.write(path, new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			throw new SimpleException("There was an error while deleting '" + path.getFileName().toString() + "'.", e);
		}
	}
}
