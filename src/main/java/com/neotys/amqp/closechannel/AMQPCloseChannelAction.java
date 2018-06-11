package com.neotys.amqp.closechannel;

import java.util.ArrayList;
import java.util.List;

import com.neotys.action.argument.Arguments;
import com.neotys.action.argument.Option.AppearsByDefault;
import com.neotys.amqp.common.AMQPAction;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;

public final class AMQPCloseChannelAction extends AMQPAction {

	private static final String TYPE = "amqp-close-channel"; 
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public List<ActionParameter> getDefaultActionParameters() {
		final ArrayList<ActionParameter> parameters = new ArrayList<>();
		for (final AMQPCloseChannelParameter parameter : AMQPCloseChannelParameter.values()) {
			if (AppearsByDefault.True.equals(parameter.getOption().getAppearsByDefault())) {
				parameters.add(new ActionParameter(parameter.getOption().getName(), parameter.getOption().getDefaultValue(),
						parameter.getOption().getType()));
			}
		}

		return parameters;
	}

	@Override
	public Class<? extends ActionEngine> getEngineClass() {
		return AMQPCloseChannelActionEngine.class;
	}

	@Override
	public String getDescription() {
		return "Close the AMQP channel.\n" + Arguments.getArgumentDescriptions(AMQPCloseChannelParameter.getOptions());
	}
}
