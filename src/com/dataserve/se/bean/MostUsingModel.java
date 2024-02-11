package com.dataserve.se.bean;

import java.util.LinkedHashSet;
import java.util.Set;

import com.dataserve.se.db.MostUsingDAO;
import com.dataserve.se.db.DatabaseException;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class MostUsingModel {
	private MostUsingBean bean;
	
	public MostUsingModel(MostUsingBean bean) {
		this.bean = bean;
	}
	
	public MostUsingModel(String propertyName, String propertyValue) throws DatabaseException {
		bean = new MostUsingBean();
		bean.setPropertyName(propertyName);
		bean.setPropertyValue(propertyValue);
	}
	
	public JSONObject getAsJson() throws DatabaseException {
		try {
			JSONObject obj = new JSONObject();
			obj.put("propertyName", bean.getPropertyName());
			obj.put("propertyValue", bean.getPropertyValue());
			return obj;
		} catch (Exception e) {
			throw new DatabaseException("Error getting group with id '" + bean.getId() + "' as json", e);
		}
	}

	public static Set<MostUsingModel> getAllGroups() throws Exception {
		try {
			Set<MostUsingModel> groups = new LinkedHashSet<MostUsingModel>();
			MostUsingDAO dao = new MostUsingDAO();
			Set<MostUsingBean> beans = dao.fetchAllGroups(); 
			for (MostUsingBean b : beans) {
				groups.add(new MostUsingModel(b));
			}
			return groups;
		} catch (Exception e) {
			throw new Exception("Error getting all groups", e);
		}
	}

	
	
}
