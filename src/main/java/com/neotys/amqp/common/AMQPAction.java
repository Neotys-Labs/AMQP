package com.neotys.amqp.common;

import java.util.Locale;
import java.util.ResourceBundle;

import com.google.common.base.Optional;
import com.neotys.extensions.action.Action;

public abstract class AMQPAction implements Action{

	private static final String BUNDLE_NAME = "com.neotys.amqp.common.bundle";
	private static final String DISPLAY_PATH = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("amqp.displayPath");
	
	private static final Optional<String> MINIMUM_NEOLOAD_VERSION = Optional.of("6.6");
	private static final Optional<String> MAXIMUM_NEOLOAD_VERSION = Optional.absent();
	
	@Override
	public final Optional<String> getMinimumNeoLoadVersion() {
		return MINIMUM_NEOLOAD_VERSION;
	}

	@Override
	public final Optional<String> getMaximumNeoLoadVersion() {
		return MAXIMUM_NEOLOAD_VERSION;
	}
	
	@Override
	public final String getDisplayPath() {
		return DISPLAY_PATH;
	}
	
	@Override
	public final String getDisplayName() {
		return ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString(getType() + ".displayName");
	}
}
