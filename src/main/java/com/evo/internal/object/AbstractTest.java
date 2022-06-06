package com.evo.internal.object;

import com.evo.exception.SimpleException;
import com.evo.vcs.exception.StatementException;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

abstract public class AbstractTest extends AbstractFileWithStatements implements ITest {
	
	protected static String TEST_NAME_PATTERN = "^.+\\.sql$";
	
	public AbstractTest(Path fileAbsolutePath, List<Statement> statements) {
		super(fileAbsolutePath, statements);
	}
	
	public String getDescription() {
		String[] filenameParts = this.getFilename().split("\\.sql", 2);
		if (filenameParts.length < 2)
			return "";
		return filenameParts[0];
	}
	
	public boolean execute(Connection conn) throws SQLException, StatementException {
		try {
			conn.setAutoCommit(false);
			Statement stmt;
			for (int i = 0, len = this.getStatements().size() - 1; i < len; i++)
				this.getStatements().get(i).execute(conn);
			
			stmt = this.getStatements().get(this.getStatements().size() - 1);
			try (java.sql.PreparedStatement statement = conn.prepareStatement(stmt.getStatement());
			     ResultSet rs = statement.executeQuery()) {
				return rs.next() && rs.getInt(1) == 1;
			} catch (SQLException e) {
				throw new StatementException(stmt, e);
			}
		} finally {
			conn.rollback();
			conn.setAutoCommit(true);
		}
	}
	
	protected static void executeAll(List<ITest> tests, Connection conn, Consumer<ITest> successEachTestCallback,
	                                 Consumer<ITest> errorEachTestCallback)
			throws SimpleException {
		for (ITest test : tests) {
			try {
				boolean isTestAccepted = test.execute(conn);
				if (isTestAccepted)
					successEachTestCallback.accept(test);
				else
					errorEachTestCallback.accept(test);
			} catch (SQLException | StatementException e) {
				if (errorEachTestCallback != null)
					errorEachTestCallback.accept(test);
				throw new SimpleException("There was an error while executing test '" + test.getDescription() + "'.", e);
			}
		}
	}
}
