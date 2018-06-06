package com.neotys.amqp.connect;

import static com.neotys.action.argument.Arguments.getArgumentLogString;
import static com.neotys.action.argument.Arguments.parseArguments;
import static com.neotys.amqp.connect.AMQPConnectParameter.CHANNELNAME;
import static com.neotys.amqp.connect.AMQPConnectParameter.HOSTNAME;
import static com.neotys.amqp.connect.AMQPConnectParameter.PASSWORD;
import static com.neotys.amqp.connect.AMQPConnectParameter.PORT;
import static com.neotys.amqp.connect.AMQPConnectParameter.SSLPROTOCOL;
import static com.neotys.amqp.connect.AMQPConnectParameter.USERNAME;
import static com.neotys.amqp.connect.AMQPConnectParameter.VIRTUALHOST;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import com.neotys.amqp.common.AMQPActionEngine;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.Logger;
import com.neotys.extensions.action.engine.SampleResult;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public final class AMQPConnectActionEngine extends AMQPActionEngine {

	private static final String STATUS_CODE_INVALID_PARAMETER = "NL-AMQP-CONNECT-ACTION-01";
	private static final String STATUS_CODE_ERROR_CONNECTION = "NL-AMQP-CONNECT-ACTION-02";
	
	@Override
	public SampleResult execute(final com.neotys.extensions.action.engine.Context context, final List<ActionParameter> parameters) {

		final Map<String, Optional<String>> parsedArgs;
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

		final Object cachedAMQPConnection = context.getCurrentVirtualUser().get(AMQP_CONNECTION_KEY);
		Connection amqpConnection = null;
		try {
			if (cachedAMQPConnection instanceof Connection) {
				amqpConnection = (Connection) cachedAMQPConnection;
			} else if (cachedAMQPConnection == null) {
				final ConnectionFactory connectionFactory = new ConnectionFactory();
				connectionFactory.setHost(parsedArgs.get(HOSTNAME.getOption().getName()).get());
				connectionFactory.setPort(Integer.parseInt(parsedArgs.get(PORT.getOption().getName()).get()));
				if (parsedArgs.get(USERNAME.getOption().getName()).isPresent()) {
					connectionFactory.setUsername(parsedArgs.get(USERNAME.getOption().getName()).get());
				}
				if (parsedArgs.get(PASSWORD.getOption().getName()).isPresent()) {
					connectionFactory.setPassword(parsedArgs.get(PASSWORD.getOption().getName()).get());
				}
				if (parsedArgs.get(VIRTUALHOST.getOption().getName()).isPresent()) {
					connectionFactory.setVirtualHost(parsedArgs.get(VIRTUALHOST.getOption().getName()).get());
				}
				final Optional<String> sslProtocol = parsedArgs.get(SSLPROTOCOL.getOption().getName());
				if (sslProtocol.isPresent()) {
					if(sslProtocol.get().isEmpty()){
						connectionFactory.useSslProtocol();
					} else {
						connectionFactory.useSslProtocol(sslProtocol.get());	
					}					
				}
				amqpConnection = connectionFactory.newConnection();

			} else {
				return newErrorResult(context, request, STATUS_CODE_ERROR_CONNECTION, "The AMQP Connection has not the correct type.");
			}
		} catch (final Exception e) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_CONNECTION, "Cannot create connection to AMQP server", e);
		} finally {
			context.getCurrentVirtualUser().put(AMQP_CONNECTION_KEY, amqpConnection);
		}

		final String channelName = parsedArgs.get(CHANNELNAME.getOption().getName()).or("");
		final Object cachedChannel = context.getCurrentVirtualUser().get(channelName);		
		if (cachedChannel instanceof Channel) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_CONNECTION, "A AMQP channel already exists with name " + channelName + ".");
		} else if (cachedChannel == null) {
			Channel amqpChannel = null;
			try {
				amqpChannel = amqpConnection.createChannel();
			} catch (final IOException e) {
				return newErrorResult(context, request, STATUS_CODE_ERROR_CONNECTION, "Cannot create channel to AMQP server connection", e);
			} finally {
				context.getCurrentVirtualUser().put(channelName, amqpChannel);
			}			
		} else {
			return newErrorResult(context, request, STATUS_CODE_ERROR_CONNECTION, "The AMQP Channel has not the correct type.");
		}
		return newOkResult(context, request, "Connected to AMQP server.");
	}
}
