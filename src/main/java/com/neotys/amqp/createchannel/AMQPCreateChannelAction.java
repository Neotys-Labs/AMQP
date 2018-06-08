package com.neotys.amqp.createchannel;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.neotys.action.argument.Arguments;
import com.neotys.action.argument.Option.AppearsByDefault;
import com.neotys.amqp.common.AMQPAction;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;

public final class AMQPCreateChannelAction extends AMQPAction {

	private static final String TYPE = "amqp-create-channel"; 

	@Override
	public String getType() {
		return TYPE;
	}

	private static final ImageIcon LOGO_ICON;

	static {
		final URL iconURL = AMQPCreateChannelAction.class.getResource(TYPE + ".png");
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
	public List<ActionParameter> getDefaultActionParameters() {
		final ArrayList<ActionParameter> parameters = new ArrayList<>();
		for (final AMQPCreateChannelParameter parameter : AMQPCreateChannelParameter.values()) {
			if (AppearsByDefault.True.equals(parameter.getOption().getAppearsByDefault())) {
				parameters.add(new ActionParameter(parameter.getOption().getName(), parameter.getOption().getDefaultValue(),
						parameter.getOption().getType()));
			}
		}
		return parameters;
	}

	@Override
	public Class<? extends ActionEngine> getEngineClass() {
		return AMQPCreateChannelActionEngine.class;
	}

	@Override
	public boolean getDefaultIsHit(){
		return false;
	}

	@Override
	public String getDescription() {
		return "Create a new channel on an AMQP connection.\n" + Arguments.getArgumentDescriptions(AMQPCreateChannelParameter.getOptions());
	}
}
