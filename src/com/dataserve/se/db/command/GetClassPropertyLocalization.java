package com.dataserve.se.db.command;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dataserve.se.business.classification.ClassificationException;
import com.dataserve.se.business.classification.ClassificationModel;
import com.dataserve.se.permissions.ActionType;
import com.dataserve.se.permissions.Module ;
import com.dataserve.se.db.command.CommandBase;
import com.dataserve.eds.fn.FileNetException;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class GetClassPropertyLocalization extends CommandBase {
	private static final String REPOSITORY_ID = "repositoryId";

	public GetClassPropertyLocalization(HttpServletRequest request) {
		super(request);
	}

	public GetClassPropertyLocalization(PluginServiceCallbacks callbacks, HttpServletRequest request, HttpServletResponse response) {
		super(callbacks, request, response);
	}

	@Override
	public String execute() throws Exception {
		try {
			String repositoryId = request.getParameter(REPOSITORY_ID);
			String classSymbolicName = request.getParameter("classSymbolicName");
			String selectedLocale = request.getParameter("selectedLocale");

			Map<String, String> properties = ClassificationModel.fetchCustomClassPropertyDefinitionLocalization(repositoryId, classSymbolicName, selectedLocale, callBacks);
			JSONArray arr = new JSONArray();
			JSONObject obj;
			if(properties.size() > 0){
				for (Map.Entry<String, String> entry : properties.entrySet()) {
					obj = new JSONObject();
					obj.put("col1", entry.getKey());
					obj.put("col2", entry.getValue());
					arr.add(obj);
				}
			}
			
			return arr.toString();
			
		} catch (ClassificationException e) {
			
			if(e.getFnErrorCode()!=null &&  !e.getFnErrorCode().isEmpty()){
				
				
				throw new ClassificationException("Error : (FileNetException) "+ e.getFnErrorCode()+" Getting Document Class properties for user with id '" + currentUserId + "'",
						e);
			}
			
			
			throw new ClassificationException("Error Getting Document Class properties for user with id '" + currentUserId + "'",
					e);
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
