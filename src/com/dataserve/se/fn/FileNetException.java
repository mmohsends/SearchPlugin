package com.dataserve.se.fn;

import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.exception.ExceptionCode;

public class FileNetException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2037419485534667923L;
	private String fnErrorCode = "";

	public FileNetException(String message, Throwable t) {
		super(message, t);
		if (t instanceof EngineRuntimeException) {

			EngineRuntimeException sqe = (EngineRuntimeException) t;
			ExceptionCode e = ((EngineRuntimeException) sqe).getExceptionCode();

			// set the FN Error code here
			this.fnErrorCode = e.getKey();


		}
	}

	public FileNetException(String message) {
		super(message);
	}

	

	public String getFnErrorCode() {
		return fnErrorCode;
	}
	
	
}
