package com.dataserve.se.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
	private static Properties props;
	private static final String FILE_NAME = "/config.properties";
	
//	## FileNet Server props
//	private static String fileNetServer ;
//	private static String fileNetServerProtocol ;
//	private static String fileNetServerPort ;
//	private static String ICNDesktop; 
//	private static String ICNRepositoryId;
//	private static String stanza;
	
//	## Database Server props
	private static String datasourceName;

	
	// Security
	private static String disableBackendAccessCheck;
	private static String superUserName;
	
	public static String getLdapDomain() {
		return props.getProperty("LDAP_DOMAIN");  
	}

	

	
	


	
	
	static {
		props = new Properties();
		loadProps();
	}
	
	public static void loadProps() {
		InputStream is = null;
		try {
			is = ConfigManager.class.getResourceAsStream(FILE_NAME);
			if (is != null) {
				props.load(is);
//				fileNetServer = props.getProperty("FILENET_SERVER"); 
//				fileNetServerProtocol = props.getProperty("FILENET_SERVER_PROTOCOL");
//				fileNetServerPort = props.getProperty("FILENET_SERVER_PORT");	
//				ICNDesktop = props.getProperty("ICN_DESKTOP");	
//				stanza = props.getProperty("STANZA");
//				ICNRepositoryId = props.getProperty("ICN_REPOSITORY_ID");
				
				datasourceName = props.getProperty("ARCHIVE_DATASOURCE_NAME");


				
				disableBackendAccessCheck = props.getProperty("DISABLE_BACKEND_ACCESS_CHECK");
				superUserName = props.getProperty("SUPER_USER_NAME");
				
				
			
				
//				System.out.println("Configurations have been loaded successfully!");
			} else {
				System.err.println("Failed to open the configuration file");
			}
		} catch (IOException e) {
			System.err.println("Failed to load the configuration file '" + FILE_NAME + "' due to the following error: " + e.getMessage());
			e.printStackTrace();
		} catch (SecurityException e) {
			System.err.println("Failed to access resources folder due to the following error: " + e.getMessage());
			e.printStackTrace();
		} catch(Exception e){
			System.err.println("Failed to initiate config due to the following error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					System.err.println("Failed to close input stream due to the following error: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	
	public static String getDataSourceName() {
		return datasourceName;
	}

	

//	public static String getStanza() {
//		return stanza;
//	}
//
//	public static String getICNRepositoryId() {
//		return ICNRepositoryId;
//	}
//	
//	public static String getFileNetServer() {
//		return fileNetServer;
//	}
//
//	public static String getFileNetServerProtocol() {
//		return fileNetServerProtocol;
//	}
//
//	public static String getFileNetServerPort() {
//		return fileNetServerPort;
//	}
//
//	public static String getICNDesktop() {
//		return ICNDesktop;
//	}
	public static String getSuperUserName() {
		return superUserName;
	}
	
	public static boolean isAccessCheckDisabled() {
		return "1".equals(disableBackendAccessCheck);
	}
	
	
	


	
	
}
