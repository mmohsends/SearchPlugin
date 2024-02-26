package com.dataserve.se.db;

	

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public abstract class AbstractDAO {
	protected Connection con;
	protected PreparedStatement stmt;
	protected ResultSet rs;

    public static final int TOMCAT = 1;
    public static final int WEB_LOGIC = 2;
    public static final int WEB_SPHERE = 3;
	public static final String DATASOURCE_NAME = "ARCHIVE";

	public AbstractDAO() throws DatabaseException {
		initConn();
	}

    private Connection getConnection(String dataSource) throws SQLException, NamingException, DatabaseException {
        Context context;
        InitialContext initContext;
        javax.sql.DataSource source = null;
        int appServer = 3;
        switch(appServer) {
            case TOMCAT:
                context = new InitialContext();
                javax.naming.Context env = (javax.naming.Context)context.lookup("java:comp/env");
                source = (javax.sql.DataSource)env.lookup(dataSource);
                break;
            case WEB_LOGIC:
                context = new InitialContext();
                source = (javax.sql.DataSource)context.lookup(dataSource);
                break;
            case WEB_SPHERE:
                Hashtable<String, String> hashtable = new Hashtable<String, String>();
                hashtable.put("java.naming.factory.initial", "com.ibm.websphere.naming.WsnInitialContextFactory");
                initContext = new InitialContext(hashtable);
                source = (javax.sql.DataSource)initContext.lookup(dataSource);
                break;
        }
        if (source != null) {
        	return source.getConnection();
        } else {
        	throw new DatabaseException("Error looking up datasource '" + dataSource + "'");
        }
    }

	protected final void initConn() throws DatabaseException {
		try {
			if ((this.con == null) || (this.con.isClosed())) {
				this.con = getConnection(DATASOURCE_NAME);
			}
		} catch (SQLException e) {
			throw new DatabaseException("Error getting connection to database", e);
		} catch (NamingException e) {
			throw new DatabaseException("Error getting connection to database", e);
		}
	}

	protected final void safeClose() {
		try {
			if (this.rs != null) {
				this.rs.close();
			}

			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException unexpected) {
			unexpected.printStackTrace();
		}
	}

	protected final void releaseResources() {
		if (this.con != null) {
			try {
				this.con.close();
			} catch (SQLException unexpected) {
				unexpected.printStackTrace();
			}
		}
	}
}
