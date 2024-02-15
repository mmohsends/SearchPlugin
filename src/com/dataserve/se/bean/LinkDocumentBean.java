package com.dataserve.se.bean;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
public class LinkDocumentBean extends AbstractBean {
	
	private String DocumentId ;
	private String DocumentClass ;
	private String CreatedBy;
	private String DocumentName ;
	private String DocumentNameAr ;
	private String MainDocId ;
	private Set<Integer> childrenIds;
	private Integer FileId;
	private Integer childFileId;
	private JSONArray ChilDocumentList;



public String getDocumentId() {
	return DocumentId;
}
public void setDocumentId(String documentId) {
	DocumentId = documentId;
}

public String getDocumentClass() {
	return DocumentClass;
}
public void setDocumentClass(String documentClass) {
	DocumentClass = documentClass;
}

public int getFileId() {
	return FileId;
}
public void setFileId(int fileId) {
	FileId = fileId;
}

public String getCreatedBy() {
	return CreatedBy;
}
public void setCreatedBy(String createdBy) {
	CreatedBy = createdBy;
}

public String getDocumentName() {
	return DocumentName;
}
public void setDocumentName(String documentName) {
	DocumentName = documentName;
}


public String getMainDocId() {
	return MainDocId;
}
public void setMainDocId(String mainDocId) {
	MainDocId = mainDocId;
}

public Set<Integer> getChildrenIds() {
	return childrenIds;
}
public void setChildrenIds(Set<Integer> childrenIds) {
	this.childrenIds = childrenIds;
}
public JSONArray getChilDocumentList() {
	return ChilDocumentList;
}
public void setChilDocumentList(JSONArray chilDocumentList) {
	ChilDocumentList = chilDocumentList;
}
public Integer getChildFileId() {
	return childFileId;
}
public void setChildFileId(int childFileId) {
	this.childFileId = childFileId;
}
public String getDocumentNameAr() {
	return DocumentNameAr;
}
public void setDocumentNameAr(String documentNameAr) {
	DocumentNameAr = documentNameAr;
}

}