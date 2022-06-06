package com.evo.internal.object;

import java.nio.file.Path;
import java.util.List;

public interface IFileWithStatements {
	
	List<Statement> getStatements();
	
	String getFilename();
	
	Path getFileAbsolutePath();
}
