package com.neotys.amqp.closechannel;

import static com.neotys.action.argument.Arguments.getArgumentLogString;
import static com.neotys.action.argument.Arguments.parseArguments;
import static com.neotys.amqp.closechannel.AMQPCloseChannelParameter.CHANNELNAME;

import java.util.List;
import java.util.Map;

import com.neotys.amqp.common.AMQPActionEngine;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.Logger;
import com.neotys.extensions.action.engine.SampleResult;
import com.rabbitmq.client.Channel;

public final class AMQPCloseChannelActionEngine extends AMQPActionEngine {

	private static final String STATUS_CODE_INVALID_PARAMETER = "NL-AMQP-CLOSE-CHANNEL-ACTION-01";
	private static final String STATUS_CODE_ERROR_CLOSECHANNEL = "NL-AMQP-CLOSE-CHANNEL-ACTION-02";	

	@Override
	public SampleResult execute(final com.neotys.extensions.action.engine.Context context, final List<ActionParameter> parameters) {

		final Map<String, com.google.common.base.Optional<String>> parsedArgs;
		try {
			parsedArgs = parseArguments(parameters, AMQPCloseChannelParameter.getOptions());
		} catch (final IllegalArgumentException iae) {
			return newErrorResult(context, "Executing AMQP close channel action.", STATUS_CODE_INVALID_PARAMETER, "Could not parse arguments: ", iae);
		}
		final String request = "Executing AMQP close channel action with parameters: " + getArgumentLogString(parsedArgs, AMQPCloseChannelParameter.getOptions())
				+ ".";
		final Logger logger = context.getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug(request);
		}
		final String channelName = parsedArgs.get(CHANNELNAME.getOption().getName()).or("");		
		final Channel channel = AMQPActionEngine.removeChannel(context, channelName);
		if(channel == null){
			return newErrorResult(context, request, STATUS_CODE_INVALID_PARAMETER,
					"No AMQP channel found with name " + channelName + ".");
		}
		final long startTime = System.currentTimeMillis();
		try {
			channel.close();
		} catch (final Exception e) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_CLOSECHANNEL, "Error while closing APQM channel.", e);
		}
		final long endTime = System.currentTimeMillis();
		return newOkResult(context, request, "AMQP channel closed.", endTime - startTime);
	}	
}
