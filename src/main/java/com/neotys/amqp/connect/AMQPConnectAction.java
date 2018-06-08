package com.neotys.amqp.connect;

import java.util.ArrayList;
import java.util.List;

import com.neotys.action.argument.Arguments;
import com.neotys.action.argument.Option.AppearsByDefault;
import com.neotys.amqp.common.AMQPAction;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;

public class AMQPConnectAction extends AMQPAction {

	private static final String TYPE = "amqp-connect"; 
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public List<ActionParameter> getDefaultActionParameters() {
		final ArrayList<ActionParameter> parameters = new ArrayList<>();
		for (final AMQPConnectParameter parameter : AMQPConnectParameter.values()) {
			if (AppearsByDefault.True.equals(parameter.getOption().getAppearsByDefault())) {
				parameters.add(new ActionParameter(parameter.getOption().getName(), parameter.getOption().getDefaultValue(),
						parameter.getOption().getType()));
			}
		}
		return parameters;
	}

	@Override
	public Class<? extends ActionEngine> getEngineClass() {
		return AMQPConnectActionEngine.class;
	}
	
	@Override
	public boolean getDefaultIsHit(){
		return false;
	}

	@Override
	public String getDescription() {
		return "Connects to an AMQP server to create a channel.\n" + Arguments.getArgumentDescriptions(AMQPConnectParameter.getOptions());
	}
}
