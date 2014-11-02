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
			for (int i = 0; i < 100; i++) {
				socket.getOutputStream().write(0x00);
				socket.getOutputStream().write(ByteBuffer.allocate(4).putInt(Integer.toString(i).getBytes().length).array());
				socket.getOutputStream().write(Integer.toString(i).getBytes());
				socket.getOutputStream().flush();
			}
			DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
			for (int i = 0; i < 11; i++) {
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
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}