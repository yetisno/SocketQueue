package org.yetiz.service.socketqueue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

/**
 * @author yeti
 * @version 1.0
 *          Created by yeti on 14/11/1.
 * @see java.lang.Thread
 */
public class Server extends Thread {

	private int port;
	private ServerSocket serverSocket;
	private ByteArrayQueue byteArrayQueue;
	private ConcurrentLinkedQueue<WorkerSession> workerSessions = new ConcurrentLinkedQueue<WorkerSession>();
	private boolean shutdown = false;
	private Semaphore semaphore = new Semaphore(1);

	public Server(int port) throws IOException {
		this.port = port;
		serverSocket = new ServerSocket();
		serverSocket.bind(new InetSocketAddress(port));
		init();
	}

	public Server(String bindIP, int port) throws IOException {
		this.port = port;
		serverSocket = new ServerSocket();
		serverSocket.bind(new InetSocketAddress(bindIP, port));
		init();
	}

	private void init() {
		byteArrayQueue = ByteArrayQueue.getChannel("LOCAL");
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		int workerID = 0;
		while (!shutdown) {
			Socket socket;
			WorkerSession workerSession;
			try {
				socket = serverSocket.accept();
				workerSession = new WorkerSession(socket, byteArrayQueue, workerSessions);
				workerSessions.add(workerSession);
				workerSession.setName(Integer.toString(workerID++));
				workerSession.start();
			} catch (IOException e) {
				System.out.println("Server socket terminated.");
			}
		}
		semaphore.release();
	}

	/**
	 * check whether server is close or not.
	 *
	 * @return true on Server is closed, and otherwise is false.
	 */
	public boolean isClosed() {
		return workerSessions.size() == 0 && shutdown;
	}

	/**
	 * Close Server.
	 */
	public synchronized void close() {
		System.out.println("Server terminating...");
		if (shutdown)
			return;
		shutdown = true;
		try {
			for (int i = workerSessions.size() - 1; i > -1; i--) {
				workerSessions.poll().close();
			}
			serverSocket.close();
			semaphore.acquire();
			while (true) {
				if (workerSessions.size() == 0) {
					break;
				}
				Thread.sleep(100);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Server terminated.");
		}
	}
}
