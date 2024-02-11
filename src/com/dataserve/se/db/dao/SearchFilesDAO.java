package com.dataserve.se.db.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.dataserve.se.db.DatabaseException;
import com.dataserve.se.util.ConfigManager;
import com.dataserve.se.db.ConnectionManager;

public class SearchFilesDAO {
	ConnectionManager dbConnection = null;

	public SearchFilesDAO(ConnectionManager dbConnection) {
		super();
		this.dbConnection = dbConnection;
	}

	public Set<com.dataserve.se.bean.DmsFiles> searchFiles() throws DatabaseException{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try{
			String SQL = " ";

			stmt = dbConnection.getCon().prepareStatement(SQL);
			rs = stmt.executeQuery();
			
			while(rs.next()){
				
				
				
			}
			
			return null;
		} catch (SQLException e) {
			throw new DatabaseException("Error searchFiles", e);
		} finally {
        	try {
				if (rs != null) {
					rs.close();
				}

				if (stmt != null) {
					stmt.close();
				}
				
			} catch (SQLException unexpected) {
				throw new DatabaseException("Error searchFiles", unexpected);
			}
        }
	}

}
