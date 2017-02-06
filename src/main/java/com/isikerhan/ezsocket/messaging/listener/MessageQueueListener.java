package com.isikerhan.ezsocket.messaging.listener;

import java.util.Queue;

import com.isikerhan.ezsocket.messaging.Message;
import com.isikerhan.ezsocket.messaging.callback.OnMessageReceiveListener;

/**
 * The {@linkplain Runnable} that listens to a message queue
 * 
 * @author Isik Erhan
 *
 */
public class MessageQueueListener implements Runnable {

	private Queue<Message> messageQueue;// ilgili mesaj kuyrugu
	private OnMessageReceiveListener listener;// callback
	private volatile boolean listening = true;

	public MessageQueueListener(Queue<Message> messageQueue, OnMessageReceiveListener listener) {
		this.messageQueue = messageQueue;
		this.listener = listener;
	}

	@Override
	public void run() {
		while (listening) {
			synchronized (messageQueue) {
				Message message = null;
				while (listening && (message = messageQueue.poll()) == null) {
					try {
						messageQueue.wait(); //wait until a message is pushed to the queue
					} catch (InterruptedException e) {
					}
				}
				if (message != null && listener != null)
					// invoke the callback if there is an incoming message
					listener.onMessageReceive(message);
			}
		}
	}

	public synchronized void stopListening() {
		this.listening = false;
		synchronized (messageQueue) {
			messageQueue.notifyAll();
		}
	}

}
