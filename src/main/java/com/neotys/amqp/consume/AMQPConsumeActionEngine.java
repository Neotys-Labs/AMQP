package com.neotys.amqp.consume;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.SettableFuture;
import com.neotys.amqp.common.AMQPActionEngine;
import com.neotys.amqp.common.AMQPMessage;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.Logger;
import com.neotys.extensions.action.engine.SampleResult;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.neotys.action.argument.Arguments.getArgumentLogString;
import static com.neotys.action.argument.Arguments.parseArguments;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Long.parseLong;

public final class AMQPConsumeActionEngine extends AMQPActionEngine {

	private static final String STATUS_CODE_INVALID_PARAMETER = "NL-AMQP-CONSUME-ACTION-01";
	private static final String STATUS_CODE_ERROR_CONSUME = "NL-AMQP-CONSUME-ACTION-02";

	static final long DEFAULT_TIMEOUT = 2000L;
	static final boolean DEFAULT_FAIL_ON_TIMEOUT = true;

	@Override
	public SampleResult execute(final Context context, final List<ActionParameter> parameters) {
		final Map<String, Optional<String>> parsedArgs;
		try {
			parsedArgs = parseArguments(parameters, AMQPConsumeParameter.getOptions());
		} catch (final IllegalArgumentException iae) {
			return newErrorResult(context, "Executing AMQP Consume action.", STATUS_CODE_INVALID_PARAMETER, "Could not parse arguments: ", iae);
		}
		final String request = "Executing AMQP Consume action with parameters: " + getArgumentLogString(parsedArgs, AMQPConsumeParameter.getOptions()) + ".";
		final Logger logger = context.getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug(request);
		}

