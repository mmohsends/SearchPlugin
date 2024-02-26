package com.dataserve.se.business.classification;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.dataserve.se.bean.ClassificationBean;
import com.dataserve.se.bean.SaveType;
import com.dataserve.se.db.ClassificationDAO;
import com.dataserve.se.db.DatabaseException;
import com.dataserve.se.fn.CEDAO;
import com.dataserve.se.fn.FileNetException;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class ClassificationModel {

private ClassificationBean bean;
//private Set<DeptClassModel> DCModels;
private ClassificationModel parent;
//private Set<DeptClassModel> departments = null;

private Set<ClassificationModel> children = new LinkedHashSet<ClassificationModel>();
	
		public ClassificationModel(ClassificationBean bean) {
			this.bean = bean;
		}
		
//		public ClassificationModel(int id, String nameAr, String nameEn, int parentID, String symbolicName, String classCode, int saveTypeId) throws ClassificationException {
//			this(id, nameAr, nameEn, parentID, symbolicName, classCode, saveTypeId, null);
//		}
		
//		public ClassificationModel(int id, String nameAr, String nameEn, int parentID, String sympolicName, String classCode, int saveTypeId, Set<DeptClassModel> DCModel) throws ClassificationException {
//			ClassificationBean bean = new ClassificationBean();
//			bean.setId(id);
//			bean.setNameAr(nameAr);
//			bean.setNameEn(nameEn);
//			bean.setParentID(parentID);
//			bean.setSymbolicName(sympolicName);
//			bean.setClassCode(classCode);
//			bean.setSaveTypeId(saveTypeId);
//			this.bean = bean;
//			this.DCModels= DCModel;	 
//		}
		
		
		
		public static Set<ClassificationModel> getFNAddedClassificationsByUserId(int userId) throws ClassificationException {
			return getFNAddedClassificationsByUserAndStorageCenter(userId, null);
		}
		
		public static Set<ClassificationModel> getFNAddedClassificationsByUserAndStorageCenter(int userId, Integer storageCenterId) throws ClassificationException {
			try {
				ClassificationDAO dao = new ClassificationDAO();
				Set<ClassificationBean> beans;
				if (storageCenterId == null) {
					beans = dao.fetchFNAddedClassificationsByUserId(userId);
				} else {
					beans = dao.fetchFNAddedClassificationsByUserAndStorageCenter(userId, storageCenterId);
				}
				Set<ClassificationModel> models = new LinkedHashSet<ClassificationModel>();
				for (ClassificationBean b : beans) {
					ClassificationModel ClassificationModel = new ClassificationModel(b);
					models.add(	ClassificationModel);
				}
				return models;
			} catch (DatabaseException e) {
				throw new ClassificationException("Error getting Classificafion for user with id '" + userId + "'", e);
			}
		}
		
		
		
		public static Set<ClassificationModel> getFNAddedClassifications() throws ClassificationException {
			try {
				ClassificationDAO dao = new ClassificationDAO();
				Set<ClassificationBean> beans = dao.fetchFNAddedClassifications();
				Set<ClassificationModel> models = new LinkedHashSet<ClassificationModel>();
				for (ClassificationBean b : beans) {
					ClassificationModel ClassificationModel = new ClassificationModel(b);
					models.add(	ClassificationModel);
				}
				return models;
			} catch (DatabaseException e) {
				throw new ClassificationException("Error getting Classificafion", e);
			}
		}
		
		
		
		public JSONObject getAsJson() throws ClassificationException {
//			departments = DeptClassModel.fetchDeptClass(bean.getId());
			
			JSONObject obj = new JSONObject();
			obj.put("id", bean.getId());
			obj.put("nameAr", bean.getNameAr());
			obj.put("nameEn", bean.getNameEn());
			obj.put("parentID", bean.getParentID());
			obj.put("sympolicName", bean.getSymbolicName());
			obj.put("ClassCode", bean.getClassCode());
			obj.put("saveType", bean.getSaveTypeId());
			obj.put("typeAr", SaveType.getSaveTypeById(bean.getSaveTypeId()).getTypeAr());
			obj.put("typeEn", SaveType.getSaveTypeById(bean.getSaveTypeId()).getTypeEn());
			obj.put("isFnAdded", bean.getisFnAdded());
			obj.put("children", new JSONArray());
			
			JSONArray depts = new JSONArray();
//			for (DeptClassModel m : departments) {
//				depts.add(m.getAsJson().toString());
//			}
////			.toString()
//			obj.put("departments", depts);
			
			return obj;
		}
		
		public JSONObject getFullStructure() throws ClassificationException {
			try {
				JSONObject obj = getAsJson();
				JSONArray jsonChildren = new JSONArray();
				loadChildren();
				for (ClassificationModel child : getChildren()) {
					jsonChildren.add(child.getFullStructure());
				}
				obj.put("children", jsonChildren);
				return obj;
			} catch (Exception e) {
				throw new ClassificationException("Error getting classification full structure for classification with id '" + bean.getId() + "'",e);
			}
		}
		
		private void loadChildren() throws ClassificationException {
			try {
				if (bean.getChildrenIds().size() > 0) {
					ClassificationDAO dao = new ClassificationDAO();
					Set<ClassificationBean> beans = dao.fetchClassification(bean.getChildrenIds());
					for (ClassificationBean b : beans) {
						children.add(new ClassificationModel(b));
					}
				} else {
					children = new LinkedHashSet<ClassificationModel>();
				}
			} catch (Exception e) {
				throw new ClassificationException("Error loading classification children for classification with id '" + bean.getId() + "'",e);
			}
		}
		
		
		
		public JSONObject getFullStructure(int userId) throws ClassificationException {
			try {
				JSONObject obj = getAsJson();
				JSONArray jsonChildren = new JSONArray();
				loadChildren(userId);
				for (ClassificationModel child : getChildren()) {
					jsonChildren.add(child.getFullStructure(userId));
				}
				obj.put("children", jsonChildren);
				return obj;
			} catch (Exception e) {
				throw new ClassificationException("Error getting classification full structure for classification with id '" + bean.getId() + "'",e);
			}
		}
		
		public JSONArray getAsJsonArray(int userId, String parentId, Locale loc) throws ClassificationException {
			try {
				JSONArray arr = new JSONArray();
				JSONObject obj = getAsJson();
				obj.put("parent", parentId);
				obj.put("name", getDisplayName(loc));
				obj.put("isLastLevel", (getChildrenIds().size() > 0 ? false : true));
				arr.add(obj);
				loadChildren(userId);
				for (ClassificationModel child : getChildren()) {
					arr.addAll(child.getAsJsonArray(userId, bean.getId() + "", loc));
				}
				return arr;
			} catch (Exception e) {
				throw new ClassificationException("Error getting classification full structure for classification with id '" + bean.getId() + "'",e);
			}
		}
		
		private String getDisplayName(Locale loc) {
			return "(" + getClassCode() + ") " + (loc.equals(Locale.ENGLISH) ? getNameEn() : getNameAr());
		}
		
		private String getName(Locale loc) {
			return (loc.equals(Locale.ENGLISH) ? getNameEn() : getNameAr());
		}
		
		private void loadChildren(int userId) throws ClassificationException {
			try {
				if (bean.getChildrenIds().size() > 0) {
					ClassificationDAO dao = new ClassificationDAO();
					Set<ClassificationBean> beans = dao.fetchClassification(userId, bean.getChildrenIds());
					for (ClassificationBean b : beans) {
						children.add(new ClassificationModel(b));
					}
				} else {
					children = new LinkedHashSet<ClassificationModel>();
				}
			} catch (Exception e) {
				throw new ClassificationException("Error loading classification children for classification with id '" + bean.getId() + "'",e);
			}
		}
		
		public String getFullQualifiedName(Locale loc) {
			if (getParent() == null) {
				return getName(loc);
			} else {
				return getParent().getFullQualifiedName(loc) + " > " + getName(loc);
			}
		}

		public String getFullQualifiedDisplayName(Locale loc) {
			if (getParent() == null) {
				return getDisplayName(loc);
			} else {
				return getParent().getFullQualifiedName(loc) + " > " + getDisplayName(loc);
			}
		}
		
		public int getParentID() {
			return bean.getParentID();
		}
		
		public String getNameAr() {
			return bean.getNameAr();
		}
		
		public String getNameEn() {
			return bean.getNameEn();
		}

		public int getUserId() {
			return bean.getId();
		}
		
		public String getSympolicName() {
			return bean.getSymbolicName();
		}
		public String getClassCode() {
			return bean.getClassCode();
		}

		public Set<ClassificationModel> getChildren() {
			return children;
		}

		public void setChildren(Set<ClassificationModel> children) {
			this.children = children;
		}
		
		public Set<Integer> getChildrenIds() {
			return bean.getChildrenIds();
		}
		
		public void setChildrenIds(Set<Integer> childrenIds) {
			bean.setChildrenIds(childrenIds);
		}
		
		public Integer getId() {
			return bean.getId();
		}
		public ClassificationModel getParent() {
			return parent;
		}
		
		public void setParent(ClassificationModel parent) {
			this.parent = parent;
		}
		
		public int getSaveTypeId() {
			return bean.getSaveTypeId();
		}
		public void setSaveTypeId(int saveTypeId) {
			bean.setSaveTypeId(saveTypeId);
		}
		
		public SaveType getSaveType() {
			return SaveType.getSaveTypeById(bean.getSaveTypeId());
		}
		public void setSaveType(SaveType saveType) {
			bean.setSaveTypeId(saveType.getId());
		}
		
		public Set<Integer> getDepartmentIds() {
			return bean.getDeptsIds();
		}
		
//		public Set<DeptClassModel> getClassificationDepartments() {
//			if (departments == null) {
////				loadDepartments();
//			}
//			
//			return departments;
//		}
}
