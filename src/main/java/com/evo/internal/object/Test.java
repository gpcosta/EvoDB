package com.evo.internal.object;

import com.evo.exception.SimpleException;
import com.evo.internal.FilesUtils;
import com.evo.internal.grammar.StatementsParsed;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Test extends AbstractTest implements ITest {
	
	private Test(Path fileAbsolutePath, List<Statement> statements) {
		super(fileAbsolutePath, statements);
	}
	
	public static void build(Project project, Migration migration) throws SimpleException {
		try {
			FileUtils.copyDirectory(
					project.getStagedTestsDirPath().toFile(),
					project.getTestsDirPath(migration).toFile()
			);
		} catch (IOException e) {
			throw new SimpleException("There was an error while copying the staged tests to working directory.", e);
		}
	}
	
	public static Test load(Project project, Migration migration, String filename) throws SimpleException {
		if (!filename.matches(AbstractTest.TEST_NAME_PATTERN))
			throw new SimpleException("'" + filename + "' is not a valid filename for a test.");
		return Test.load(Paths.get(project.getTestsDirPath(migration).toString(), filename));
	}
	
	public static Test load(Path fileAbsolutePath) throws SimpleException {
		if (!fileAbsolutePath.getFileName().toString().matches(AbstractTest.TEST_NAME_PATTERN))
			throw new SimpleException("'" + fileAbsolutePath.getFileName().toString() + "' " +
					"is not a valid filename for a test.");
		try {
			StatementsParsed statementsParsed = new StatementsParsed(fileAbsolutePath);
			return new Test(fileAbsolutePath, statementsParsed.getStatementsOrComments());
		} catch (IOException e) {
			throw new SimpleException("'" + fileAbsolutePath.getFileName().toString() + "' is not a valid test file.", e);
		}
	}
	
	public static void executeAll(Project project, Migration migration, Consumer<ITest> successEachTestCallback,
	                              Consumer<ITest> errorEachTestCallback)
			throws SimpleException {
		AbstractTest.executeAll(Test.getTestsByMigration(project, migration), project.getDatabaseConnection(),
				successEachTestCallback, errorEachTestCallback);
	}
	
	private static List<ITest> getTestsByMigration(Project project, Migration migration) throws SimpleException {
		List<ITest> tests = new ArrayList<>();
		for (Path testPath : Test.getTestsPathsByMigration(project, migration))
			tests.add(Test.load(testPath));
		return tests;
	}
	
	private static List<Path> getTestsPathsByMigration(Project project, Migration migration) {
		try {
			return FilesUtils.filterFilesRecursivelyByName(
					Paths.get(project.getTestsDirPath(migration).toString()),
					AbstractTest.TEST_NAME_PATTERN
			);
		} catch (NotDirectoryException e) {
			return new ArrayList<>();
		}
	}
}
