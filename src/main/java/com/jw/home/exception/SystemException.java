package com.jw.home.exception;

public class SystemException extends CustomBusinessException {
	public static SystemException INSTANCE = new SystemException();

	SystemException() {
		super();
		this.errorCode = 101;
		this.errorMessage = "System exception";
	}
}
