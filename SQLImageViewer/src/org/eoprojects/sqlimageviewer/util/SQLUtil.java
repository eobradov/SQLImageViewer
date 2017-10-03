package org.eoprojects.sqlimageviewer.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SQLUtil {
	
	public static Object lock = new Object();
	public static Connection conn = null;
	public static ResultSet results = null;
	
	private static String driver;
	private static String connectionString;
	private static String username;
	private static String password;
	
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
	
	public static void connect() throws Exception {
		synchronized(lock) {
			if (conn == null) {
				Class.forName(driver);
				conn = DriverManager.getConnection(connectionString, username, password);
			}	
		}
	}
	
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
	
	public static void disconnect() {
		synchronized(lock) {
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

	public static void executeQuery(String text) throws Exception {
		if (conn != null) {
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(1);
			results = stmt.executeQuery(text);
		}
	}

	public static InputStream getBinaryFromRow() throws SQLException {
		InputStream returnValue = null;
		try {
			ResultSetMetaData rsmd = results.getMetaData();
			for (int i=1; i <= rsmd.getColumnCount(); i++) {
				int columnType = rsmd.getColumnType(i);
				if (columnType == java.sql.Types.BINARY || columnType == java.sql.Types.VARBINARY) {
					returnValue = results.getBinaryStream(i);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return returnValue;	
	}

	public static String getStringFromRow() {
		String returnValue = null;
		try {
			ResultSetMetaData rsmd = results.getMetaData();
			for (int i=1; i <= rsmd.getColumnCount(); i++) {
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
