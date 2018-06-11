package com.neotys.amqp.common;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.neotys.action.result.ResultFactory;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.Logger;
import com.neotys.extensions.action.engine.SampleResult;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.neotys.action.result.ResultFactory.STATUS_CODE_OK;

public abstract class AMQPActionEngine implements ActionEngine {

	private static final String AMQP_CONNECTION_PREFIX = "AMQP_CONNECTION_PREFIX";
	private static final String AMQP_CHANNEL_PREFIX = "AMQP_CHANNEL_PREFIX";

	private static final LoadingCache<String, Class<?>> TYPES_CACHE;

	static {
		TYPES_CACHE = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build(new CacheLoader<String, Class<?>>() {
			@Override
			public Class<?> load(final String key) throws ClassNotFoundException {
				return Class.forName(key);
			}
		});
	}

	protected static Connection getConnection(final Context context, final String connectionName) {
		final Object connection = context.getCurrentVirtualUser().get(AMQP_CONNECTION_PREFIX + connectionName);
		if (connection instanceof Connection) {
			return (Connection) connection;
		}
		return null;
	}

	protected static Connection removeConnection(final Context context, final String connectionName) {
		final Object connection = context.getCurrentVirtualUser().remove(AMQP_CONNECTION_PREFIX + connectionName);
		if (connection instanceof Connection) {
			return (Connection) connection;
		}
		return null;
	}

	protected static void setConnection(final Context context, final String connectionName, final Connection connection) {
		context.getCurrentVirtualUser().put(AMQP_CONNECTION_PREFIX + connectionName, connection);
	}

	protected static Channel getChannel(final Context context, final String channelName) {
		final Object channel = context.getCurrentVirtualUser().get(AMQP_CHANNEL_PREFIX + channelName);
		if (channel instanceof Channel) {
			return (Channel) channel;
		}
		return null;
	}

	protected static Channel removeChannel(final Context context, final String channelName) {
		final Object channel = context.getCurrentVirtualUser().remove(AMQP_CHANNEL_PREFIX + channelName);
		if (channel instanceof Channel) {
			return (Channel) channel;
		}
		return null;
	}

	protected static void setChannel(final Context context, final String connectionName, final Channel channel) {
		context.getCurrentVirtualUser().put(AMQP_CHANNEL_PREFIX + connectionName, channel);
	}

	protected static SampleResult newErrorResult(final Context context, final String requestContent, final String statusCode,
												 final String responseContent, final Exception e) {
		context.getLogger().error(responseContent, e);
		return newResult(context, ResultFactory.State.ERROR, statusCode, responseContent + e.toString(), requestContent);
	}

	protected static SampleResult newOkResult(final Context context, final String requestContent, final String responseContent) {
		return newResult(context, ResultFactory.State.OK, STATUS_CODE_OK, responseContent, requestContent);
	}

	protected static SampleResult newOkResult(final Context context, final String requestContent, final String responseContent, final long duration) {
		final SampleResult result = newOkResult(context, requestContent, responseContent);
		result.setDuration(duration);
		return result;
	}

	protected static SampleResult newErrorResult(final Context context, final String requestContent, final String statusCode,
												 final String responseContent) {
		context.getLogger().error(responseContent);
		return newResult(context, ResultFactory.State.ERROR, statusCode, responseContent, requestContent);
	}

	private static SampleResult newResult(final Context context, ResultFactory.State state, final String statusCode,
										  final String responseContent, final String requestContent) {
		final SampleResult result = new SampleResult();
		result.setStatusCode(statusCode);
		result.setError(ResultFactory.State.ERROR.equals(state));

		context.getLogger().debug("Action execution finished with status code: " + statusCode);

		result.setResponseContent(responseContent);
		result.setRequestContent(requestContent);
		return result;
	}

	/**
	 * Get properties. Properties are separated by line separator.
	 */
	protected static Map<String, Object> getProperties(final Logger logger, final Map<String, Optional<String>> parsedArgs, final AMQPParameterOption option, String propertyType) {
		final Optional<String> param = parsedArgs.get(option.getName());
		if (!param.isPresent() || isNullOrEmpty(param.get())) {
			return Collections.emptyMap();
		}
		final String[] properties = param.get().split("\n");
		final ImmutableMap.Builder<String, Object> mapBuilder = ImmutableMap.builder();
		for (final String property : properties) {
			if (isNullOrEmpty(property)) {
				continue;
			}
			addProperty(logger, mapBuilder, property, propertyType);
		}
		return mapBuilder.build();
	}

	/**
	 * Add one property. Pattern name=[class]value.
	 */
	private static void addProperty(final Logger logger, final ImmutableMap.Builder<String, Object> builder, final String property, final String propertyType) {
		final int equalIndex = property.indexOf('=');
		if (equalIndex == -1 && equalIndex != property.length() - 1) {
			logger.debug("Invalid " + propertyType + ": " + property);
			return;
		}

		final String name = property.substring(0, equalIndex);
		final String typeAndValue = property.substring(equalIndex + 1);

		final int openBracketIndex = typeAndValue.indexOf('[');
		final int closeBracketIndex = typeAndValue.indexOf(']');
		Object value;
		if (openBracketIndex != -1 && closeBracketIndex != -1 && closeBracketIndex != typeAndValue.length() - 1) {
			// we have a type
			String typeString = null;
			try {
				typeString = typeAndValue.substring(openBracketIndex + 1, closeBracketIndex);
				final Class<?> type = TYPES_CACHE.get(typeString);
				value = getValueFromType(type, typeAndValue.substring(closeBracketIndex + 1));
			} catch (final ExecutionException e) {
				logger.debug("Invalid value type: " + typeString + " in property: " + property);
				// default type : String
				value = typeAndValue.substring(closeBracketIndex + 1);
			}
		} else {
			// default type : String
			value = typeAndValue;
		}

		builder.put(name, value);
	}

	/**
	 * @return return an object value, of type from argument, by its String representation.
	 */
	@VisibleForTesting
	// TODO to put in common with JMS.
	static Object getValueFromType(final Class<?> type, final String valueString) {
		if (Integer.class.isAssignableFrom(type)) {
			return Integer.valueOf(valueString);
		} else if (Boolean.class.isAssignableFrom(type)) {
			return Boolean.valueOf(valueString);
		} else if (Long.class.isAssignableFrom(type)) {
			return Long.valueOf(valueString);
		} else if (Double.class.isAssignableFrom(type)) {
			return Double.valueOf(valueString);
		} else if (Short.class.isAssignableFrom(type)) {
			return Short.valueOf(valueString);
		} else if (Byte.class.isAssignableFrom(type)) {
			return Byte.valueOf(valueString);
		} else if (Float.class.isAssignableFrom(type)) {
			return Float.valueOf(valueString);
		}
		return valueString;
	}

	protected static boolean getBooleanValue(final Map<String, Optional<String>> parsedArgs, final AMQPParameterOption option, final boolean defaultValue) {
		return parsedArgs.get(option.getName()).transform(Boolean::parseBoolean).or(defaultValue);
	}

	@Override
	public void stopExecute() {
		// Not implemented
	}
}
