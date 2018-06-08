package com.neotys.amqp.deleteexchange;

import com.neotys.action.argument.Arguments;
import com.neotys.action.argument.Option;
import com.neotys.amqp.common.AMQPAction;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AMQPDeleteExchangeAction extends AMQPAction {

	private static final String TYPE = "amqp-delete-exchange";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public List<ActionParameter> getDefaultActionParameters() {
		final ArrayList<ActionParameter> parameters = new ArrayList<>();

		for (final AMQPDeleteExchangeParameter parameter : AMQPDeleteExchangeParameter.values()) {
			if (Option.AppearsByDefault.True.equals(parameter.getOption().getAppearsByDefault())) {
				parameters.add(new ActionParameter(parameter.getOption().getName(), parameter.getOption().getDefaultValue(),
						parameter.getOption().getType()));
			}
		}

		return parameters;
	}

	@Override
	public Class<? extends ActionEngine> getEngineClass() {
		return AMQPDeleteExchangeActionEngine.class;
	}

	private static final ImageIcon LOGO_ICON;

	static {
		// TODO find icon
		final URL iconURL = AMQPDeleteExchangeAction.class.getResource(TYPE + ".png");
		if (iconURL != null) {
			LOGO_ICON = new ImageIcon(iconURL);
		} else {
			LOGO_ICON = null;
		}
	}

	@Override
	public Icon getIcon() {
		return LOGO_ICON;
	}

	@Override
	public boolean getDefaultIsHit() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Publish a message on an AMQP channel.\n" + Arguments.getArgumentDescriptions(AMQPDeleteExchangeParameter.getOptions());
	}
}