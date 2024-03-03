package com.dataserve.se.bean;

import java.util.List;
import java.util.Set;


public class PropertyTemplateBean extends AbstractBean{
	private String symbolicName;
	private String displayName;
	private boolean isMultiCardinality;
	private String dataType;
	private boolean isChoiceList;

	private String defaultVal;
	private boolean isRequired;
	private boolean isHidden;

	private String classSymbolicName;
	
	private List<ChoiceListBean> choiceListBeans;
	
	
	//EDS Attributes
	// min and max value for integer , float only;
	private Float maxValue;
	private Float minValue;

	// max length for String only
	private Integer maxLength;
	
	private String displayMode = "readwrite";

	private String format;
	private String formatDesc;
	private boolean hasDependent;
	private String depOn;

	private Set<String> classSymbolicNames;
	
	
	public PropertyTemplateBean() {
		
	}
	
	

	public PropertyTemplateBean(String symbolicName, String displayName, boolean isMultiCardinality, String dataType,
			String defaultVal, Integer size,boolean isChoiceList,  String classSymbolicName,Float minValue, Float maxValue, boolean isRequired) {
		
		this.symbolicName = symbolicName;
		this.displayName = displayName;
		this.isMultiCardinality = isMultiCardinality;
		this.dataType = dataType;
		this.defaultVal = defaultVal;
		this.maxLength = size;
		this.isChoiceList = isChoiceList;
		this.classSymbolicName = classSymbolicName;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.isRequired = isRequired;
	}

	public PropertyTemplateBean(String classSymbolicName) {
		this.classSymbolicName= classSymbolicName;
	}
	public String getSymbolicName() {
		return symbolicName;
	}

	public void setSymbolicName(String symbolicName) {
		this.symbolicName = symbolicName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isMultiCardinality() {
		return isMultiCardinality;
	}

	public void setMultiCardinality(boolean isMultiCardinality) {
		this.isMultiCardinality = isMultiCardinality;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public boolean isChoiceList() {
		return isChoiceList;
	}

	public void setChoiceList(boolean isChoiceList) {
		this.isChoiceList = isChoiceList;
	}
	
 
	public String getDefaultVal() {
		return defaultVal;
	}

	public void setDefaultVal(String defaultVal) {
		this.defaultVal = defaultVal;
	}

	public String getDisplayMode() {
		return displayMode;
	}

	public void setDisplayMode(String displayMode) {
		this.displayMode = displayMode;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getFormatDesc() {
		return formatDesc;
	}

	public void setFormatDesc(String formatDesc) {
		this.formatDesc = formatDesc;
	}

	public boolean isHasDependent() {
		return hasDependent;
	}

	public void setHasDependent(boolean hasDependent) {
		this.hasDependent = hasDependent;
	}

	public String getClassSymbolicName() {
		return classSymbolicName;
	}

	public void setClassSymbolicName(String classSymbolicName) {
		this.classSymbolicName = classSymbolicName;
	}
	public Float getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(Float maxValue) {
		this.maxValue = maxValue;
	}
	public Float getMinValue() {
		return minValue;
	}
	public void setMinValue(Float minValue) {
		this.minValue = minValue;
	}
	public Integer getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}
	public String getDepOn() {
		return depOn;
	}
	public void setDepOn(String depOn) {
		this.depOn = depOn;
	}
	public Set<String> getClassSymbolicNames() {
		return classSymbolicNames;
	}
	public void setClassSymbolicNames(Set<String> classSymbolicNames) {
		this.classSymbolicNames = classSymbolicNames;
	}

	public List<ChoiceListBean> getChoiceListBeans() {
		return choiceListBeans;
	}

	public void setChoiceListBeans(List<ChoiceListBean> choiceListBeans) {
		this.choiceListBeans = choiceListBeans;
	}
	
	

	

}
