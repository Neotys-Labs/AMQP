package com.neotys.amqp.disconnect;

import static com.neotys.action.argument.Arguments.getArgumentLogString;
import static com.neotys.action.argument.Arguments.parseArguments;
import static com.neotys.amqp.disconnect.AMQPDisconnectParameter.CHANNELNAME;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.google.common.base.Optional;
import com.neotys.amqp.common.AMQPActionEngine;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.Logger;
import com.neotys.extensions.action.engine.SampleResult;
import com.rabbitmq.client.Channel;

public final class AMQPDisconnectActionEngine extends AMQPActionEngine {

	private static final String STATUS_CODE_INVALID_PARAMETER = "NL-AMQP-DISCONNECT-ACTION-01";
	private static final String STATUS_CODE_ERROR_DISCONNECTION = "NL-AMQP-DISCONNECT-ACTION-02";	

	@Override
	public SampleResult execute(final com.neotys.extensions.action.engine.Context context, final List<ActionParameter> parameters) {

		final Map<String, Optional<String>> parsedArgs;
		try {
			parsedArgs = parseArguments(parameters, AMQPDisconnectParameter.getOptions());
		} catch (final IllegalArgumentException iae) {
			return newErrorResult(context, "Executing AMQP Disconnect action.", STATUS_CODE_INVALID_PARAMETER, "Could not parse arguments: ", iae);
		}
		final String request = "Executing AMQP Disconnect action with parameters: " + getArgumentLogString(parsedArgs, AMQPDisconnectParameter.getOptions())
				+ ".";
		final Logger logger = context.getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug(request);
		}
		final String channelName = parsedArgs.get(CHANNELNAME.getOption().getName()).get();
		final Object cachedChannel = context.getCurrentVirtualUser().remove(channelName);
		if (!(cachedChannel instanceof Channel)) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_DISCONNECTION, "Connection to channel " + channelName + " has not been created.");
		}
		
		final Channel channel = (Channel) cachedChannel;
		try {
			channel.close();
			return newOkResult(context, request, "Disconnected from channel " + channelName + ".");
		} catch (final IOException | TimeoutException exception) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_DISCONNECTION, "Could not disconnect from channel: ", exception);
		}
	}
}
