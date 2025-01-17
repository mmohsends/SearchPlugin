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
	        System.out.println("linkDocumentBeanParent: " + linkDocumentBeanParent.getFileId());
	        System.out.println("getChilDocumentList: " + bean.getChilDocumentList());

	        boolean anyInserts = false; // Flag to check if any inserts were performed

	        for (Object obj : bean.getChilDocumentList()) {
	            String childDocId = obj.toString().replace("{", "").replace("}", "");
	            System.out.println("childDocId:" + childDocId);

	            LinkDocumentBean linkDocumentChild = fetchLinkByDocumentId(childDocId);
	            System.out.println("linkDocumentBeanParent.getFileId(): " + linkDocumentBeanParent.getFileId());
	            System.out.println("linkDocumentChild.getFileId(): " + linkDocumentChild.getFileId());

	            stmt = con.prepareStatement("INSERT INTO DMS_FILES_LINKS (FILE_ID, CHILD_FILE_ID) " +
	                    "SELECT ?, ? " +
	                    "WHERE NOT EXISTS (SELECT 1 " +
	                    "                  FROM DMS_FILES_LINKS " +
	                    "                  WHERE FILE_ID = ? " +
	                    "                    AND CHILD_FILE_ID = ?)");

	            stmt.setInt(1, linkDocumentBeanParent.getFileId());
	            stmt.setInt(2, linkDocumentChild.getFileId());
	            stmt.setInt(3, linkDocumentBeanParent.getFileId());
	            stmt.setInt(4, linkDocumentChild.getFileId());

	            boolean inserted = stmt.executeUpdate() > 0; // Check if any row was inserted

	            if (inserted) {
	                anyInserts = true;
	            }
	        }

	        return anyInserts; // Return true if any inserts were performed
	    } catch (SQLException e) {
	        throw new DatabaseException("Error adding link documents", e);
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
//			safeClose();
//			releaseResources();
		}
	}
	
	public Set<LinkDocumentBean> fetchLinkDocument(String mainDocId) throws DatabaseException {
	    try {
	        LinkDocumentBean linkDocumentChild = fetchLinkByDocumentId(mainDocId);
	        System.out.println("linkDocumentChild from get: " + linkDocumentChild.getFileId());

	        String sql = "SELECT DMS_FILES_LINKS.FILE_ID, DOCUMENT_ID , CHILD_FILE_ID, DOCUMENT_CLASS, DOCUMENT_NAME, CLASS_AR_NAME " +
	                     "FROM DMS_FILES_LINKS " +
	                     "LEFT JOIN DMS_FILES ON DMS_FILES_LINKS.CHILD_FILE_ID = DMS_FILES.FILE_ID " +
	                     "LEFT JOIN CLASSIFICTIONS ON DMS_FILES.DOCUMENT_CLASS = CLASSIFICTIONS.SYMPOLIC_NAME " +
	                     "WHERE DMS_FILES_LINKS.FILE_ID = ?";

	        stmt = con.prepareStatement(sql);
	        stmt.setInt(1, linkDocumentChild.getFileId());
	        rs = stmt.executeQuery();

	        Set<LinkDocumentBean> beans = new LinkedHashSet<>();

	        while (rs.next()) {
	            LinkDocumentBean bean = new LinkDocumentBean();
	            bean.setFileId(rs.getInt("FILE_ID"));
	            bean.setChildFileId(rs.getInt("CHILD_FILE_ID"));
	            bean.setDocumentClass(rs.getString("DOCUMENT_CLASS"));
	            bean.setDocumentName(rs.getString("DOCUMENT_NAME"));
	            bean.setDocumentNameAr(rs.getString("CLASS_AR_NAME"));
	            bean.setDocumentId(rs.getString("DOCUMENT_ID"));
	            beans.add(bean);
	        }

	        System.out.println("beans: " + beans);
	        return beans;
	    } catch (SQLException e) {
	        throw new DatabaseException("Error fetching link documents", e);
	    } finally {
	        safeClose();
	        releaseResources();
	    }
	}
	
	public int deleteLinkDocument(int faildId, int childFaildId) throws DatabaseException {
		System.out.println("inside deleteLinkDocument: "+faildId +"::::"+childFaildId);
	    try {
	        stmt = con.prepareStatement(
	                "DELETE FROM DMS_FILES_LINKS " +
	                "WHERE FILE_ID = ? " +
	                "AND CHILD_FILE_ID = ?");
	        stmt.setInt(1, faildId);
	        stmt.setInt(2, childFaildId);
	        int rowsAffected = stmt.executeUpdate();
	        return rowsAffected;
	    } catch (SQLException e) {
	        throw new DatabaseException("Error deleting record from table DMS_FILES_LINK", e);
	    } finally {
	        safeClose();
	        releaseResources();
	    }
	}
	
	
}