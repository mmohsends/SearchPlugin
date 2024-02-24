package com.dataserve.se.db.command;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.dataserve.se.bean.LinkDocumentModel;
import com.dataserve.se.business.classification.ClassificationException;
import com.dataserve.se.business.classification.ClassificationModel;
import com.dataserve.se.permissions.ActionType;
import com.dataserve.se.permissions.Module;
import com.dataserve.se.db.command.CommandBase;
import com.ibm.json.java.JSONArray;

public class GetLinkDocument extends CommandBase{
	
	public GetLinkDocument(HttpServletRequest request) {
		super(request);
	}

	
	public String execute() throws Exception {
		String mainDocId =  request.getParameter("mainDocId");
		System.out.println("getlink mainDocId : "+mainDocId);
		try{
			Set<LinkDocumentModel> docs = LinkDocumentModel.getLinkDocument(mainDocId);
			System.out.println("getLink groups: "+ docs);
			JSONArray arr = new JSONArray();
			for (LinkDocumentModel lm : docs) {
				arr.add(lm.getAsJson());
			}
			return arr.toString();
		} catch (Exception e) {
			throw new Exception("Error getting Linked Document", e);
		}
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
