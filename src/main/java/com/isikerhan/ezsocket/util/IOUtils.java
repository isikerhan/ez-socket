package com.isikerhan.ezsocket.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utilities for I/O.
 * 
 * @author Isik Erhan
 *
 */
public class IOUtils {

	private IOUtils(){ //prevent instantiation
	}
	
	public static byte[] readSocketStream(InputStream is) throws IOException {
		
		DataInputStream in = new DataInputStream(is);
		byte[] bytes = new byte[in.readInt()];
		in.read(bytes);
		return bytes;
	}
	
	public static void writeSocketStream(OutputStream os, byte[] bytes) throws IOException {
		
		DataOutputStream out = new DataOutputStream(os);
		out.writeInt(bytes.length);
		out.write(bytes);
		out.flush();
	}
}