		final String channelName = parsedArgs.get(AMQPConsumeParameter.CHANNELNAME.getOption().getName()).get();
		final Channel channel = AMQPActionEngine.getChannel(context, channelName);
		if (channel == null) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_CONSUME, "Connection to channel " + channelName + " do not exist.");
		}

		final boolean declareExchange = getBooleanValue(parsedArgs, AMQPConsumeParameter.DECLAREEXCHANGE, false);
		if (declareExchange) {
			try {
				declareExchange(context, channel, parsedArgs);
			} catch (final IOException exception) {
				return newErrorResult(context, request, STATUS_CODE_ERROR_CONSUME, "Could not declare exchange: ", exception);
			}
		}

		final boolean declareQueue = getBooleanValue(parsedArgs, AMQPConsumeParameter.DECLAREQUEUE, false);
		final String queueName;
		if (declareQueue) {
			try {
				queueName = declareQueue(context, channel, parsedArgs);
			} catch (final IOException exception) {
				return newErrorResult(context, request, STATUS_CODE_ERROR_CONSUME, "Could not declare queue: ", exception);
			}
		} else {
			queueName = parsedArgs.get(AMQPConsumeParameter.QUEUENAME.getOption().getName()).get();
		}

		final long timeout = getTimeout(parsedArgs);
		final boolean autoAck = getBooleanValue(parsedArgs, AMQPConsumeParameter.AUTOACK, false);

		try {
			return doConsume(context, request, channel, queueName, timeout, autoAck);
		} catch (final InterruptedException | ExecutionException | IOException exception) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_CONSUME, "Could not consume: ", exception);
		} catch (final TimeoutException e) {
			final String statusMessage = "Message not received before timeout of " + timeout + " ms.";
			if (failOnTimeout(parsedArgs)) {
				return newErrorResult(context, request, STATUS_CODE_ERROR_CONSUME, statusMessage);
			} else {
				logger.debug(statusMessage);
				return newOkResult(context, request, statusMessage);
			}
		}
	}

	private String declareQueue(final Context context, final Channel channel, final Map<String, Optional<String>> parsedArgs) throws IOException {
		final Optional<String> queueNameOptional = parsedArgs.get(AMQPConsumeParameter.QUEUENAME.getOption().getName());
		final Optional<String> exchangeNameOptional = parsedArgs.get(AMQPConsumeParameter.EXCHANGE.getOption().getName());
		final Optional<String> rountingKeyOptional = parsedArgs.get(AMQPConsumeParameter.ROUTINGKEY.getOption().getName());
		final String queueName;
		if (queueNameOptional.isPresent()) {
			context.getLogger().debug("Declaring queue: " + queueNameOptional.get());
			final boolean durable = getBooleanValue(parsedArgs, AMQPConsumeParameter.QUEUEDURABLE, false);
			final boolean exclusive = getBooleanValue(parsedArgs, AMQPConsumeParameter.QUEUEEXCLUSIVE, false);
			final boolean autoDelete = getBooleanValue(parsedArgs, AMQPConsumeParameter.QUEUEAUTODELETE, false);
			final Map<String, Object> arguments = getProperties(context.getLogger(), parsedArgs, AMQPConsumeParameter.QUEUEARGUMENTS.getOption(), "argument");
			queueName = channel.queueDeclare(queueNameOptional.get(), durable, exclusive, autoDelete, arguments).getQueue();
		} else {
			context.getLogger().debug("Declaring queue");
			queueName = channel.queueDeclare().getQueue();
		}
		context.getLogger().debug(String.format("Binding queue %s to exchange %s on routing key %s", queueName, exchangeNameOptional.get(), rountingKeyOptional.get()));
		channel.queueBind(queueName, exchangeNameOptional.get(), rountingKeyOptional.get());
		return queueName;
	}

	private void declareExchange(final Context context, final Channel channel, final Map<String, Optional<String>> parsedArgs) throws IOException {
		final Optional<String> exchangeNameOptional = parsedArgs.get(AMQPConsumeParameter.EXCHANGE.getOption().getName());
		final String exchangeType = parsedArgs.get(AMQPConsumeParameter.EXCHANGETYPE.getOption().getName()).or("direct");
		final boolean durable = getBooleanValue(parsedArgs, AMQPConsumeParameter.EXCHANGEDURABLE, false);
		final boolean autoDelete = getBooleanValue(parsedArgs, AMQPConsumeParameter.EXCHANGEAUTODELETE, false);
		final Map<String, Object> arguments = getProperties(context.getLogger(), parsedArgs, AMQPConsumeParameter.EXCHANGEARGUMENTS.getOption(), "argument");
		context.getLogger().debug("Declaring exchange: " + exchangeNameOptional.get());
		channel.exchangeDeclare(exchangeNameOptional.get(), exchangeType, durable, autoDelete, arguments);
	}

	private boolean getBooleanValue(final Map<String, Optional<String>> parsedArgs, final AMQPConsumeParameter parameter, final boolean defaultValue) {
		return parsedArgs.get(parameter.getOption().getName()).transform(Boolean::parseBoolean).or(defaultValue);
	}

	private SampleResult doConsume(final Context context,
								   final String request,
								   final Channel channel,
								   final String queueName,
								   final long timeout,
								   final boolean autoAck) throws IOException, InterruptedException, ExecutionException, TimeoutException {
		final SettableFuture<AMQPMessage> messageFuture = SettableFuture.create();
		context.getLogger().debug("Consuming message on queue: " + queueName);
		final long startTime = System.currentTimeMillis();
		channel.basicConsume(queueName, autoAck, "",
				new DefaultConsumer(channel) {
					@Override
					public void handleDelivery(final String consumerTag,
											   final Envelope envelope,
											   final AMQP.BasicProperties properties,
											   final byte[] body) throws IOException {
						messageFuture.set(new AMQPMessage(consumerTag, envelope, properties, new String(body)));

						if (!autoAck) {
							channel.basicAck(envelope.getDeliveryTag(), false);
						}
					}
				});
		final AMQPMessage message = timeout == 0 ? messageFuture.get() : messageFuture.get(timeout, TimeUnit.MILLISECONDS);
		final long endTime = System.currentTimeMillis();
		if (context.getLogger().isDebugEnabled()) {
			context.getLogger().debug("Message received: " + message.toString());
		}
		// TODO write message with properties in response
		return newOkResult(context, request, message.getBody(), endTime - startTime);
	}

	/**
	 * Get fail on timeout from arguments if its present, default value otherwise.
	 */
	private static boolean failOnTimeout(final Map<String, Optional<String>> parsedArgs) {
		final Optional<String> argument = parsedArgs.get(AMQPConsumeParameter.FAILONTIMEOUT.getOption().getName());
		return argument.isPresent() ? parseBoolean(argument.get()) : DEFAULT_FAIL_ON_TIMEOUT;
	}

	/**
	 * Get timeout from arguments if its present, default value otherwise.
	 */
	private static long getTimeout(final Map<String, Optional<String>> parsedArgs) {
		final Optional<String> argument = parsedArgs.get(AMQPConsumeParameter.TIMEOUT.getOption().getName());
		return argument.isPresent() ? parseLong(argument.get()) : DEFAULT_TIMEOUT;
	}
}
