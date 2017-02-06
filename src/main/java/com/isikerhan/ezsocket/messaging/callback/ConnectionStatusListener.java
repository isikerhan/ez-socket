package com.isikerhan.ezsocket.messaging.callback;

import java.net.Socket;

/**
 * Interface definition for callbacks to be invoked when status of the connection of a pair of messaging peer is changed.
 *
 * @author Isik Erhan
 * 
 */
public interface ConnectionStatusListener {
	void onConnectionEstablished(Socket socket);
	void onPeerDisconnected(Socket socket);
}
