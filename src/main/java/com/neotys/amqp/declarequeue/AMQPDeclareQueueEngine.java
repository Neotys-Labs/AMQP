package com.neotys.amqp.declarequeue;

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

public final class AMQPDeclareQueueEngine extends AMQPActionEngine {

	private static final String STATUS_CODE_INVALID_PARAMETER = "NL-AMQP-DECLARE-QUEUE-ACTION-01";
	private static final String STATUS_CODE_ERROR_DECLARE_QUEUE = "NL-AMQP-DECLARE-QUEUE-ACTION-02";

	@Override
	public SampleResult execute(final Context context, final List<ActionParameter> parameters) {
		final Map<String, Optional<String>> parsedArgs;
		try {
			parsedArgs = parseArguments(parameters, AMQPDeclareQueueParameter.getOptions());
		} catch (final IllegalArgumentException iae) {
			return newErrorResult(context, "Executing AMQP Declare Queue action.", STATUS_CODE_INVALID_PARAMETER, "Could not parse arguments: ", iae);
		}
		final String request = "Executing AMQP Declare Queue action with parameters: " + getArgumentLogString(parsedArgs, AMQPDeclareQueueParameter.getOptions()) + ".";
		final Logger logger = context.getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug(request);
		}

		final String channelName = parsedArgs.get(AMQPDeclareQueueParameter.CHANNELNAME.getOption().getName()).get();
		final Channel channel = AMQPActionEngine.getChannel(context, channelName);
		if (channel == null) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_DECLARE_QUEUE, "Connection to channel " + channelName + " do not exist.");
		}

		try {
			final String queueName = declareQueue(context, channel, parsedArgs);
			return newOkResult(context, request, "Queue " + queueName + " created");
		} catch (final IOException exception) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_DECLARE_QUEUE, "Could not declare exchange: ", exception);
		}
	}

	private String declareQueue(final Context context, final Channel channel, final Map<String, Optional<String>> parsedArgs) throws IOException {
		final Optional<String> queueNameOptional = parsedArgs.get(AMQPDeclareQueueParameter.QUEUENAME.getOption().getName());
		final String exchangeName = parsedArgs.get(AMQPDeclareQueueParameter.EXCHANGENAME.getOption().getName()).get();
		final String rountingKey = parsedArgs.get(AMQPDeclareQueueParameter.ROUTINGKEY.getOption().getName()).get();
		final String queueName;
		if (queueNameOptional.isPresent()) {
			context.getLogger().debug("Declaring queue: " + queueNameOptional.get());
			final boolean durable = getBooleanValue(parsedArgs, AMQPDeclareQueueParameter.DURABLE.getOption(), false);
			final boolean exclusive = getBooleanValue(parsedArgs, AMQPDeclareQueueParameter.EXCLUSIVE.getOption(), false);
			final boolean autoDelete = getBooleanValue(parsedArgs, AMQPDeclareQueueParameter.AUTODELETE.getOption(), false);
			final Map<String, Object> arguments = getProperties(context.getLogger(), parsedArgs, AMQPDeclareQueueParameter.ARGUMENTS.getOption(), "argument");
			queueName = channel.queueDeclare(queueNameOptional.get(), durable, exclusive, autoDelete, arguments).getQueue();
		} else {
			context.getLogger().debug("Declaring queue");
			queueName = channel.queueDeclare().getQueue();
		}
		context.getLogger().debug(String.format("Binding queue %s to exchange %s on routing key %s", queueName, exchangeName, rountingKey));
		channel.queueBind(queueName, exchangeName, rountingKey);
		return queueName;
	}
}
