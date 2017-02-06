package com.isikerhan.ezsocket.messaging.server;

import java.io.Closeable;

import com.isikerhan.ezsocket.address.SimpleAddress;
import com.isikerhan.ezsocket.messaging.callback.ConnectionStatusListener;
import com.isikerhan.ezsocket.messaging.exception.ConnectionException;

/**
 * Interface definition for a messaging server in a messaging protocol with client-server architecture.
 * All protocol specific server implementations should implement this interface
 * 
 * @author Isik Erhan
 * 
 */
public interface MessagingServer extends Closeable{

	/**
	 * Starts listening on the given port.
	 * @param port
	 * @throws ConnectionException
	 */
	void listen(int port) throws ConnectionException;
	
	/**
	 * Stops listening on the port that is currently being listened.
	 * @param port
	 * @throws ConnectionException
	 */
	void stopListening() throws ConnectionException;
	
	/**
	 * Sends the given message to the peer which is at given <b>host:port</b>
	 * @param message	byte array message
	 * @param host 
	 * @param port 
	 * @throws ConnectionException
	 */
	void sendMessage(byte[] message, String host, int port) throws ConnectionException;
	
	/**
	 * Sends the given message to the peer which is at given {@linkplain SimpleAddress}
	 * @param message	byte array message
	 * @param address
	 * @throws ConnectionException
	 */
	void sendMessage(byte[] message, SimpleAddress address) throws ConnectionException;
	
	/**
	 * Sets connection status listener
	 * @param callback
	 */
	void setConnectionStatusListener(ConnectionStatusListener callback);
}
