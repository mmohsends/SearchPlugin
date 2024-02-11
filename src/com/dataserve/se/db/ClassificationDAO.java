package com.dataserve.se.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Set;

import com.dataserve.se.bean.ClassificationBean;

public class ClassificationDAO extends AbstractDAO{
	public ClassificationDAO() throws DatabaseException {
		super();
	}
	
	
	public Set<ClassificationBean> fetchFNAddedClassificationsByUserId(int id) throws DatabaseException {		
		try {
			stmt = con.prepareStatement("SELECT distinct CLASSIFICTIONS.CLASSIFICATION_ID CLASSIFICATION_ID, CLASSIFICTIONS.CLASS_AR_NAME CLASS_AR_NAME, " +
				"CLASSIFICTIONS.CLASS_EN_NAME CLASS_EN_NAME, CLASSIFICTIONS.SYMPOLIC_NAME SYMPOLIC_NAME, " +
				"CLASSIFICTIONS.PARENT_ID PARENT_ID, CLASSIFICTIONS.CLASS_CODE CLASS_CODE, CLASSIFICTIONS.SAVE_TYPE SAVE_TYPE " +
				"FROM CLASSIFICTIONS CLASSIFICTIONS " +
                "INNER JOIN CLASS_DEPT CLASS_DEPT ON CLASSIFICTIONS.CLASSIFICATION_ID = CLASS_DEPT.CLASSIFICATION_ID " + 
                "INNER JOIN DEPARTMENTS DEPARTMENTS ON CLASS_DEPT.DEPT_ID = DEPARTMENTS.DEPT_ID " + 
                "INNER JOIN USERS USERS ON DEPARTMENTS.DEPT_ID = USERS.DEPARTMENT_ID " + 
				"WHERE USERS.USER_ID = ? And  CLASSIFICTIONS.is_fn_added = 1" );
			
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			Set<ClassificationBean> beans = new LinkedHashSet<ClassificationBean>();			
			while (rs.next()) {
				ClassificationBean bean = new ClassificationBean();
				bean.setId(rs.getInt("CLASSIFICATION_ID"));
				bean.setNameAr(rs.getString("CLASS_AR_NAME"));
				bean.setNameEn(rs.getString("CLASS_EN_NAME"));
				bean.setSymbolicName(rs.getString("SYMPOLIC_NAME"));
				bean.setParentID(rs.getInt("PARENT_ID"));
				bean.setClassCode(rs.getString("CLASS_CODE"));
				bean.setSaveTypeId(rs.getInt("SAVE_TYPE"));
				bean.setChildrenIds(getFNAddedChildrenIds(id,bean.getId()));
				beans.add(bean);
			}
			return beans;
		} catch (SQLException e) {
			throw new DatabaseException("Error fetching record from table CLASSIFICTIONS BY User ID ", e);
		} finally {
			safeClose();
			releaseResources();
		}
	}

