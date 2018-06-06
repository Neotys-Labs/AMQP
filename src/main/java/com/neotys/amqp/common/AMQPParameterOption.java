package com.neotys.amqp.common;

import com.neotys.action.argument.ArgumentValidator;
import com.neotys.action.argument.Option;
import com.neotys.extensions.action.ActionParameter.Type;

public class AMQPParameterOption implements Option {

	private final String name;
	private final OptionalRequired optionalRequired;
	private final AppearsByDefault appearsByDefault;
	private final Type type;
	private final String defaultValue;
	private final String description;
	private final ArgumentValidator argumentValidator;

	/**
	 * @param appearsByDefault Whether or not the parameter is included in the description (whether the user can see it or not).
	 */
	public AMQPParameterOption(final String name, final OptionalRequired optionalRequired,
			final AppearsByDefault appearsByDefault,
			final Type type, final String defaultValue, final String description,
			final ArgumentValidator argumentValidator) {
		this.name = name;
		this.optionalRequired = optionalRequired;
		this.appearsByDefault = appearsByDefault;
		this.type = type;
		this.defaultValue = defaultValue;
		this.description = description;
		this.argumentValidator = argumentValidator;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public OptionalRequired getOptionalRequired() {
		return optionalRequired;
	}

	@Override
	public AppearsByDefault getAppearsByDefault() {
		return appearsByDefault;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public ArgumentValidator getArgumentValidator() {
		return argumentValidator;
	}

}
