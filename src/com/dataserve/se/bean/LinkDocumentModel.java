package com.dataserve.se.bean;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.dataserve.se.bean.LinkDocumentBean;
import com.dataserve.se.business.classification.ClassificationException;
import com.dataserve.se.db.LinkDocumentDAO;
import com.dataserve.se.db.DatabaseException;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class LinkDocumentModel {
	private LinkDocumentBean bean;
	private LinkDocumentModel parent;

	private Set<LinkDocumentModel> children = new LinkedHashSet<LinkDocumentModel>();
		
			public LinkDocumentModel(LinkDocumentBean bean) {
				this.bean = bean;
			}
			
			
			public LinkDocumentModel(String documentId, String documentClass, String createdBy, String documentName,String mainDocId)throws DatabaseException {
				LinkDocumentBean bean = new LinkDocumentBean();
				bean.setDocumentId(documentId);
				bean.setDocumentClass(documentClass);
				bean.setCreatedBy(createdBy);
				bean.setDocumentName(documentName);
				bean.setMainDocId(mainDocId);
			
				this.bean = bean;
			}
			
			public LinkDocumentModel(JSONArray chilDocumentList, String mainDocId)throws DatabaseException {
				LinkDocumentBean bean = new LinkDocumentBean();
				bean.setChilDocumentList(chilDocumentList);
				bean.setMainDocId(mainDocId);
			
				this.bean = bean;
			}
			
			public static Set<LinkDocumentModel> getLinkDocument(String mainDocId) throws ClassificationException {
				try {
					LinkDocumentDAO dao = new LinkDocumentDAO();			
					Set<LinkDocumentBean> beans = dao.fetchLinkDocument(mainDocId);
					Set<LinkDocumentModel> lmSet = new LinkedHashSet<LinkDocumentModel>();
					for (LinkDocumentBean b : beans) {
						lmSet.add(new LinkDocumentModel(b));
					}
					return lmSet;
				} catch (DatabaseException e) {
					throw new ClassificationException("Error getting Linked Document with Main Doc '" + mainDocId + "'", e);
				}
			}
			
	

			
			
			
			public JSONObject getAsJson() throws ClassificationException {
				
				JSONObject obj = new JSONObject();
				obj.put("documentId", bean.getDocumentId());
				obj.put("documentClass", bean.getDocumentClass());
				obj.put("createdBy", bean.getCreatedBy());
				obj.put("documentName", bean.getDocumentName());
				obj.put("documentNameAr", bean.getDocumentNameAr());
				obj.put("mainDocId", bean.getMainDocId());
				obj.put("fileId", bean.getFileId());
				obj.put("childFaildId", bean.getChildFileId());
				obj.put("children", new JSONArray());
				return obj;
			}
			

			

			
			public void save() throws ClassificationException {
				try {
					LinkDocumentDAO dao = new LinkDocumentDAO();
						dao.addLinkDocument(bean);
				} catch (DatabaseException e) {
					throw new ClassificationException("Error happend while trying to save Classification!", e);
				}
			}
			
						
			public String getDocumentClass() {
				return bean.getDocumentClass();
			}
			public void setDocumentClass(String documentClass) {
				bean.setDocumentClass(documentClass);
			}

			public String getCreatedBy() {
				return bean.getCreatedBy();
			}
			public void setCreatedBy(String createdBy) {
				bean.setCreatedBy(createdBy);
			}

			public String getDocumentName() {
				return bean.getDocumentName();
			}
			public void setDocumentName(String documentName) {
				bean.setDocumentName(documentName);
			}
			
			public String getDocumentNameAr() {
				return bean.getDocumentNameAr();
			}
			public void setDocumentNameAr(String documentNameAr) {
				bean.setDocumentNameAr(documentNameAr);
			}
			
			public String getMainDocId() {
				return bean.getMainDocId();
			}
			public void setMainDocId(String mainDocId) {
				bean.setMainDocId(mainDocId);
			}
			
			public int getFileId() {
				return bean.getFileId();
			}
			public void setFileId(int fileId) {
				bean.setFileId(fileId);
			}
			
			public int getChildFileId() {
				return bean.getChildFileId();
			}
			public void setChildFileId(int childFileId) {
				bean.setChildFileId(childFileId);
			}
			
			
			public Set<LinkDocumentModel> getChildren() {
				return children;
			}
			public void setChildren(Set<LinkDocumentModel> children) {
				this.children = children;
			}

			public Set<Integer> getChildrenIds() {
				return bean.getChildrenIds();
			}
			public void setChildrenIds(Set<Integer> childrenIds) {
				bean.setChildrenIds(childrenIds);
			}
			
}
