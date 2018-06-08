package com.neotys.amqp.declareexchange;

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
enum AMQPDeclareExchangeParameter {

	CHANNELNAME("channelName", Required, True, TEXT, "myAMQPChannel", "The name of the AMQP channel.", NON_EMPTY),
	EXCHANGENAME("exchangeName", Required, True, TEXT, "myAMQPExchange", "The AMQP exchange to declare.", NON_EMPTY),

	TYPE("type", Optional, False, TEXT, "", "The type of the created exchange. Default value is  \"direct\".", ALWAYS_VALID),
	DURABLE("durable", Optional, False, TEXT, "", "If set to true, the created exchange will be durable. Default value is false.", BOOLEAN_VALIDATOR),
	AUTODELETE("autoDelete", Optional, False, TEXT, "", "If set to true, the created exchange will be auto deleted if not used. Default value is false.", BOOLEAN_VALIDATOR),
	ARGUMENTS("arguments", Optional, False, TEXT, "", "The arguments used to create the exchange. An argument must follow the pattern name=[class]value separated by '\\n'. Example : size=[java.lang.Integer]150. One line per argument.", ALWAYS_VALID);

	private final AMQPParameterOption option;

	AMQPDeclareExchangeParameter(final String name, final OptionalRequired optionalRequired,
								 final AppearsByDefault appearsByDefault,
								 final Type type, final String defaultValue, final String description,
								 final ArgumentValidator argumentValidator) {
		this.option = new AMQPParameterOption(name, optionalRequired, appearsByDefault, type, defaultValue, description, argumentValidator);
	}

	public AMQPParameterOption getOption() {
		return option;
	}

	public static Option[] getOptions() {
		return Arrays.stream(AMQPDeclareExchangeParameter.values()).map(AMQPDeclareExchangeParameter::getOption).toArray(Option[]::new);
	}

}
