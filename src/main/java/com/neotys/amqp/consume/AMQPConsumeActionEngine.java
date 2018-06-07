package com.neotys.amqp.consume;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.SettableFuture;
import com.neotys.amqp.common.AMQPActionEngine;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.Logger;
import com.neotys.extensions.action.engine.SampleResult;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

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
		final Object cachedChannel = context.getCurrentVirtualUser().get(channelName);
		if (!(cachedChannel instanceof Channel)) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_CONSUME, "Connection to channel " + channelName + " do not exist.");
		}
		final Channel channel = (Channel) cachedChannel;

		final String queueName = parsedArgs.get(AMQPConsumeParameter.QUEUENAME.getOption().getName()).get();

		final long timeout = getTimeout(parsedArgs);

		final SettableFuture<String> messageFuture = SettableFuture.create();

		// TODO test code that declare the queue, the exchange and bind it to the queue.
//		try {
//			channel.exchangeDeclare("myExchange", "direct", true);
//			channel.queueDeclare(queueName, true, false, false, null);
//			channel.queueBind(queueName, "myExchange", "my.routing.key");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		boolean autoAck = false;
		final long startTime = System.currentTimeMillis();
		try {
			channel.basicConsume(queueName, autoAck, "myConsumerTag",
					new DefaultConsumer(channel) {
						@Override
						public void handleDelivery(String consumerTag,
												   Envelope envelope,
												   AMQP.BasicProperties properties,
												   byte[] body)
								throws IOException {
							messageFuture.set(new String(body));

							channel.basicAck(envelope.getDeliveryTag(), false);
						}
					});
			final String message = timeout == 0 ? messageFuture.get() : messageFuture.get(timeout, TimeUnit.MILLISECONDS);
			final long endTime = System.currentTimeMillis();
			return newOkResult(context, request, message, endTime - startTime);
		} catch (final InterruptedException | ExecutionException | IOException exception) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_CONSUME, "Could not consume on channel ", exception);
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
