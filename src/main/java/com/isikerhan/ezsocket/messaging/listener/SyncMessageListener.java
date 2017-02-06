package com.isikerhan.ezsocket.messaging.listener;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Queue;

import com.isikerhan.ezsocket.address.SimpleAddress;
import com.isikerhan.ezsocket.messaging.Message;
import com.isikerhan.ezsocket.messaging.callback.OnMessageReceivedListener;
import com.isikerhan.ezsocket.messaging.exception.ConnectionException;
import com.isikerhan.ezsocket.util.IOUtils;

/**
 * This class listens to the byte array messages which are sent to the given {@linkplain Socket}.
 * 
 * @author Isik Erhan
 *
 */
public class SyncMessageListener {

	protected Socket socket;
	private Queue<Message> messageQueue;

	public SyncMessageListener(Socket socket, Queue<Message> messageQueue) {
		super();
		this.socket = socket;
		this.messageQueue = messageQueue;
	}

	public void listenForMessages(OnMessageReceivedListener callback) throws ConnectionException {

		boolean connected = true;
		while (connected) {
			try {
				// read the bytes
				byte[] bytes = IOUtils.readSocketStream(socket.getInputStream());
				// push the message to the message queue
				// and notify the observers
				if (messageQueue != null)
					synchronized (messageQueue) {
						messageQueue.add(new Message(new SimpleAddress((InetSocketAddress)socket.getRemoteSocketAddress()), bytes));
						messageQueue.notifyAll();
					}
			} catch (EOFException | SocketException e) {// EOF means the peer has closed the connection
				try {
					socket.close();
				} catch (IOException e1) {
					throw new ConnectionException("Socket could not be closed.", e1);
				}
				connected = false;
			} catch (IOException e) {
				throw new ConnectionException(e);
			}
		}
	}
	
	public synchronized void closeConnection(){
		try {
			socket.close();
		} catch (IOException e) {
		}
	}
}
