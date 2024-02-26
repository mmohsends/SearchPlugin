package com.dataserve.se.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;



public class ConnectionManager {
	private Connection con;
	// public PreparedStatement stmt;
	// public ResultSet rs;

	private static final int TOMCAT = 1;
	private static final int WEB_LOGIC = 2;
	private static final int WEB_SPHERE = 3;
	public static final String DATASOURCE_NAME = "ARCHIVE";

	public Connection getCon() {
		return con;
	}

	private Connection openConnection(String dataSource)
			throws DatabaseException {
		try {
			Context context;
			InitialContext initContext;
			javax.sql.DataSource source = null;
			int appServer = 3;
			switch (appServer) {
			case TOMCAT:
				context = new InitialContext();
				javax.naming.Context env = (javax.naming.Context) context
						.lookup("java:comp/env");
				source = (javax.sql.DataSource) env.lookup(dataSource);
				break;
			case WEB_LOGIC:
				context = new InitialContext();
				source = (javax.sql.DataSource) context.lookup(dataSource);
				break;
			case WEB_SPHERE:
				Hashtable<String, String> hashtable = new Hashtable<String, String>();
				hashtable.put("java.naming.factory.initial",
						"com.ibm.websphere.naming.WsnInitialContextFactory");
				initContext = new InitialContext(hashtable);
				source = (javax.sql.DataSource) initContext.lookup(dataSource);
				break;
			}
			return source.getConnection();

		} catch (NamingException ex) {
			throw new DatabaseException("Error looking up datasource '"
					+ dataSource + "'");
		} catch (SQLException ex) {
			throw new DatabaseException("Error get connection '" + dataSource
					+ "'");
		}

	}

	public void initConn() throws DatabaseException {
		try {
			if ((this.con == null) || (this.con.isClosed())) {
				this.con = openConnection(DATASOURCE_NAME);
				this.con.setAutoCommit(false);
			}
		} catch (SQLException e) {
			throw new DatabaseException("Error getting connection to database",
					e);
		}
	}

	public void commit() throws DatabaseException {
		try {
			if (this.con != null && !this.con.isClosed()) {
				this.con.commit();
			}
		} catch (SQLException e) {
			throw new DatabaseException("Error commit DB connection ",
					e);
		}
	}

	public void releaseConnection() throws DatabaseException {
		try {
			if (this.con != null && !this.con.isClosed()) {
				this.con.close();
			}
		} catch (SQLException e) {
			throw new DatabaseException("Error release DB Connection",
					e);
		}
	}

	public void rollBack() throws DatabaseException {
		try {
			if (this.con != null && !this.con.isClosed()) {
				this.con.rollback();
			}
		} catch (SQLException e) {
			throw new DatabaseException("Error rollback DB connection",
					e);
		}
	}

	// protected final void safeClose() {
	// try {
	// if (this.rs != null) {
	// this.rs.close();
	// }
	//
	// if (stmt != null) {
	// stmt.close();
	// }
	// } catch (SQLException unexpected) {
	// unexpected.printStackTrace();
	// }
	// }
}
