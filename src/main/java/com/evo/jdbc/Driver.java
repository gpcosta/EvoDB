package com.evo.jdbc;

import com.evo.exception.SimpleException;
import com.evo.internal.grammar.StatementsParsed;
import com.evo.internal.object.Project;
import com.evo.internal.object.StagedMigration;
import com.evo.jdbc.info.StatementInfo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Driver<C extends Connection, PS extends PreparedStatement, S extends Statement>
		extends AbstractDriver<C, PS, S> {
	
	static {
		try {
			DriverManager.registerDriver(new Driver<>());
		} catch (SQLException e) {
			// eat
		}
	}
	
	private static final Path jdbcPropertiesPath =
			Paths.get(System.getProperty("user.home"), ".evo", "jdbc.properties");
	
	private String projectName = null;
	
	public Driver() {
		super(
				"evo",
				(Class<C>) Connection.class,
				(Class<PS>) PreparedStatement.class,
				(Class<S>) Statement.class
		);
		this.setEventListener(new EventListenerImpl(this));
	}
	
	@Override
	public C connect(String url, Properties info) throws SQLException {
		this.projectName = this.getProjectNameFromUrl(url);
		if (this.projectName == null)
			return null;
		this.setLabel("evo:" + this.projectName);
		return super.connect(url, info, this.readJdbcDriver(this.projectName));
	}
	
	private String getProjectNameFromUrl(String url) {
		Pattern pattern = Pattern.compile("^jdbc:evo:([a-zA-Z]+):.+$");
		Matcher matcher = pattern.matcher(url);
		if (!matcher.find())
			return null;
		return matcher.group(1);
	}
	
	private String readJdbcDriver(String projectName) throws SQLException {
		Path evoProjectPath = Paths.get(Driver.loadProperties().getProperty(projectName));
		Properties evoConfig = Driver.loadProperties(Paths.get(evoProjectPath.toString(), Project.CONFIG_FILE_NAME));
		return evoConfig.getProperty("jdbc_driver");
	}
	
	protected void storeStatementsIntoStagedMigration(StatementInfo statementInfo) throws SQLException {
		try {
			String projectRoot = Driver.loadProperties().getProperty(this.projectName);
			Project project = Project.load(Paths.get(projectRoot));
			StagedMigration stagedMigration = StagedMigration.load(project.getStagedMigrationPath());
			List<com.evo.internal.object.Statement> statements = new StatementsParsed(statementInfo.getStatement())
					.getStatementsOrComments();
			
			for (com.evo.internal.object.Statement statement : statements) {
				if (!project.getFilters().isFiltered(statement))
					stagedMigration.append(statement);
			}
		} catch (SimpleException e) {
			throw new SQLException("There was an error while storing the statements in staged migration.", e);
		}
	}
	
	public static void setNewProject(String projectName, Path projectRoot) throws SQLException {
		Driver.writeProperty(projectName, projectRoot.toString());
	}
	
	private static void writeProperty(String name, String value) throws SQLException {
		Properties jdbcProperties;
		if (Driver.jdbcPropertiesPath.toFile().exists())
			jdbcProperties = Driver.loadProperties();
		else
			jdbcProperties = new Properties();
		
		try (FileOutputStream out = new FileOutputStream(Driver.jdbcPropertiesPath.toString())) {
			jdbcProperties.setProperty(name, value);
			jdbcProperties.store(out, null);
		} catch (IOException e) {
			throw new SQLException("There was an error while creating " +
					"'" + Driver.jdbcPropertiesPath.getFileName().toString() + "'.", e);
		}
	}
	
	private static Properties loadProperties() throws SQLException {
		return Driver.loadProperties(Driver.jdbcPropertiesPath);
	}
	
	private static Properties loadProperties(Path propertiesPath) throws SQLException {
		try (FileInputStream in = new FileInputStream(propertiesPath.toString())) {
			Properties properties = new Properties();
			properties.load(in);
			return properties;
		} catch (IOException e) {
			throw new SQLException("There was an error while loading " +
					"'" + propertiesPath.getFileName().toString() + "'.", e);
		}
	}
}
