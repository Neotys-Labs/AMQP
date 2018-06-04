package com.neotys.amqp;
import static com.neotys.action.argument.DefaultArgumentValidator.ALWAYS_VALID;
import static com.neotys.action.argument.DefaultArgumentValidator.INTEGER_VALIDATOR;
import static com.neotys.action.argument.DefaultArgumentValidator.NON_EMPTY;
import static com.neotys.action.argument.Option.AppearsByDefault.False;
import static com.neotys.action.argument.Option.AppearsByDefault.True;
import static com.neotys.action.argument.Option.OptionalRequired.Optional;
import static com.neotys.action.argument.Option.OptionalRequired.Required;
import static com.neotys.extensions.action.ActionParameter.Type.TEXT;

import com.neotys.action.argument.ArgumentValidator;
import com.neotys.action.argument.Option;
import com.neotys.extensions.action.ActionParameter.Type;

/**
 * 
 * @author srichert
 * @date 4 juin 2018
 */
public enum AMQPConnectParameter implements Option {

	CHANNELNAME("channelName", Required, True, TEXT, "myAMQPChannel", "Name of the AMQP channel to be refereced in further usage", NON_EMPTY),
	HOSTNAME("hostname", Required, True, TEXT, "localhost", "AMQP server hostname or IP address", NON_EMPTY),
	PORT("port", Required, True, TEXT, "5672", "AMQP server port", INTEGER_VALIDATOR),	
	USERNAME("username", Optional, False, TEXT, "", "Username to connect to the AMQP server", ALWAYS_VALID),
	PASSWORD("password", Optional, False, TEXT, "", "Password to connect to the AMQP server", ALWAYS_VALID),
	VIRTUALHOST("virtualHost", Optional, False, TEXT, "/", "Virtual host", ALWAYS_VALID);

	private final String name;
	private final OptionalRequired optionalRequired;
	private final AppearsByDefault appearsByDefault;
	private final Type type;
	private final String defaultValue;
	private final String description;
	private final ArgumentValidator argumentValidator;

	/**
	 * @param name
	 * @param description
	 * @param required
	 * @param visible Whether or not the parameter is included in the description (whether the user can see it or not). 
	 */
	AMQPConnectParameter(final String name, final OptionalRequired optionalRequired,
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