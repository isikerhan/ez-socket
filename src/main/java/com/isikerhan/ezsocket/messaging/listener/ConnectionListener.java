package com.isikerhan.ezsocket.messaging.listener;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.isikerhan.ezsocket.messaging.callback.ConnectionStatusListener;

/**
 * This class listens for the peers that connects to the given
 * {@linkplain ServerSocket}.
 * 
 * @author Isik Erhan
 * 
 */
public class ConnectionListener implements Runnable, Closeable {

	private volatile boolean listening = true;
	private ServerSocket serverSocket;
	private ConnectionStatusListener connCallback;

	public ConnectionListener(ServerSocket serverSocket) {
		super();
		this.serverSocket = serverSocket;
	}

	public void setConnectionStatusListener(ConnectionStatusListener listener) {
		this.connCallback = listener;
	}

	@Override
	public void run() {
		while (listening) {
			try {// wait for a client to connect
				Socket socket = serverSocket.accept();
				if (connCallback != null)// call the connection status callback
					connCallback.onConnectionEstablished(socket);
			} catch (IOException e) {
			}
		}
	}

	public boolean isListening() {
		return listening;
	}

	public void setListening(boolean listening) {
		this.listening = listening;
	}

	@Override
	public void close() throws IOException {
		listening = false; // setting listener to false thus the loop breaks and
							// the thread finishes its execution
		serverSocket.close();// close the server socket
	}
}
