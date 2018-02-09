package com.ssst.manager.node.util;

import java.io.File;
import java.io.IOException;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.ConnectionInfo;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;

/**
 * scp helper
 * 
 * @author yuzhengqun
 *
 */
@SuppressWarnings("unused")
public class ScpclientHelper {

	/**
	 * upload file
	 * 
	 * @param ip
	 * @param port
	 * @param userName
	 * @param password
	 * @param sourceFilePath
	 * @param targetDir
	 * @throws Exception
	 */
	public static void putFile(String ip, int port, String userName,
			String password, String sourceFilePath, String targetDir,
			String mode) throws Exception {
		Connection connect = new Connection(ip, port);
		ConnectionInfo connectInfo = connect.connect();
		boolean authResult = connect.authenticateWithPassword(userName,
				password);
		if (authResult) {
			SCPClient scpClient = new SCPClient(connect);
			scpClient.put(sourceFilePath, targetDir, mode);
			connect.close();
		} else {
			throw new Exception("authenticate failed.userName:" + userName
					+ ",password:" + password);
		}
	}

	public static void exec(String ip, int port, String userName,
			String password, String shell) throws Exception {
		Connection connect = new Connection(ip, port);
		connect.connect();
		boolean authResult = connect.authenticateWithPassword(userName,
				password);
		if (authResult) {
			Session session = connect.openSession();
			session.execCommand(shell);
			session.close();
			connect.close();
		} else {
			throw new Exception("authenticate failed.userName:" + userName
					+ ",password:" + password);
		}
	}
}
