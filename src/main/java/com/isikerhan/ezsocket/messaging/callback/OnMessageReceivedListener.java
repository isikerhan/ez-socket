package com.isikerhan.ezsocket.messaging.callback;

import com.isikerhan.ezsocket.messaging.Message;

/**
 * Interface definition for a callback to be invoked when a messaging peer received a message.
 *
 * @author Isik Erhan
 * 
 */
public interface OnMessageReceivedListener {
	public void onMessageReceived(Message message);
}
