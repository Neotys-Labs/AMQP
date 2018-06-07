package com.neotys.amqp.common;

import com.google.common.base.MoreObjects;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;

public class AMQPMessage {

	private final String consumerTag;
	private final Envelope envelope;
	private final AMQP.BasicProperties properties;
	private final String body;

	public AMQPMessage(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, String body) {
		this.consumerTag = consumerTag;
		this.envelope = envelope;
		this.properties = properties;
		this.body = body;
	}

	public String getConsumerTag() {
		return consumerTag;
	}

	public Envelope getEnvelope() {
		return envelope;
	}

	public AMQP.BasicProperties getProperties() {
		return properties;
	}

	public String getBody() {
		return body;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("consumerTag", consumerTag)
				.add("envelope", envelope)
				.add("properties", properties)
				.add("body", body)
				.toString();
	}
}
