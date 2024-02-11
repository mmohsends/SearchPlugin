package com.dataserve.se.bean;

import java.io.Serializable;
import java.util.List;



public class Department implements Serializable {
	private static final long serialVersionUID = 1L;
	private int deptId;
	private String deptArName;
	private String deptCode;
	private String deptEnName;
	private boolean enabled;
	private String deptEmail;

	public Department() {
	}

	public Department(int deptId) {
		this.deptId = deptId;		
	}

	public Department(int deptId, String deptEmail) {
		super();
		this.deptId = deptId;
		this.deptEmail = deptEmail;
	}

	public int getDeptId() {
		return this.deptId;
	}

	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}


	public String getDeptArName() {
		return this.deptArName;
	}

	public void setDeptArName(String deptArName) {
		this.deptArName = deptArName;
	}


	public String getDeptCode() {
		return this.deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}


	public String getDeptEnName() {
		return this.deptEnName;
	}

	public void setDeptEnName(String deptEnName) {
		this.deptEnName = deptEnName;
	}


	public boolean getEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getDeptEmail() {
		return deptEmail;
	}

	public void setDeptEmail(String deptEmail) {
		this.deptEmail = deptEmail;
	}

	

}