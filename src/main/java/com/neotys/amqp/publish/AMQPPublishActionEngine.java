package com.neotys.amqp.publish;

import com.google.common.base.Optional;
import com.neotys.amqp.common.AMQPActionEngine;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.Logger;
import com.neotys.extensions.action.engine.SampleResult;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.neotys.action.argument.Arguments.getArgumentLogString;
import static com.neotys.action.argument.Arguments.parseArguments;

public final class AMQPPublishActionEngine extends AMQPActionEngine {

	private static final String STATUS_CODE_INVALID_PARAMETER = "NL-AMQP-PUBLISH-ACTION-01";
	private static final String STATUS_CODE_ERROR_PUBLISH = "NL-AMQP-PUBLISH-ACTION-02";
	
	@Override
	public SampleResult execute(final Context context, final List<ActionParameter> parameters) {
		final Map<String, Optional<String>> parsedArgs;
		try {
			parsedArgs = parseArguments(parameters, AMQPPublishParameter.getOptions());
		} catch (final IllegalArgumentException iae) {
			return newErrorResult(context, "Executing AMQP Publish action.", STATUS_CODE_INVALID_PARAMETER, "Could not parse arguments: ", iae);
		}
		final String request = "Executing AMQP Publish action with parameters: " + getArgumentLogString(parsedArgs, AMQPPublishParameter.getOptions())+ ".";
		final Logger logger = context.getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug(request);
		}

		final String channelName = parsedArgs.get(AMQPPublishParameter.CHANNELNAME.getOption().getName()).get();
		final Object cachedChannel = context.getCurrentVirtualUser().get(channelName);
		if (!(cachedChannel instanceof Channel)) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_PUBLISH, "Connection to channel " + channelName + " do not exist.");
		}
		final Channel channel = (Channel) cachedChannel;

		final String exchange = parsedArgs.get(AMQPPublishParameter.EXCHANGE.getOption().getName()).get();
		final String routingKey = parsedArgs.get(AMQPPublishParameter.ROUTINGKEY.getOption().getName()).get();
		final String contentType = parsedArgs.get(AMQPPublishParameter.CONTENTTYPE.getOption().getName()).or("");
		final AMQP.BasicProperties properties = getProperties(contentType);
		final String textContent = parsedArgs.get(AMQPPublishParameter.TEXTCONTENT.getOption().getName()).or("");
		final byte[] messageBytes = textContent.getBytes();

		try {
			channel.basicPublish(exchange, routingKey, properties, messageBytes);
			return newOkResult(context, request, "Message published on channel " + channelName + ".");
		} catch (final IOException exception) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_PUBLISH, "Could not publish on channel ", exception);
		}
	}

	private AMQP.BasicProperties getProperties(final String contentType) {
		final AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();

		// TODO handle more properties
//		final int deliveryMode = getPersistent() ? 2 : 1;

		builder.contentType(contentType);
//				.deliveryMode(deliveryMode)
//				.priority(0)
//				.correlationId(getCorrelationId())
//				.replyTo(getReplyToQueue())
//				.type(getMessageType())
//				.headers(prepareHeaders())
//				.build();
//		if (getMessageId() != null && !getMessageId().isEmpty()) {
//			builder.messageId(getMessageId());
//		}
		return builder.build();
	}
}
