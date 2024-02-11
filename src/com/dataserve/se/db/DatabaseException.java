package com.dataserve.se.db;

public class DatabaseException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2037419485534667923L;

	public DatabaseException(String message, Throwable t) {
		super(message, t);
	}

	public DatabaseException(String message) {
		super(message);
	}
}
