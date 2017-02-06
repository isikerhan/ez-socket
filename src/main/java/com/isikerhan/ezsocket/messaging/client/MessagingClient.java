package com.isikerhan.ezsocket.messaging.client;

import java.io.Closeable;

import com.isikerhan.ezsocket.address.SimpleAddress;
import com.isikerhan.ezsocket.messaging.callback.ConnectionStatusListener;
import com.isikerhan.ezsocket.messaging.exception.ConnectionClosedException;
import com.isikerhan.ezsocket.messaging.exception.ConnectionException;

/**
 * Interface definition for a messaging client in a messaging protocol with client-server architecture.
 * All protocol specific client implementations should implement this interface
 * 
 * @author Isik Erhan
 * 
 */
public interface MessagingClient extends Closeable {


	/**
	 * Connects to the messaging server which is hosted at given host and listens to given port.
	 * @param host
	 * @param port
	 * @throws ConnectionException
	 */
	void connect(String host, int port) throws ConnectionException;
	
	/**
	 * Connects to the messaging server which is at given address.
	 * @param address
	 * @throws ConnectionException
	 */
	void connect(SimpleAddress address) throws ConnectionException;
	
	/**
	 * Disconnects from the connected messaging server.
	 * @throws ConnectionClosedException if the connection between this client and the server has already been closed.
	 */
	void disconnect() throws ConnectionClosedException;
	
	/**
	 * Sends byte array message to the peer
	 * @param message	message to send
	 * @throws ConnectionException
	 */
	void sendMessage(byte[] message) throws ConnectionException;
	
	/**
	 * Sets connection status listener
	 * @param callback
	 */
	void setConnectionStatusListener(ConnectionStatusListener callback);
}
