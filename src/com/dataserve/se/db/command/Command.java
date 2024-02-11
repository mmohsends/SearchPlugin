package com.dataserve.se.db.command;


public interface Command {
	public String execute() throws Exception;
	public boolean checkAccess() throws Exception;
	public String getAccessDescription();
}
