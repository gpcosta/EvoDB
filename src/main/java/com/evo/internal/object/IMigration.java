package com.evo.internal.object;

import com.evo.vcs.exception.StatementException;

import java.sql.Connection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface IMigration extends IFileWithStatements {
	
	Version getVersion();
	
	void execute(Connection conn, Consumer<Statement> successCallback,
	             BiConsumer<Statement, StatementException> errorCallback) throws StatementException;
}
