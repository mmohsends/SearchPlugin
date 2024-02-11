package com.dataserve.se.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;

import com.filenet.api.collection.PageIterator;
import com.filenet.api.core.Document;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;
import com.ibm.ecm.extension.PluginRequestFilter;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONMessage;
import com.ibm.ecm.json.JSONResultSetResponse;
import com.ibm.ecm.json.JSONResultSetRow;
import com.ibm.json.java.JSONArtifact;
import com.ibm.json.java.JSONObject;

public class ContinueQueryService extends PluginRequestFilter {

	public static final String REPOSITORY_ID = "repositoryId";
	public static final String REPOSITORY_TYPE = "repositoryType";
	public static final int pageSize = 50;

	public String getId() {
		return "continueQueryService";
	}

	public void execute(PluginServiceCallbacks callbacks, HttpServletRequest request, JSONResultSetResponse jsonResults) throws Exception {
		String methodName = "execute";
		callbacks.getLogger().logEntry(this, methodName, request);

		String repositoryId = request.getParameter(REPOSITORY_ID);
		String continueQeurySessionKey = request.getParameter("continuationData");

		jsonResults.setPageSize(pageSize);
		List<Object> searchResults = new ArrayList<Object>(pageSize);
		int itemCount = 0;
		try {
			Subject subject = callbacks.getP8Subject(repositoryId);
			UserContext.get().pushSubject(subject);

			Object synchObject = callbacks.getSynchObject(repositoryId, "p8");
			if (synchObject != null) {
				synchronized (synchObject) {
					PageIterator pageIterator = (PageIterator) request.getSession().getAttribute(continueQeurySessionKey);
					if (pageIterator.nextPage()) {
						for (Object obj : pageIterator.getCurrentPage()) {
							searchResults.add(obj);
							itemCount++;
						}
					}
					if (itemCount == pageSize) {
						String sessionKey = "pagerIterator";
						jsonResults.put("continuationData", sessionKey);
					} else {
						request.getSession().removeAttribute(continueQeurySessionKey);
					}
				}
			} else {
				PageIterator pageIterator = (PageIterator) request.getSession().getAttribute(continueQeurySessionKey);
				if (pageIterator.nextPage()) {
					for (Object obj : pageIterator.getCurrentPage()) {
						searchResults.add(obj);
						itemCount++;
					}
				}
				if (itemCount == pageSize) {
					String sessionKey = "pagerIterator";
					jsonResults.put("continuationData", sessionKey);
				} else {
					request.getSession().removeAttribute(continueQeurySessionKey);
				}
			}
			// Retrieve the privilege masks for the search results.
			HashMap<Object, Long> privMasks = callbacks.getP8PrivilegeMasks(repositoryId, searchResults);
			ObjectStore objectStore = callbacks.getP8ObjectStore(repositoryId);
			for (Object searchResult : searchResults) {
				Document doc = (Document) searchResult;
				/*
				 *  IDs use the form:
				 *  <object class name>,<object store ID>,<object ID>
				 */
				StringBuffer sbId = new StringBuffer();
				sbId.append(doc.getClassName()).append(",").append(objectStore.get_Id().toString()).append(",").append(doc.get_Id().toString());

				long privileges = (privMasks != null) ? privMasks.get(doc) : 0L;

				JSONResultSetRow row = new JSONResultSetRow(sbId.toString(), doc.get_Name(), doc.get_MimeType(), privileges);

				// Add locked user information (if any)
				row.addAttribute("locked", doc.isLocked(), JSONResultSetRow.TYPE_BOOLEAN, null, (new Boolean(doc.isLocked())).toString());
				row.addAttribute("lockedUser", doc.get_LockOwner(), JSONResultSetRow.TYPE_STRING, null, doc.get_LockOwner());
				row.addAttribute("currentVersion", doc.get_IsCurrentVersion(), JSONResultSetRow.TYPE_BOOLEAN, null, (new Boolean(doc.get_IsCurrentVersion())).toString());

				// Add the attributes
				row.addAttribute("ID", doc.get_Id().toString(), JSONResultSetRow.TYPE_STRING, null, doc.get_Id().toString());
				row.addAttribute("className", doc.getClassName(), JSONResultSetRow.TYPE_STRING, null, doc.getClassName());
				row.addAttribute("ModifiedBy", doc.get_LastModifier(), JSONResultSetRow.TYPE_STRING, null, doc.get_LastModifier());
				row.addAttribute("LastModified", doc.get_DateLastModified().toString(), JSONResultSetRow.TYPE_TIMESTAMP, null, doc.get_DateLastModified().toString());
				row.addAttribute("Version", doc.get_MajorVersionNumber() + "." + doc.get_MinorVersionNumber(), JSONResultSetRow.TYPE_STRING, null, doc.get_MajorVersionNumber() + "." + doc.get_MinorVersionNumber());
				row.addAttribute("{NAME}", doc.get_Name(), JSONResultSetRow.TYPE_STRING, null, doc.get_Name());
				row.addAttribute("ContentSize", doc.get_ContentSize(), JSONResultSetRow.TYPE_INTEGER, null, null);

				jsonResults.addRow(row);
			}

		} catch (Exception e) {
			// provide error information
			callbacks.getLogger().logError(this, methodName, request, e);

			JSONMessage jsonMessage = new JSONMessage(0, e.getMessage(), "This error may occur if the search string is invalid.", "Ensure the search string is the correct syntax.", "Check the IBM Content Navigator logs for more details.", "");
			jsonResults.addErrorMessage(jsonMessage);
		} finally {
			UserContext.get().popSubject();
			callbacks.getLogger().logExit(this, methodName, request);
		}
	}

	@Override
	public String[] getFilteredServices() {
		return new String[] { "/p8/continueQuery" };
	}

	@Override
	public JSONObject filter(PluginServiceCallbacks callbacks, HttpServletRequest request, JSONArtifact jsonRequest) throws Exception {
		String continueQeurySessionKey = request.getParameter("continuationData");
		if (request.getSession().getAttribute(continueQeurySessionKey) != null) {
			//the request is from plug-in service
			JSONResultSetResponse jsonResults = new JSONResultSetResponse();
			this.execute(callbacks, request, jsonResults);
			return jsonResults;
		} else {
			//the request is not related with sample plug-in search service,
			//goto the default ICN service handler.
			return null;
		}
	}
}
