package com.neotys.amqp.common;

import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.SampleResult;
import com.neotys.jms.JmsResultFactory;

public abstract class AMQPActionEngine implements ActionEngine {

	public static final String AMQP_CONNECTION_KEY = "AMQPConnectionKey";
	
	protected static SampleResult newErrorResult(final Context context, final String requestContent, final String statusCode,
			final String statusMessage, final Exception e) {
		final SampleResult result = JmsResultFactory.newErrorResult(context, statusCode, statusMessage, e);
		result.setRequestContent(requestContent);
		return result;
	}

	protected static SampleResult newOkResult(final Context context, final String requestContent, final String statusMessage) {
		final SampleResult result = JmsResultFactory.newOkResult(context, statusMessage);
		result.setRequestContent(requestContent);
		return result;
	}

	protected static SampleResult newErrorResult(final Context context, final String requestContent, final String statusCode,
			final String statusMessage) {
		final SampleResult result = JmsResultFactory.newErrorResult(context, statusCode, statusMessage);
		result.setRequestContent(requestContent);
		return result;
	}

	@Override
	public void stopExecute() {
		// Not implemented
	}
}
