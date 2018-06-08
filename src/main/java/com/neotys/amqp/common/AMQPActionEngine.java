package com.neotys.amqp.common;

import com.neotys.action.result.ResultFactory;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.SampleResult;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public abstract class AMQPActionEngine implements ActionEngine {

	private static final String AMQP_CONNECTION_PREFIX = "AMQP_CONNECTION_PREFIX";
	private static final String AMQP_CHANNEL_PREFIX = "AMQP_CHANNEL_PREFIX";
	
	protected static final Connection getConnection(final Context context, final String connectionName){
		final Object connection = context.getCurrentVirtualUser().get(AMQP_CONNECTION_PREFIX + connectionName);
		if(connection instanceof Connection){
			return (Connection) connection;
		}
		return null;
	}
	
	protected static final Connection removeConnection(final Context context, final String connectionName){
		final Object connection = context.getCurrentVirtualUser().remove(AMQP_CONNECTION_PREFIX + connectionName);
		if(connection instanceof Connection){
			return (Connection) connection;
		}
		return null;
	}
	
	protected static final void setConnection(final Context context, final String connectionName, final Connection connection){
		context.getCurrentVirtualUser().put(AMQP_CONNECTION_PREFIX + connectionName, connection);
	}

	protected static final Channel getChannel(final Context context, final String channelName){
		final Object channel = context.getCurrentVirtualUser().get(AMQP_CHANNEL_PREFIX + channelName);
		if(channel instanceof Channel){
			return (Channel) channel;
		}
		return null;
	}
	
	protected static final Channel removeChannel(final Context context, final String channelName){
		final Object channel = context.getCurrentVirtualUser().remove(AMQP_CHANNEL_PREFIX + channelName);
		if(channel instanceof Channel){
			return (Channel) channel;
		}
		return null;
	}
	
	protected static final void setChannel(final Context context, final String connectionName, final Channel channel){
		context.getCurrentVirtualUser().put(AMQP_CHANNEL_PREFIX + connectionName, channel);
	}

	
	protected static SampleResult newErrorResult(final Context context, final String requestContent, final String statusCode,
			final String statusMessage, final Exception e) {
		final SampleResult result = ResultFactory.newErrorResult(context, statusCode, statusMessage, e);
		result.setRequestContent(requestContent);
		return result;
	}

	protected static SampleResult newOkResult(final Context context, final String requestContent, final String statusMessage) {
		final SampleResult result = ResultFactory.newOkResult(context, statusMessage);
		result.setRequestContent(requestContent);
		return result;
	}

	protected static SampleResult newOkResult(final Context context, final String requestContent, final String statusMessage, final long duration) {
		final SampleResult result = AMQPActionEngine.newOkResult(context, requestContent, statusMessage);
		result.setDuration(duration);
		return result;
	}

	protected static SampleResult newErrorResult(final Context context, final String requestContent, final String statusCode,
			final String statusMessage) {
		final SampleResult result = ResultFactory.newErrorResult(context, statusCode, statusMessage);
		result.setRequestContent(requestContent);
		return result;
	}

	@Override
	public void stopExecute() {
		// Not implemented
	}
}
