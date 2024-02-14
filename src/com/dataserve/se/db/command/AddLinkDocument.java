package com.dataserve.se.db.command;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.dataserve.se.business.classification.ClassificationException;
import com.dataserve.se.business.classification.ClassificationModel;
import com.dataserve.se.permissions.ActionType;
import com.dataserve.se.permissions.Module;
import com.dataserve.se.bean.LinkDocumentModel;
import com.dataserve.se.db.command.CommandBase;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class AddLinkDocument extends CommandBase {
	

	public AddLinkDocument(HttpServletRequest request) {
		super(request);
	}
	
	public String execute() throws Exception {

		String mainDocId = request.getParameter("mainDocId");
		JSONArray docInfo = JSONArray.parse(request.getParameter("docInfo"));
		        
		try {
//    			LinkDocumentModel LM = new LinkDocumentModel(jsonObject.get("ID").toString().replace("{", "").replace("}", ""), jsonObject.get("className").toString(), jsonObject.get("Creator").toString(), jsonObject.get("This").toString(), mainDocId);
    			LinkDocumentModel LM = new LinkDocumentModel(docInfo, mainDocId);
				LM.save();

				return "SUCCESS";
		} 
		catch (ClassificationException e) {
			throw new Exception("Failed to create a new Classification due to the following error: ", e);
		}
	
	}
		

	@Override
	protected Module getModule() {
		return Module.CLASSIFICATION;
	}

	@Override
	protected ActionType getActionType() {
		return ActionType.CREATE;
	}

}
