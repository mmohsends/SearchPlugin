/*
 * Licensed Materials - Property of IBM (c) Copyright IBM Corp. 2012, 2013 All Rights Reserved.
 * 
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with
 * IBM Corp.
 * 
 * DISCLAIMER OF WARRANTIES :
 * 
 * Permission is granted to copy and modify this Sample code, and to distribute modified versions provided that both the
 * copyright notice, and this permission notice and warranty disclaimer appear in all copies and modified versions.
 * 
 * THIS SAMPLE CODE IS LICENSED TO YOU AS-IS. IBM AND ITS SUPPLIERS AND LICENSORS DISCLAIM ALL WARRANTIES, EITHER
 * EXPRESS OR IMPLIED, IN SUCH SAMPLE CODE, INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL IBM OR ITS LICENSORS OR SUPPLIERS BE LIABLE FOR
 * ANY DAMAGES ARISING OUT OF THE USE OF OR INABILITY TO USE THE SAMPLE CODE, DISTRIBUTION OF THE SAMPLE CODE, OR
 * COMBINATION OF THE SAMPLE CODE WITH ANY OTHER CODE. IN NO EVENT SHALL IBM OR ITS LICENSORS AND SUPPLIERS BE LIABLE
 * FOR ANY LOST REVENUE, LOST PROFITS OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, EVEN IF IBM OR ITS LICENSORS OR SUPPLIERS HAVE
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */

package com.dataserve.se.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.util.MessageResources;

import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.collection.PageIterator;
import com.filenet.api.core.Document;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.ibm.ecm.configuration.Config;
import com.ibm.ecm.configuration.RepositoryConfig;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONResultSetColumn;
import com.ibm.ecm.json.JSONResultSetResponse;
import com.ibm.ecm.json.JSONResultSetRow;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

/**
 * This class contains P8 specific logic for the sample plugin search service. It demonstrates running a search using
 * the P8 APIs and populating a JSONResultSetResponse object, which is used to populate the ecm.model.ResultSet
 * JavaScript model class. This class provides the structure and rows for the ecm.widget.listView.ContentList DOJO
 * widget.
 */
public class SearchServiceP8 {
	public static final int pageSize = 50;

