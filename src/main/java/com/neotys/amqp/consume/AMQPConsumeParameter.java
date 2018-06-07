package com.neotys.amqp.consume;

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
enum AMQPConsumeParameter {

	CHANNELNAME("channelName", Required, True, TEXT, "myAMQPChannel", "the name of the AMQP channel.", NON_EMPTY),

	QUEUENAME("queueName", Optional, True, TEXT, "myQueueName", "the AMQP queue where the message will be consumed. If not provided, a queue with a generated name will be created.", ALWAYS_VALID),
	EXCHANGE("exchange", Optional, True, TEXT, "myExchange", "The AMQP exchange where the queue will be bind. If not provided, the queue must exist.", ALWAYS_VALID),
	ROUTINGKEY("routingKey", Optional, True, TEXT, "my.routing.key", "The AMQP routing key where the queue will be bind. If not provided, the queue must exist.", ALWAYS_VALID),

	// TODO handle theses parameters
	DECLAREQUEUE("declareQueue", Optional, False, TEXT, "", "If set to true, the queue will be created. Default value is false.", BOOLEAN_VALIDATOR),
	QUEUEDURABLE("queue.durable", Optional, False, TEXT, "", "If set to true, the created queue will be durable. Default value is false.", BOOLEAN_VALIDATOR),
	QUEUEEXCLUSIVE("queue.exclusive", Optional, False, TEXT, "", "If set to true, the created queue will be exclusive. Default value is false.", BOOLEAN_VALIDATOR),
	QUEUEAUTODELETE("queue.autoDelete", Optional, False, TEXT, "", "If set to true, the created queue will be auto deleted if not used. Default value is true.", BOOLEAN_VALIDATOR),
	QUEUEARGUMENTS("queue.arguments", Optional, False, TEXT, "", "The arguments used to create the queue. An argument must follow the pattern name=[class]value separated by '\\n'. Example : size=[java.lang.Integer]150. One line per argument.", ALWAYS_VALID),
	QUEUEDELETE("queue.delete", Optional, False, TEXT, "", "If set to true, the created queue will be deleted between each iteration. Default value is false.", BOOLEAN_VALIDATOR),
	QUEUEREDECLARE("queue.redeclare", Optional, False, TEXT, "", "If set to true, the created queue will be redeclared in each iteration. Default value is false.", BOOLEAN_VALIDATOR),

	// TODO handle theses parameters
	DECLAREEXCHANGE("declareExchange", Optional, False, TEXT, "", "If set to true, the exchange will be created.", BOOLEAN_VALIDATOR),
	EXCHANGETYPE("exchange.type", Optional, False, TEXT, "", "The type of the created exchange. Default value is  \'direct\'", ALWAYS_VALID),
	EXCHANGEDURABLE("exchange.durable", Optional, False, TEXT, "", "If set to true, the created exchange will be durable. Default value is false.", BOOLEAN_VALIDATOR),
	EXCHANGEXCLUSIVE("exchange.exclusive", Optional, False, TEXT, "", "If set to true, the created exchange will be exclusive. Default value is false.", BOOLEAN_VALIDATOR),
	EXCHANGEAUTODELETE("exchange.autoDelete", Optional, False, TEXT, "", "If set to true, the created exchange will be auto deleted if not used. Default value is true.", BOOLEAN_VALIDATOR),
	EXCHANGEARGUMENTS("exchange.arguments", Optional, False, TEXT, "", "The arguments used to create the exchange. An argument must follow the pattern name=[class]value separated by '\\n'. Example : size=[java.lang.Integer]150. One line per argument.", ALWAYS_VALID),
	EXCHANGEDELETE("exchange.delete", Optional, False, TEXT, "", "If set to true, the exchange queue will be deleted between each iteration. Default value is false.", BOOLEAN_VALIDATOR),
	EXCHANGEDECLARE("exchange.redeclare", Optional, False, TEXT, "", "If set to true, the exchange queue will be redeclared in each iteration. Default value is false.", BOOLEAN_VALIDATOR),

	TIMEOUT("timeout", Optional, True, TEXT, "2000", "Timeout (in ms) applied to wait a message. 0=none.", POSITIVE_LONG_VALIDATOR),
	FAILONTIMEOUT("failOnTimeout", Optional, False, TEXT, "true", "If set to true, the action fails when timeout is reached.", BOOLEAN_VALIDATOR),
	AUTOACK("autoAck", Optional, False, TEXT, "true", "If set to true, the server will consider messages acknowledged once delivered. Default value is false", BOOLEAN_VALIDATOR);

	private final AMQPParameterOption option;
	
	AMQPConsumeParameter(final String name, final OptionalRequired optionalRequired,
						 final AppearsByDefault appearsByDefault,
						 final Type type, final String defaultValue, final String description,
						 final ArgumentValidator argumentValidator){
		this.option = new AMQPParameterOption(name, optionalRequired, appearsByDefault, type, defaultValue, description, argumentValidator);
	}

	public AMQPParameterOption getOption() {
		return option;
	}
	
	public static Option[] getOptions() {
		return Arrays.stream(AMQPConsumeParameter.values()).map(AMQPConsumeParameter::getOption).toArray(Option[]::new);
	}

}
