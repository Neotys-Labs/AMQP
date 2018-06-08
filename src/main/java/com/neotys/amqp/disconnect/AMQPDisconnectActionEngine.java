package com.neotys.amqp.disconnect;

import static com.neotys.action.argument.Arguments.getArgumentLogString;
import static com.neotys.action.argument.Arguments.parseArguments;
import static com.neotys.amqp.disconnect.AMQPDisconnectParameter.CONNECTIONNAME;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.neotys.amqp.common.AMQPActionEngine;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.Logger;
import com.neotys.extensions.action.engine.SampleResult;
import com.rabbitmq.client.Connection;

public final class AMQPDisconnectActionEngine extends AMQPActionEngine {

	private static final String STATUS_CODE_INVALID_PARAMETER = "NL-AMQP-DISCONNECT-ACTION-01";
	private static final String STATUS_CODE_ERROR_DISCONNECTION = "NL-AMQP-DISCONNECT-ACTION-02";	

	@Override
	public SampleResult execute(final com.neotys.extensions.action.engine.Context context, final List<ActionParameter> parameters) {

		final Map<String, com.google.common.base.Optional<String>> parsedArgs;
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
		final String connectionName = getArgument(parsedArgs, CONNECTIONNAME).get();
		final Connection connection = AMQPActionEngine.removeConnection(context, connectionName);
		if(connection == null){
			return newErrorResult(context, request, STATUS_CODE_INVALID_PARAMETER,
					"No AMQP connection found with name " + connectionName + ".");
		}
		try {
			connection.close();
		} catch (final Exception e) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_DISCONNECTION, "Error while disconnecting with AMQP server.", e);
		}
		return newOkResult(context, request, "AMQP connection closed.");
	}
	
	private static final Optional<String> getArgument(Map<String, com.google.common.base.Optional<String>> parsedArgs, final AMQPDisconnectParameter parameter){
		return Optional.ofNullable(parsedArgs.get(parameter.getOption().getName()).orNull());				
	}
}
