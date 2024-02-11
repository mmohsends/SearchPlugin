package com.dataserve.se.db.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.dataserve.se.bean.Department;
import com.dataserve.se.bean.PermissionBean;
import com.dataserve.se.bean.User;
import com.dataserve.se.db.ConnectionManager;
import com.dataserve.se.db.DatabaseException;

public class UserDAO {
	ConnectionManager dbConnection = null;

	public UserDAO(ConnectionManager connectionManager) throws DatabaseException {
		this.dbConnection = connectionManager;
	}
	

	public User fetUserByNameLDAP(String userNameLDAP) throws DatabaseException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			
			stmt = dbConnection.getCon().prepareStatement("SELECT USER_ID, UserArname, UserEnName, UsernameLDAP, IsLogin, IsActive, DEPARTMENT_ID  "
					+ "	FROM USERS "
					+ "	WHERE UsernameLDAP = ?");
			stmt.setString(1, userNameLDAP);
			rs = stmt.executeQuery();
			if (rs.next()) {
				User bean = new User();
				bean.setUserId(rs.getInt("USER_ID"));
				bean.setUserArName(rs.getString("UserArname"));
				bean.setUserEnName(rs.getString("UserEnName"));
				bean.setUserNameLDAP(rs.getString("UsernameLDAP"));
				bean.setIsLogin(rs.getBoolean("IsLogin"));
				bean.setIsActive(rs.getBoolean("IsActive"));
				bean.setDepartmentId(rs.getInt("DEPARTMENT_ID"));
				bean.setDepartment(fetUserDepartment(bean.getUserId()));

				return bean;
			}
		} catch (SQLException e) {
			throw new DatabaseException("Error fetching record from table User", e);
		} finally {
        	try {
				if (rs != null) {
					rs.close();
				}

				if (stmt != null) {
					stmt.close();
				}
				
			} catch (SQLException unexpected) {
				throw new DatabaseException("Error fetching record from table User", unexpected);
			}
        }
		return null;
	}
	
	
	public Set<PermissionBean> fetchUserPermission(int userId) throws DatabaseException {
		String query = " SELECT GROUP_PERMISSIONS.PERMISSION_ID PERMISSION_ID, "
				+ "	PERMISSIONS.MODULE_ID, PERMISSIONS.ACTION_TYPE_ID, "
				+ "	PERMISSIONS.PermissionArName , PERMISSIONS.PermissionEnName, PERMISSIONS.Enabled "
				+ " FROM dbo.GROUP_PERMISSIONS GROUP_PERMISSIONS "
				+ " JOIN dbo.USERS_GROUPS USERS_GROUPS ON (GROUP_PERMISSIONS.GROUP_ID = USERS_GROUPS.GROUP_ID) "
				+ "	INNER JOIN dbo.PERMISSIONS ON PERMISSIONS.PermissionID = GROUP_PERMISSIONS.PERMISSION_ID "
				+ " WHERE USERS_GROUPS.USER_ID = ?";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = dbConnection.getCon().prepareStatement(query);
			stmt.setInt(1, userId);
			rs = stmt.executeQuery();
			Set<PermissionBean> pemisssions = new HashSet<PermissionBean>();
					
			while (rs.next()) {
				PermissionBean permissionBean = new PermissionBean();
				permissionBean.setId(rs.getInt("PERMISSION_ID"));
				permissionBean.setModuleId(rs.getInt("MODULE_ID"));
				permissionBean.setTypeId(rs.getInt("ACTION_TYPE_ID"));
//				System.out.println(permissionBean.getId());
				pemisssions.add(permissionBean);
				
			}
			return pemisssions;
		} catch (SQLException e) {
			throw new DatabaseException("Error  fetchUserPermission", e);
		} finally {
        	try {
				if (rs != null) {
					rs.close();
				}

				if (stmt != null) {
					stmt.close();
				}
				
			} catch (SQLException unexpected) {
				throw new DatabaseException("Error fetchUserPermission", unexpected);
			}
        }
	}
	
	public Department fetUserDepartment(int userId) throws DatabaseException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			List<Department> departments = new ArrayList<Department>();
			stmt = dbConnection
					.getCon()
					.prepareStatement(
							"SELECT DEPARTMENTS.DEPT_ID, DEPARTMENTS.DEPT_AR_NAME, DEPARTMENTS.DEPT_EN_NAME"
							+ "	,DEPARTMENTS.ENABLED, DEPARTMENTS.DEPT_CODE   "
							+ " FROM dbo.DEPARTMENTS "
							+ "	INNER JOIN dbo.USERS ON (USERS.DEPARTMENT_ID = DEPARTMENTS.DEPT_ID) "
							+ "	WHERE USERS.USER_ID = ?");
			stmt.setInt(1, userId);
			rs = stmt.executeQuery();
			Department bean = null;
			if (rs.next()) {
				bean = new Department();
				bean.setDeptId(rs.getInt("DEPT_ID"));
				bean.setDeptArName(rs.getString("DEPT_AR_NAME"));
				departments.add(bean);
				
			}
			return bean;
		} catch (SQLException e) {
			throw new DatabaseException(
					"Error fetching  User Department from table User_departments", e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}

				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException unexpected) {
				throw new DatabaseException(
						"Error fetching  User Department from table User_departments", unexpected);
			}
		}
	
	}

	
	public User getUserManagerByDeptId(int deptId) throws DatabaseException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String SQL = "SELECT USERS.USER_ID, USERS.UserArname, USERS.UserEnName, USERS.UsernameLDAP, USERS.IsLogin, USERS.IsActive,USERS.UserEmail "
					+ " FROM USERS "
					+ " INNER JOIN dbo.DEPARTMENTS ON (USERS.USER_ID = DEPARTMENTS.MANAGER_USER_ID)"
					+ " WHERE DEPARTMENTS.DEPT_ID = ?";
			
			stmt = dbConnection.getCon().prepareStatement(SQL);
			stmt.setInt(1, deptId);

			rs = stmt.executeQuery();
			User bean = null;
			if (rs.next()) {
				bean = new User();
				bean.setUserId(rs.getInt("USER_ID"));
				bean.setUserArName(rs.getString("UserArname"));
				bean.setUserEnName(rs.getString("UserEnName"));
				bean.setUserNameLDAP(rs.getString("UsernameLDAP"));
				bean.setIsLogin(rs.getBoolean("IsLogin"));
				bean.setIsActive(rs.getBoolean("IsActive"));
				bean.setUserEmail(rs.getString("UserEmail"));
			}
			return bean;
		} catch (SQLException e) {
			throw new DatabaseException( "Error  getRequesterManagerByDeptId table Departments",e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}

				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException unexpected) {
				throw new DatabaseException(
						"Error  getRequesterManagerByDeptId table WF_USER_ROLES",unexpected);
			}
		}
	}
}