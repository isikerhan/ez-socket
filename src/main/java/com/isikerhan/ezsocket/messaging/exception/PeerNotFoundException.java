package com.isikerhan.ezsocket.messaging.exception;

public class PeerNotFoundException extends ConnectionException{

	private static final long serialVersionUID = 1L;

	public PeerNotFoundException(String clientAddress) {
		super(String.format("Client with address %s is not found.", clientAddress));
	}
	
	public PeerNotFoundException(Throwable cause){
		super(cause);
	}
}
