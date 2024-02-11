package com.dataserve.se.bean;

import java.util.Map;
import java.util.Set;

public class FileSearchBean {

	private String classSymbolicName;
	
	private Map<String, String> searchProperties;

	
	public String getClassSymbolicName() {
		return classSymbolicName;
	}

	public void setClassSymbolicName(String classSymbolicName) {
		this.classSymbolicName = classSymbolicName;
	}

	public Map<String, String> getSearchProperties() {
		return searchProperties;
	}

	public void setSearchProperties(Map<String, String> searchProperties) {
		this.searchProperties = searchProperties;
	}


}
