package com.dataserve.se.business.classification;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.dataserve.se.bean.ChoiceListBean;
import com.dataserve.se.bean.PropertyTemplateBean;
import com.dataserve.se.db.dao.PropertyTemplateDAO;
import com.dataserve.se.fn.CEDAO;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class PropertyTemplateModel {

	private PropertyTemplateBean bean;
	private String classSymbolicName;
	Locale locale ;
	public PropertyTemplateModel(PropertyTemplateBean bean) throws ClassificationException {
		this.bean = bean;
	}

	public PropertyTemplateModel(Locale locale , String classSymbolicName) {
		this.classSymbolicName = classSymbolicName;
		this.locale = locale;
	}


	public Set<PropertyTemplateModel> GetClassProperty(String repositoryId, PluginServiceCallbacks callBacks,
			String classSymbolicName) throws ClassificationException {
		try {

			// get class property from Class Property Definition
			CEDAO cedao = new CEDAO(repositoryId, callBacks);
			Set<PropertyTemplateModel> templateModels = new HashSet<PropertyTemplateModel>();
			Set<PropertyTemplateBean> templateBeans = cedao.fetchCustomClassPropertyDefinition(classSymbolicName);
			if (templateBeans != null && templateBeans.size() > 0) {
				PropertyTemplateDAO propertyTemplateDAO = new PropertyTemplateDAO();
				// get class property from db
				Map<String, PropertyTemplateBean> dbPropertyMap = propertyTemplateDAO.GetClassProperty(classSymbolicName,callBacks);

				for (PropertyTemplateBean propertyTemplateBean : templateBeans) {
					PropertyTemplateBean bean = dbPropertyMap.get(propertyTemplateBean.getSymbolicName());
					if(bean!= null ){
						// set Required and choice list from DB
						if ( bean.isRequired()){
							propertyTemplateBean.setRequired(bean.isRequired());
						}
						if ( bean.isChoiceList()){
							propertyTemplateBean.setChoiceList(bean.isChoiceList());
						}
						if (bean.getChoiceListBeans() != null  && !bean.getChoiceListBeans().isEmpty()){
							propertyTemplateBean.setChoiceListBeans(bean.getChoiceListBeans());
						}
						if(bean.getDepOn() !=null && !bean.getDepOn().equals("")){
							propertyTemplateBean.setDepOn(bean.getDepOn());
						}

						PropertyTemplateModel templateModel = new PropertyTemplateModel(propertyTemplateBean);
						templateModels.add(templateModel);
					}
					
					
				}
			}

			return templateModels;
		} catch (Exception e) {
			
			throw new ClassificationException("Error Get Class Property with classSymbolicName '" + classSymbolicName
					+ "'", e);
		}
	}


	public Object getAsJSON() {
		JSONObject obj = new JSONObject();
		obj.put("symbolicName", bean.getSymbolicName());
		obj.put("classSymbolicName", bean.getClassSymbolicName());
		obj.put("dataType", bean.getDataType());
		obj.put("isMultiCardinality", bean.isMultiCardinality());

		obj.put("size", bean.getMaxLength());

		obj.put("defaultVal", bean.getDefaultVal());

		obj.put("isChoiceList", bean.isChoiceList());

		obj.put("minValue", bean.getMinValue());
		obj.put("maxValue", bean.getMaxValue());
		obj.put("isRequired", bean.isRequired());
		obj.put("depOn", bean.getDepOn());
		obj.put("displayName", bean.getDisplayName());
		
		JSONArray choiceListObj = new JSONArray();
		
//		System.out.println("bean.getChoiceListBeans()");
		
		if(bean.isChoiceList() && bean.getChoiceListBeans() != null && !bean.getChoiceListBeans().isEmpty()) {
		
		for (ChoiceListBean choice : bean.getChoiceListBeans()) {
			
			System.out.println("bean.getChoiceListBeans()" + choice.getDispName());
		    // Creating a JSONObject for each ChoiceListBean
		    JSONObject jsonObject = new JSONObject();
		    jsonObject.put("lang", choice.getLang());
		    jsonObject.put("dispName", choice.getDispName());
		    jsonObject.put("value", choice.getValue());
		    jsonObject.put("depOn", choice.getDepOn());
		    jsonObject.put("depValue", choice.getDepValue());

		    // Adding the JSONObject to the JSONArray
		    choiceListObj.add(jsonObject);
		}
		
		}
		obj.put("choiceListBeans", choiceListObj);
		
		
		return obj;
	}

	public Object getSymbloicNames() {
		JSONObject obj = new JSONObject();
		obj.put("id", bean.getSymbolicName());
		obj.put("name",  bean.getSymbolicName());
		return obj;
		
		
	}
	
	public void print() {
//		System.out.println("Property Definition " + "\t" + "ClassSymbolicName : " + this.bean.getClassSymbolicName()
//				+ "\t" + "SymbolicName : " + this.bean.getSymbolicName() + "\t" + "DataType : "
//				+ this.bean.getDataType() + "\t" + "DefaultVal : " + this.bean.getDefaultVal() + "\t" + "MaxLength : "
//				+ this.bean.getMaxLength() + "\t" + "MinValue : " + this.bean.getMinValue() + "\t" + "MaxValue : "
//				+ this.bean.getMaxValue() + "\t" + "isChoiceList : " + this.bean.isChoiceList() + "\t"
//				+ "isMultiCardinality : " + this.bean.isMultiCardinality()
//
//		);

	}

	public PropertyTemplateBean getBean() {
		return bean;
	}

	public void setBean(PropertyTemplateBean bean) {
		this.bean = bean;
	}

}
