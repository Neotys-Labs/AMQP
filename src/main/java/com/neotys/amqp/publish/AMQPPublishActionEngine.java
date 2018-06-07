package com.neotys.amqp.publish;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.neotys.amqp.common.AMQPActionEngine;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.Logger;
import com.neotys.extensions.action.engine.SampleResult;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import java.io.*;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.neotys.action.argument.Arguments.getArgumentLogString;
import static com.neotys.action.argument.Arguments.parseArguments;

public final class AMQPPublishActionEngine extends AMQPActionEngine {

	private static final String STATUS_CODE_INVALID_PARAMETER = "NL-AMQP-PUBLISH-ACTION-01";
	private static final String STATUS_CODE_ERROR_PUBLISH = "NL-AMQP-PUBLISH-ACTION-02";

	static final boolean DEFAULT_FILE_PARSE = false;
	
	@Override
	public SampleResult execute(final Context context, final List<ActionParameter> parameters) {
		final Map<String, Optional<String>> parsedArgs;
		try {
			parsedArgs = parseArguments(parameters, AMQPPublishParameter.getOptions());
			validateContentParameters(parsedArgs);
		} catch (final IllegalArgumentException iae) {
			return newErrorResult(context, "Executing AMQP Publish action.", STATUS_CODE_INVALID_PARAMETER, "Could not parse arguments: ", iae);
		}
		final String request = "Executing AMQP Publish action with parameters: " + getArgumentLogString(parsedArgs, AMQPPublishParameter.getOptions())+ ".";
		final Logger logger = context.getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug(request);
		}

		final String channelName = parsedArgs.get(AMQPPublishParameter.CHANNELNAME.getOption().getName()).get();
		final Object cachedChannel = context.getCurrentVirtualUser().get(channelName);
		if (!(cachedChannel instanceof Channel)) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_PUBLISH, "Connection to channel " + channelName + " do not exist.");
		}
		final Channel channel = (Channel) cachedChannel;

		final String exchange = parsedArgs.get(AMQPPublishParameter.EXCHANGE.getOption().getName()).get();
		final String routingKey = parsedArgs.get(AMQPPublishParameter.ROUTINGKEY.getOption().getName()).get();
		final String contentType = parsedArgs.get(AMQPPublishParameter.CONTENTTYPE.getOption().getName()).or("");
		final AMQP.BasicProperties properties = getProperties(contentType);

		try {
			final String messageContent = getMessageContent(context, parsedArgs);
			final byte[] messageBytes = messageContent.getBytes();
			channel.basicPublish(exchange, routingKey, properties, messageBytes);
			return newOkResult(context, request, "Message published on channel " + channelName + ".");
		} catch (final IOException exception) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_PUBLISH, "Could not publish on channel ", exception);
		}
	}

	/**
	 * make sure that exactly one content is provided and that the file path is valid.
	 */
	// FIXME to put in common with JMS.
	private static void validateContentParameters(final Map<String, Optional<String>> parsedArgs) {
		final String textContentParameterName = AMQPPublishParameter.TEXTCONTENT.getOption().getName();
		final Optional<String> textValue = parsedArgs.get(textContentParameterName);
		final String filePathParameterName = AMQPPublishParameter.FILEPATH.getOption().getName();
		final Optional<String> filePath = parsedArgs.get(filePathParameterName);

		if (isAbsentOrEmpty(textValue) && isAbsentOrEmpty(filePath)) {
			throw new IllegalArgumentException("Missing message content in " + textContentParameterName + " or " + filePathParameterName);
		} else if (!isNullOrEmpty(textValue.or("")) && !isNullOrEmpty(filePath.or(""))) {
			throw new IllegalArgumentException("Two message content provided " + textContentParameterName + " and " + filePathParameterName
					+ ", only one is necessary.");
		}

		if (!isAbsentOrEmpty(filePath)) {
			final File f = new File(filePath.get());
			if (!f.exists() || f.isDirectory()) {
				throw new IllegalArgumentException("Invalid path in " + filePathParameterName + " : " + filePath.get() + ".");
			}
		}

	}

	/**
	 *
	 * @return true if value is absent or null or empty.
	 */
	// FIXME to put in common with JMS.
	private static boolean isAbsentOrEmpty(final Optional<String> value) {
		return !value.isPresent() || isNullOrEmpty(value.get());
	}

	// FIXME to put in common with JMS.
	private String getMessageContent(final Context context, final Map<String, Optional<String>> parsedArgs) throws IOException {
		final Optional<String> textContent = parsedArgs.get(AMQPPublishParameter.TEXTCONTENT.getOption().getName());
		if (textContent.isPresent() && !Strings.isNullOrEmpty(textContent.get())) {
			return textContent.get();
		}else {
			final String filePath = parsedArgs.get(AMQPPublishParameter.FILEPATH.getOption().getName()).get();
			final String content = readFile(filePath, parsedArgs.get(AMQPPublishParameter.FILECHARSET.getOption().getName()));
			final boolean parseFile = parsedArgs.get(AMQPPublishParameter.PARSEFILE.getOption().getName())
					.transform(Boolean::parseBoolean)
					.or(DEFAULT_FILE_PARSE);
			return parseFile ? context.getVariableManager().parseVariables(content) : content;
		}
	}

	/**
	 * Read the content of the file according to charset.
	 */
	// FIXME to put in common with JMS.
	private static String readFile(final String filePath, final Optional<String> charset) throws IOException {
		try (final InputStream is = new FileInputStream(filePath);
			 final InputStreamReader isr = getInputStreamReader(is, charset);
			 final BufferedReader buffReader = new BufferedReader(isr)) {
			final StringBuilder sb = new StringBuilder();
			String line;
			while ((line = buffReader.readLine()) != null) {
				if (sb.length() != 0) {
					sb.append('\n');
				}
				sb.append(line);
			}
			return sb.toString();
		}
	}

	/**
	 * Avoid to have a compilation warning on eclipse in a try with resources.
	 */
	// FIXME to put in common with JMS.
	private static InputStreamReader getInputStreamReader(final InputStream is, final Optional<String> charset) throws UnsupportedEncodingException {
		return charset.isPresent() ? new InputStreamReader(is, charset.get()) : new InputStreamReader(is);
	}

	private AMQP.BasicProperties getProperties(final String contentType) {
		final AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();

		// TODO handle more properties
//		final int deliveryMode = getPersistent() ? 2 : 1;

		builder.contentType(contentType);
//				.deliveryMode(deliveryMode)
//				.priority(0)
//				.correlationId(getCorrelationId())
//				.replyTo(getReplyToQueue())
//				.type(getMessageType())
//				.headers(prepareHeaders())
//				.build();
//		if (getMessageId() != null && !getMessageId().isEmpty()) {
//			builder.messageId(getMessageId());
//		}
		return builder.build();
	}
}
