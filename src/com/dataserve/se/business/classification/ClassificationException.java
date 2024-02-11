package com.dataserve.se.business.classification;



import com.dataserve.eds.fn.FileNetException;

public class ClassificationException extends Exception {

	private static final long serialVersionUID = 20371948553466793L;
	private String fnErrorCode = "";

	public ClassificationException(String message, Throwable t) {
		super(message, t);
		if (t instanceof FileNetException) {

			FileNetException sqe = (FileNetException) t;
			// set the FN Error code here
			if(sqe.getFnErrorCode()!=null &&  !sqe.getFnErrorCode().isEmpty()){
				this.fnErrorCode = sqe.getFnErrorCode();
			}


		}
	}

	public ClassificationException(String message) {
		super(message);
	}
	
	
	

	public String getFnErrorCode() {
		return fnErrorCode;
	}
}
