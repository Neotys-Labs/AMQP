package com.neotys.amqp.declarequeue;

import com.neotys.action.argument.ArgumentValidator;
import com.neotys.action.argument.Option;
import com.neotys.action.argument.Option.AppearsByDefault;
import com.neotys.action.argument.Option.OptionalRequired;
import com.neotys.amqp.common.AMQPParameterOption;
import com.neotys.extensions.action.ActionParameter.Type;

import java.util.Arrays;

import static com.neotys.action.argument.DefaultArgumentValidator.*;
import static com.neotys.action.argument.Option.AppearsByDefault.False;
import static com.neotys.action.argument.Option.AppearsByDefault.True;
import static com.neotys.action.argument.Option.OptionalRequired.Optional;
import static com.neotys.action.argument.Option.OptionalRequired.Required;
import static com.neotys.extensions.action.ActionParameter.Type.TEXT;

/**
 * 
 * @author srichert
 * @date 4 juin 2018
 */
enum AMQPDeclareQueueParameter {

	CHANNELNAME("channelName", Required, True, TEXT, "myAMQPChannel", "Name of the AMQP channel.", NON_EMPTY),

	QUEUENAME("queueName", Optional, True, TEXT, "myAMQPQueue", "Name of the AMQP queue to declare. If not provided, a queue with a generated name will be created.", ALWAYS_VALID),
	EXCHANGENAME("exchangeName", Required, True, TEXT, "myAMQPExchange", "Name of the AMQP exchange where the queue will be bind.", NON_EMPTY),
	ROUTINGKEY("routingKey", Optional, True, TEXT, "my.routing.key", "AMQP routing key where the queue will be bind.", ALWAYS_VALID),

	DURABLE("durable", Optional, False, TEXT, "", "If set to true, the created queue will be durable. Default value is false.", BOOLEAN_VALIDATOR),
	EXCLUSIVE("exclusive", Optional, False, TEXT, "", "If set to true, the created queue will be exclusive. Default value is false.", BOOLEAN_VALIDATOR),
	AUTODELETE("autoDelete", Optional, False, TEXT, "", "If set to true, the created queue will be auto deleted if not used. Default value is true.", BOOLEAN_VALIDATOR),
	ARGUMENTS("arguments", Optional, False, TEXT, "", "The arguments used to create the queue. An argument must follow the pattern name=[class]value separated by '\\n'. Example : size=[java.lang.Integer]150. One line per argument.", ALWAYS_VALID);

	private final AMQPParameterOption option;
	
	AMQPDeclareQueueParameter(final String name, final OptionalRequired optionalRequired,
							  final AppearsByDefault appearsByDefault,
							  final Type type, final String defaultValue, final String description,
							  final ArgumentValidator argumentValidator){
		this.option = new AMQPParameterOption(name, optionalRequired, appearsByDefault, type, defaultValue, description, argumentValidator);
	}

	public AMQPParameterOption getOption() {
		return option;
	}
	
	public static Option[] getOptions() {
		return Arrays.stream(AMQPDeclareQueueParameter.values()).map(AMQPDeclareQueueParameter::getOption).toArray(Option[]::new);
	}

}
