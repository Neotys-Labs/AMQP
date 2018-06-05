package com.neotys.amqp.connect;
import static com.neotys.action.argument.DefaultArgumentValidator.ALWAYS_VALID;
import static com.neotys.action.argument.DefaultArgumentValidator.INTEGER_VALIDATOR;
import static com.neotys.action.argument.DefaultArgumentValidator.NON_EMPTY;
import static com.neotys.action.argument.Option.AppearsByDefault.False;
import static com.neotys.action.argument.Option.AppearsByDefault.True;
import static com.neotys.action.argument.Option.OptionalRequired.Optional;
import static com.neotys.action.argument.Option.OptionalRequired.Required;
import static com.neotys.extensions.action.ActionParameter.Type.TEXT;

import java.util.Arrays;

import com.neotys.action.argument.ArgumentValidator;
import com.neotys.action.argument.Option;
import com.neotys.action.argument.Option.AppearsByDefault;
import com.neotys.action.argument.Option.OptionalRequired;
import com.neotys.amqp.common.AMQPParameterOption;
import com.neotys.extensions.action.ActionParameter.Type;

/**
 * 
 * @author srichert
 * @date 4 juin 2018
 */
enum AMQPConnectParameter {

	CHANNELNAME("channelName", Required, True, TEXT, "myAMQPChannel", "Name of the AMQP channel to be refereced in further usage", NON_EMPTY),
	HOSTNAME("hostname", Required, True, TEXT, "localhost", "AMQP server hostname or IP address", NON_EMPTY),
	PORT("port", Required, True, TEXT, "5672", "AMQP server port", INTEGER_VALIDATOR),	
	USERNAME("username", Optional, False, TEXT, "", "Username to connect to the AMQP server", ALWAYS_VALID),
	PASSWORD("password", Optional, False, TEXT, "", "Password to connect to the AMQP server", ALWAYS_VALID),
	VIRTUALHOST("virtualHost", Optional, False, TEXT, "/", "Virtual host", ALWAYS_VALID);

	private final AMQPParameterOption option;
	
	AMQPConnectParameter(final String name, final OptionalRequired optionalRequired,
			final AppearsByDefault appearsByDefault,
			final Type type, final String defaultValue, final String description,
			final ArgumentValidator argumentValidator){
		this.option = new AMQPParameterOption(name, optionalRequired, appearsByDefault, type, defaultValue, description, argumentValidator);
	}

	public AMQPParameterOption getOption() {
		return option;
	}
	
	public static Option[] getOptions() {
		return Arrays.stream(AMQPConnectParameter.values()).map(AMQPConnectParameter::getOption).toArray(Option[]::new);
	}

}