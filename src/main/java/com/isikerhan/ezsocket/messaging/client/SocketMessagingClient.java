package com.isikerhan.ezsocket.messaging.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.isikerhan.ezsocket.address.SimpleAddress;
import com.isikerhan.ezsocket.messaging.Message;
import com.isikerhan.ezsocket.messaging.MessageSender;
import com.isikerhan.ezsocket.messaging.callback.ConnectionStatusListener;
import com.isikerhan.ezsocket.messaging.callback.OnMessageReceivedListener;
import com.isikerhan.ezsocket.messaging.exception.AlreadyClosedException;
import com.isikerhan.ezsocket.messaging.exception.ConnectionClosedException;
import com.isikerhan.ezsocket.messaging.exception.ConnectionException;
import com.isikerhan.ezsocket.messaging.exception.PeerNotFoundException;
import com.isikerhan.ezsocket.messaging.listener.AsyncMessageListener;
import com.isikerhan.ezsocket.messaging.listener.MessageQueueListener;

/**
 * Implementation of {@linkplain MessagingClient} using TCP sockets.
 * 
 * @author Isik Erhan
 *
 */
public class SocketMessagingClient implements MessagingClient, ConnectionStatusListener {

	private ExecutorService executor = Executors.newCachedThreadPool();
	private OnMessageReceivedListener msgCallback;
	private ConnectionStatusListener connCallback;
	private Socket socket;
	private Queue<Message> messageQueue;
	private AsyncMessageListener messageListener;
	private MessageQueueListener queueListener; // thread that listens to the message queue
	private boolean closed = false;
	
	public SocketMessagingClient(OnMessageReceivedListener messageListener) {
		this.msgCallback = messageListener;
		this.messageQueue = new ConcurrentLinkedQueue<Message>();
	}

	@Override
	public void connect(String host, int port) throws ConnectionException {
		if(isClosed())
			throw new AlreadyClosedException();
		try {
			socket = new Socket(host, port); // connect to server
			
			// initialize the message listener
			messageListener = new AsyncMessageListener(socket, messageQueue);
			messageListener.setConnectionStatusListener(this);

			// initialize the message queue listener
			queueListener = new MessageQueueListener(messageQueue, msgCallback);

			// submit listeners to the executor service
			executor.execute(messageListener);
			executor.execute(queueListener);
		} catch (IOException e) {
			throw new ConnectionException(e);
		}
	}
	
	@Override
	public void connect(SimpleAddress address) throws ConnectionException {
		if(address == null)
			throw new IllegalArgumentException("Address can't be null");
		connect(address.getHost(), address.getPort());
	}

	@Override
	public void disconnect() throws ConnectionClosedException {
		if(isClosed())
			throw new AlreadyClosedException();
		if (socket == null)
			throw new ConnectionClosedException("Connection is already closed.");
		synchronized (socket) { // close the socket
			try {
				socket.close();
			} catch (IOException e) {
				throw new ConnectionClosedException("Socket is currently not connected.", e);
			}
		}
		
		// no need to listen the message queue
		// since the connection is terminated
		// stop listening the message queue
		queueListener.stopListening();
	}

	@Override
	public void sendMessage(byte[] message) throws ConnectionException {
		if(isClosed())
			throw new AlreadyClosedException();
		try {
			new MessageSender().sendMessage(message, socket);
		} catch (IllegalStateException e) {
			throw new PeerNotFoundException(e);
		} catch (IOException e) {
			throw new ConnectionException("Message could not be sent.", e);
		}
	}

	private boolean isClosed() {
		return closed;
	}

	@Override
	public void close() throws IOException {
		try {
			executor.shutdown();
		} catch(Exception e){
			throw new IOException(e);
		}
		try {
			disconnect();
		} catch (ConnectionClosedException e) {
			throw new IOException(e);
		}
		closed = true;
	}

	@Override
	public void onConnectionEstablished(Socket socket) {
		if(connCallback != null)
			connCallback.onConnectionEstablished(socket);
	}

	@Override
	public void onPeerDisconnected(Socket socket) {
		this.socket = null;
		if(connCallback != null)
			connCallback.onPeerDisconnected(socket);
	}

	@Override
	public void setConnectionStatusListener(ConnectionStatusListener callback) {
		this.connCallback = callback;
	}
}
