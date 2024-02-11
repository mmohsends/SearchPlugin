package com.dataserve.se.manager;

import java.util.Set;

import com.dataserve.se.db.ConnectionManager;
import com.dataserve.se.db.DatabaseException;
import com.dataserve.se.db.dao.SearchFilesDAO;
import com.dataserve.se.bean.DmsFiles;

public class SearchManager {
	ConnectionManager dbConnection = new ConnectionManager();

	public SearchManager() throws DatabaseException {
		try {
			dbConnection.initConn();
		} catch (DatabaseException e) {
			throw new DatabaseException("Error to open data base connection",
					e);
		}
	}

	public Set<DmsFiles> searchFiles(String currentUserId) throws Exception{
		try {
			SearchFilesDAO searchFilesDAO = new SearchFilesDAO(dbConnection);
			searchFilesDAO.searchFiles();
			return null;
		} catch (Exception e) {

			try {

				dbConnection.rollBack();
			} catch (DatabaseException ex) {
				throw new Exception("Error rollback DB connection", ex);
			}
			// }
			throw new Exception("Error getAllDestroyFiles", e);

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
