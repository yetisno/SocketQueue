package org.yetiz.util.io;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;

public class IOBufferTest {
	IOBuffer ioBuffer;

	@Before
	public void setUp() throws Exception {
		ioBuffer = new IOBuffer();
	}

	@After
	public void tearDown() throws Exception {
		ioBuffer = null;
	}

	@Test
	public void testClear() throws Exception {
		ioBuffer.put(1);
		ioBuffer.put(1);
		ioBuffer.clear();
		Assert.assertEquals(0, ioBuffer.remaining());
	}

	@Test
	public void testPut() throws Exception {
		ioBuffer.put(0x09);
		Assert.assertEquals(0x09, ioBuffer.get());
	}

	@Test
	public void testPut1() throws Exception {
		ioBuffer.put(new byte[]{(byte) 0xf1, (byte) 0x02});
		Assert.assertEquals((byte) 0xf1, (byte) ioBuffer.get());
		Assert.assertEquals((byte) 0x02, (byte) ioBuffer.get());
	}

	@Test
	public void testGet() throws Exception {
		ByteBuffer byteBuffer = ByteBuffer.allocate(1300 * 1024);
		for (int i = 0; i < 1100 * 1024; i++) {
			byteBuffer.put((byte) 0xff);
		}
		ioBuffer.put(byteBuffer.array());
		for (int i = 0; i < 1100 * 1023; i++) {
			ioBuffer.get();
		}
		Assert.assertEquals((byte) (255), ioBuffer.get());
	}

	@Test
	public void testRemaining() throws Exception {
		ioBuffer.put(new byte[]{(byte) 0x01, (byte) 0x02});
		Assert.assertEquals(2, ioBuffer.remaining());
	}

	@Test
	public void testMark() throws Exception {
		ioBuffer.put(new byte[]{(byte) 0x01, (byte) 0x02});
		ioBuffer.mark();
		ioBuffer.get();
		ioBuffer.get();
		ioBuffer.reset();
		Assert.assertEquals(2, ioBuffer.remaining());
	}

	@Test
	public void testReset() throws Exception {
		ioBuffer.reset();
		ioBuffer.put(new byte[]{(byte) 0x01, (byte) 0x02});
		ioBuffer.get();
		ioBuffer.mark();
		ioBuffer.get();
		ioBuffer.reset();
		Assert.assertEquals(1, ioBuffer.remaining());
	}
}