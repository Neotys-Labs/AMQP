package com.neotys.amqp.createchannel;

import static com.neotys.action.argument.Arguments.getArgumentLogString;
import static com.neotys.action.argument.Arguments.parseArguments;
import static com.neotys.amqp.createchannel.AMQPCreateChannelParameter.CHANNELNAME;
import static com.neotys.amqp.createchannel.AMQPCreateChannelParameter.CONNECTIONNAME;

import java.util.List;
import java.util.Map;

import com.neotys.amqp.common.AMQPActionEngine;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.Logger;
import com.neotys.extensions.action.engine.SampleResult;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public final class AMQPCreateChannelActionEngine extends AMQPActionEngine {

	private static final String STATUS_CODE_INVALID_PARAMETER = "NL-AMQP-CREATE-CHANNEL-ACTION-01";
	private static final String STATUS_CODE_ERROR_CHANNEL_CREATION = "NL-AMQP-CREATE-CHANNEL-ACTION-02";

	@Override
	public SampleResult execute(final com.neotys.extensions.action.engine.Context context, final List<ActionParameter> parameters) {

		final Map<String, com.google.common.base.Optional<String>> parsedArgs;
		try {
			parsedArgs = parseArguments(parameters, AMQPCreateChannelParameter.getOptions());
		} catch (final IllegalArgumentException iae) {
			return newErrorResult(context, "Executing AMQP create channel action.", STATUS_CODE_INVALID_PARAMETER, "Could not parse arguments: ", iae);
		}
		final String request = "Executing AMQP create channel action with parameters: " + getArgumentLogString(parsedArgs, AMQPCreateChannelParameter.getOptions())	+ ".";
		final Logger logger = context.getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug(request);
		}
		final String connectionName = parsedArgs.get(CONNECTIONNAME.getOption().getName()).or("");
		final String channelName = parsedArgs.get(CHANNELNAME.getOption().getName()).or("");
		
		final Connection connection = AMQPActionEngine.getConnection(context, connectionName);
		if(connection == null){
			return newErrorResult(context, request, STATUS_CODE_INVALID_PARAMETER,
					"No AMQP connection found with name " + connectionName + ".");
		}
		if (AMQPActionEngine.getChannel(context, channelName) != null) {
			return newErrorResult(context, request, STATUS_CODE_INVALID_PARAMETER,
					"A AMQP channel already exists with name " + channelName + ".");
		}	
							
		try {
			final long startTime = System.currentTimeMillis();
			final Channel channel = connection.createChannel();
			// Force to consume only one message at a time
			channel.basicQos(1);
			AMQPActionEngine.setChannel(context, channelName, channel);
			final long endTime = System.currentTimeMillis();
			return newOkResult(context, request, "Channel created on AMQP connection.", endTime - startTime);
		} catch (final Exception e) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_CHANNEL_CREATION, "Cannot create channel on AMQP connection.", e);
		}
	}	
}
