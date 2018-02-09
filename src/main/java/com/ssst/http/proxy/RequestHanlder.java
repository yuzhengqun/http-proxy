package com.ssst.http.proxy;

import java.io.IOException;
import java.net.Socket;

import org.apache.http.HttpException;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;

public class RequestHanlder implements Runnable {
	private Socket insocket = null;

	public RequestHanlder(Socket insocket) {
		this.insocket = insocket;
	}

	@Override
	public void run() {
		final HttpProcessor inhttpproc = new ImmutableHttpProcessor(
				new HttpRequestInterceptor[] { new RequestContent(true),
						new RequestTargetHost(), new RequestConnControl(),
						new RequestUserAgent("Test/1.1"),
						new RequestExpectContinue(true) });

		final UriHttpRequestHandlerMapper reqistry = new UriHttpRequestHandlerMapper();
		reqistry.register("*", new MyProxyHandler());

		final int bufsize = 8 * 1024;
		final DefaultBHttpServerConnection inconn = new DefaultBHttpServerConnection(
				bufsize);
		try {
			inconn.bind(insocket);
			final HttpContext context = new BasicHttpContext(null);
			HttpService httpService = new HttpService(inhttpproc, reqistry);
			httpService.handleRequest(inconn, context);
			inconn.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		}

	}

}
