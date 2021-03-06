package org.eoprojects.sqlimageviewer.util;

/**
 * Contains attributes and methods used between other classes
 * 
 * @author Edi Obradovic
 *
 */
public class DriverConstants {
	
	public static final String EMPTY_LIST_ITEM 					= "----";

	public static final String PROGRAM_TITLE 					= "SQL Image Viewer";
	public static final String PROGRAM_NAME 					= "SQL Image Viewer";
	public static final String PROGRAM_VERSION 					= "1.0";
	public static final String PROGRAM_AUTHOR 					= "Edi Obradovic";
	public static final String PROGRAM_EMAIL					= "edi.obradovic@gmail.com";

	public static final String MICROSOFT_SQL_NAME 				= "Microsoft SQL Server";
	public static final String MICROSOFT_SQL_VALUE 				= "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static final String MICROSOFT_SQL_PREFIX 			= "jdbc:sqlserver://";
	public static final String MICROSOFT_SQL_DEFAULT_SERVER 	= "127.0.0.1\\SQLEXPRESS";
	public static final String MICROSOFT_SQL_DEFAULT_USERNAME 	= "sa";
	
	public static final String MYSQL_NAME 						= "MySQL";
	public static final String MYSQL_VALUE 						= "com.mysql.jdbc.Driver";
	public static final String MYSQL_PREFIX 					= "jdbc:mysql://";
	public static final String MYSQLL_DEFAULT_SERVER 			= "127.0.0.1";
	public static final String MYSQLL_DEFAULT_USERNAME 			= "root";
	
	public static final String COLOR_NEUTRAL 					= "#000000";
	public static final String COLOR_SUCCESS 					= "#09b52e";
	public static final String COLOR_FAIL 						= "#b50808";

	/**
	 * Returns a driver value (example:
	 * com.microsoft.sqlserver.jdbc.SQLServerDriver) depending on selected SQL type
	 * 
	 * @param driverName
	 * @return
	 */
	public static String getDriverValue(String driverName) {
		if (driverName.trim().equals(MICROSOFT_SQL_NAME)) {
			return MICROSOFT_SQL_VALUE;
		} else if (driverName.trim().equals(MYSQL_NAME)) {
			return MYSQL_VALUE;
		} else {
			return "";
		}
	}

	/**
	 * Returns a string prefix (example: jdbc:sqlserver://) depending on selected
	 * SQL type
	 * 
	 * @param driverName
	 * @return
	 */
	public static String getConnectionPrefix(String driverName) {
		if (driverName.trim().equals(MICROSOFT_SQL_NAME)) {
			return MICROSOFT_SQL_PREFIX;
		} else if (driverName.trim().equals(MYSQL_NAME)) {
			return MYSQL_PREFIX;
		} else {
			return "";
		}
	}
	
	/**
	 * Returns a default server (example: 127.0.0.1\SQLEXPRESS) depending on selected SQL type
	 * 
	 * @param driverName
	 * @return
	 */
	public static String getDefaultServer(String driverName) {
		if (driverName.trim().equals(MICROSOFT_SQL_NAME)) {
			return MICROSOFT_SQL_DEFAULT_SERVER;
		} else if (driverName.trim().equals(MYSQL_NAME)) {
			return MYSQLL_DEFAULT_SERVER;
		} else {
			return "";
		}
	}
	
	/**
	 * Returns a default username (example: sa) depending on selected SQL type
	 * 
	 * @param driverName
	 * @return
	 */
	public static String getDefaultUsername(String driverName) {
		if (driverName.trim().equals(MICROSOFT_SQL_NAME)) {
			return MICROSOFT_SQL_DEFAULT_USERNAME;
		} else if (driverName.trim().equals(MYSQL_NAME)) {
			return MYSQLL_DEFAULT_USERNAME;
		} else {
			return "";
		}
	}
}
