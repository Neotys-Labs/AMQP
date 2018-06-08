package com.neotys.amqp.createchannel;
import static com.neotys.action.argument.DefaultArgumentValidator.NON_EMPTY;
import static com.neotys.action.argument.Option.AppearsByDefault.True;
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
enum AMQPCreateChannelParameter {

	CONNECTIONNAME("connectionName", Required, True, TEXT, "myAMQPConnection", "Name of the AMQP connection to use to create a channel.", NON_EMPTY),
	CHANNELNAME("channelName", Required, True, TEXT, "myAMQPChannel", "Name of the AMQP channel to be refereced for further usage.", NON_EMPTY);

	private final AMQPParameterOption option;
	
	AMQPCreateChannelParameter(final String name, final OptionalRequired optionalRequired,
			final AppearsByDefault appearsByDefault,
			final Type type, final String defaultValue, final String description,
			final ArgumentValidator argumentValidator){
		this.option = new AMQPParameterOption(name, optionalRequired, appearsByDefault, type, defaultValue, description, argumentValidator);
	}

	public AMQPParameterOption getOption() {
		return option;
	}
	
	public static Option[] getOptions() {
		return Arrays.stream(AMQPCreateChannelParameter.values()).map(AMQPCreateChannelParameter::getOption).toArray(Option[]::new);
	}

}