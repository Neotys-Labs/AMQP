package com.neotys.amqp.disconnect;
import static com.neotys.action.argument.DefaultArgumentValidator.INTEGER_VALIDATOR;
import static com.neotys.action.argument.DefaultArgumentValidator.NON_EMPTY;
import static com.neotys.action.argument.Option.AppearsByDefault.*;
import static com.neotys.action.argument.Option.OptionalRequired.*;
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
enum AMQPDisconnectParameter {

	CONNECTIONNAME("connectionName", Required, True, TEXT, "myAMQPConnection", "Name of the AMQP connection to disconnect", NON_EMPTY),
	TIMEOUT("timeout", Optional, False, TEXT, "", "Set the shutdown timeout in milliseconds.", INTEGER_VALIDATOR);

	private final AMQPParameterOption option;
	
	AMQPDisconnectParameter(final String name, final OptionalRequired optionalRequired,
			final AppearsByDefault appearsByDefault,
			final Type type, final String defaultValue, final String description,
			final ArgumentValidator argumentValidator){
		this.option = new AMQPParameterOption(name, optionalRequired, appearsByDefault, type, defaultValue, description, argumentValidator);
	}

	public AMQPParameterOption getOption() {
		return option;
	}
	
	public static Option[] getOptions() {
		return Arrays.stream(AMQPDisconnectParameter.values()).map(AMQPDisconnectParameter::getOption).toArray(Option[]::new);
	}

}