package com.channelsoft.zhuaiwa.dal.exception;


public class DALException extends Exception {
	private static final long serialVersionUID = -5050845944917620860L;
	
	public DALException(Throwable e) {
		super(e.getMessage() == null ? "" : e.getMessage(), e);
		e.printStackTrace();
	}
	public DALException(String message) {
		super(message);
	}
	public DALException(String message, Throwable e) {
		super(message == null ? "" : message, e);
	}

}
