package org.yetiz.service.socketqueue.run;

import org.yetiz.service.socketqueue.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by yeti on 14/10/31.
 */
public class RunServer {
	Server server = null;

	public static void main(String[] args) {
		try {
			//Server
			if (args.length != 2) {
				System.out.println("java -jar SocketQueue.jar <bind address> <port>");
				return;
			}
			RunServer runServer = new RunServer();
			runServer.start(new String(args[0]), new Integer(new String(args[1])));
			cmdTrap();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void cmdTrap() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String cmd;
		while (true) {
			try {
				cmd = br.readLine();
				if (cmd.equals("") || cmd.equals("help")) {
					System.out.println("Command: stop.");
					continue;
				}
				if (cmd.equals("stop")) {
					Runtime.getRuntime().exit(0);
					continue;
				}
			} catch (IOException ex) {
			}

		}
	}

	private void start(String bindIP, int port) throws IOException {

		if (bindIP.equals("*"))
			server = new Server(port);
		else
			server = new Server(bindIP, port);
		server.start();
		Runtime.getRuntime().addShutdownHook(new Shutdown());
	}

	private class Shutdown extends Thread {
		@Override
		public void run() {
			server.close();
		}
	}

}
