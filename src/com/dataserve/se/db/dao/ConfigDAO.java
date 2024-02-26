package com.dataserve.se.db.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import com.dataserve.se.bean.ConfigBean;
import com.dataserve.se.db.ConnectionManager;
import com.dataserve.se.db.DatabaseException;

public class ConfigDAO {
	ConnectionManager dbConnection = null;

	public ConfigDAO(ConnectionManager dbConnection) {
		super();
		this.dbConnection = dbConnection;
	}
	
	public Set<ConfigBean> fetchAllConfigs() throws DatabaseException{
		Set<ConfigBean> configs = new LinkedHashSet<ConfigBean>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = dbConnection.getCon().prepareStatement("SELECT NAME, ISNULL(VALUE, '') VALUE, COMMENT FROM CONFIG ORDER BY NAME");
			rs = stmt.executeQuery();
			while (rs.next()) {	
				ConfigBean bean = new ConfigBean();
				bean.setName(rs.getString("NAME"));
				bean.setValue(rs.getString("VALUE"));
				bean.setComment(rs.getString("COMMENT"));
				configs.add(bean);
			}
		}catch (SQLException e) {
			throw new DatabaseException("Error fetching record from table CONFIG", e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}

				if (stmt != null) {
					stmt.close();
				}
				
			} catch (SQLException unexpected) {
				throw new DatabaseException("Error fetching record from table CONFIG", unexpected);
			}
		}
		return configs;
	}
}
