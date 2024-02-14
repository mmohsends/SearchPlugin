package com.dataserve.se.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.dataserve.se.bean.LinkDocumentBean;
import com.dataserve.se.db.AbstractDAO;
import com.dataserve.se.db.DatabaseException;
import com.ibm.json.java.JSONObject;

public class LinkDocumentDAO extends AbstractDAO{
	public LinkDocumentDAO() throws DatabaseException {
		super();
	}
	
	public boolean addLinkDocument(LinkDocumentBean bean) throws DatabaseException {
		try {
			LinkDocumentBean linkDocumentBeanParent = fetchLinkByDocumentId(bean.getMainDocId());
			for(Object obj: bean.getChilDocumentList()) {
				String childDocId = obj.toString().replace("{", "").replace("}", "");
				LinkDocumentBean linkDocumentChild = fetchLinkByDocumentId(childDocId);
				System.out.println("linkDocumentBeanParent.getFileId(): "+linkDocumentBeanParent.getFileId());
				System.out.println("linkDocumentChild.getFileId(): "+linkDocumentChild.getFileId());
				
				stmt = con.prepareStatement("INSERT INTO DMS_FILES_LINKS (FILE_ID, CHILD_FILE_ID) " +
	                    "SELECT ?, ?" +
	                    "WHERE NOT EXISTS (SELECT 1 " +
	                    "                  FROM DMS_FILES_LINKS " +
	                    "                  WHERE FILE_ID = ? " +
	                    "                    AND CHILD_FILE_ID = ?)");

				stmt.setInt(1, linkDocumentBeanParent.getFileId());
				stmt.setInt(2, linkDocumentChild.getFileId());
				stmt.setInt(3,  linkDocumentBeanParent.getFileId());
				stmt.setInt(4, linkDocumentChild.getFileId());
	            return stmt.execute();
			}
			
            return true;
        } catch (SQLException e) {
            throw new DatabaseException("Error adding new record to table CLASSIFICTIONS", e);
        } finally {
            safeClose();
            releaseResources();
        }
	}
	
	public LinkDocumentBean fetchLinkByDocumentId(String DocId) throws DatabaseException {
		try {
			stmt = con.prepareStatement("SELECT * from DMS_FILES where DMS_FILES.DOCUMENT_ID = ?");
			stmt.setString(1, DocId);	
			rs = stmt.executeQuery();
			System.out.println("after excuteQuery");
			if (rs.next()) {
                LinkDocumentBean bean = new LinkDocumentBean();
                bean.setDocumentId(rs.getString("DOCUMENT_ID"));
                bean.setFileId(rs.getInt("FILE_ID"));
                return bean; // Return the bean directly
            } else {
                return null; // Return null if no record is found
            }
		} catch (SQLException e) {
			throw new DatabaseException("Error fetching record from table CLASSIFICTIONS", e);
		} finally {
			safeClose();
			releaseResources();
		}
	}
	
	public Set<LinkDocumentBean> fetchLinkDocument(String mainDocId) throws DatabaseException {
		try {
			stmt = con.prepareStatement("SELECT DOCUMENT_ID, DOCUMENT_CLASS, CREATED_BY, DOCUMENT_NAME, MAIN_DOC_ID " +
                    "FROM DMS_FILES_LINK WHERE MAIN_DOC_ID = ?");
			stmt.setString(1, mainDocId);	
			rs = stmt.executeQuery();
			Set<LinkDocumentBean> beans = new LinkedHashSet<LinkDocumentBean>();			
			while (rs.next()) {
				LinkDocumentBean bean = new LinkDocumentBean();
				bean.setDocumentId(rs.getString("DOCUMENT_ID"));
				bean.setDocumentClass(rs.getString("DOCUMENT_CLASS"));
				bean.setCreatedBy(rs.getString("CREATED_BY"));
				bean.setDocumentName(rs.getString("DOCUMENT_NAME"));
				bean.setMainDocId(rs.getString("MAIN_DOC_ID"));
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
	
	public int deleteLinkDocument(String documentId, String mainDocId) throws DatabaseException {
		try {
			stmt = con.prepareStatement(
				    "DELETE FROM DMS_FILES_LINK " +
				    "WHERE DOCUMENT_ID = ? " +
				    "  AND MAIN_DOC_ID = ?");
				stmt.setString(1, documentId);
				stmt.setString(2, mainDocId);
				int rowsAffected = stmt.executeUpdate();
				return rowsAffected;
		} catch (SQLException e) {
			throw new DatabaseException("Error fetching record from table CLASSIFICTIONS", e);
		} finally {
			safeClose();
			releaseResources();
		}
	}
	
	
}