package org.yetiz.service.socketqueue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ServerTest {
	Server server;

	@Before
	public void setUp() throws Exception {
		server = Mockito.mock(Server.class);
		Mockito.when(server.isClosed()).thenReturn(true);
	}

	@Test
	public void testClose() throws Exception {
		Assert.assertTrue(server.isClosed());
	}
}