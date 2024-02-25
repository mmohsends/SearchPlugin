package com.dataserve.eds.fn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;

import com.dataserve.se.bean.PropertyDataType;
import com.dataserve.se.bean.PropertyTemplateBean;
import com.filenet.api.admin.ClassDefinition;
import com.filenet.api.admin.LocalizedString;
import com.filenet.api.admin.PropertyDefinition;
import com.filenet.api.admin.PropertyDefinitionDateTime;
import com.filenet.api.admin.PropertyDefinitionFloat64;
import com.filenet.api.admin.PropertyDefinitionInteger32;
import com.filenet.api.admin.PropertyDefinitionString;
import com.filenet.api.admin.PropertyTemplate;
import com.filenet.api.collection.LocalizedStringList;
import com.filenet.api.collection.PropertyDefinitionList;
import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.constants.FilteredPropertyType;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.UserContext;
import com.ibm.ecm.extension.PluginServiceCallbacks;

public class CEDAO {

	String repositoryId;
	PluginServiceCallbacks callBacks;

	public CEDAO(String repositoryId, PluginServiceCallbacks callBacks) {
		this.repositoryId = repositoryId;
		this.callBacks = callBacks;
		// TODO can we initialize ObjectStore Here
	}





	public PropertyTemplate getPropertyTemplate(String name, ObjectStore objectStore) {
		String queryFormat = "SELECT This FROM PropertyTemplate WHERE SymbolicName = '" + name + "'";
		SearchScope scope = new SearchScope(objectStore);
		// String query = MessageFormat.format(queryFormat, name );
		RepositoryRowSet fetchRows = scope.fetchRows(new SearchSQL(queryFormat), null, null, null);
		Iterator<?> iterator = fetchRows.iterator();
		if (!iterator.hasNext()) {
//			return null;
			return null;
		}

		RepositoryRow row = (RepositoryRow) iterator.next();
		return (PropertyTemplate) row.getProperties().getObjectValue("This");
	}

	public PropertyTemplateBean getPropertyTemplateBySymbolicName(String symbolicName) throws FileNetException {

		try {
			PropertyTemplateBean propertyTemplateBean = null;
			Subject subject = callBacks.getP8Subject(repositoryId);
			UserContext.get().pushSubject(subject);
			ObjectStore os = callBacks.getP8ObjectStore(repositoryId);

			// get property template from OS
			PropertyTemplate propertyTemplate = getPropertyTemplate(symbolicName, os);

			if (propertyTemplate != null) {

				propertyTemplateBean = new PropertyTemplateBean();
				propertyTemplateBean.setSymbolicName(propertyTemplate.get_SymbolicName());
				propertyTemplateBean.setDisplayName(propertyTemplate.get_DisplayName());

				return propertyTemplateBean;

			} else {
				return null;
			}

		} catch (EngineRuntimeException e) {
			
			throw new FileNetException( "Error get Property Template By  symbloic Name  '" + symbolicName + "'",
					e);
		} finally {
			UserContext.get().popSubject();
		}
	}



