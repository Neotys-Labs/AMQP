package com.neotys.amqp.publish;

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
 * @author srichert
 * @date 4 juin 2018
 */
enum AMQPPublishParameter {

	CHANNELNAME("channelName", Required, True, TEXT, "myAMQPChannel", "The name of the AMQP channel.", NON_EMPTY),
	EXCHANGE("exchange", Required, True, TEXT, "myExchange", "The AMQP exchange where the message will be published.", NON_EMPTY),
	ROUTINGKEY("routingKey", Required, True, TEXT, "my.routing.key", "The AMQP routing key.", NON_EMPTY),

	// TODO handle theses parameters
	DECLAREEXCHANGE("declareExchange", Optional, False, TEXT, "", "If set to true, the exchange will be created.", BOOLEAN_VALIDATOR),
	EXCHANGETYPE("exchange.type", Optional, False, TEXT, "", "The type of the created exchange. Default value is  \'direct\'", ALWAYS_VALID),
	EXCHANGEDURABLE("exchange.durable", Optional, False, TEXT, "", "If set to true, the created exchange will be durable. Default value is false.", BOOLEAN_VALIDATOR),
	EXCHANGEXCLUSIVE("exchange.exclusive", Optional, False, TEXT, "", "If set to true, the created exchange will be exclusive. Default value is false.", BOOLEAN_VALIDATOR),
	EXCHANGEAUTODELETE("exchange.autoDelete", Optional, False, TEXT, "", "If set to true, the created exchange will be auto deleted if not used. Default value is true.", BOOLEAN_VALIDATOR),
	EXCHANGEARGUMENTS("exchange.arguments", Optional, False, TEXT, "", "The arguments used to create the exchange. An argument must follow the pattern name=[class]value separated by '\\n'. Example : size=[java.lang.Integer]150. One line per argument.", ALWAYS_VALID),
	EXCHANGEDELETE("exchange.delete", Optional, False, TEXT, "", "If set to true, the exchange queue will be deleted between each iteration. Default value is false.", BOOLEAN_VALIDATOR),
	EXCHANGEDECLARE("exchange.redeclare", Optional, False, TEXT, "", "If set to true, the exchange queue will be redeclared in each iteration. Default value is false.", BOOLEAN_VALIDATOR),

	TEXTCONTENT("textContent", Optional, True, TEXT, "", "The message content.", ALWAYS_VALID),
	FILEPATH("contentFile.path", Optional, True, TEXT, "", "The path of the content file. Use the variable \'${NL-CustomResources}\' to access on the Load Generator the synchronized resources located in the \'custom-resources\' folder of the project..", ALWAYS_VALID),
	FILECHARSET("contentFile.charset", Optional, False, TEXT, "", "The charset of the file.", ALWAYS_VALID),
	PARSEFILE("parseFile", Optional, False, TEXT, "", "Whether to parse the file to replace variables. Default value is false.", ALWAYS_VALID),

	CONTENTTYPE("contentType", Optional, False, TEXT, "", "The message content type.", ALWAYS_VALID),
	CONTENTENCODING("contentEncoding", Optional, False, TEXT, "", "the message content encoding.", ALWAYS_VALID),
	HEADERS("headers", Optional, False, TEXT, "", "You can setup the name, the value and the class (type) of a header. Default type is String. The header must follow the pattern name=[class]value separated by '\\n'. Example : size=[java.lang.Integer]150. One line per header.", ALWAYS_VALID),
	PERSISTENT("persistent", Optional, False, TEXT, "", "Whether the message will be persisted. Default value is false.", BOOLEAN_VALIDATOR),
	PRIORITY("priority", Optional, False, TEXT, "", "The message priority. Default value is 0.", INTEGER_VALIDATOR),
	CORRELATIONID("correlationId", Optional, False, TEXT, "", "The message correlation ID.", ALWAYS_VALID),
	REPLYTO("replyTo", Optional, False, TEXT, "", "The message queue to reply to.", ALWAYS_VALID),
	EXPIRATION("expiration", Optional, False, TEXT, "", "The message expiration.", ALWAYS_VALID),
	MESSAGEID("messageId", Optional, False, TEXT, "", "The message ID.", ALWAYS_VALID),
	TIMESTAMP("timestamp", Optional, False, TEXT, "", "The message timestamp as a long specifying the number of milliseconds since the standard base time known as \'the epoch\'.", LONG_VALIDATOR),
	TYPE("type", Optional, False, TEXT, "", "The message type.", ALWAYS_VALID),
	USERID("userId", Optional, False, TEXT, "", "The message user ID.", ALWAYS_VALID),
	APPID("appId", Optional, False, TEXT, "", "The message app ID.", ALWAYS_VALID),
	CLUSTERID("clusterId", Optional, False, TEXT, "", "The message cluster ID.", ALWAYS_VALID);

	private final AMQPParameterOption option;

	AMQPPublishParameter(final String name, final OptionalRequired optionalRequired,
						 final AppearsByDefault appearsByDefault,
						 final Type type, final String defaultValue, final String description,
						 final ArgumentValidator argumentValidator) {
		this.option = new AMQPParameterOption(name, optionalRequired, appearsByDefault, type, defaultValue, description, argumentValidator);
	}

	public AMQPParameterOption getOption() {
		return option;
	}

	public static Option[] getOptions() {
		return Arrays.stream(AMQPPublishParameter.values()).map(AMQPPublishParameter::getOption).toArray(Option[]::new);
	}

}
