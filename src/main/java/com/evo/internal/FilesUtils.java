package com.evo.internal;

import java.io.File;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FilesUtils {
	
	public static List<Path> filterFilesRecursivelyByName(final Path baseFolder, final String pattern)
			throws NotDirectoryException {
		return FilesUtils.filterFilesRecursivelyByName(new File(baseFolder.toString()), pattern);
	}
	
	/**
	 * Traverse baseFolder recursively comparing the pattern provided with the name of each file
	 *
	 * @param baseFolder folder where the search begins
	 * @param pattern regex is used to filter the name of files
	 * @return all Paths of files inside baseFolder where their name matches the given pattern
	 */
	private static List<Path> filterFilesRecursivelyByName(final File baseFolder, final String pattern)
			throws NotDirectoryException {
		return FilesUtils.filterFilesRecursivelyByName(baseFolder, pattern, new ArrayList<>());
	}
	
	private static List<Path> filterFilesRecursivelyByName(final File baseFolder, final String pattern,
	                                                        List<Path> fileAbsolutePathsList)
			throws NotDirectoryException {
		if (!baseFolder.exists() || !baseFolder.isDirectory())
			throw new NotDirectoryException(baseFolder.getAbsolutePath());
		for (final File f : Objects.requireNonNull(baseFolder.listFiles())) {
			if (f.isDirectory())
				FilesUtils.filterFilesRecursivelyByName(f, pattern, fileAbsolutePathsList);
			
			if (f.isFile() && f.getName().matches(pattern))
				fileAbsolutePathsList.add(Paths.get(f.getAbsolutePath()));
		}
		return fileAbsolutePathsList;
	}
}
