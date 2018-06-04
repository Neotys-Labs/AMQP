package com.neotys.amqp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AMQPActionTest {
	@Test
	public void shouldReturnType() {
		final AMQPAction action = new AMQPAction();
		assertEquals("AMQP", action.getType());
	}

}
