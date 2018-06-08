package com.neotys.amqp.publish;

import java.util.ArrayList;
import java.util.List;

import com.neotys.action.argument.Arguments;
import com.neotys.action.argument.Option;
import com.neotys.amqp.common.AMQPAction;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;

public class AMQPPublishAction extends AMQPAction {

	private static final String TYPE = "amqp-publish"; 
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public List<ActionParameter> getDefaultActionParameters() {
		final ArrayList<ActionParameter> parameters = new ArrayList<>();

		for (final AMQPPublishParameter parameter : AMQPPublishParameter.values()) {
			if (Option.AppearsByDefault.True.equals(parameter.getOption().getAppearsByDefault())) {
				parameters.add(new ActionParameter(parameter.getOption().getName(), parameter.getOption().getDefaultValue(),
						parameter.getOption().getType()));
			}
		}

		return parameters;
	}
	
	@Override
	public Class<? extends ActionEngine> getEngineClass() {
		return AMQPPublishActionEngine.class;
	}
		
	@Override
	public boolean getDefaultIsHit() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Publish a message on an AMQP channel.\n" + Arguments.getArgumentDescriptions(AMQPPublishParameter.getOptions());
	}
}
