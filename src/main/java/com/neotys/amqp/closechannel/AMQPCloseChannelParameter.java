package com.neotys.amqp.closechannel;
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
enum AMQPCloseChannelParameter {

	CHANNELNAME("channelName", Required, True, TEXT, "myAMQPChannel", "Name of the AMQP channel to close", NON_EMPTY);	

	private final AMQPParameterOption option;
	
	AMQPCloseChannelParameter(final String name, final OptionalRequired optionalRequired,
			final AppearsByDefault appearsByDefault,
			final Type type, final String defaultValue, final String description,
			final ArgumentValidator argumentValidator){
		this.option = new AMQPParameterOption(name, optionalRequired, appearsByDefault, type, defaultValue, description, argumentValidator);
	}

	public AMQPParameterOption getOption() {
		return option;
	}
	
	public static Option[] getOptions() {
		return Arrays.stream(AMQPCloseChannelParameter.values()).map(AMQPCloseChannelParameter::getOption).toArray(Option[]::new);
	}

}