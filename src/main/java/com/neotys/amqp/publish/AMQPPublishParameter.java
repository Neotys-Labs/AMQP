package com.neotys.amqp.publish;

import com.neotys.action.argument.ArgumentValidator;
import com.neotys.action.argument.Option;
import com.neotys.action.argument.Option.AppearsByDefault;
import com.neotys.action.argument.Option.OptionalRequired;
import com.neotys.amqp.common.AMQPParameterOption;
import com.neotys.extensions.action.ActionParameter.Type;

import java.util.Arrays;

import static com.neotys.action.argument.DefaultArgumentValidator.ALWAYS_VALID;
import static com.neotys.action.argument.DefaultArgumentValidator.NON_EMPTY;
import static com.neotys.action.argument.Option.AppearsByDefault.True;
import static com.neotys.action.argument.Option.OptionalRequired.Optional;
import static com.neotys.action.argument.Option.OptionalRequired.Required;
import static com.neotys.extensions.action.ActionParameter.Type.TEXT;

/**
 * 
 * @author srichert
 * @date 4 juin 2018
 */
enum AMQPPublishParameter {

	CHANNELNAME("channelName", Required, True, TEXT, "myAMQPChannel", "the name of the AMQP channel.", NON_EMPTY),
	EXCHANGE("exchange", Required, True, TEXT, "myExchange", "the AMQP exchange where the message will be published.", NON_EMPTY),
	ROUTINGKEY("routingKey", Required, True, TEXT, "my.routing.key", "the AMQP routing key.", NON_EMPTY),
	TEXTCONTENT("textContent", Optional, True, TEXT, "", "the message content.", ALWAYS_VALID),
	CONTENTTYPE("contentType", Optional, True, TEXT, "text/plain", "the message content type.", ALWAYS_VALID);

	private final AMQPParameterOption option;
	
	AMQPPublishParameter(final String name, final OptionalRequired optionalRequired,
						 final AppearsByDefault appearsByDefault,
						 final Type type, final String defaultValue, final String description,
						 final ArgumentValidator argumentValidator){
		this.option = new AMQPParameterOption(name, optionalRequired, appearsByDefault, type, defaultValue, description, argumentValidator);
	}

	public AMQPParameterOption getOption() {
		return option;
	}
	
	public static Option[] getOptions() {
		return Arrays.stream(AMQPPublishParameter.values()).map(AMQPPublishParameter::getOption).toArray(Option[]::new);
	}

}
