package com.dataserve.se.db.command;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.dataserve.se.bean.DmsFiles;
import com.dataserve.se.db.command.CommandBase;
import com.dataserve.se.manager.SearchManager;
import com.dataserve.se.permissions.ActionType;
import com.dataserve.se.permissions.Module;
import com.ibm.json.java.JSONArray;

public class AdvancedFileSearch extends CommandBase{

	public AdvancedFileSearch(HttpServletRequest request) {
		super(request);
	}
	@Override
	public String execute() throws Exception {
		try {
			SearchManager manager  = new SearchManager();
			Set<DmsFiles> files = manager.searchFiles(currentUserId);
			
			JSONArray arr = new JSONArray();
			for (DmsFiles file : files) {
				arr.add(file.getAsJson(callBacks.getLocale()));
			}
			return arr.toString();
		} catch (Exception e) {
			throw new Exception("Error returning departments for user with username '" + currentUserId + "'", e);
		}
	}
	
	@Override
	protected Module getModule() {
		return Module.ELECTRONIC_ARCHIVE;
	}

	@Override
	protected ActionType getActionType() {
		return ActionType.SEARCH;
	}
}
