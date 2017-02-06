package com.isikerhan.ezsocket.messaging;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.isikerhan.ezsocket.util.IOUtils;

/**
 * Helper class that sends a byte array message to the given {@linkplain Socket}.
 * 
 * @author Isik Erhan
 * 
 */
public final class MessageSender {

	/**
	 * Sends message to the given {@linkplain Socket}
	 * @param message
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public void sendMessage(byte[] message, Socket socket) throws IllegalStateException, IOException {
		if (socket == null)
			throw new IllegalStateException();
		synchronized (socket) {
			OutputStream os = socket.getOutputStream();
			IOUtils.writeSocketStream(os, message);
			os.flush();
		}
	}
}
