package com.jw.home.exception;

public class NotFoundDeviceException extends CustomBusinessException {
	public static NotFoundDeviceException INSTANCE = new NotFoundDeviceException();

	NotFoundDeviceException() {
		super();
		this.errorCode = 401;
		this.errorMessage = "not found device";
	}
}
