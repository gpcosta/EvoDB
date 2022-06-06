package com.evo.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

public class App {
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		Properties properties = new Properties();
		properties.setProperty("user", "root");
		properties.setProperty("password", "rootroot");
		Class.forName("com.evo.jdbc.Driver");
		Connection conn = DriverManager.getConnection(
				"jdbc:evo:flow:mariadb://localhost:3307",
				properties
		);
		
		
		System.out.print("> ");
		Scanner reader = new Scanner(System.in);
		reader.useDelimiter(";");
		while (reader.hasNext()) {
			String nextStatement = reader.next().trim() + ";";
			if (nextStatement.equalsIgnoreCase("exit"))
				break;
			
			PreparedStatement stmt = null;
			ResultSet resultSet = null;
			try {
				stmt = conn.prepareStatement(nextStatement);
				boolean isResultSet = stmt.execute();
				resultSet = stmt.getResultSet();
				/*if (isResultSet && resultSet != null)
					com.d2p.dev.App.printResultSet(resultSet);*/
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println();
			} finally {
				try {
					if (stmt != null)
						stmt.close();
					if (resultSet != null)
						resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println();
				}
			}
			
			System.out.print("> ");
		}
	}
}