	public Set<ClassificationBean> fetchFNAddedClassificationsByUserAndStorageCenter(int userId, int storageCenterId) throws DatabaseException {
		try {
			stmt = con.prepareStatement("SELECT distinct " +
				"CLASSIFICTIONS.CLASSIFICATION_ID CLASSIFICATION_ID, CLASSIFICTIONS.CLASS_AR_NAME CLASS_AR_NAME, CLASSIFICTIONS.CLASS_EN_NAME CLASS_EN_NAME, " + 
				"CLASSIFICTIONS.SYMPOLIC_NAME SYMPOLIC_NAME, CLASSIFICTIONS.PARENT_ID PARENT_ID, CLASSIFICTIONS.CLASS_CODE CLASS_CODE, CLASSIFICTIONS.SAVE_TYPE " +
				"FROM CLASSIFICTIONS CLASSIFICTIONS " + 
				"INNER JOIN CLASS_DEPT CLASS_DEPT ON CLASSIFICTIONS.CLASSIFICATION_ID = CLASS_DEPT.CLASSIFICATION_ID " +
                "INNER JOIN DEPARTMENTS DEPARTMENTS ON CLASS_DEPT.DEPT_ID = DEPARTMENTS.DEPT_ID " + 
                "INNER JOIN USERS USERS ON DEPARTMENTS.DEPT_ID = USERS.DEPARTMENT_ID " + 
                "INNER JOIN STORAGE_CENTER CENTER ON DEPARTMENTS.DEPT_ID = CENTER.DEPT_ID " + 
				"WHERE USERS.USER_ID = ? AND CENTER.CENTER_ID = ? AND  is_fn_added = 1");
			stmt.setInt(1, userId);
			stmt.setInt(2, storageCenterId);
			rs = stmt.executeQuery();
			Set<ClassificationBean> beans = new LinkedHashSet<ClassificationBean>();			
			while (rs.next()) {
				ClassificationBean bean = new ClassificationBean();
				bean.setId(rs.getInt("CLASSIFICATION_ID"));
				bean.setNameAr(rs.getString("CLASS_AR_NAME"));
				bean.setNameEn(rs.getString("CLASS_EN_NAME"));
				bean.setSymbolicName(rs.getString("SYMPOLIC_NAME"));
				bean.setParentID(rs.getInt("PARENT_ID"));
				bean.setClassCode(rs.getString("CLASS_CODE"));
				bean.setSaveTypeId(rs.getInt("SAVE_TYPE"));
				bean.setChildrenIds(getFNAddedChildrenIds(userId, bean.getId()));
				beans.add(bean);
			}
			return beans;
		} catch (SQLException e) {
			throw new DatabaseException("Error fetching record from table CLASSIFICTIONS BY User ID ", e);
		} finally {
			safeClose();
			releaseResources();
		}
	}
	
	public Set<ClassificationBean> fetchClassificationsByIds(String ids) throws DatabaseException {		
		String params = ids.replace("[", "").replace("]", "");
		try {
			stmt = con.prepareStatement("SELECT CLASSIFICATION_ID, CLASS_AR_NAME, CLASS_EN_NAME, SYMPOLIC_NAME, PARENT_ID, CLASS_CODE, SAVE_TYPE ,is_fn_added FROM CLASSIFICTIONS WHERE CLASSIFICATION_ID IN (" + params + ")");		
			rs = stmt.executeQuery();
			Set<ClassificationBean> beans = new LinkedHashSet<ClassificationBean>();			
			while (rs.next()) {
				ClassificationBean bean = new ClassificationBean();
				bean.setId(rs.getInt("CLASSIFICATION_ID"));
				bean.setNameAr(rs.getString("CLASS_AR_NAME"));
				bean.setNameEn(rs.getString("CLASS_EN_NAME"));
				bean.setSymbolicName(rs.getString("SYMPOLIC_NAME"));
				bean.setParentID(rs.getInt("PARENT_ID"));
				bean.setClassCode(rs.getString("CLASS_CODE"));
				bean.setSaveTypeId(rs.getInt("SAVE_TYPE"));
				bean.setisFnAdded(rs.getBoolean("is_fn_added"));
				bean.setChildrenIds(getFNAddedChildrenIds(bean.getId()));
				beans.add(bean);
			}
			return beans;
		} catch (SQLException e) {
			throw new DatabaseException("Error fetching record from table CLASSIFICTIONS BY User ID ", e);
		} finally {
			safeClose();
			releaseResources();
		}
	}

	public Set<ClassificationBean> fetchFNAddedClassifications() throws DatabaseException {
		try {
			stmt = con.prepareStatement("SELECT CLASSIFICATION_ID, CLASS_AR_NAME, CLASS_EN_NAME, SYMPOLIC_NAME, PARENT_ID, CLASS_CODE, SAVE_TYPE "
					+ "	FROM CLASSIFICTIONS WHERE is_fn_added = 1");		
			rs = stmt.executeQuery();
			Set<ClassificationBean> beans = new LinkedHashSet<ClassificationBean>();			
			while (rs.next()) {
				ClassificationBean bean = new ClassificationBean();
				bean.setId(rs.getInt("CLASSIFICATION_ID"));
				bean.setNameAr(rs.getString("CLASS_AR_NAME"));
				bean.setNameEn(rs.getString("CLASS_EN_NAME"));
				bean.setSymbolicName(rs.getString("SYMPOLIC_NAME"));
				bean.setParentID(rs.getInt("PARENT_ID"));
				bean.setClassCode(rs.getString("CLASS_CODE"));
				bean.setSaveTypeId(rs.getInt("SAVE_TYPE"));
				bean.setChildrenIds(getFNAddedChildrenIds(bean.getId()));
				beans.add(bean);
			}
			return beans;
		} catch (SQLException e) {
			throw new DatabaseException("Error fetching record from table CLASSIFICTIONS", e);
		} finally {
			safeClose();
			releaseResources();
		}
	}
	
