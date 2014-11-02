package org.yetiz.service.socketqueue;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by yeti on 14/11/1.
 */
public class WorkerSession extends Thread {

	private Socket socket;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;
	private ByteArrayQueue byteArrayQueue;
	private ConcurrentLinkedQueue<WorkerSession> workerSessions;
	private boolean _shutdown = false;
	private Semaphore sleepTimer = new Semaphore(1);

	/**
	 * Create a WorkerSession to process connection.
	 *
	 * @param socket         the connected socket from server.
	 * @param byteArrayQueue store and get data in this queue.
	 * @param workerSessions the list of all active session.
	 */
	public WorkerSession(Socket socket, ByteArrayQueue byteArrayQueue, ConcurrentLinkedQueue<WorkerSession> workerSessions) {
		this.socket = socket;
		this.byteArrayQueue = byteArrayQueue;
		this.workerSessions = workerSessions;
		try {
			dataInputStream = new DataInputStream(socket.getInputStream());
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			sleepTimer.acquire();
		} catch (IOException e) {
			System.err.println("Socket stream error. " + e.toString());
		} catch (InterruptedException e) {
			System.err.println("Interrupt Occurred. " + e.toString());
		}
	}

	private String fixErrorMessage(String errorMessage) {
		return _shutdown ? "Socket Closed." : errorMessage;
	}

	@Override
	public void run() {
		while (true) {
			if (socket.isClosed())
				break;
			byte[] flag = new byte[1];
			try {
				dataInputStream.readFully(flag);
			} catch (EOFException e) {
				System.err.println(fixErrorMessage("Remote Socket Closed."));
				break;
			} catch (IOException e) {
				System.err.println(fixErrorMessage("get flag error."));
				continue;
			}
			dispatch(flag[0]);
		}
		workerSessions.remove(this);
		System.out.println("Worker session " + getName() + " closed.");
	}

	private void doIngress() {
		int length;
		byte[] data;
		try {
			byte[] size = new byte[4];
			dataInputStream.readFully(size, 0, 4);
			length = ByteBuffer.wrap(size).getInt();
		} catch (Exception e) {
			System.err.println(fixErrorMessage("get data length error."));
			return;
		}
		try {
			data = new byte[length];
			dataInputStream.readFully(data, 0, length);
		} catch (Exception e) {
			data = null;
			System.err.println(fixErrorMessage("get data length error."));
			return;
		}
		if (data != null) {
			byteArrayQueue.offer(data);
		}
	}

	private void doEgress() {
		byte[] data = null;
		try {
			while (data == null) {
				data = byteArrayQueue.poll(1, TimeUnit.SECONDS);
			}
			byte[] sendData = ByteBuffer.allocate(4 + data.length)
					.putInt(data.length)
					.put(data).array();
			dataOutputStream.write(sendData);
			dataOutputStream.flush();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(fixErrorMessage("Socket Closed. (Egress)"));
		}
	}

	private void dispatch(byte flag) {
		if (flag == 0x00) {
			doIngress();
			return;
		}
		if (flag == 0x01) {
			doEgress();
			return;
		}
	}

	/**
	 * Check whether WorkerSession is closed or not.
	 *
	 * @return true on close or otherwise is false.
	 */
	public boolean shutdowned() {
		return _shutdown;
	}

	/**
	 * Close this WorkerSession
	 */
	public void close() {
		_shutdown = true;
		try {
			dataInputStream.close();
			dataOutputStream.close();
			socket.close();
		} catch (IOException e) {
			System.out.println("WorkerSession Closed.");
		}
	}
}
