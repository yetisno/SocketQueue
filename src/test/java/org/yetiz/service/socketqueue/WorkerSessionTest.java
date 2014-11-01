package org.yetiz.service.socketqueue;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.yetiz.util.io.IOBufferIOStream;

import java.io.DataInputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WorkerSessionTest {
	Socket socket;
	ByteArrayQueue byteArrayQueue;
	ConcurrentLinkedQueue<WorkerSession> workerSessions;
	WorkerSession workerSession;
	IOBufferIOStream socketin;
	IOBufferIOStream socketout;


	@Before
	public void setUp() throws Exception {
		socket = Mockito.mock(Socket.class);
		byte[] inputData = ByteBuffer.allocate(10).put((byte) 0x00)
				.put(ByteBuffer.allocate(4).putInt("test".getBytes().length).array())
				.put("test".getBytes())
				.put(((byte) 0x01))
				.array();
		socketin = new IOBufferIOStream();
		socketout = new IOBufferIOStream();
		socketin.getOutputStream().write(inputData, 0, inputData.length);
		Mockito.when(socket.getInputStream()).thenReturn(socketin.getInputStream());
		Mockito.when(socket.getOutputStream()).thenReturn(socketout.getOutputStream());
		byteArrayQueue = ByteArrayQueue.getChannel("test");
		workerSessions = new ConcurrentLinkedQueue<WorkerSession>();
		workerSession = new WorkerSession(socket, byteArrayQueue, workerSessions);
		workerSession.setName("test");
		workerSessions.add(workerSession);
	}

	@After
	public void tearDown() throws Exception {
		ByteArrayQueue.destroyAll();
		workerSessions.clear();
	}

	@Test
	public void testRun() throws Exception {
		DataInputStream dataInputStream = new DataInputStream(socketout.getInputStream());
		workerSession.start();
		Thread.sleep(100);
		byte[] lengthByteArray = new byte[4];
		dataInputStream.read(lengthByteArray, 0, 4);
		int length = ByteBuffer.wrap(lengthByteArray).getInt();
		byte[] data = new byte[length];
		dataInputStream.read(data, 0, length);
		Assert.assertEquals("test", new String(data).toString());
	}

	@Test
	public void testClose() throws Exception {
		workerSession.start();
		workerSession.close();
		Assert.assertTrue(workerSession.shutdowned());
		Thread.sleep(100);
	}
}