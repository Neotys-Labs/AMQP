package com.neotys.amqp.declareexchange;

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

public final class AMQPDeclareExchangeActionEngine extends AMQPActionEngine {

	private static final String STATUS_CODE_INVALID_PARAMETER = "NL-AMQP-DECLARE-EXCHANGE-ACTION-01";
	private static final String STATUS_CODE_ERROR_DECLARE_EXCHANGE = "NL-AMQP-DECLARE-EXCHANGE-ACTION-02";

	@Override
	public SampleResult execute(final Context context, final List<ActionParameter> parameters) {
		final Map<String, Optional<String>> parsedArgs;
		try {
			parsedArgs = parseArguments(parameters, AMQPDeclareExchangeParameter.getOptions());
		} catch (final IllegalArgumentException iae) {
			return newErrorResult(context, "Executing AMQP Declare Exchange action.", STATUS_CODE_INVALID_PARAMETER, "Could not parse arguments: ", iae);
		}
		final String request = "Executing AMQP Declare Exchange action with parameters: " + getArgumentLogString(parsedArgs, AMQPDeclareExchangeParameter.getOptions()) + ".";
		final Logger logger = context.getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug(request);
		}

		final String channelName = parsedArgs.get(AMQPDeclareExchangeParameter.CHANNELNAME.getOption().getName()).get();
		final Channel channel = AMQPActionEngine.getChannel(context, channelName);
		if (channel == null) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_DECLARE_EXCHANGE, "Connection to channel " + channelName + " do not exist.");
		}

		final String exchangeName = parsedArgs.get(AMQPDeclareExchangeParameter.EXCHANGENAME.getOption().getName()).get();
		try {
			final long duration = declareExchange(context, channel, exchangeName, parsedArgs);
			return newOkResult(context, request, "Exchange " + exchangeName + " created", duration);
		} catch (final IOException exception) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_DECLARE_EXCHANGE, "Could not declare exchange: ", exception);
		}
	}

	private long declareExchange(final Context context, final Channel channel, final String exchangeName, final Map<String, Optional<String>> parsedArgs) throws IOException {
		final String exchangeType = parsedArgs.get(AMQPDeclareExchangeParameter.TYPE.getOption().getName()).or("direct");
		final boolean durable = getBooleanValue(parsedArgs, AMQPDeclareExchangeParameter.DURABLE.getOption(), false);
		final boolean autoDelete = getBooleanValue(parsedArgs, AMQPDeclareExchangeParameter.AUTODELETE.getOption(), false);
		final Map<String, Object> arguments = getProperties(context.getLogger(), parsedArgs, AMQPDeclareExchangeParameter.ARGUMENTS.getOption(), "argument");
		context.getLogger().debug("Declaring exchange: " + exchangeName);
		final long startTime = System.currentTimeMillis();
		channel.exchangeDeclare(exchangeName, exchangeType, durable, autoDelete, arguments);
		final long endTime = System.currentTimeMillis();
		return endTime - startTime;
	}

}
