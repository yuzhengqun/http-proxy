package com.ssst.http.proxy;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyProxyHandler implements HttpRequestHandler {

	private static Logger logger = LoggerFactory
			.getLogger(MyProxyHandler.class);

	public void handle(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		String uri = request.getRequestLine().getUri();
		logger.info("uri:" + uri);
		String host = this.getHost(request);
		String domainName = this.getDomainName(host);
		String serverPort = this.getServerPort(host);
		DefaultBHttpClientConnection outconn = null;
		HttpResponse targetResponse = null;

		try {
			final Socket outsocket = new Socket(domainName,
					Integer.parseInt(serverPort));

			outconn = new DefaultBHttpClientConnection(1024 * 8);
			outconn.bind(outsocket);

			final HttpRequestExecutor httpexecutor = new HttpRequestExecutor();
			targetResponse = httpexecutor.execute(request, outconn, context);
		} catch (Exception e) {
			if (outconn != null) {
				try {
					outconn.close();
				} catch (IOException e1) {

				}
			}
			logger.info(e.getMessage(), e);
		}

		response.setStatusLine(targetResponse.getStatusLine());
		response.setHeaders(targetResponse.getAllHeaders());
		response.setEntity(targetResponse.getEntity());
		response.setHeader("Connection", "close");
	}

	private String getHost(HttpRequest request) {
		String result = null;
		Header[] headers = request.getAllHeaders();
		for (Header header : headers) {
			String name = header.getName().toLowerCase();
			if ("host".equals(name)) {
				result = header.getValue();
			}
		}
		return result;
	}

	private String getDomainName(String host) {
		String[] array = host.split(":");
		return array[0];
	}

	private String getServerPort(String host) {
		String[] array = host.split(":");
		String result = "80";
		if (array.length == 2) {
			result = array[1];
		}
		return result;
	}
}
