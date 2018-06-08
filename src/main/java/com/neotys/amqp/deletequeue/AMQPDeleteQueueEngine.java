package com.neotys.amqp.deletequeue;

import com.google.common.base.Optional;
import com.neotys.amqp.common.AMQPActionEngine;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.Logger;
import com.neotys.extensions.action.engine.SampleResult;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.neotys.action.argument.Arguments.getArgumentLogString;
import static com.neotys.action.argument.Arguments.parseArguments;

public final class AMQPDeleteQueueEngine extends AMQPActionEngine {

	private static final String STATUS_CODE_INVALID_PARAMETER = "NL-AMQP-DELETE-QUEUE-ACTION-01";
	private static final String STATUS_CODE_ERROR_DELETE_QUEUE = "NL-AMQP-DELETE-QUEUE-ACTION-02";

	@Override
	public SampleResult execute(final Context context, final List<ActionParameter> parameters) {
		final Map<String, Optional<String>> parsedArgs;
		try {
			parsedArgs = parseArguments(parameters, AMQPDeleteQueueParameter.getOptions());
		} catch (final IllegalArgumentException iae) {
			return newErrorResult(context, "Executing AMQP Delete Queue action.", STATUS_CODE_INVALID_PARAMETER, "Could not parse arguments: ", iae);
		}
		final String request = "Executing AMQP Delete Queue action with parameters: " + getArgumentLogString(parsedArgs, AMQPDeleteQueueParameter.getOptions()) + ".";
		final Logger logger = context.getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug(request);
		}

		final String channelName = parsedArgs.get(AMQPDeleteQueueParameter.CHANNELNAME.getOption().getName()).get();
		final Channel channel = AMQPActionEngine.getChannel(context, channelName);
		if (channel == null) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_DELETE_QUEUE, "Connection to channel " + channelName + " do not exist.");
		}

		final String queueName = parsedArgs.get(AMQPDeleteQueueParameter.QUEUENAME.getOption().getName()).get();
		try {
			channel.queueDelete(queueName);
			return newOkResult(context, request, "Queue " + queueName + " deleted.");
		} catch (final IOException exception) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_DELETE_QUEUE, "Could not declare exchange: ", exception);
		}
	}
}
