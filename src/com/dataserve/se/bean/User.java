package com.dataserve.se.bean;

import java.util.Locale;
import java.util.Set;

import com.dataserve.se.bean.Department;
import com.ibm.json.java.JSONObject;

public class User {
 
	private int userId;
	private String userNameLDAP;
	private boolean isLogin;
	private boolean isActive;
	private String userArName;
	private String userEnName;
	private String userEmail;
	private int departmentId;
	private Set<Integer> DeptIds;
	private Department department;

	public User() {
		super();
	}
	public User(int userId) {
		super();
		this.userId = userId;
	}
	public User(int userId, String userNameLDAP, String userEmail) {
		super();
		this.userId = userId;
		this.userNameLDAP = userNameLDAP;
		this.userEmail = userEmail;
	}

	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserNameLDAP() {
		return userNameLDAP;
	}
	public void setUserNameLDAP(String usernameLDAP) {
		userNameLDAP = usernameLDAP;
	}
	public boolean getIsLogin() {
		return isLogin;
	}
	public void setIsLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}
	public boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
	public Set<Integer> getDeptIds() {
		return DeptIds;
	}
	public void setDeptIds(Set<Integer> deptIds) {
		DeptIds = deptIds;
	}
	public String getUserArName() {
		return userArName;
	}
	public void setUserArName(String userNameAr) {
		this.userArName = userNameAr;
	}
	public String getUserEnName() {
		return userEnName;
	}
	public void setUserEnName(String userNameEn) {
		this.userEnName = userNameEn;
	}
	
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public int getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public Object getAsJson(Locale locale) {
		JSONObject obj = new JSONObject();
		obj.put("userId", this.getUserId());
		obj.put("userNameLDAP", this.getUserNameLDAP());
		obj.put("userArName", this.getUserArName());
		obj.put("userEnName", this.getUserEnName());
		obj.put("departmentId", this.getDepartmentId());
		
		return obj;
	}
	
		 
 
	
}
