package com.evo.internal.object;

import com.evo.exception.SimpleException;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Config {
	
	private String projectName;
	
	private List<String> schemas;
	
	private String jdbcUrl;
	
	private String jdbcUrlTemp;
	
	private String jdbcDriver;
	
	private String jdbcDriverTemp;
	
	private String dbUser;
	
	private String dbUserTemp;
	
	private String dbPassword;
	
	private String dbPasswordTemp;
	
	private String dbHostname;
	
	private String dbHostnameTemp;
	
	private Config(String projectName, String schemas, String jdbcUrl, String jdbcDriver, String dbUser,
	               String dbPassword, String dbHostname) {
		this.projectName = projectName;
		this.schemas = Arrays.asList(schemas.split(","));
		this.jdbcUrl = jdbcUrl;
		this.jdbcUrlTemp = "";
		this.jdbcDriver = jdbcDriver;
		this.jdbcDriverTemp = "";
		this.dbUser = dbUser;
		this.dbUserTemp = "";
		this.dbPassword = dbPassword;
		this.dbPasswordTemp = "";
		this.dbHostname = dbHostname;
		this.dbHostnameTemp = "";
	}
	
	public String getProjectName() {
		return this.projectName;
	}
	
	public List<String> getSchemas() {
		return this.schemas;
	}
	
	public String getDefaultSchema() {
		return this.schemas.get(0);
	}
	
	public String getJdbcUrl() {
		return this.jdbcUrlTemp.equals("") ? this.jdbcUrl : this.jdbcUrlTemp;
	}
	
	public String getJdbcDriver() {
		return this.jdbcDriverTemp.equals("") ? this.jdbcDriver : this.jdbcDriverTemp;
	}
	
	public String getDbUser() {
		return this.dbUserTemp.equals("") ? this.dbUser : this.dbUserTemp;
	}
	
	public String getDbPassword() {
		return this.dbPasswordTemp.equals("") ? this.dbPassword : this.dbPasswordTemp;
	}
	
	public String getDbHostname() {
		return this.dbHostnameTemp.equals("") ? this.dbHostname : this.dbHostnameTemp;
	}
	
	public void setTempJdbcUrl(String jdbcUrlTemp) {
		this.jdbcUrlTemp = jdbcUrlTemp;
	}
	
	public void setTempJdbcDriver(String jdbcDriverTemp) {
		this.jdbcDriverTemp = jdbcDriverTemp;
	}
	
	public void setTempDbUser(String dbUserTemp) {
		this.dbUserTemp = dbUserTemp;
	}
	
	public void setTempDbPassword(String dbPasswordTemp) {
		this.dbPasswordTemp = dbPasswordTemp;
	}
	
	public void setTempDbHostname(String dbHostnameTemp) {
		this.dbHostnameTemp = dbHostnameTemp;
	}
	
	public static Config build(Path path, String projectName, String schemas, String jdbcUrl, String jdbcDriver,
	                           String dbUser, String dbPassword, String dbHostname)
			throws SimpleException {
		List<String> schemasList = Arrays.asList(schemas.split(","));
		schemas = StringUtils.join(schemasList, ",");
		try(FileOutputStream out = new FileOutputStream(path.toString())) {
			Properties properties = new Properties();
			properties.setProperty("project_name", projectName);
			properties.setProperty("schemas", schemas);
			properties.setProperty("jdbc_url", jdbcUrl);
			properties.setProperty("jdbc_driver", jdbcDriver);
			properties.setProperty("db_user", dbUser);
			properties.setProperty("db_password", dbPassword);
			properties.setProperty("db_hostname", dbHostname);
			properties.store(out, null);
			return new Config(projectName, schemas, jdbcUrl, jdbcDriver, dbUser, dbPassword, dbHostname);
		} catch (IOException e) {
			throw new SimpleException("There was an error while creating '" + path.getFileName().toString() + "'.", e);
		}
	}
	
	public static Config load(Path path) throws SimpleException {
		try(FileInputStream in = new FileInputStream(path.toString())) {
			Properties properties = new Properties();
			properties.load(in);
			return new Config(
					properties.getProperty("project_name"),
					properties.getProperty("schemas"),
					properties.getProperty("jdbc_url"),
					properties.getProperty("jdbc_driver"),
					properties.getProperty("db_user"),
					properties.getProperty("db_password"),
					properties.getProperty("db_hostname")
			);
		} catch (IOException e) {
			throw new SimpleException("There was an error while loading '" + path.getFileName().toString() + "'.", e);
		}
	}
	
	public static boolean exist(Path path) throws SimpleException {
		File file = new File(path.toString());
		return file.exists() && file.isFile();
	}
}
