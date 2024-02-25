package com.dataserve.se.db.command;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.dataserve.se.business.classification.ClassificationException;
import com.dataserve.se.business.classification.PropertyTemplateModel;
import com.dataserve.se.permissions.ActionType;
import com.dataserve.se.permissions.Module;
import com.ibm.json.java.JSONArray;

public class GetClassProperty extends CommandBase {
	private static final String REPOSITORY_ID = "repositoryId";

	public GetClassProperty(HttpServletRequest request) {
		super(request);
	}

	@Override
	public String execute() throws Exception {
		String classSymbolicName = "";
		try {
			// UserModel user = UserModel.getUserByLDAPName(currentUserId);
			String repositoryId = request.getParameter(REPOSITORY_ID);
			classSymbolicName = request.getParameter("classSymbolicName");


			PropertyTemplateModel propertyTemplateModel = new PropertyTemplateModel(callBacks.getLocale(),classSymbolicName);
			Set<PropertyTemplateModel> templateModels = propertyTemplateModel.GetClassProperty(repositoryId, callBacks,
					classSymbolicName);
			JSONArray jsonArray = new JSONArray();
			for (PropertyTemplateModel templateModel : templateModels) {
				jsonArray.add(templateModel.getAsJSON());
			}

			return jsonArray.toString();

		} catch (ClassificationException e) {
			
				if(e.getFnErrorCode()!=null &&  !e.getFnErrorCode().isEmpty()){
					throw new ClassificationException("Error : (FileNetException) "+ e.getFnErrorCode()+" Get Class Property  user with id '" + currentUserId
							+ "' , Symbolic Name : ' " + classSymbolicName + "'", e);
				}
				


			}
			throw new ClassificationException("Error Get Class Property  user with id '" + currentUserId
					+ "' , Symbolic Name : ' " + classSymbolicName + "'");
		}
	
	@Override
	protected Module getModule() {
		return null;
	}

	@Override
	protected ActionType getActionType() {
		return null;
	}
}