	public Set<PropertyTemplateBean> fetchCustomClassPropertyDefinition(String classSymbolicName)
			throws FileNetException {
		try {
			Set<PropertyTemplateBean> propertyTemplateBeanList = new HashSet<PropertyTemplateBean>();
			Subject subject = callBacks.getP8Subject(repositoryId);
			UserContext.get().pushSubject(subject);
			ObjectStore os = callBacks.getP8ObjectStore(repositoryId);
			// Construct property filter to ensure PropertyDefinitions property
			// of CD is returned as evaluated
			PropertyFilter pf = new PropertyFilter();
			pf.addIncludeType(0, null, Boolean.TRUE, FilteredPropertyType.ANY, null);

			// Fetch selected class definition from the server
			ClassDefinition objClassDef = Factory.ClassDefinition.fetchInstance(os, classSymbolicName, pf);
			// Get PropertyDefinitions property from the property cache
			PropertyDefinitionList objPropDefs = objClassDef.get_PropertyDefinitions();
			Iterator iter = objPropDefs.iterator();
			PropertyDefinition objPropDef = null;

			// Loop until property definition found
			while (iter.hasNext()) {
				objPropDef = (PropertyDefinition) iter.next();

				// check system proerty

				if (!objPropDef.get_IsSystemOwned() && !objPropDef.get_IsHidden()
						&& !objPropDef.get_SymbolicName().equals("SourceDocument")
						&& !objPropDef.get_SymbolicName().equals("CmFederatedLockStatus")
						&& !objPropDef.get_SymbolicName().equals("DocumentTitle")
						&& !objPropDef.get_SymbolicName().equals("DestinationDocuments")
						) {
					// Custom PropertyDefinition object found
					PropertyTemplateBean bean = new PropertyTemplateBean();
					bean.setSymbolicName(objPropDef.get_SymbolicName());
					
					PropertyTemplate propTemp = objPropDef.get_PropertyTemplate();
					LocalizedStringList localizedStringList = propTemp.get_DisplayNames();
					LocalizedString localizedString;
					for (int i = 0; i < localizedStringList.size(); i++) {
						localizedString = (LocalizedString) localizedStringList.get(i);
						if(localizedString.get_LocaleName().contains(callBacks.getLocale().getLanguage())){
							bean.setDisplayName(localizedString.get_LocalizedText());
							break;
						}
					}
					
					//bean.setDisplayName(objPropDef.get_DisplayName());
					
					switch(objPropDef.get_DataType().getValue()){
					case  8 : 
						bean.setDataType(PropertyDataType.STRING);
						PropertyDefinitionString  objPropDefStr= (PropertyDefinitionString)objPropDef;
						bean.setMaxLength(objPropDefStr.get_MaximumLengthString());		
						
						bean.setDefaultVal(objPropDefStr.get_PropertyDefaultString());
						break;
					case  3 : 
						bean.setDataType(PropertyDataType.DATE);
						PropertyDefinitionDateTime  objPropDefDate= (PropertyDefinitionDateTime)objPropDef;
						//bean.setDefaultVal(objPropDefDate.get_PropertyDefaultDateTime());
						break;
					case  6 : 
						bean.setDataType(PropertyDataType.INTEGER);
						PropertyDefinitionInteger32  objPropDefInt= (PropertyDefinitionInteger32)objPropDef;
						if(objPropDefInt.get_PropertyDefaultInteger32() !=null )
							bean.setDefaultVal(String.valueOf(objPropDefInt.get_PropertyDefaultInteger32() ));
						
						if(objPropDefInt.get_PropertyMinimumInteger32() !=null )
							bean.setMinValue(objPropDefInt.get_PropertyMinimumInteger32().floatValue() );
						
						if(objPropDefInt.get_PropertyMaximumInteger32() !=null )
							bean.setMaxValue(objPropDefInt.get_PropertyMaximumInteger32().floatValue() );
						break;
					case  4 : 
						bean.setDataType(PropertyDataType.FLOAT);
						PropertyDefinitionFloat64  objPropDefFloat= (PropertyDefinitionFloat64)objPropDef;
						if(objPropDefFloat.get_PropertyDefaultFloat64() !=null )
							bean.setDefaultVal(String.valueOf(objPropDefFloat.get_PropertyDefaultFloat64() ));	
						
						if(objPropDefFloat.get_PropertyMinimumFloat64() !=null )
							bean.setMinValue(objPropDefFloat.get_PropertyMinimumFloat64().floatValue() );
						
						if(objPropDefFloat.get_PropertyMinimumFloat64() !=null )
							bean.setMaxValue(objPropDefFloat.get_PropertyMinimumFloat64().floatValue() );
						break;
					
					}
					switch (objPropDef.get_Cardinality().getValue()){
						case 0 : bean.setMultiCardinality(false);
						case 2 : bean.setMultiCardinality(true);
					}
					
					bean.setClassSymbolicName(classSymbolicName);
					
					propertyTemplateBeanList.add(bean);
					
				}
			}
			return propertyTemplateBeanList;
		} catch (EngineRuntimeException e) {
			
			throw new FileNetException("Error fetch Custom Class Property Template Symbolic Name  classSymbolicName  '"
					+ classSymbolicName + "'", e);
		} finally {
			UserContext.get().popSubject();
		}
	}
	

	

	/* static {
    instances = new HashMap();
    DATE = new TypeID(3);
    BINARY = new TypeID(1);
    GUID = new TypeID(5);
    STRING = new TypeID(8);
    DOUBLE = new TypeID(4);
    OBJECT = new TypeID(7);
    BOOLEAN = new TypeID(2);
    LONG = new TypeID(6);
    serialPersistentFields = new ObjectStreamField[0];
}*/
	
//	 static {
//	        instances = new HashMap();
//	        ENUM = new Cardinality(1);
//	        SINGLE = new Cardinality(0);
//	        LIST = new Cardinality(2);
//	        serialPersistentFields = new ObjectStreamField[0];
//	    }
}
