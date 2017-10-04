package org.eoprojects.sqlimageviewer.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Handles SQL connection and operations
 * 
 * @author Edi Obradovic
 *
 */
public class SQLUtil {

	public static Connection conn = null;
	public static ResultSet results = null;
	public static Object lock = new Object();

	private static String driver;
	private static String connectionString;
	private static String username;
	private static String password;

	// Getters/Setters
	
	public static void setDriver(String driver) {
		SQLUtil.driver = driver;
	}

	public static void setConnectionString(String connectionString) {
		SQLUtil.connectionString = connectionString;
	}

	public static String getConnectionString() {
		return connectionString;
	}

	public static void setUsername(String username) {
		SQLUtil.username = username;
	}

	public static void setPassword(String password) {
		SQLUtil.password = password;
	}

	/**
	 * Establish SQL connection
	 * 
	 * @throws Exception
	 */
	public static void connect() throws Exception {
		synchronized (lock) {
			if (conn == null) {
				Class.forName(driver);
				conn = DriverManager.getConnection(connectionString, username, password);
			}
		}
	}

	/**
	 * Get list of databases from SQL server
	 * 
	 * @return ArrayList<String> result
	 * @throws Exception
	 */
	public static ArrayList<String> getDatabases() throws Exception {
		ArrayList<String> result = new ArrayList<String>();
		if (conn != null) {
			String query = "SELECT name FROM master.dbo.sysdatabases";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				result.add(rs.getString(1));
			}
			rs.close();
			stmt.close();
		}
		return result;
	}

	/**
	 * Close SQL connection
	 */
	public static void disconnect() {
		synchronized (lock) {
			if (conn != null) {
				try {
					conn.close();
					conn = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Executes defined SQL query
	 * 
	 * @param text - Query to execute
	 * @throws Exception
	 */
	public static void executeQuery(String text) throws Exception {
		if (conn != null) {
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(1);
			results = stmt.executeQuery(text);
		}
	}

	/**
	 * Get VARBINARY/BINARY value from ResulSet row
	 * 
	 * @return InputStream returnValue
	 * @throws SQLException
	 */
	public static InputStream getBinaryFromRow() throws SQLException {
		InputStream returnValue = null;
		try {
			ResultSetMetaData rsmd = results.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				int columnType = rsmd.getColumnType(i);
				if (columnType == java.sql.Types.VARBINARY || columnType == java.sql.Types.BINARY) {
					returnValue = results.getBinaryStream(i);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return returnValue;
	}

	/**
	 * Get VARCHAR/CHAR value from ResulSet row
	 * 
	 * @return InputStream returnValue
	 * @throws SQLException
	 */
	public static String getStringFromRow() {
		String returnValue = null;
		try {
			ResultSetMetaData rsmd = results.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				int columnType = rsmd.getColumnType(i);
				if (columnType == java.sql.Types.VARCHAR || columnType == java.sql.Types.CHAR) {
					returnValue = results.getString(i);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnValue;
	}
}
