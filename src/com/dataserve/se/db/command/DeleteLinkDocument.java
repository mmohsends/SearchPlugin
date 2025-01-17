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
import com.ibm.json.java.JSONObject;


public class DeleteLinkDocument extends CommandBase{


	public DeleteLinkDocument(HttpServletRequest request) {
		super(request);
	}
	public String execute() throws Exception {
		JSONArray gridData = JSONArray.parse(request.getParameter("gridData"));

		int delete = 0;

			try {
				for (Object obj : gridData) {
					JSONObject jsonObject = (JSONObject) obj;
		            LinkDocumentDAO dao = new LinkDocumentDAO();
		            Object childFaildIdObj = jsonObject.get("childFaildId");
		            int childFaildId = Integer.parseInt(childFaildIdObj.toString().replace("[", "").replace("]", ""));
		            Object fileIdObj = jsonObject.get("fileId");
		            int fileId = Integer.parseInt(fileIdObj.toString().replace("[", "").replace("]", ""));
		    		System.out.println("Before deleteLinkDocument: "+fileId +"::::"+childFaildId);
		            dao.deleteLinkDocument(fileId, childFaildId);
				}
				return delete+"";

					
				} catch (DatabaseException e) {
					throw new ClassificationException("Error getting Linked Document with Main Doc");
			}
	}
    public static String decodeUnicode(String unicodeText) {
        StringBuilder decodedText = new StringBuilder();
        String[] codePoints = unicodeText.split("\\\\u");
        for (String codePoint : codePoints) {
            if (!codePoint.isEmpty()) {
                int charValue = Integer.parseInt(codePoint, 16);
                decodedText.append((char) charValue);
            }
        }
        return decodedText.toString();
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
