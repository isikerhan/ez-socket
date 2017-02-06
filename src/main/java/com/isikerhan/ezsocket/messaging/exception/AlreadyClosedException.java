package com.isikerhan.ezsocket.messaging.exception;

public class AlreadyClosedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AlreadyClosedException() {
		super("The connection has already been closed.");
	}

	public AlreadyClosedException(String message, Throwable cause) {
		super(message, cause);
	}

	public AlreadyClosedException(String message) {
		super(message);	}

	public AlreadyClosedException(Throwable cause) {
		super(cause);
	}
}
