package com.isikerhan.ezsocket.messaging.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.isikerhan.ezsocket.address.SimpleAddress;
import com.isikerhan.ezsocket.messaging.Message;
import com.isikerhan.ezsocket.messaging.MessageSender;
import com.isikerhan.ezsocket.messaging.callback.ConnectionStatusListener;
import com.isikerhan.ezsocket.messaging.callback.OnMessageReceivedListener;
import com.isikerhan.ezsocket.messaging.exception.AlreadyClosedException;
import com.isikerhan.ezsocket.messaging.exception.ConnectionException;
import com.isikerhan.ezsocket.messaging.exception.PeerNotFoundException;
import com.isikerhan.ezsocket.messaging.listener.AsyncMessageListener;
import com.isikerhan.ezsocket.messaging.listener.ConnectionListener;
import com.isikerhan.ezsocket.messaging.listener.MessageQueueListener;

/**
 * Implementation of {@linkplain MessagingServer} using TCP server sockets.
 * 
 * @author Isik Erhan
 *
 */
public class SocketMessagingServer implements MessagingServer, ConnectionStatusListener {

	private OnMessageReceivedListener msgCallback;
	private ConnectionStatusListener connCallback;
	private ConnectionListener connListener;
	private ExecutorService executor = Executors.newCachedThreadPool();
	private Map<SimpleAddress, Socket> clients;
	private Queue<Message> messageQueue;
	private MessageQueueListener queueListener;
	private List<AsyncMessageListener> msgListeners;
	private boolean closed = false;

	public SocketMessagingServer(OnMessageReceivedListener messageListener) {
		this.msgCallback = messageListener;
		this.clients = new HashMap<SimpleAddress, Socket>();
		this.messageQueue = new ConcurrentLinkedQueue<Message>();
		this.msgListeners = new ArrayList<AsyncMessageListener>();
	}

	@Override
	public void listen(int port) throws ConnectionException {
		if (isClosed())
			throw new AlreadyClosedException();
		if (connListener != null)
			throw new ConnectionException(
					new IllegalStateException("This server instance is already listening on a port."));

		queueListener = new MessageQueueListener(messageQueue, msgCallback);
		try {// initialize the connection status listener
			connListener = new ConnectionListener(new ServerSocket(port));
			connListener.setConnectionStatusListener(this);
			// submit the connection listener to the executor service
			executor.execute(connListener);
		} catch (IOException e) {
			throw new ConnectionException(e);
		}
	}

	@Override
	public void stopListening() throws ConnectionException {
		if (isClosed())
			throw new AlreadyClosedException();

		if (connListener == null) // if this instance is currently not listening
									// on any port
			throw new ConnectionException(new IllegalStateException("Server is currently not listening."));
		synchronized (connListener) {
			try {
				connListener.close();
			} catch (IOException e) {
				throw new ConnectionException(e);
			}
		}
		connListener = null;
		queueListener.stopListening();
		msgListeners.forEach(new Consumer<AsyncMessageListener>() {
			@Override
			public void accept(AsyncMessageListener t) {
				t.closeConnection();
			}
		});
		msgListeners.removeAll(msgListeners);
	}

	public void setConnectionStatusListener(ConnectionStatusListener callback) {
		this.connCallback = callback;
	}

	@Override
	public void sendMessage(byte[] message, SimpleAddress address) throws ConnectionException {
		if (isClosed())
			throw new AlreadyClosedException();
		if(address == null)
			throw new IllegalArgumentException("Address can't be null.");
		Socket peer = clients.get(address);// the target socket
		try {
			new MessageSender().sendMessage(message, peer);
		} catch (IllegalStateException e) {
			throw new PeerNotFoundException(address.toString());
		} catch (IOException e) {
			throw new ConnectionException(e);
		}
	}
	
	@Override
	public void sendMessage(byte[] message, String host, int port) throws ConnectionException {
		SimpleAddress address = new SimpleAddress(host, port);
		sendMessage(message, address);
	}

	@Override
	public void close() throws IOException {

		try {
			executor.shutdown();
		} catch (Exception e) {
			throw new IOException(e);
		}
		try {
			stopListening();
		} catch (ConnectionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof IOException)
				throw (IOException) cause;
			else
				throw new IOException(e);
		}
		closed = true;
	}

	@Override
	public void onConnectionEstablished(Socket socket) { // the callback method
														// which is invoked when
														// the connection is
														// established between
														// this instance and a
														// messaging client

		// initialize the message queue and its listener
		AsyncMessageListener messageListener = new AsyncMessageListener(socket, messageQueue);
		messageListener.setConnectionStatusListener(this);
		msgListeners.add(messageListener);
		//submit the message and message queue listener to the executor service
		executor.submit(messageListener);
		executor.submit(queueListener);
		// add the socket to the clients map
		clients.put(new SimpleAddress((InetSocketAddress) socket.getRemoteSocketAddress()), socket);
		if (connCallback != null)
			connCallback.onConnectionEstablished(socket);
	}

	@Override
	public void onPeerDisconnected(Socket socket) {
		// remove the disconnected socket from the clients map
		clients.remove(new SimpleAddress((InetSocketAddress) socket.getRemoteSocketAddress()));
		try {
			socket.close();
		} catch (IOException e) {
		}
		if (connCallback != null)
			connCallback.onPeerDisconnected(socket);
	}

	public boolean isClosed() {
		return closed;
	}
}
