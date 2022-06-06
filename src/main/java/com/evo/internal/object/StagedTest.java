package com.evo.internal.object;

import com.evo.exception.SimpleException;
import com.evo.internal.FilesUtils;
import com.evo.internal.grammar.StatementsParsed;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class StagedTest extends AbstractTest implements ITest {
	
	private StagedTest(Path fileAbsolutePath, List<Statement> statements) {
		super(fileAbsolutePath, statements);
	}
	
	private static StagedTest load(Path fileAbsolutePath) throws SimpleException {
		if (!fileAbsolutePath.toFile().exists())
			throw new SimpleException("'" + fileAbsolutePath.getFileName().toString() + "' is not a valid test file.");
		
		try {
			StatementsParsed statementsParsed = new StatementsParsed(fileAbsolutePath);
			return new StagedTest(fileAbsolutePath, statementsParsed.getStatementsOrComments());
		} catch (IOException e) {
			throw new SimpleException("'" + fileAbsolutePath.getFileName().toString() + "' is not a valid test file.", e);
		}
	}
	
	public static void executeAll(Project project, Consumer<ITest> successEachTestCallback,
	                              Consumer<ITest> errorEachTestCallback) throws SimpleException {
		AbstractTest.executeAll(StagedTest.getStagedTests(project), project.getDatabaseConnection(), successEachTestCallback,
				errorEachTestCallback);
	}
	
	private static List<ITest> getStagedTests(Project project) throws SimpleException {
		List<ITest> tests = new ArrayList<>();
		for (Path testPath : StagedTest.getStagedTestsPaths(project))
			tests.add(StagedTest.load(testPath));
		return tests;
	}
	
	private static List<Path> getStagedTestsPaths(Project project) {
		try {
			return FilesUtils.filterFilesRecursivelyByName(
					project.getStagedTestsDirPath(),
					AbstractTest.TEST_NAME_PATTERN
			);
		} catch (NotDirectoryException e) {
			return new ArrayList<>();
		}
	}
}
