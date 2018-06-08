package com.neotys.amqp.consume;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.neotys.action.argument.Arguments;
import com.neotys.action.argument.Option;
import com.neotys.amqp.common.AMQPAction;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;

public class AMQPConsumeAction extends AMQPAction {

	private static final String TYPE = "amqp-consume"; 
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public List<ActionParameter> getDefaultActionParameters() {
		final ArrayList<ActionParameter> parameters = new ArrayList<>();

		for (final AMQPConsumeParameter parameter : AMQPConsumeParameter.values()) {
			if (Option.AppearsByDefault.True.equals(parameter.getOption().getAppearsByDefault())) {
				parameters.add(new ActionParameter(parameter.getOption().getName(), parameter.getOption().getDefaultValue(),
						parameter.getOption().getType()));
			}
		}

		return parameters;
	}

	@Override
	public Class<? extends ActionEngine> getEngineClass() {
		return AMQPConsumeActionEngine.class;
	}

	private static final ImageIcon LOGO_ICON;

	static {
		final URL iconURL = AMQPConsumeAction.class.getResource(TYPE + ".png");
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
		return "Publish a message on an AMQP channel.\n" + Arguments.getArgumentDescriptions(AMQPConsumeParameter.getOptions());
	}
}
