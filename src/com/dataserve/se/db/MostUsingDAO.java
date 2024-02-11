package com.dataserve.se.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.LinkedHashSet;
import java.util.Set;

import com.dataserve.se.bean.MostUsingBean;

public class MostUsingDAO extends AbstractDAO {
	public MostUsingDAO() throws DatabaseException {
		super();
	}
	
	
	public Set<MostUsingBean> fetchAllGroups() throws DatabaseException {
		Set<MostUsingBean> groups = new LinkedHashSet<MostUsingBean>();
		try {
			stmt = con.prepareStatement("SELECT top 20 count( DMS_PROPERTIES_AUDIT.PROPERTY_VALUE) as NUMBER_COUNT,DMS_PROPERTIES_AUDIT.PROPERTY_NAME, DMS_PROPERTIES_AUDIT.PROPERTY_VALUE\r\n" + 
					"FROM    DMS_PROPERTIES_AUDIT INNER JOIN\r\n" + 
					"                  DMS_AUDIT ON DMS_PROPERTIES_AUDIT.DMS_AUDIT_ID = DMS_AUDIT.DMS_AUDIT_ID\r\n" + 
					"				 where DMS_AUDIT.OPERATION_ID =2 group by DMS_PROPERTIES_AUDIT.PROPERTY_NAME,PROPERTY_VALUE\r\n" + 
					"				 order by count( DMS_PROPERTIES_AUDIT.PROPERTY_VALUE) desc");
			rs = stmt.executeQuery();
			while (rs.next()) {		
				MostUsingBean bean = new MostUsingBean();;
				bean.setPropertyName(rs.getString("PROPERTY_NAME"));
				bean.setPropertyValue(rs.getString("PROPERTY_VALUE"));
				groups.add(bean);
			}
		} catch (SQLException e) {
			throw new DatabaseException("Error fetching record from table GROUPS", e);
		} finally {
			safeClose();
			releaseResources();
		}
		return groups;
	}
	

}