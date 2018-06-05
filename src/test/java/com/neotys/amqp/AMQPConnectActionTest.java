package com.neotys.amqp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.neotys.amqp.connect.AMQPConnectAction;

public class AMQPConnectActionTest {
	@Test
	public void shouldReturnType() {
		final AMQPConnectAction action = new AMQPConnectAction();
		assertEquals("AMQPConnect", action.getType());
	}

}
