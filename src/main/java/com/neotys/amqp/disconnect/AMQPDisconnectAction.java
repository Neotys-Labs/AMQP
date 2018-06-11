package com.neotys.amqp.disconnect;

import java.util.ArrayList;
import java.util.List;

import com.neotys.action.argument.Arguments;
import com.neotys.action.argument.Option.AppearsByDefault;
import com.neotys.amqp.common.AMQPAction;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;

public final class AMQPDisconnectAction extends AMQPAction {

	private static final String TYPE = "amqp-disconnect"; 

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public List<ActionParameter> getDefaultActionParameters() {
		final ArrayList<ActionParameter> parameters = new ArrayList<>();

		for (final AMQPDisconnectParameter parameter : AMQPDisconnectParameter.values()) {
			if (AppearsByDefault.True.equals(parameter.getOption().getAppearsByDefault())) {
				parameters.add(new ActionParameter(parameter.getOption().getName(), parameter.getOption().getDefaultValue(),
						parameter.getOption().getType()));
			}
		}

		return parameters;
	}

	@Override
	public Class<? extends ActionEngine> getEngineClass() {
		return AMQPDisconnectActionEngine.class;
	}

	@Override
	public String getDescription() {
		return "Close all AMQP channel and disconnect from AMQP server.\n" + Arguments.getArgumentDescriptions(AMQPDisconnectParameter.getOptions());
	}
}
