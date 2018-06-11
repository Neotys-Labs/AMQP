package com.neotys.amqp.deleteexchange;

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

public final class AMQPDeleteExchangeActionEngine extends AMQPActionEngine {

	private static final String STATUS_CODE_INVALID_PARAMETER = "NL-AMQP-DELETE-EXCHANGE-ACTION-01";
	private static final String STATUS_CODE_ERROR_DELETE_EXCHANGE = "NL-AMQP-DELETE-EXCHANGE-ACTION-02";

	@Override
	public SampleResult execute(final Context context, final List<ActionParameter> parameters) {
		final Map<String, Optional<String>> parsedArgs;
		try {
			parsedArgs = parseArguments(parameters, AMQPDeleteExchangeParameter.getOptions());
		} catch (final IllegalArgumentException iae) {
			return newErrorResult(context, "Executing AMQP Delete Exchange action.", STATUS_CODE_INVALID_PARAMETER, "Could not parse arguments: ", iae);
		}
		final String request = "Executing AMQP Delete Exchange action with parameters: " + getArgumentLogString(parsedArgs, AMQPDeleteExchangeParameter.getOptions()) + ".";
		final Logger logger = context.getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug(request);
		}

		final String channelName = parsedArgs.get(AMQPDeleteExchangeParameter.CHANNELNAME.getOption().getName()).get();
		final Channel channel = AMQPActionEngine.getChannel(context, channelName);
		if (channel == null) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_DELETE_EXCHANGE, "Connection to channel " + channelName + " do not exist.");
		}

		final String exchangeName = parsedArgs.get(AMQPDeleteExchangeParameter.EXCHANGENAME.getOption().getName()).get();
		try {
			final long startTime = System.currentTimeMillis();
			channel.exchangeDelete(exchangeName);
			final long endTime = System.currentTimeMillis();
			return newOkResult(context, request, "Exchange "+exchangeName+" deleted.", endTime - startTime);
		} catch (final IOException exception) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_DELETE_EXCHANGE, "Could not delete exchange: ", exception);
		}
	}

}
