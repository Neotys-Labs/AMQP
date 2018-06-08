package com.neotys.amqp.connect;

import static com.neotys.action.argument.Arguments.getArgumentLogString;
import static com.neotys.action.argument.Arguments.parseArguments;
import static com.neotys.amqp.connect.AMQPConnectParameter.CONNECTIONNAME;
import static com.neotys.amqp.connect.AMQPConnectParameter.HOSTNAME;
import static com.neotys.amqp.connect.AMQPConnectParameter.PASSWORD;
import static com.neotys.amqp.connect.AMQPConnectParameter.PORT;
import static com.neotys.amqp.connect.AMQPConnectParameter.SSLPROTOCOL;
import static com.neotys.amqp.connect.AMQPConnectParameter.USERNAME;
import static com.neotys.amqp.connect.AMQPConnectParameter.VIRTUALHOST;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.neotys.amqp.common.AMQPActionEngine;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.Logger;
import com.neotys.extensions.action.engine.SampleResult;
import com.rabbitmq.client.ConnectionFactory;

public final class AMQPConnectActionEngine extends AMQPActionEngine {

	private static final String STATUS_CODE_INVALID_PARAMETER = "NL-AMQP-CONNECT-ACTION-01";
	private static final String STATUS_CODE_ERROR_CONNECTION = "NL-AMQP-CONNECT-ACTION-02";

	@Override
	public SampleResult execute(final com.neotys.extensions.action.engine.Context context, final List<ActionParameter> parameters) {

		final Map<String, com.google.common.base.Optional<String>> parsedArgs;
		try {
			parsedArgs = parseArguments(parameters, AMQPConnectParameter.getOptions());
		} catch (final IllegalArgumentException iae) {
			return newErrorResult(context, "Executing AMQP Connect action.", STATUS_CODE_INVALID_PARAMETER, "Could not parse arguments: ", iae);
		}
		final String request = "Executing AMQP Connect action with parameters: " + getArgumentLogString(parsedArgs, AMQPConnectParameter.getOptions())
				+ ".";
		final Logger logger = context.getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug(request);
		}
		final String connectionName = getArgument(parsedArgs, CONNECTIONNAME).get();
		if (AMQPActionEngine.getConnection(context, connectionName) != null) {
			return newErrorResult(context, request, STATUS_CODE_INVALID_PARAMETER,
					"A AMQP connection already exists with name " + connectionName + ".");
		}					
		try {
			final ConnectionFactory connectionFactory = new ConnectionFactory();
			connectionFactory.setHost(getArgument(parsedArgs, HOSTNAME).get());
			connectionFactory.setPort(Integer.parseInt(getArgument(parsedArgs, PORT).get()));
			getArgument(parsedArgs, USERNAME).ifPresent(u -> connectionFactory.setUsername(u));
			getArgument(parsedArgs, PASSWORD).ifPresent(p -> connectionFactory.setPassword(p));
			getArgument(parsedArgs, VIRTUALHOST).ifPresent(v -> connectionFactory.setVirtualHost(v));
			final Optional<String> sslProtocol = getArgument(parsedArgs, SSLPROTOCOL);
			if(sslProtocol.isPresent() && "".equals(sslProtocol.get())){
				connectionFactory.useSslProtocol();
			} else if(sslProtocol.isPresent()){
				connectionFactory.useSslProtocol(sslProtocol.get());
			}			
			AMQPActionEngine.setConnection(context, connectionName, connectionFactory.newConnection());
		} catch (final Exception e) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_CONNECTION, "Cannot create connection to AMQP server.", e);
		}
		return newOkResult(context, request, "Connected to AMQP server.");
	}
	
	private static final Optional<String> getArgument(Map<String, com.google.common.base.Optional<String>> parsedArgs, final AMQPConnectParameter parameter){
		return Optional.ofNullable(parsedArgs.get(parameter.getOption().getName()).orNull());				
	}
}
