package com.dataserve.se.db.command;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.dataserve.se.util.ConfigManager;
import com.dataserve.se.bean.User;
import com.dataserve.se.business.classification.ClassificationException;
import com.dataserve.se.business.classification.ClassificationModel;
import com.dataserve.se.manager.UserManager;
import com.dataserve.se.permissions.ActionType;
import com.dataserve.se.permissions.Module;
import com.ibm.json.java.JSONArray;

public class GetClassificationsByUser extends CommandBase {

	public GetClassificationsByUser(HttpServletRequest request) {
		super(request);
	}

	@Override
	public String execute() throws Exception {
		try {
			if (currentUserId == null) {
				currentUserId = request.getParameter("userId");
			}
			
			Set<ClassificationModel> classifications;
			if (ConfigManager.getSuperUserName().equals(currentUserId)) {
				classifications = ClassificationModel.getFNAddedClassifications();
			} else {
				UserManager userManager = new  UserManager() ;
				User user = userManager.fetUserByNameLDAP(currentUserId);
				classifications = ClassificationModel.getFNAddedClassificationsByUserId(user.getUserId());
			}
			
			JSONArray arr = new JSONArray();
			for (ClassificationModel c : classifications){
				if (c.getParentID() == 0) {
					arr.add(c.getFullStructure());
				}
			}
			return arr.toString();
		} catch (Exception e) {
			throw new ClassificationException("Error getting classification for user with id '" + currentUserId + "'", e);
		}
	}

	@Override
	protected Module getModule() {
		return Module.CLASSIFICATION;
	}

	@Override
	protected ActionType getActionType() {
		return ActionType.VIEW;
	}

}