	private Set<Integer> getFNAddedChildrenIds(int parentId) throws DatabaseException {
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;
		Set<Integer> results = new LinkedHashSet<Integer>();
		try {
			stmt2 = con.prepareStatement("SELECT CLASSIFICATION_ID FROM CLASSIFICTIONS WHERE PARENT_ID = ?  AND   CLASSIFICTIONS.is_fn_added = 1");
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
	
	private Set<Integer> getFNAddedChildrenIds( int UserId,int parentId) throws DatabaseException {
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;
		Set<Integer> results = new LinkedHashSet<Integer>();
		try {
			stmt2 = con.prepareStatement("SELECT CLASSIFICTIONS.CLASSIFICATION_ID FROM     CLASSIFICTIONS INNER JOIN " +
                 " CLASS_DEPT ON CLASSIFICTIONS.[CLASSIFICATION_ID] = CLASS_DEPT.CLASSIFICATION_ID INNER JOIN " +
                " DEPARTMENTS ON CLASS_DEPT.DEPT_ID = DEPARTMENTS.DEPT_ID INNER JOIN " +
                 " USERS ON DEPARTMENTS.DEPT_ID = USERS.DEPARTMENT_ID " +
				   " WHERE USERS.USER_ID = ? AND CLASSIFICTIONS.PARENT_ID = ? AND   CLASSIFICTIONS.is_fn_added = 1");
			stmt2.setInt(1, UserId);
			stmt2.setInt(2, parentId);
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

	@SuppressWarnings("resource")
	public Set<ClassificationBean> fetchClassification(int  userId, Set<Integer> ids) throws DatabaseException {
		String params = ids.toString().replace("[", "").replace("]", "");
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;
		Set<ClassificationBean> beans = new LinkedHashSet<ClassificationBean>();
		try {
			stmt = con.prepareStatement("SELECT CLASSIFICATION_ID, CLASS_AR_NAME, CLASS_EN_NAME, SYMPOLIC_NAME, PARENT_ID, CLASS_CODE, SAVE_TYPE FROM CLASSIFICTIONS WHERE CLASSIFICATION_ID IN (" + params + ")");
			rs = stmt.executeQuery();
			while (rs.next()) {
				ClassificationBean bean = new ClassificationBean();
				bean.setId(rs.getInt("CLASSIFICATION_ID"));
				bean.setNameAr(rs.getString("CLASS_AR_NAME"));
				bean.setNameEn(rs.getString("CLASS_EN_NAME"));
				bean.setSymbolicName(rs.getString("SYMPOLIC_NAME"));
				bean.setParentID(rs.getInt("PARENT_ID"));
				bean.setClassCode(rs.getString("CLASS_CODE"));
				bean.setSaveTypeId(rs.getInt("SAVE_TYPE"));
				
				stmt2 = con.prepareStatement("SELECT DEPT_ID FROM  CLASS_DEPT where  CLASSIFICATION_ID = ?");
				stmt2.setInt(1, bean.getId());
				rs2 = stmt2.executeQuery();
				bean.setDeptsIds(new LinkedHashSet<Integer>());
				while (rs2.next()) {
					bean.getDeptsIds().add(rs2.getInt("DEPT_ID"));
				}

				stmt2 = con.prepareStatement("SELECT FOLDER_ID FROM FOLDER WHERE CLASSIFICATION_ID = ?");
				stmt2.setInt(1, bean.getId());
				rs2 = stmt2.executeQuery();
				bean.setFoldersIds(new LinkedHashSet<Integer>());
				while (rs2.next()) {
					bean.getFoldersIds().add(rs2.getInt("FOLDER_ID"));
				}

				bean.setChildrenIds(getFNAddedChildrenIds( userId,bean.getId()));
				
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

	@SuppressWarnings("resource")
	public Set<ClassificationBean> fetchClassification(Set<Integer> ids) throws DatabaseException {
		String params = ids.toString().replace("[", "").replace("]", "");
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;
		Set<ClassificationBean> beans = new LinkedHashSet<ClassificationBean>();
		try {
			stmt = con.prepareStatement("SELECT CLASSIFICATION_ID, CLASS_AR_NAME, CLASS_EN_NAME, SYMPOLIC_NAME, PARENT_ID, CLASS_CODE, SAVE_TYPE FROM CLASSIFICTIONS WHERE CLASSIFICATION_ID IN (" + params + ")");
			rs = stmt.executeQuery();
			while (rs.next()) {
				ClassificationBean bean = new ClassificationBean();
				bean.setId(rs.getInt("CLASSIFICATION_ID"));
				bean.setNameAr(rs.getString("CLASS_AR_NAME"));
				bean.setNameEn(rs.getString("CLASS_EN_NAME"));
				bean.setSymbolicName(rs.getString("SYMPOLIC_NAME"));
				bean.setParentID(rs.getInt("PARENT_ID"));
				bean.setClassCode(rs.getString("CLASS_CODE"));
				bean.setSaveTypeId(rs.getInt("SAVE_TYPE"));
				
				stmt2 = con.prepareStatement("SELECT DEPT_ID FROM  CLASS_DEPT where  CLASSIFICATION_ID = ?");
				stmt2.setInt(1, bean.getId());
				rs2 = stmt2.executeQuery();
				bean.setDeptsIds(new LinkedHashSet<Integer>());
				while (rs2.next()) {
					bean.getDeptsIds().add(rs2.getInt("DEPT_ID"));
				}

				stmt2 = con.prepareStatement("SELECT FOLDER_ID FROM FOLDER WHERE CLASSIFICATION_ID = ?");
				stmt2.setInt(1, bean.getId());
				rs2 = stmt2.executeQuery();
				bean.setFoldersIds(new LinkedHashSet<Integer>());
				while (rs2.next()) {
					bean.getFoldersIds().add(rs2.getInt("FOLDER_ID"));
				}

				bean.setChildrenIds(getFNAddedChildrenIds(bean.getId()));
				
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

	@SuppressWarnings("resource")
	public ClassificationBean fetchClassificationBySymbolicName(String className) throws DatabaseException {
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;
		try {
			stmt = con.prepareStatement("SELECT CLASSIFICATION_ID, CLASS_AR_NAME, CLASS_EN_NAME, SYMPOLIC_NAME, PARENT_ID, CLASS_CODE, SAVE_TYPE FROM CLASSIFICTIONS WHERE SYMPOLIC_NAME = ?");
			stmt.setString(1, className);
			rs = stmt.executeQuery();
			if (rs.next()) {
				ClassificationBean bean = new ClassificationBean();
				bean.setId(rs.getInt("CLASSIFICATION_ID"));
				bean.setNameAr(rs.getString("CLASS_AR_NAME"));
				bean.setNameEn(rs.getString("CLASS_EN_NAME"));
				bean.setSymbolicName(rs.getString("SYMPOLIC_NAME"));
				bean.setParentID(rs.getInt("PARENT_ID"));
				bean.setClassCode(rs.getString("CLASS_CODE"));
				bean.setSaveTypeId(rs.getInt("SAVE_TYPE"));
				stmt2 = con.prepareStatement("SELECT DEPT_ID FROM  CLASS_DEPT where  CLASSIFICATION_ID = ?");
				stmt2.setInt(1, bean.getId());
				rs2 = stmt2.executeQuery();
				bean.setDeptsIds(new LinkedHashSet<Integer>());
				while (rs2.next()) {
					bean.getDeptsIds().add(rs2.getInt("DEPT_ID"));
				}

				stmt2 = con.prepareStatement("SELECT FOLDER_ID FROM FOLDER WHERE CLASSIFICATION_ID = ?");
				stmt2.setInt(1, bean.getId());
				rs2 = stmt2.executeQuery();
				bean.setFoldersIds(new LinkedHashSet<Integer>());
				while (rs2.next()) {
					bean.getFoldersIds().add(rs2.getInt("FOLDER_ID"));
				}

				bean.setChildrenIds(getFNAddedChildrenIds(bean.getId()));
				return bean;
			} else {
				throw new DatabaseException("Classification with symbolic name '" + className + "' does not exist in database");
			}
			
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