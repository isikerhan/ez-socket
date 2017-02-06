package com.isikerhan.ezsocket.messaging.exception;

public class ConnectionException extends Exception {

	private static final long serialVersionUID = 1L;

	public ConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConnectionException(String message) {
		super(message);
	}

	public ConnectionException(Throwable cause) {
		super(cause);
	}

	
}
