package com.dataserve.se.db.command;


import com.dataserve.se.business.classification.ClassificationException;
import com.dataserve.se.business.classification.ClassificationModel;
import com.dataserve.se.permissions.ActionType;
import com.dataserve.se.permissions.Module;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.dataserve.se.bean.LinkDocumentModel;
import com.dataserve.se.db.DatabaseException;
import com.dataserve.se.db.LinkDocumentDAO;
import com.dataserve.se.db.command.CommandBase;
import com.ibm.json.java.JSONArray;


public class DeleteLinkDocument extends CommandBase{

	private static final String String = null;

	public DeleteLinkDocument(HttpServletRequest request) {
		super(request);
	}
	public String execute() throws Exception {

		String documentId = request.getParameter("documentId");
		String documentClass = request.getParameter("documentClass");
		String createdBy = request.getParameter("createdBy");
		String documentName = request.getParameter("documentName");
		String mainDocId = request.getParameter("mainDocId");
			try {
				LinkDocumentDAO dao = new LinkDocumentDAO();			
				int delete = dao.deleteLinkDocument(documentId, documentClass, createdBy, documentName, mainDocId);
				return delete + "";
					
				} catch (DatabaseException e) {
					throw new ClassificationException("Error getting Linked Document with Main Doc '" + mainDocId + "'", e);
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
