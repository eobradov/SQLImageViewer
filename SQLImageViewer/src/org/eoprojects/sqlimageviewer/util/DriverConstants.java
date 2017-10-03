package org.eoprojects.sqlimageviewer.util;

public class DriverConstants {
	
	public static final String PROGRAM_TITLE 		= "SQL Image Viewer";
	public static final String PROGRAM_NAME			= "SQL Image Viewer";
	public static final String PROGRAM_VERSION		= "1.0";
	public static final String PROGRAM_AUTHOR		= "Edi Obradovic";
	
	public static final String MICROSOFT_SQL_NAME 	= "Microsoft SQL Server";
	public static final String MICROSOFT_SQL_VALUE 	= "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static final String MICROSOFT_SQL_PREFIX = "jdbc:sqlserver://";
	
	public static final String COLOR_NEUTRAL 		= "#000000";
	public static final String COLOR_SUCCESS 		= "#09b52e";
	public static final String COLOR_FAIL 			= "#b50808";
	
	public static String getDriverValue(String driverName) {
		if (driverName.trim().equals(MICROSOFT_SQL_NAME)) {
			return MICROSOFT_SQL_VALUE;
		} else {
			return "";
		}
	}
	
	public static String getConnectionPrefix(String driverName) {
		if (driverName.trim().equals(MICROSOFT_SQL_NAME)) {
			return MICROSOFT_SQL_PREFIX;
		} else {
			return "";
		}
	}
}

