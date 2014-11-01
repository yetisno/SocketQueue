package org.yetiz.service.socketqueue;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ByteArrayQueueTest {
	ByteArrayQueue byteArrayQueue1;
	ByteArrayQueue byteArrayQueue2;

	@Before
	public void setUp() throws Exception {
		byteArrayQueue1 = ByteArrayQueue.getChannel("test1");
		byteArrayQueue2 = ByteArrayQueue.getChannel("test2");
		byteArrayQueue1.add("".getBytes());
	}

	@After
	public void tearDown() throws Exception {
		byteArrayQueue1.clear();
		byteArrayQueue2.clear();
	}

	@Test
	public void testGetQueueName() throws Exception {
		Assert.assertEquals("test1", byteArrayQueue1.getQueueName());
		Assert.assertEquals("test2", byteArrayQueue2.getQueueName());
	}

	@Test
	public void testGetChannel() throws Exception {
		Assert.assertEquals(1, ByteArrayQueue.getChannel("test1").size());
		Assert.assertEquals(0, ByteArrayQueue.getChannel("test2").size());
	}
}