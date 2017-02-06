package com.isikerhan.ezsocket.messaging.exception;

public class ConnectionClosedException extends ConnectionException{

	private static final long serialVersionUID = 1L;
	
	public ConnectionClosedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConnectionClosedException(String message) {
		super(message);
	}

	public ConnectionClosedException(Throwable cause) {
		super(cause);
	}

}
