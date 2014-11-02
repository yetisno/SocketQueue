package org.yetiz.service.run;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.yetiz.service.socketqueue.Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RunServerTest {
	Server server;

	@Before
	public void setUp() throws Exception {
		try {
			//Server
			server = new Server(8888);
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() throws Exception {
		server.close();
	}

	@Test
	public void testMain() throws Exception {
		try {
			//Server
			Socket socket = new Socket("localhost", 8888);
			System.out.println("Small test");
			for (int i = 0; i < 10; i++) {
				socket.getOutputStream().write(0x00);
				socket.getOutputStream().write(ByteBuffer.allocate(4).putInt(Integer.toString(i).getBytes().length).array());
				socket.getOutputStream().write(Integer.toString(i).getBytes());
				socket.getOutputStream().flush();
			}
			DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
			for (int i = 0; i < 10; i++) {
				socket.getOutputStream().write(0x01);
				socket.getOutputStream().flush();
				int length;
				byte[] data;
				byte[] size = new byte[4];
				dataInputStream.readFully(size, 0, 4);
				length = ByteBuffer.wrap(size).getInt();
				data = new byte[length];
				dataInputStream.readFully(data, 0, length);
				Assert.assertArrayEquals(Integer.toString(i).getBytes(), data);
				System.out.println("ASSERT - Expect " + i + " Result " + new String(data));
			}
			System.out.println("1MB test");
			ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
			for (int i = 0; i < 10; i++) {
				socket.getOutputStream().write(0x00);
				socket.getOutputStream().write(ByteBuffer.allocate(4).putInt(byteBuffer.array().length).array());
				socket.getOutputStream().write(byteBuffer.array());
				socket.getOutputStream().flush();
			}
			for (int i = 0; i < 10; i++) {
				socket.getOutputStream().write(0x01);
				socket.getOutputStream().flush();
				int length;
				byte[] data;
				byte[] size = new byte[4];
				dataInputStream.readFully(size, 0, 4);
				length = ByteBuffer.wrap(size).getInt();
				data = new byte[length];
				dataInputStream.readFully(data, 0, length);
				Assert.assertEquals(1024 * 1024, data.length);
				System.out.println("ASSERT - Expect length 1024 * 1024 Result " + data.length);
			}
			System.out.println("10MB test");
			byteBuffer = ByteBuffer.allocate(10 * 1024 * 1024);
			for (int i = 0; i < 10; i++) {
				socket.getOutputStream().write(0x00);
				socket.getOutputStream().write(ByteBuffer.allocate(4).putInt(byteBuffer.array().length).array());
				socket.getOutputStream().write(byteBuffer.array());
				socket.getOutputStream().flush();
			}
			for (int i = 0; i < 10; i++) {
				socket.getOutputStream().write(0x01);
				socket.getOutputStream().flush();
				int length;
				byte[] data;
				byte[] size = new byte[4];
				dataInputStream.readFully(size, 0, 4);
				length = ByteBuffer.wrap(size).getInt();
				data = new byte[length];
				dataInputStream.readFully(data, 0, length);
				Assert.assertEquals(10 * 1024 * 1024, data.length);
				System.out.println("ASSERT - Expect length 10 * 1024 * 1024 Result " + data.length);
			}
			System.out.println("100000 records test");
			for (int i = 0; i < 100000; i++) {
				socket.getOutputStream().write(0x00);
				socket.getOutputStream().write(ByteBuffer.allocate(4).putInt(Integer.toString(i).getBytes().length).array());
				socket.getOutputStream().write(Integer.toString(i).getBytes());
				if ((i + 1) % 10000 == 0) {
					System.out.println((i + 1) + " records pushed.");
				}
			}
			socket.getOutputStream().flush();
			socket.close();

			ExecutorService executorService = Executors.newCachedThreadPool();
			Future<Integer> count1 = executorService.submit(new MultiRECVClient(0, 25000));
			Future<Integer> count2 = executorService.submit(new MultiRECVClient(25000, 50000));
			Future<Integer> count3 = executorService.submit(new MultiRECVClient(50000, 75000));
			Future<Integer> count4 = executorService.submit(new MultiRECVClient(75000, 100000));
			Assert.assertEquals(100000, count1.get() + count2.get() + count3.get() + count4.get());

			System.out.println("1M records test passed.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public class MultiRECVClient implements Callable<Integer> {

		private int from;
		private int to;

		public MultiRECVClient(int from, int to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public Integer call() throws Exception {
			Integer count = 0;
			try {
				Socket socket = new Socket("localhost", 8888);
				DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
				for (int i = from; i < to; i++) {
					socket.getOutputStream().write(0x01);
					int length;
					byte[] data;
					byte[] size = new byte[4];
					dataInputStream.readFully(size, 0, 4);
					length = ByteBuffer.wrap(size).getInt();
					data = new byte[length];
					dataInputStream.readFully(data, 0, length);
					count++;
				}
				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
			return count;
		}
	}
}