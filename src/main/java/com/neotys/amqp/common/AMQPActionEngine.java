package com.neotys.amqp.common;

import com.neotys.action.result.ResultFactory;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.SampleResult;

public abstract class AMQPActionEngine implements ActionEngine {

	protected static final String AMQP_CONNECTION_KEY = "AMQPConnectionKey";
	
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
