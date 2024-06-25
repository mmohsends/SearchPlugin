package com.dataserve.se.db.command;


import javax.servlet.http.HttpServletRequest;
import com.dataserve.se.permissions.ActionType;
import com.dataserve.se.permissions.Module;
import com.dataserve.se.util.EncryptionUtil;
import com.dataserve.se.db.command.CommandBase;

public class EncryptionUtilCommand extends CommandBase {
	

	public EncryptionUtilCommand(HttpServletRequest request) {
		super(request);
	}
	
	public String execute() throws Exception {

		String keywordSearch = request.getParameter("keywordSearch");
		        
		try {
			return EncryptionUtil.encrypt(keywordSearch).toString();
		} 
		catch (Exception e) {
			throw new Exception("Failed to encrypt keywordSearch ", e);
		}
	
	}
		

	@Override
	protected Module getModule() {
		return null;
	}

	@Override
	protected ActionType getActionType() {
		return null;
	}

}
