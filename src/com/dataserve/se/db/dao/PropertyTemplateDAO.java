package com.dataserve.se.db.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dataserve.se.bean.ChoiceListBean;
import com.dataserve.se.bean.ClassificationBean;
import com.dataserve.se.bean.ConfigBean;
import com.dataserve.se.bean.PropertyTemplateBean;
import com.dataserve.se.db.AbstractDAO;
import com.dataserve.se.db.DatabaseException;
import com.ibm.ecm.extension.PluginServiceCallbacks;

public class PropertyTemplateDAO extends AbstractDAO {

	public PropertyTemplateDAO() throws DatabaseException {
		super();
		// TODO Auto-generated constructor stub
	}

	public String addPropertyToEDSTable(PropertyTemplateBean bean) throws DatabaseException {

		try {
			// , REQUIRED, HIDDEN
			// ,MAXVAL,MINVAL,MAXLEN,FORMAT,FORMATDESC,HASDEPENDANT


			String SQL = "INSERT INTO  EDS (OBJECTTYPE , PROPERTY, DISPMODE, IS_CHOICE_LIST, MAXLEN, MINVAL, MAXVAL, REQUIRED )"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?)  ";
			stmt = con.prepareStatement(SQL);
			
			stmt.setNString(1, bean.getClassSymbolicName());
			stmt.setNString(2, bean.getSymbolicName());
			stmt.setString(3, bean.getDisplayMode());
			stmt.setBoolean(4, bean.isChoiceList());
			if (bean.getMaxLength() != null)
				stmt.setInt(5, bean.getMaxLength());
			else
				stmt.setNull(5, Types.INTEGER);

			if (bean.getMinValue() != null)
				stmt.setFloat(6, bean.getMinValue());
			else
				stmt.setNull(6, Types.FLOAT);
				
			if (bean.getMaxValue() != null)
				stmt.setFloat(7, bean.getMaxValue());
			else
				stmt.setNull(7, Types.FLOAT);
				
			stmt.setBoolean(8, bean.isRequired());
			con.setAutoCommit(false);

			if (stmt.executeUpdate() > 0) {
				con.commit();
			}
			return "SUCCESS";
		} catch (SQLException e) {
			throw new DatabaseException("Error insert Property To EDS Table  CLASSIFICTIONS BY Symbolic Name '" + bean
					+ "'", e);
		} finally {
			safeClose();
			releaseResources();
		}
	}
	public String addPropertyBatchToEDSTable(PropertyTemplateBean bean) throws DatabaseException {

		try {
			// , REQUIRED, HIDDEN
			// ,MAXVAL,MINVAL,MAXLEN,FORMAT,FORMATDESC,HASDEPENDANT
			String SQL = "INSERT INTO  EDS (OBJECTTYPE , PROPERTY, DISPMODE, IS_CHOICE_LIST, MAXLEN, MINVAL, MAXVAL, REQUIRED )"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?)  ";
			stmt = con.prepareStatement(SQL);
			
			Set<String> classSymbolicNames = bean.getClassSymbolicNames() ;
			for (String classSymbolicName : classSymbolicNames) {
				stmt.setNString(1, classSymbolicName);
				stmt.setNString(2, bean.getSymbolicName());
				stmt.setString(3, bean.getDisplayMode());
				stmt.setBoolean(4, bean.isChoiceList());
				if (bean.getMaxLength() != null)
					stmt.setInt(5, bean.getMaxLength());
				else
					stmt.setNull(5, Types.INTEGER);

				if (bean.getMinValue() != null)
					stmt.setFloat(6, bean.getMinValue());
				else
					stmt.setNull(6, Types.FLOAT);
					
				if (bean.getMaxValue() != null)
					stmt.setFloat(7, bean.getMaxValue());
				else
					stmt.setNull(7, Types.FLOAT);
					
				stmt.setBoolean(8, bean.isRequired());

				stmt.addBatch(); 
			}
			stmt.executeBatch();
			
			con.commit();
			
			return "SUCCESS";
		} catch (SQLException e) {
			throw new DatabaseException("Error insert Property Batch To EDS Table  CLASSIFICTIONS BY Symbolic Name '" + bean
					+ "'", e);
		} finally {
			safeClose();
			releaseResources();
		}
	}
	
	public Map<String, PropertyTemplateBean> GetClassProperty(String classSymbolicName, PluginServiceCallbacks  callBacks) throws DatabaseException {
		try {
			stmt = con.prepareStatement("SELECT OBJECTTYPE,PROPERTY,DISPMODE,REQUIRED,HIDDEN ,MAXVAL,MINVAL,MAXLEN,"
					+ "FORMAT,FORMATDESC,HASDEPENDANT ,IS_CHOICE_LIST , DEPON FROM EDS" + " WHERE EDS.OBJECTTYPE = ? ");

			stmt.setString(1, classSymbolicName);
			rs = stmt.executeQuery(); 
			
			Map<String, PropertyTemplateBean> beans = new HashMap<String, PropertyTemplateBean>();
			while (rs.next()) {
				PropertyTemplateBean bean = new PropertyTemplateBean();
				bean.setClassSymbolicName(rs.getString("OBJECTTYPE"));
				bean.setSymbolicName(rs.getString("PROPERTY"));
				bean.setDisplayMode(rs.getString("DISPMODE"));
				if (rs.getString("REQUIRED") != null && rs.getString("REQUIRED").equalsIgnoreCase("1"))
					bean.setRequired(true);
				else
					bean.setRequired(false);

				bean.setMaxValue(rs.getFloat("MAXVAL"));
				bean.setMinValue(rs.getFloat("MINVAL"));
				bean.setMaxLength(rs.getInt("MAXLEN"));
				bean.setChoiceList(rs.getBoolean("IS_CHOICE_LIST"));
				bean.setDepOn(rs.getString("DEPON"));
				if(bean.isChoiceList()) {
					PreparedStatement stmt2 = null;
					stmt2 = con.prepareStatement("SELECT LANG , DISPNAME , VALUE , DEPON , DEPVALUE FROM  [dbo].[EDS_CHOICES] WHERE OBJECTTYPE = ? and PROPERTY = ? and LANG =?");
					stmt2.setString(1, classSymbolicName);
					stmt2.setString(2,bean.getSymbolicName());
					stmt2.setString(3,callBacks.getLocale().getLanguage() ); 
					
					ResultSet res = stmt2.executeQuery(); 
					List<ChoiceListBean> choiceListBeans = new ArrayList<ChoiceListBean>();
					while (res.next()) {	
						ChoiceListBean choiceListBean = new ChoiceListBean();
						choiceListBean.setLang(res.getNString("LANG"));
						choiceListBean.setDispName(res.getNString("DISPNAME"));
						choiceListBean.setValue(res.getNString("VALUE"));
						choiceListBean.setDepOn(res.getNString("DEPON"));
						choiceListBean.setDepValue(res.getNString("DEPVALUE"));
						choiceListBeans.add(choiceListBean);
					}
					bean.setChoiceListBeans(choiceListBeans);
				}
				
				beans.put(bean.getSymbolicName(), bean);		
				
			}
			return beans;
		} catch (SQLException e) {
			throw new DatabaseException("Error Get Class Property From EDS Table  CLASSIFICTIONS BY Symbolic Name '"
					+ classSymbolicName + "'", e);
		} finally {
			safeClose();
			releaseResources();
		}
	}
	

	/**
	 * check if class property have choice list values
	 * 
	 * @param className
	 * @param propertySymbolicName
	 * @return
	 * @throws DatabaseException
	 */
	public boolean isClassPropertyHasChoiceValues(String className, String propertySymbolicName)
			throws DatabaseException {
		try {
			stmt = con.prepareStatement("SELECT OBJECTTYPE,PROPERTY FROM [dbo].[EDS_CHOICES]"
					+ " WHERE OBJECTTYPE = ? and PROPERTY = ?");

			stmt.setString(1, className);
			stmt.setString(2, propertySymbolicName);

			rs = stmt.executeQuery();
			if (rs.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			throw new DatabaseException("Error isClassPropertyHasChoiceValues Class Name '"
					+ className + "'  propertySymbolicName ' " + propertySymbolicName, e);
		} finally {
			safeClose();
			releaseResources();
		}
	}

	/** 
	 * delete class property from EDS table
	 * @param className
	 * @param propertySymbolicName
	 * @return
	 * @throws DatabaseException
	 */
	public boolean deletClassProperty(String className, String propertySymbolicName) throws DatabaseException {
		try {
			stmt = con.prepareStatement("DELETE FROM [dbo].[EDS]"
					+ " WHERE OBJECTTYPE = ? and PROPERTY = ?");

			stmt.setString(1, className);
			stmt.setString(2, propertySymbolicName);
			con.setAutoCommit(false);
			if (stmt.executeUpdate() > 0) {
				con.commit();
			}
			return true;
		} catch (SQLException e) {
			throw new DatabaseException("Error delet Class Property Class Name '"
					+ className + "'  propertySymbolicName ' " + propertySymbolicName, e);
		} finally {
			safeClose();
			releaseResources();
		}
	}

	/**
	 * Get IS Choice List Property 
	 * @param classSymbolicName
	 * @return
	 * @throws DatabaseException
	 */
	public Set<PropertyTemplateBean> GetClassChoiceListProperty(String classSymbolicName) throws DatabaseException {
		try {
			stmt = con.prepareStatement("SELECT OBJECTTYPE,PROPERTY,DISPMODE,REQUIRED,HIDDEN ,MAXVAL,MINVAL,MAXLEN,"
					+ "FORMAT,FORMATDESC,HASDEPENDANT ,IS_CHOICE_LIST, DEPON  FROM EDS" 
					+ " WHERE EDS.OBJECTTYPE = ? AND EDS.IS_CHOICE_LIST = 1 ORDER BY DEPON DESC");

			stmt.setString(1, classSymbolicName);
			rs = stmt.executeQuery();
			Set<PropertyTemplateBean> beans = new HashSet<PropertyTemplateBean>();
			while (rs.next()) {
				PropertyTemplateBean bean = new PropertyTemplateBean();
				bean.setClassSymbolicName(rs.getString("OBJECTTYPE"));
				bean.setSymbolicName(rs.getString("PROPERTY"));
				bean.setDisplayMode(rs.getString("DISPMODE"));
				if (rs.getString("REQUIRED") != null && rs.getString("REQUIRED").equalsIgnoreCase("1"))
					bean.setRequired(true);
				bean.setMaxValue(rs.getFloat("MAXVAL"));
				bean.setMinValue(rs.getFloat("MINVAL"));
				bean.setMaxLength(rs.getInt("MAXLEN"));
				bean.setChoiceList(rs.getBoolean("IS_CHOICE_LIST"));
				bean.setDepOn(rs.getString("DEPON"));

				beans.add(bean);
			}
			return beans;
		} catch (SQLException e) {
			throw new DatabaseException("Error Get Class Property From EDS Table  CLASSIFICTIONS BY Symbolic Name '"
					+ classSymbolicName + "'", e);
		} finally {
			safeClose();
			releaseResources();
		}
	}

	/** 
	 * Update Choice List depend on in EDS table
	 * @param classSymbolicName
	 * @param beans
	 * @return
	 * @throws DatabaseException
	 */
	public String updateCLPropertyDepOn(String classSymbolicName, Set<PropertyTemplateBean> beans) throws DatabaseException {
		try {
			
			stmt = con.prepareStatement("UPDATE  [dbo].[EDS]"
					+ " SET  [HASDEPENDANT] = ? WHERE OBJECTTYPE = ? and PROPERTY = ?");
			con.setAutoCommit(false);
			for (PropertyTemplateBean propertyTemplateBean : beans) {
				if(propertyTemplateBean.getDepOn() !=null && !propertyTemplateBean.getDepOn().equalsIgnoreCase("")){
					stmt.setString(1, "1");
					stmt.setString(2, classSymbolicName);
					stmt.setString(3, propertyTemplateBean.getDepOn()); 				
				}else{
					stmt.setString(1, "0");
					stmt.setString(2, classSymbolicName);
					// get depon from DB before update Has dependent properties 
					String dependeOn = getDepOnProperty(classSymbolicName, propertyTemplateBean);
					
					stmt.setString(3, dependeOn);

				}
				
				
				stmt.addBatch();  // this will just collect the data values
			}
			stmt.executeBatch(); 
			con.commit();
			PreparedStatement stmt2 = null;
			stmt2 = con.prepareStatement("UPDATE  [dbo].[EDS]"
						+ " SET  DEPON = ? WHERE OBJECTTYPE = ? and PROPERTY = ?");
			for (PropertyTemplateBean propertyTemplateBean : beans) {

				stmt2.setString(1, propertyTemplateBean.getDepOn());
				stmt2.setString(2, classSymbolicName);
				stmt2.setString(3, propertyTemplateBean.getSymbolicName());
				stmt2.addBatch(); // this will just collect the data values
					
				}
				stmt2.executeBatch(); // this will actually execute the updates all in one
				con.commit();
				return "SUCCESS";

			 
		} catch (SQLException e) {
			throw new DatabaseException("Error delet Class Property Class Name '"
					+ classSymbolicName + "'", e);
		} finally {
			safeClose();
			releaseResources();
		}
	}

	private String getDepOnProperty(String classSymbolicName, PropertyTemplateBean propertyTemplateBean)
			throws SQLException {
		PreparedStatement stmt3 = null;
		stmt3 = con.prepareStatement("SELECT DEPON  FROM EDS" 
				+ " WHERE EDS.OBJECTTYPE = ? AND EDS.PROPERTY = ? ");

		stmt3.setString(1, classSymbolicName);
		stmt3.setString(2, propertyTemplateBean.getSymbolicName());

		rs = stmt3.executeQuery();
		String dependeOn="";
		while (rs.next()) {
			dependeOn = rs.getString("DEPON");
		}
		return dependeOn;
	}
	
	public ClassificationBean GetClassBySymbolicName(String symbolicName) throws DatabaseException {
		try {
			stmt = con.prepareStatement("SELECT  CLASSIFICTIONS.CLASSIFICATION_ID, CLASSIFICTIONS.CLASS_AR_NAME"
					+ ",CLASSIFICTIONS.CLASS_EN_NAME, CLASSIFICTIONS.SYMPOLIC_NAME, CLASSIFICTIONS.PARENT_ID , CLASSIFICTIONS.CLASS_CODE  "
					+ ", CLASSIFICTIONS.IS_FN_ADDED"
					+ " FROM	CLASSIFICTIONS "
					+ " WHERE CLASSIFICTIONS.SYMPOLIC_NAME = ?  ") ; 
			
			stmt.setString(1, symbolicName);
			rs = stmt.executeQuery();
			ClassificationBean bean = null;
			while (rs.next()) {
				bean = new ClassificationBean();
				bean.setId(rs.getInt("CLASSIFICATION_ID"));
				bean.setNameAr(rs.getString("CLASS_AR_NAME"));
				bean.setNameEn(rs.getString("CLASS_EN_NAME"));
				bean.setSymbolicName(rs.getString("SYMPOLIC_NAME"));
				bean.setParentID(rs.getInt("PARENT_ID"));
				bean.setClassCode(rs.getString("CLASS_CODE"));
				bean.setIs_fn_added(rs.getBoolean("IS_FN_ADDED"));
				bean.setChildrenIds(getChildrenIds(bean.getId()));
			}
			return bean;
		} catch (SQLException e) {
			throw new DatabaseException("Error fetching record from table CLASSIFICTIONS BY Symbolic Name ", e);
		} finally { 
			safeClose();
			releaseResources();
		}
	}
	private Set<Integer> getChildrenIds( int parentId) throws DatabaseException {
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;
		Set<Integer> results = new LinkedHashSet<Integer>();
		try {
			stmt2 = con.prepareStatement("SELECT CLASSIFICTIONS.CLASSIFICATION_ID "
					+ "FROM     CLASSIFICTIONS "
					+" WHERE  CLASSIFICTIONS.PARENT_ID = ?");
			stmt2.setInt(1, parentId);
			rs2 = stmt2.executeQuery();
			while (rs2.next()) {
				results.add(rs2.getInt("CLASSIFICATION_ID"));
			}
			return results;
		} catch (Exception e) {
			throw new DatabaseException("Error loading children of classification with id " + parentId, e);
		} finally {
			if (rs2 != null) {
				try {
					rs2.close();
				} catch (SQLException e) {}
			}
			if (stmt2 != null) {
				try {
					stmt2.close();
				} catch (SQLException e) {}
			}
		}
	}
	
	public Set<ClassificationBean> fetchClassification( Set<Integer> ids) throws DatabaseException {
		String params = ids.toString().replace("[", "").replace("]", "");
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;
		Set<ClassificationBean> beans = new LinkedHashSet<ClassificationBean>();
		try {
			stmt = con.prepareStatement("SELECT CLASSIFICATION_ID, CLASS_AR_NAME, CLASS_EN_NAME, SYMPOLIC_NAME, PARENT_ID,CLASS_CODE, CLASSIFICTIONS.IS_FN_ADDED "
					+ "FROM CLASSIFICTIONS "
					+ "WHERE CLASSIFICATION_ID IN (" + params + ")");
			rs = stmt.executeQuery();
			while (rs.next()) {
				ClassificationBean bean = new ClassificationBean();
				bean.setId(rs.getInt("CLASSIFICATION_ID"));
				bean.setNameAr(rs.getString("CLASS_AR_NAME"));
				bean.setNameEn(rs.getString("CLASS_EN_NAME"));
				bean.setSymbolicName(rs.getString("SYMPOLIC_NAME"));
				bean.setParentID(rs.getInt("PARENT_ID"));
				bean.setClassCode(rs.getString("CLASS_CODE"));
				bean.setIs_fn_added(rs.getBoolean("IS_FN_ADDED"));



				bean.setChildrenIds(getChildrenIds(bean.getId()));
				
				beans.add(bean);
			}
			return beans;
		} catch (SQLException e) {
			throw new DatabaseException("Error fetching record from table CLASSIFICTIONS", e);
		} finally {
			if (rs2 != null) {
				try {
					rs2.close();
				} catch (SQLException e) {}
			}
			if (stmt2 != null) {
				try {
					stmt2.close();
				} catch (SQLException e) {}
			}
			safeClose();
			releaseResources();
		}
	}
}
