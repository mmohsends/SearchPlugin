package com.dataserve.se.db.command;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

import com.dataserve.se.db.AuditDao ;
import com.dataserve.se.db.DatabaseException;
import com.dataserve.se.permissions.ActionType;
import com.dataserve.se.permissions.Module;
import com.filenet.api.core.ObjectStore;
import com.dataserve.se.bean.DMSAuditBean;
import com.dataserve.se.business.classification.ClassificationException;
import com.dataserve.se.business.classification.ClassificationModel;

public class AuditGenerator extends CommandBase  {

	private static final String REPOSITORY_ID = "repositoryId";
	
	public AuditGenerator(HttpServletRequest request) {
		super(request);
	}

	public AuditGenerator(PluginServiceCallbacks callbacks, HttpServletRequest request, HttpServletResponse response) {
		super(callbacks, request, response);
	}

	
	@Override
	public String execute() throws Exception {
		try {

			String classSymbolicName = request.getParameter("classSymbolicName");
			String currentUserId = request.getParameter("currentUserId");
			JSONObject searchPropertiesJsonObj = JSONObject.parse(request.getParameter("searchProperties"));
			logSearchAudit(currentUserId,classSymbolicName, searchPropertiesJsonObj);
			return "Success saved data";
			
		}catch (Exception e) {
			throw new DatabaseException("Error saved  searchProperties", e);
		}
	}
	
	
	
	
	private void logSearchAudit(String currentUserId ,String classSymbolic, JSONObject jsonObj) {
		DMSAuditBean bean = new DMSAuditBean();
		try {
			Map<String, String> searchedFields = new HashMap<>();
		    for (Object currentKey : jsonObj.keySet()) {
		    	 String finalValue = "";
		        String strKey = (String) currentKey;
		        List<?> value = (List<?>)jsonObj.get(currentKey);
			    if(value.get(1).toString().equalsIgnoreCase("String") ) {
			    	finalValue = "LIKE : " + value.get(0).toString()+";";
		   		 }
			    else if(value.get(1).toString().equalsIgnoreCase("Date")  
			    		|| value.get(1).toString().equalsIgnoreCase("Float")
			    		|| value.get(1).toString().equalsIgnoreCase("Integer")) {
			    	finalValue = "EQUAL : " + value.get(0).toString()+";";
		    		
		    	}
				if (finalValue.trim().length() > 0) {
					searchedFields.put(strKey,finalValue );
				}
		    }
			bean.setDocumentClass(classSymbolic);	
			bean.setProperties(searchedFields);
			bean.setDate(new Timestamp(System.currentTimeMillis()));
			bean.setUsername(currentUserId);
			logAudit(bean);
		 
		} catch (Exception e) {
			System.err.println("Error adding audit log for Search operation");
			e.printStackTrace();
		}
	}
	
	private void logAudit(DMSAuditBean bean) {

		try {
			AuditDao auditDao = new AuditDao();
			auditDao.addDMSAudit(bean);
		} catch (DatabaseException e) {
			System.err.println("Error saving audit log in database");
			e.printStackTrace();
		}
	}


	@Override
	protected Module getModule() {
		return Module.CLASSMANAGERFILENET;
	}

	@Override
	protected ActionType getActionType() {
		return ActionType.ALL_ACTIONS;
	}
}
