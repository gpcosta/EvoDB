package com.evo.vcs.command;

import com.evo.exception.SimpleException;
import com.evo.internal.object.ITest;
import com.evo.internal.object.Migration;
import com.evo.internal.object.Project;
import com.evo.vcs.exception.StatementException;
import picocli.CommandLine;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

abstract public class AbstractCommand {
	
	protected Project project = null;
	
	private final String userHome = System.getProperty("user.home");
	
	private final String userDir = System.getProperty("user.dir");
	
	public Project getProject() throws SimpleException {
		return this.project;
	}
	
	protected Path getUserDirPath() {
		return Paths.get(this.userDir);
	}
	
	/*protected void createFileIfNotExistsAndOpenWithDefaultEditor(Path filepath) throws IOException {
		this.createFileIfNotExistsAndOpenWithDefaultEditor(filepath, "");
	}
	
	protected void createFileIfNotExistsAndOpenWithDefaultEditor(Path filepath, String initialContent) throws IOException {
		File file = new File(filepath.toString());
		Files.createDirectories(file.getParentFile().toPath());
		if (!file.exists()) {
			file.createNewFile();
			
			if (!initialContent.equals("")) {
				try (FileOutputStream outputStream = new FileOutputStream(file)) {
					outputStream.write(initialContent.getBytes());
				}
			}
		}
		
		java.awt.Desktop.getDesktop().edit(file);
	}*/
	
	protected Consumer<Migration> getSuccessCallbackForEachMigration() {
		return (Migration migration) -> System.out.println(CommandLine.Help.Ansi.AUTO.string(
				"@|fg(green) - " + migration.getFilename() + "|@ migration was successful."
		));
	}
	
	protected BiConsumer<Migration, StatementException> getErrorCallbackForEachMigration() {
		return (Migration migration, StatementException e) -> System.err.println(CommandLine.Help.Ansi.AUTO.string(
				"@|fg(red) - " + migration.getFilename() + "|@ migration was not successful. " +
						"There was an error in the following statement: " + e.getStatement().getStatement()
		));
	}
	
	protected Consumer<String> getSuccessCallbackForDropSchema() {
		return (String schemaName) -> System.out.println("@|fg(green) Schema " + schemaName + " dropped successfully.");
	}
	
	protected BiConsumer<String, SQLException> getErrorCallbackForDropSchema() {
		return (String schemaName, SQLException e) -> System.err.println("@|fg(red) Schema " + schemaName + " dropped successfully.");
	}
	
	protected Consumer<ITest> getSuccessCallbackForEachTest() {
		return (ITest test) -> System.out.println(CommandLine.Help.Ansi.AUTO.string(
				"- @|fg(green) " + test.getFilename() + " was successful.|@"
		));
	}
	
	protected Consumer<ITest> getErrorCallbackForEachTest() {
		return (ITest test) -> System.err.println(CommandLine.Help.Ansi.AUTO.string(
				"- @|fg(red) " + test.getFilename() + " was not successful.|@"
		));
	}
}
