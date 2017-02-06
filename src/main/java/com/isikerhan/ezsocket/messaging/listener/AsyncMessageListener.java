package com.isikerhan.ezsocket.messaging.listener;

import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.isikerhan.ezsocket.messaging.Message;
import com.isikerhan.ezsocket.messaging.callback.ConnectionStatusListener;
import com.isikerhan.ezsocket.messaging.callback.OnMessageReceiveListener;
import com.isikerhan.ezsocket.messaging.exception.ConnectionException;

/**
 * Asynchronous variant of {@linkplain SyncMessageListener}
 */
public class AsyncMessageListener extends SyncMessageListener implements Runnable {

	private OnMessageReceiveListener messageListener;
	private ConnectionStatusListener connListener;

	public AsyncMessageListener(Socket socket, Queue<Message> messageQueue) {
		super(socket, messageQueue);
	}

	public void setConnectionStatusListener(ConnectionStatusListener listener) {
		this.connListener = listener;
	}

	@Override
	public void run() {
		try {
			super.listenForMessages(messageListener);
		} catch (ConnectionException e) {
		} // stopping listening of the synchronous listener means that the connection is closed
		if (connListener != null){
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.submit(new Runnable(){
				@Override
				public void run() {
					connListener.onDisconnect(socket);
				}
			});
			executor.shutdown();
		}
	}

}
