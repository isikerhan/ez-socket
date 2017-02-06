package com.isikerhan.ezsocket.messaging;

import com.isikerhan.ezsocket.address.SimpleAddress;

/**
 * This class represents a single message that is sent from a messaging peer to another.
 * 
 * @author Isik Erhan
 *
 */
public class Message {

	private SimpleAddress sender;
	private byte[] bytes;
	public Message(SimpleAddress sender, byte[] bytes) {
		super();
		this.sender = sender;
		this.bytes = bytes;
	}
	public SimpleAddress getSender() {
		return sender;
	}
	public byte[] getBytes() {
		return bytes;
	}
}
