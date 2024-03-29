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
 * @author srichert
 * @date 4 juin 2018
 */
enum AMQPConsumeParameter {

	CHANNELNAME("channelName", Required, True, TEXT, "myAMQPChannel", "Name of the AMQP channel.", NON_EMPTY),
	QUEUENAME("queueName", Required, True, TEXT, "myAMQPQueue", "Name of AMQP queue where the message will be consumed.", NON_EMPTY),

	TIMEOUT("timeout", Optional, True, TEXT, "2000", "Timeout (in ms) applied to wait a message. 0=none.", POSITIVE_LONG_VALIDATOR),
	FAILONTIMEOUT("failOnTimeout", Optional, False, TEXT, "true", "If set to true, the action fails when timeout is reached.", BOOLEAN_VALIDATOR),
	AUTOACK("autoAck", Optional, False, TEXT, "false", "If set to true, the server will consider messages acknowledged once delivered.", BOOLEAN_VALIDATOR),
	NACK("negativeAck", Optional, False, TEXT, "false", "If set to true, the received message will be rejected with a negative ack.", BOOLEAN_VALIDATOR),
	REQUEUE("requeue", Optional, False, TEXT, "false", "If set to true, the rejected message will be requeued.", BOOLEAN_VALIDATOR);

	private final AMQPParameterOption option;

	AMQPConsumeParameter(final String name, final OptionalRequired optionalRequired,
						 final AppearsByDefault appearsByDefault,
						 final Type type, final String defaultValue, final String description,
						 final ArgumentValidator argumentValidator) {
		this.option = new AMQPParameterOption(name, optionalRequired, appearsByDefault, type, defaultValue, description, argumentValidator);
	}

	public AMQPParameterOption getOption() {
		return option;
	}

	public static Option[] getOptions() {
		return Arrays.stream(AMQPConsumeParameter.values()).map(AMQPConsumeParameter::getOption).toArray(Option[]::new);
	}

}