	/**
	 * Runs the P8 search SQL
	 * 
	 * @param objectStore
	 *            Handle to the ObjectStore
	 * @param query
	 *            The query to run
	 * @param callbacks
	 *            The PluginServiceCallbacks object
	 * @param jsonResultSet
	 *            JSONResultSetResponse to build up the grid structure and rows.
	 * @param clientLocale
	 *            The locale of the client
	 */
	public static void executeP8Search(HttpServletRequest request, String repositoryId, String query, PluginServiceCallbacks callbacks, JSONResultSetResponse jsonResultSet, Locale clientLocale) throws Exception {
		ObjectStore objectStore = callbacks.getP8ObjectStore(repositoryId);

		buildP8ResultStructure(request, jsonResultSet, callbacks.getResources(), clientLocale);

		SearchSQL searchSQL = new SearchSQL(query);
		SearchScope searchScope = new SearchScope(objectStore);

		// Use callbacks.getP8FolderResultsPropertyFilter() for folder results.
		PropertyFilter filter = callbacks.getP8DocumentResultsPropertyFilter();

		// Retrieve the first pageSize results.
		List<Object> searchResults = new ArrayList<Object>(pageSize);
		IndependentObjectSet resultsObjectSet = searchScope.fetchObjects(searchSQL, pageSize, null, true);
		PageIterator pageIterator = resultsObjectSet.pageIterator();
		int itemCount = 0;
		if (pageIterator.nextPage()) {
			for (Object obj : pageIterator.getCurrentPage()) {
				searchResults.add(obj);
				itemCount++;
			}
		}


		// Retrieve the privilege masks for the search results.
		HashMap<Object, Long> privMasks = callbacks.getP8PrivilegeMasks(repositoryId, searchResults);

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
			row.addAttribute("Creator", doc.get_Creator(), JSONResultSetRow.TYPE_STRING, null, doc.get_Creator());
			row.addAttribute("ClassDescription", doc.get_ClassDescription().toString(), JSONResultSetRow.TYPE_STRING, null, doc.get_ClassDescription().toString());
			row.addAttribute("ModifiedBy", doc.get_LastModifier(), JSONResultSetRow.TYPE_STRING, null, doc.get_LastModifier());
			row.addAttribute("LastModified", doc.get_DateLastModified().toString(), JSONResultSetRow.TYPE_TIMESTAMP, null, doc.get_DateLastModified().toString());
			row.addAttribute("Version", doc.get_MajorVersionNumber() + "." + doc.get_MinorVersionNumber(), JSONResultSetRow.TYPE_STRING, null, doc.get_MajorVersionNumber() + "." + doc.get_MinorVersionNumber());
			row.addAttribute("{NAME}", doc.get_Name(), JSONResultSetRow.TYPE_STRING, null, doc.get_Name());
			row.addAttribute("ContentSize", doc.get_ContentSize(), JSONResultSetRow.TYPE_INTEGER, null, null);

			jsonResultSet.addRow(row);
		}
		String sessionKey = "pagerIterator";
		request.getSession().removeAttribute(sessionKey);
		if (itemCount == pageSize) {
			jsonResultSet.put("continuationData", sessionKey);
			//this require CE version >=5.0
			request.getSession().setAttribute(sessionKey, pageIterator);
		}
	}

	/**
	 * Builds the details and magazine structure for P8. This method will use a set of predefined columns and fields
	 * that always exist on every P8 object.
	 * 
	 * @param jsonResultSet
	 *            The JSONResultSetResponse object to populate with the structure
	 * @param messageResources
	 *            The resource bundle to retrieve default column names
	 * @param clientLocale
	 *            The locale of the client
	 */
	private static void buildP8ResultStructure(HttpServletRequest request, JSONResultSetResponse jsonResultSet, MessageResources resources, Locale clientLocale) {
		RepositoryConfig repoConf = Config.getRepositoryConfig(request);
		String[] folderColumns = repoConf.getSearchDefaultColumns();


		String[] states = new String[1];
		states[0] = JSONResultSetColumn.STATE_LOCKED;

		jsonResultSet.addColumn(new JSONResultSetColumn("&nbsp;", "multiStateIcon", false, states));
		jsonResultSet.addColumn(new JSONResultSetColumn("&nbsp;", "17px", "mimeTypeIcon", null, false));
		//jsonResultSet.addColumn(new JSONResultSetColumn(resources.getMessage(clientLocale, "search.results.header.id"), "200px", "ID", null, false));
		//jsonResultSet.addColumn(new JSONResultSetColumn("Class Name", "125px", "className", null, false));
		for (String columnString : folderColumns) {
			if (columnString.equals("LastModifier"))
				jsonResultSet.addColumn(new JSONResultSetColumn(resources.getMessage(clientLocale, "search.results.header.lastModifiedByUser"), "125px", "ModifiedBy", null, false));
			else if (columnString.equals("DateLastModified"))
				jsonResultSet.addColumn(new JSONResultSetColumn(resources.getMessage(clientLocale, "search.results.header.lastModifiedTimestamp"), "175px", "LastModified", null, false));
			else if (columnString.equals("MajorVersionNumber"))
				jsonResultSet.addColumn(new JSONResultSetColumn(resources.getMessage(clientLocale, "search.results.header.version"), "50px", "Version", null, false));
			else if (columnString.equals("{NAME}"))
				jsonResultSet.addColumn(new JSONResultSetColumn("Name", "200px", columnString, null, false));
			else {
				jsonResultSet.addColumn(new JSONResultSetColumn(columnString, "80px", columnString, null, true));
			}
		}
		// Magazine view
		jsonResultSet.addMagazineColumn(new JSONResultSetColumn("thumbnail", "60px", "thumbnail", null, null));

		JSONArray fieldsToDisplay = new JSONArray();
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("field", "className");
		jsonObj.put("displayName", "Class");
		fieldsToDisplay.add(jsonObj);

		jsonObj = new JSONObject();
		jsonObj.put("field", "ModifiedBy");
		jsonObj.put("displayName", resources.getMessage(clientLocale, "search.results.header.lastModifiedByUser"));
		fieldsToDisplay.add(jsonObj);

		jsonObj = new JSONObject();
		jsonObj.put("field", "LastModified");
		jsonObj.put("displayName", resources.getMessage(clientLocale, "search.results.header.lastModifiedTimestamp"));
		fieldsToDisplay.add(jsonObj);

		jsonObj = new JSONObject();
		jsonObj.put("field", "Version");
		jsonObj.put("displayName", resources.getMessage(clientLocale, "search.results.header.lastModifiedTimestamp"));
		fieldsToDisplay.add(jsonObj);

		jsonResultSet.addMagazineColumn(new JSONResultSetColumn("content", "100%", "content", fieldsToDisplay, null));
	}
	
	private static void buildP8ResultStructureCustom(HttpServletRequest request, JSONResultSetResponse jsonResultSet, MessageResources resources, Locale clientLocale) {
		RepositoryConfig repoConf = Config.getRepositoryConfig(request);
//		String[] folderColumns = repoConf.getSearchDefaultColumns();
//		String[] folderColumns = repoConf.getDocumentSystemProperties();
		String[] folderColumns = {"{NAME}","testchoice1"};

		String[] states = new String[1];
		states[0] = JSONResultSetColumn.STATE_LOCKED;

		jsonResultSet.addColumn(new JSONResultSetColumn("&nbsp;", "multiStateIcon", false, states));
		jsonResultSet.addColumn(new JSONResultSetColumn("&nbsp;", "17px", "mimeTypeIcon", null, false));
		//jsonResultSet.addColumn(new JSONResultSetColumn(resources.getMessage(clientLocale, "search.results.header.id"), "200px", "ID", null, false));
		//jsonResultSet.addColumn(new JSONResultSetColumn("Class Name", "125px", "className", null, false));
		for (String columnString : folderColumns) {
			if (columnString.equals("LastModifier"))
				jsonResultSet.addColumn(new JSONResultSetColumn(resources.getMessage(clientLocale, "search.results.header.lastModifiedByUser"), "125px", "ModifiedBy", null, false));
			else if (columnString.equals("DateLastModified"))
				jsonResultSet.addColumn(new JSONResultSetColumn(resources.getMessage(clientLocale, "search.results.header.lastModifiedTimestamp"), "175px", "LastModified", null, false));
			else if (columnString.equals("MajorVersionNumber"))
				jsonResultSet.addColumn(new JSONResultSetColumn(resources.getMessage(clientLocale, "search.results.header.version"), "50px", "Version", null, false));
			else if (columnString.equals("{NAME}"))
				jsonResultSet.addColumn(new JSONResultSetColumn("Name", "200px", columnString, null, false));
			else if (columnString.equals("testchoice1"))
				jsonResultSet.addColumn(new JSONResultSetColumn("testchoice1", "200px", columnString, null, false));
			else {
				jsonResultSet.addColumn(new JSONResultSetColumn(columnString, "80px", columnString, null, true));
			}
		}
		// Magazine view
		jsonResultSet.addMagazineColumn(new JSONResultSetColumn("thumbnail", "60px", "thumbnail", null, null));

		JSONArray fieldsToDisplay = new JSONArray();
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("field", "className");
		jsonObj.put("displayName", "Class");
		fieldsToDisplay.add(jsonObj);

		jsonObj = new JSONObject();
		jsonObj.put("field", "ModifiedBy");
		jsonObj.put("displayName", resources.getMessage(clientLocale, "search.results.header.lastModifiedByUser"));
		fieldsToDisplay.add(jsonObj);

		jsonObj = new JSONObject();
		jsonObj.put("field", "LastModified");
		jsonObj.put("displayName", resources.getMessage(clientLocale, "search.results.header.lastModifiedTimestamp"));
		fieldsToDisplay.add(jsonObj);

		jsonObj = new JSONObject();
		jsonObj.put("field", "Version");
		jsonObj.put("displayName", resources.getMessage(clientLocale, "search.results.header.lastModifiedTimestamp"));
		fieldsToDisplay.add(jsonObj);

		jsonResultSet.addMagazineColumn(new JSONResultSetColumn("content", "100%", "content", fieldsToDisplay, null));
	}
	
	

}