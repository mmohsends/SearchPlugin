package com.dataserve.se.bean;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.ibm.json.java.JSONObject;

public class ClassificationBean extends AbstractBean {
	private int classificationId;
	private String symbolicName;
	private int ParentID;
	private String ClassCode ;
	private int saveTypeId;
	private boolean isFnAdded;
	private Set<Integer> childrenIds;
	private Set<Integer> DeptsIds;
	private Set<Integer> FoldersIds;
	private boolean is_fn_added;
	public String getClassCode() {
		return ClassCode;
	}
	public void setClassCode(String classCode) {
		ClassCode = classCode;
	}
	public Set<Integer> getFoldersIds() {
		return FoldersIds;
	}
	public void setFoldersIds(Set<Integer> foldersIds) {
		FoldersIds = foldersIds;
	}
 
	public String getSymbolicName() {
		return symbolicName;
	}
	public void setSymbolicName(String symbolicName) {
		this.symbolicName = symbolicName;
	}
	public int getParentID() {
		return ParentID;
	}
	public void setParentID(int parentID) {
		ParentID = parentID;
	}
	public boolean getisFnAdded() {
		return isFnAdded;
	}
	public void setisFnAdded(Boolean IsFnAdded) {
		isFnAdded = IsFnAdded;
	}
	public Set<Integer> getDeptsIds() {
		return DeptsIds;
	}
	public void setDeptsIds(Set<Integer> deptsIds) {
		DeptsIds = deptsIds;
	}
	
	public Set<Integer> getChildrenIds() {
		return childrenIds;
	}
	public void setChildrenIds(Set<Integer> childrenIds) {
		this.childrenIds = childrenIds;
	}
	public int getSaveTypeId() {
		return saveTypeId;
	}
	public void setSaveTypeId(int saveTypeId) {
		this.saveTypeId = saveTypeId;
	}
	public int getClassificationId() {
		return classificationId;
	}
	public void setClassificationId(int classificationId) {
		this.classificationId = classificationId;
	}
	
	public JSONObject getAsJson(Locale locale) {
		JSONObject obj = new JSONObject();
		obj.put("classificationId", this.getClassificationId());
		obj.put("classEnName", this.getNameEn());
		obj.put("classArName", this.getNameAr());
		
		return obj;
	}
	
	public boolean getIs_fn_added() {
		return is_fn_added;
	}
	public void setIs_fn_added(boolean is_fn_added) {
		this.is_fn_added = is_fn_added;
	}
}
