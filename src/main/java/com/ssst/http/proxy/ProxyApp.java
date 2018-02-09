package com.ssst.http.proxy;

import java.net.ServerSocket;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyApp {

	private static Logger logger = LoggerFactory
			.getLogger(ProxyApp.class);
	private static ServerSocket serversocket;

	public static void main(String[] args) throws Exception {
		int port = 8082;
		serversocket = new ServerSocket(port);
		logger.info("start server. port:" + port);
		while (true) {
			Socket insocket = serversocket.accept();
			RequestHanlder requestHanlder = new RequestHanlder(insocket);
			final Thread t = new Thread(requestHanlder);
			t.setDaemon(false);
			t.start();
		}

	}
}