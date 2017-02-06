package com.isikerhan.ezsocket.address;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * This class represents an Internet Protocol address simply as host and port.
 * 
 * @author Isik Erhan
 * 
 */
public class SimpleAddress {

	private String host;
	private int port;

	public SimpleAddress(String host, int port) {
		if ("localhost".equalsIgnoreCase(host))
			host = "127.0.0.1";
		if (host.startsWith("/"))
			host = host.substring(1);
		this.host = host;
		this.port = port;
	}

	public SimpleAddress(InetSocketAddress addr) {
		this(addr.getAddress().toString(), addr.getPort());
	}

	public SimpleAddress(InetAddress addr) {
		this(addr.toString(), 80);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {// host:port
		return String.format("%s:%d", host, port);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SimpleAddress))
			return false;
		SimpleAddress addr = (SimpleAddress) obj;
		return this.host != null && this.host.equals(addr.host) && this.port == addr.port;
	}

	@Override
	public int hashCode() {
		return ((this.host != null ? this.host.hashCode() : 0) + port) % Integer.MAX_VALUE;
	}
}
