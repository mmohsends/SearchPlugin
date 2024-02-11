package com.dataserve.se.db.command;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dataserve.se.bean.PermissionBean;
import com.dataserve.se.manager.PermissionManager;
import com.dataserve.se.permissions.ActionType;
import com.dataserve.se.permissions.Module;
import com.dataserve.se.util.ConfigManager;
import com.ibm.ecm.extension.PluginServiceCallbacks;

public abstract class CommandBase implements Command {
	protected HttpServletRequest request;
	protected PluginServiceCallbacks callBacks;
	protected HttpServletResponse response;
	protected String currentUserId;
	
	
	public CommandBase(HttpServletRequest request) {
		this.request = request;
		this.callBacks = (PluginServiceCallbacks)request.getAttribute("callBacks");
		this.currentUserId = (String)request.getAttribute("curretUserId");
	}
	
	
	public CommandBase(PluginServiceCallbacks callbacks, HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.callBacks = callbacks;
		this.response= response;
		this.currentUserId = (String)request.getAttribute("curretUserId");
	}
	protected abstract Module getModule(); 
	protected abstract ActionType getActionType();
	
	public boolean checkAccess() throws Exception {
		if (ConfigManager.isAccessCheckDisabled()) {
			return true;
		}
		
		if (currentUserId.equalsIgnoreCase(ConfigManager.getSuperUserName())) {
			return true;
		}
		
		if (getModule() == null || getActionType() == null) {
			return true;
		}
		
		try {
			PermissionManager permissionManager = new PermissionManager();
			Set<PermissionBean> permisssions = permissionManager.getUserPermissions(currentUserId);
			for (PermissionBean pm : permisssions) {
				if (pm.getModuleId() == getModule().getId() && pm.getTypeId()== getActionType().getId()) {
					return true;
				}
			}

			return false;
		} catch (Exception e) {
			throw new Exception("Error checking access for command '" + request.getParameter("method") + "'", e);
		}
	}
	
	public String getAccessDescription() {
		StringBuilder sb = new StringBuilder(200);
		sb.append("Access type " + getActionType().getId() + "- " + getActionType().name() + ", ");
		sb.append("Module: " + getModule().getId() + "- " + getModule().name());
		return sb.toString();
	}
	
	
	@Override
	public abstract String execute() throws Exception;
	
	
}
