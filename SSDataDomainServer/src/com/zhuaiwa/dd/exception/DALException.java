package com.zhuaiwa.dd.exception;

import java.beans.ConstructorProperties;


public class DALException extends RuntimeException {
	private static final long serialVersionUID = -5050845944917620860L;
	
	@ConstructorProperties({"e"})
	public DALException(Throwable e) {
		super(e.getMessage() == null ? "" : e.getMessage(), e);
		e.printStackTrace();
	}
	@ConstructorProperties({"message"})
	public DALException(String message) {
		super(message);
	}
	@ConstructorProperties({"message", "e"})
	public DALException(String message, Throwable e) {
		super(message == null ? "" : message, e);
		e.printStackTrace();
	}

}
