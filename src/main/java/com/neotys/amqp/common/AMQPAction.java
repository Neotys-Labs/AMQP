package com.neotys.amqp.common;

import com.google.common.base.Optional;
import com.neotys.extensions.action.Action;

public abstract class AMQPAction implements Action{

	protected static final String BUNDLE_NAME = "com.neotys.amqp.bundle";
	
	private final Optional<String> MINIMUM_NEOLOAD_VERSION = Optional.of("6.4");// TODO seb 6.6
	private final Optional<String> MAXIMUM_NEOLOAD_VERSION = Optional.absent();
	
	@Override
	public final Optional<String> getMinimumNeoLoadVersion() {
		return MINIMUM_NEOLOAD_VERSION;
	}

	@Override
	public final Optional<String> getMaximumNeoLoadVersion() {
		return MAXIMUM_NEOLOAD_VERSION;
	}
}
