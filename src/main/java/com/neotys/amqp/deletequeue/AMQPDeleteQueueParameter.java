package com.neotys.amqp.deletequeue;

import com.neotys.action.argument.ArgumentValidator;
import com.neotys.action.argument.Option;
import com.neotys.action.argument.Option.AppearsByDefault;
import com.neotys.action.argument.Option.OptionalRequired;
import com.neotys.amqp.common.AMQPParameterOption;
import com.neotys.extensions.action.ActionParameter.Type;

import java.util.Arrays;

import static com.neotys.action.argument.DefaultArgumentValidator.NON_EMPTY;
import static com.neotys.action.argument.Option.AppearsByDefault.True;
import static com.neotys.action.argument.Option.OptionalRequired.Required;
import static com.neotys.extensions.action.ActionParameter.Type.TEXT;

/**
 * 
 * @author srichert
 * @date 4 juin 2018
 */
enum AMQPDeleteQueueParameter {

	CHANNELNAME("channelName", Required, True, TEXT, "myAMQPChannel", "Name of the AMQP channel.", NON_EMPTY),
	QUEUENAME("queueName", Required, True, TEXT, "myAMQPQueue", "Name of the AMQP queue to delete.", NON_EMPTY);

	private final AMQPParameterOption option;
	
	AMQPDeleteQueueParameter(final String name, final OptionalRequired optionalRequired,
							 final AppearsByDefault appearsByDefault,
							 final Type type, final String defaultValue, final String description,
							 final ArgumentValidator argumentValidator){
		this.option = new AMQPParameterOption(name, optionalRequired, appearsByDefault, type, defaultValue, description, argumentValidator);
	}

	public AMQPParameterOption getOption() {
		return option;
	}
	
	public static Option[] getOptions() {
		return Arrays.stream(AMQPDeleteQueueParameter.values()).map(AMQPDeleteQueueParameter::getOption).toArray(Option[]::new);
	}

}
