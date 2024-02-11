package com.dataserve.se.db.command;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.dataserve.se.bean.MostUsingModel;
import com.dataserve.se.db.DatabaseException;
import com.dataserve.se.permissions.ActionType;
import com.dataserve.se.permissions.Module;
import com.ibm.json.java.JSONArray;

public class GetMostUsing extends CommandBase {

	public GetMostUsing(HttpServletRequest request) {
		super(request);
	}

	@Override
	public String execute() throws Exception {
		try{
			Set<MostUsingModel> groups = MostUsingModel.getAllGroups();
			JSONArray arr = new JSONArray();
			for (MostUsingModel gm : groups) {
				arr.add(gm.getAsJson());
			}
			return arr.toString();
		} catch (Exception e) {
			throw new DatabaseException("Error getting all groups", e);
		}
	}

	@Override
	protected Module getModule() {
		return Module.GROUP;
	}

	@Override
	protected ActionType getActionType() {
		return ActionType.VIEW;
	}

}
