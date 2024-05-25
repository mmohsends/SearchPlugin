package com.dataserve.se.util;

import java.util.Properties;
import java.util.Set;

import com.dataserve.se.bean.ConfigBean;
import com.dataserve.se.db.ConnectionManager;
import com.dataserve.se.db.DatabaseException;
import com.dataserve.se.db.dao.ConfigDAO;

public class ConfigManager {
	private static Properties props;
	

	
	// Security
	private static String disableBackendAccessCheck;
	private static String superUserName;
	

	
	static {
		props = new Properties();
		loadProps();
	}
	
	public static void loadProps() {
		try {
			ConnectionManager dbConnection= new ConnectionManager();
			dbConnection.initConn();
			ConfigDAO dao = new ConfigDAO(dbConnection);
			Set<ConfigBean> beans = dao.fetchAllConfigs();
			for (ConfigBean b : beans) {
				if(b.IsEncrypted() == true) {
					props.put(b.getName(), EncryptionUtil.decrypt(b.getValue()));
				}else {
					props.put(b.getName(), b.getValue());
				}
			}
			
			if (beans != null && beans.size() > 0) {

				
				
				disableBackendAccessCheck = props.getProperty("SECURITY_DISABLE_BACKEND_ACCESS_CHECK");
				superUserName = props.getProperty("SECURITY_SUPER_USER_NAME");
				
				
				
				
//				System.out.println("Configurations have been loaded successfully!");
			} else {
				System.err.println("Failed to open the configuration file");
			}
		} catch (SecurityException e) {
			System.err.println("Failed to access resources folder due to the following error: " + e.getMessage());
			e.printStackTrace();
		} catch(DatabaseException e){
			try {
				throw new DatabaseException("Error to open data base connection", e);
			} catch (DatabaseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch(Exception e){
			System.err.println("Failed to initiate config due to the following error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			
		}
	}

	
	public static String getSuperUserName() {
		return superUserName;
	}
	
	public static boolean isAccessCheckDisabled() {
		return "1".equals(disableBackendAccessCheck);
	}


	
	
}
