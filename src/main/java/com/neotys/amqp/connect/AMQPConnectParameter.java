package com.neotys.amqp.connect;
import static com.neotys.action.argument.DefaultArgumentValidator.ALWAYS_VALID;
import static com.neotys.action.argument.DefaultArgumentValidator.BOOLEAN_VALIDATOR;
import static com.neotys.action.argument.DefaultArgumentValidator.INTEGER_VALIDATOR;
import static com.neotys.action.argument.DefaultArgumentValidator.LONG_VALIDATOR;
import static com.neotys.action.argument.DefaultArgumentValidator.NON_EMPTY;
import static com.neotys.action.argument.Option.AppearsByDefault.False;
import static com.neotys.action.argument.Option.AppearsByDefault.True;
import static com.neotys.action.argument.Option.OptionalRequired.Optional;
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
enum AMQPConnectParameter {

	CONNECTIONNAME("connectionName", Required, True, TEXT, "myAMQPConnection", "Name of the AMQP connection to be refereced for further usage", NON_EMPTY),
	HOSTNAME("hostname", Required, True, TEXT, "localhost", "AMQP server hostname or IP address", NON_EMPTY),
	PORT("port", Required, True, TEXT, "5672", "AMQP server port", INTEGER_VALIDATOR),	
	USERNAME("username", Optional, False, TEXT, "", "Username to connect to the AMQP server.", ALWAYS_VALID),
	PASSWORD("password", Optional, False, TEXT, "", "Password to connect to the AMQP server.", ALWAYS_VALID),
	VIRTUALHOST("virtualHost", Optional, False, TEXT, "/", "Virtual host", ALWAYS_VALID),
	SSLPROTOCOL("sslProtocol", Optional, False, TEXT, "", "SSL protocol to use, e.g. TLSv1, TLSv1.2 or keep value empty for default SSL protocol.", ALWAYS_VALID),
	REQUESTEDCHANNELMAX("requestedChannelMax", Optional, False, TEXT, "", "Maximum channel number to ask for.", INTEGER_VALIDATOR),
	REQUESTEDFRAMEMAX("requestedFrameMax", Optional, False, TEXT, "", "Frame-max parameter to ask for (in bytes).", INTEGER_VALIDATOR),
	CONNECTIONTIMEOUT("connectionTimeout", Optional, False, TEXT, "", "Timeout setting for connection attempts (in milliseconds).", INTEGER_VALIDATOR),
	HANDSHAKETIMEOUT("handshakeTimeout", Optional, False, TEXT, "", "Set the AMQP0-9-1 protocol handshake timeout.", INTEGER_VALIDATOR),
	SHUTDOWNTIMEOUT("shutdownTimeout", Optional, False, TEXT, "", "Set the shutdown timeout.", INTEGER_VALIDATOR),
	TOPOLOGYRECOVERYENABLED("topologyRecoveryEnabled", Optional, False, TEXT, "", "Enables or disables topology recovery (true or false).", BOOLEAN_VALIDATOR),
	NETWORKRECOVERYINTERVAL("networkRecoveryInterval", Optional, False, TEXT, "5000", "Sets connection recovery interval (milliseconds).", LONG_VALIDATOR),
	CHANNELSHOULDCHECKRPCRESPONSETYPE("channelShouldCheckRpcResponseType", Optional, False, TEXT, "", "Define if the AMQP channel should check the RPC response type or not (true or false).", BOOLEAN_VALIDATOR),
	WORKPOOLTIMEOUT("workPoolTimeout", Optional, False, TEXT, "", "Timeout in milliseconds for work pool enqueueing. The WorkPool dispatches several types of responses from the broker (e.g. deliveries). A high-traffic client with slow consumers can exhaust the work pool and compromise the whole connection (by e.g. letting the broker saturate the receive TCP buffers). Setting a timeout would make the connection fail early and avoid hard-to-diagnose TCP connection failure. Note this shouldn't happen with clients that set appropriate QoS values.", INTEGER_VALIDATOR),
	CHANNELRPCTIMEOUT("channelRpcTimeout", Optional, False, TEXT, "", "Continuation timeout in milliseconds for RPC calls in channels.", INTEGER_VALIDATOR);	

	private final AMQPParameterOption option;
	
	AMQPConnectParameter(final String name, final OptionalRequired optionalRequired,
			final AppearsByDefault appearsByDefault,
			final Type type, final String defaultValue, final String description,
			final ArgumentValidator argumentValidator){
		this.option = new AMQPParameterOption(name, optionalRequired, appearsByDefault, type, defaultValue, description, argumentValidator);
	}

	public AMQPParameterOption getOption() {
		return option;
	}
	
	public static Option[] getOptions() {
		return Arrays.stream(AMQPConnectParameter.values()).map(AMQPConnectParameter::getOption).toArray(Option[]::new);
	}

}