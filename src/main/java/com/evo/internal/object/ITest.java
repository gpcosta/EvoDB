package com.evo.internal.object;

import com.evo.vcs.exception.StatementException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

public interface ITest extends IFileWithStatements {
	
	String getDescription();
	
	boolean execute(Connection conn) throws SQLException, StatementException;
}
