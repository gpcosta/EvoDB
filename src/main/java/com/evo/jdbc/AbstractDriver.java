package com.evo.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Abstract Class where is the basic functionality of a JDBC Driver is implemented
 * It should be extended by classes that pretend to be a JDBC Driver
 *
 * @param <C> extends Connection
 * @param <PS> extends PreparedStatement
 * @param <S> extends Statement
 */
public abstract class AbstractDriver <C extends Connection, PS extends PreparedStatement, S extends Statement>
		implements java.sql.Driver {
	
	/**
	 * Labels this AbstractDriver - is used in the url that represents the connection of this Driver in DriverManager
	 * url: "jdbc:" + label + ":(the rest of url of the real driver)"
	 */
	private String label;
	
	/**
	 * Object that has the callbacks already implemented for the several moments of the working flow of this Driver
	 */
	private EventListener eventListener;
	
	/**
	 * Class that should be used as Connection
	 */
	private Class<C> connectionClass;
	
	/**
	 * Class that should be used as PreparedStatement
	 */
	private Class<PS> preparedStatementClass;
	
	/**
	 * Class that should be used as Statement
	 */
	private Class<S> statementClass;
	
	/**
	 * @param label label that represents this Driver and appears in url after "jdbc:"
	 *              url = "jdbc:" + label + ":(the rest of url of the real driver)"
	 * @param eventListener JdbcEventListener that has the callbacks already implemented
	 * @param connectionClass class that should be used as Connection by this Driver
	 * @param preparedStatementClass class that should be used as PreparedStatement by this Driver
	 * @param statementClass class that should be used as Statement by this Driver
	 */
	public AbstractDriver(String label, EventListener eventListener, Class<C> connectionClass,
	                      Class<PS> preparedStatementClass, Class<S> statementClass) {
		this.label = label;
		this.eventListener = eventListener;
		this.connectionClass = connectionClass;
		this.preparedStatementClass = preparedStatementClass;
		this.statementClass = statementClass;
	}
	
	/**
	 * @param label label that represents this Driver and appears in url after "jdbc:"
	 *              url = "jdbc:" + label + ":(the rest of url of the real driver)"
	 * @param connectionClass class that should be used as Connection by this Driver
	 * @param preparedStatementClass class that should be used as PreparedStatement by this Driver
	 * @param statementClass class that should be used as Statement by this Driver
	 */
	public AbstractDriver(String label, Class<C> connectionClass,
	                      Class<PS> preparedStatementClass, Class<S> statementClass) {
		this.label = label;
		this.eventListener = new EventListener() {};
		this.connectionClass = connectionClass;
		this.preparedStatementClass = preparedStatementClass;
		this.statementClass = statementClass;
	}
	
	protected AbstractDriver<C, PS, S> setLabel(String label) {
		this.label = label;
		return this;
	}
	
	protected AbstractDriver<C, PS, S> setEventListener(EventListener eventListener) {
		this.eventListener = eventListener;
		return this;
	}
	
	protected AbstractDriver<C, PS, S> setConnectionClass(Class<C> connectionClass) {
		this.connectionClass = connectionClass;
		return this;
	}
	
	protected AbstractDriver<C, PS, S> setPreparedStatementClass(Class<PS> preparedStatementClass) {
		this.preparedStatementClass = preparedStatementClass;
		return this;
	}
	
	protected AbstractDriver<C, PS, S> setStatementClass(Class<S> statementClass) {
		this.statementClass = statementClass;
		return this;
	}
	
	/**
	 * If there is some label, the beginning of this url is "jdbc:" + label + ":" and the label must be removed
	 * to get the real url string of the driver
	 *
	 * @param url url provided to this Driver
	 * @return url of the real Database Driver
	 */
	private String getDbDriverUrl(String url) {
		// url does not start by "jdbc:"
		if (!url.startsWith("jdbc:"))
			throw new RuntimeException("Invalid Db Driver Url. Url: " + url + "; label: " + this.label);
		
		// there is no label, so there is nothing to remove from the url string
		if (this.label.equals(""))
			return url;
		
		String label = this.label + ":";
		// 5 = "jdbc:".length()
		if (url.indexOf(label) != 5)
			throw new RuntimeException("Invalid Db Driver Url. Url: " + url + "; label: " + this.label);
		return "jdbc:" + url.substring(5 + label.length());
	}
	
	/**
	 * @param url url provided by the Driver Manager
	 * @return true if this Driver accepts the url provided by the Driver Manager
	 * @throws SQLException
	 */
	@Override
	public boolean acceptsURL(String url) throws SQLException {
		//throw new SQLException(System.getProperty("java.class.path"));
		return url != null && (this.label.equals("") || url.startsWith("jdbc:" + this.label + ":"));
	}
	
	/**
	 * @param url url provided by the Driver Manager
	 * @param info properties that are provided by the database client to perform the login in the database
	 * @return Connection for the url and info provided or null if something failed
	 * @throws SQLException
	 */
	@Override
	public C connect(String url, Properties info) throws SQLException {
		return this.connect(url, info, "");
	}
	
	/**
	 * @param url url provided by the Driver Manager
	 * @param info properties that are provided by the database client to perform the login in the database
	 * @param jdbcDriver full class name of the internal JDBC Driver
	 * @return Connection for the url and info provided or null if something failed
	 * @throws SQLException
	 */
	public C connect(String url, Properties info, String jdbcDriver) throws SQLException {
		if (!this.acceptsURL(url))
			return null;
		
		this.eventListener.onBeforeGetConnection();
		try {
			if (!jdbcDriver.equals(""))
				Class.forName(jdbcDriver);
			
			C conn = this.connectionClass
					.getConstructor(
							java.sql.Connection.class,
							EventListener.class,
							Class.class,
							Class.class
					)
					.newInstance(
							DriverManager.getConnection(this.getDbDriverUrl(url), info),
							this.eventListener,
							this.preparedStatementClass,
							this.statementClass
					);
			/*new Connection<>(
					DriverManager.getConnection(this.getDbDriverUrl(url), info),
					this.eventListener,
					connectionInfo,
					this.preparedStatementClass,
					this.statementClass
			);*/
			this.eventListener.onAfterGetConnectionSuccess(conn);
			return conn;
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException
				| ClassNotFoundException e) {
			SQLException exception = new SQLException(
					"Connection class must have a constructor with the following arguments:\n" +
							"- " + java.sql.Connection.class.getCanonicalName() + " connection\n" +
							"- " + EventListener.class.getCanonicalName() + " eventListener\n" +
							"- " + Class.class.getCanonicalName() + " preparedStatementClass\n" +
							"- " + Class.class.getCanonicalName() + " statementClass\n",
					e
			);
			this.eventListener.onAfterGetConnectionError(exception);
			throw exception;
		}
	}
	
	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		return new DriverPropertyInfo[0];
		/*Configuration config = Configuration.parse(url);
		if (config == null)
			return new DriverPropertyInfo[0];
		
		return DriverManager
				.getDriver(config.getDbDriverUrl())
				.getPropertyInfo(config.getDbDriverUrl(), info);*/
	}
	
	@Override
	public int getMajorVersion() {
		return 0;
	}
	
	@Override
	public int getMinorVersion() {
		return 0;
	}
	
	@Override
	public boolean jdbcCompliant() {
		return true;
	}
	
	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}
}
