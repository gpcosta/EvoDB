package com.evo.internal.object;

import com.evo.exception.SimpleException;
import com.evo.internal.grammar.StatementsParsed;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Filters extends AbstractFileWithStatements {
	
	private static String INITIAL_COMMENT = "/* Please separate filters by semicolons (;). Each filter is a regex string. */";
	
	private List<Pattern> filters;
	
	private Filters(Path path, List<Statement> filters) throws IOException {
		super(path, filters);
		this.filters = new ArrayList<>();
		for (Statement filter : filters) {
			if (!filter.isComment())
				this.filters.add(Pattern.compile(filter.getStatement()));
		}
	}
	
	@Override
	public List<Statement> getStatements() {
		List<Statement> statements = super.getStatements();
		for (int i = statements.size() - 1; i >= 0; i--) {
			if (statements.get(i).getStatement().equals(Filters.INITIAL_COMMENT))
				statements.remove(i);
		}
		return statements;
	}
	
	public boolean isFiltered(Statement stmt) {
		return this.isFiltered(stmt.getStatement());
	}
	
	public boolean isFiltered(String stmt) {
		for (Pattern filterPattern : this.filters) {
			if (filterPattern.matcher(stmt).find())
				return true;
		}
		return false;
	}
	
	public static Filters build(Path path) throws SimpleException {
		File file = new File(path.toString());
		try {
			file.createNewFile();
			FileOutputStream outputStream = new FileOutputStream(file);
			outputStream.write(Filters.INITIAL_COMMENT.getBytes(StandardCharsets.UTF_8));
			outputStream.close();
			return new Filters(path, new ArrayList<>());
		} catch (IOException e) {
			throw new SimpleException("There was an error while creating '" + file.getName() + "'.", e);
		}
	}
	
	public static Filters load(Path path) throws SimpleException {
		File file = new File(path.toString());
		if (!file.exists())
			throw new SimpleException("There is no valid filters file.");
		
		try {
			StatementsParsed statementsParsed = new StatementsParsed(path);
			return new Filters(path, statementsParsed.getStatementsOrComments());
		} catch (IOException e) {
			throw new SimpleException("'" + path.getFileName().toString() + "' is not a valid filters file.", e);
		}
	}
	
	public static boolean exist(Path path) throws SimpleException {
		File file = new File(path.toString());
		return file.exists() && file.isFile();
	}
}
