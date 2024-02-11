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

public class LinkDocumentDAO extends AbstractDAO{
	public LinkDocumentDAO() throws DatabaseException {
		super();
	}
	
	public boolean addLinkDocument(LinkDocumentBean bean) throws DatabaseException {
		try {
			stmt = con.prepareStatement("INSERT INTO DMS_FILES_LINK (DOCUMENT_ID, DOCUMENT_CLASS, CREATED_BY, DOCUMENT_NAME, MAIN_DOC_ID) " +
                    "SELECT ?, ?, ?, ?, ? " +
                    "WHERE NOT EXISTS (SELECT 1 " +
                    "                  FROM DMS_FILES_LINK " +
                    "                  WHERE DOCUMENT_ID = ? " +
                    "                    AND DOCUMENT_CLASS = ? " +
                    "                    AND CREATED_BY = ? " +
                    "                    AND DOCUMENT_NAME = ? " +
                    "                    AND MAIN_DOC_ID = ?)");

			stmt.setNString(1, bean.getDocumentId());
			stmt.setNString(2, bean.getDocumentClass());
			stmt.setNString(3, bean.getCreatedBy());
			stmt.setNString(4, bean.getDocumentName());
			stmt.setNString(5, bean.getMainDocId());
			stmt.setNString(6, bean.getDocumentId());
			stmt.setNString(7, bean.getDocumentClass());
			stmt.setNString(8, bean.getCreatedBy());
			stmt.setNString(9, bean.getDocumentName());
			stmt.setNString(10, bean.getMainDocId());
            return stmt.execute();
        } catch (SQLException e) {
            throw new DatabaseException("Error adding new record to table CLASSIFICTIONS", e);
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
	
	public int deleteLinkDocument(String documentId, String documentClass, String createdBy, String documentName,String mainDocId) throws DatabaseException {
		LinkDocumentBean bean = new LinkDocumentBean();
		try {
			stmt = con.prepareStatement(
				    "DELETE FROM DMS_FILES_LINK " +
				    "WHERE DOCUMENT_ID = ? " +
				    "  AND DOCUMENT_CLASS = ? " +
				    "  AND CREATED_BY = ? " +
				    "  AND DOCUMENT_NAME = ? " +
				    "  AND MAIN_DOC_ID = ?");
				stmt.setString(1, documentId);
				stmt.setString(2, documentClass);
				stmt.setString(3, createdBy);
				stmt.setString(4, documentName);
				stmt.setString(5, mainDocId);
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