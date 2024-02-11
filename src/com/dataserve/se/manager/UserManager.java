package com.dataserve.se.manager;

import java.util.Set;

import com.dataserve.se.bean.DmsFiles;
import com.dataserve.se.bean.User;
import com.dataserve.se.db.ConnectionManager;
import com.dataserve.se.db.DatabaseException;
import com.dataserve.se.db.dao.UserDAO;

public class UserManager {
	ConnectionManager dbConnection = new ConnectionManager();

	public UserManager() throws DatabaseException {
		try {
			dbConnection.initConn();
		} catch (DatabaseException e) {
			throw new DatabaseException("Error to open data base connection",
					e);
		}
	}
	
	
	public User fetUserByNameLDAP(String currentUserId) throws Exception{
		try {
			UserDAO userDao = new UserDAO(dbConnection);
			User user =  userDao.fetUserByNameLDAP(currentUserId);
			return user;
		} catch (Exception e) {

			try {

				dbConnection.rollBack();
			} catch (DatabaseException ex) {
				throw new Exception("Error rollback DB connection", ex);
			}
			// }
			throw new Exception("Error fetUserByNameLDAP", e);

		} finally {
			// close connection
			try {
				dbConnection.releaseConnection();
			} catch (DatabaseException ex) {
				throw new Exception("Error releaseConnection DB connection",ex);
			}

		}
	}
}
