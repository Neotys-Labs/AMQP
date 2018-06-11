package com.neotys.amqp.connect;

import static com.neotys.action.argument.Arguments.getArgumentLogString;
import static com.neotys.action.argument.Arguments.parseArguments;
import static com.neotys.amqp.connect.AMQPConnectParameter.CHANNELRPCTIMEOUT;
import static com.neotys.amqp.connect.AMQPConnectParameter.CHANNELSHOULDCHECKRPCRESPONSETYPE;
import static com.neotys.amqp.connect.AMQPConnectParameter.CONNECTIONNAME;
import static com.neotys.amqp.connect.AMQPConnectParameter.CONNECTIONTIMEOUT;
import static com.neotys.amqp.connect.AMQPConnectParameter.CONSUMERTHREADPOOLSIZE;
import static com.neotys.amqp.connect.AMQPConnectParameter.DISABLENIO;
import static com.neotys.amqp.connect.AMQPConnectParameter.HANDSHAKETIMEOUT;
import static com.neotys.amqp.connect.AMQPConnectParameter.HOSTNAME;
import static com.neotys.amqp.connect.AMQPConnectParameter.NETWORKRECOVERYINTERVAL;
import static com.neotys.amqp.connect.AMQPConnectParameter.PASSWORD;
import static com.neotys.amqp.connect.AMQPConnectParameter.PORT;
import static com.neotys.amqp.connect.AMQPConnectParameter.REQUESTEDCHANNELMAX;
import static com.neotys.amqp.connect.AMQPConnectParameter.REQUESTEDFRAMEMAX;
import static com.neotys.amqp.connect.AMQPConnectParameter.SHUTDOWNTIMEOUT;
import static com.neotys.amqp.connect.AMQPConnectParameter.SSLPROTOCOL;
import static com.neotys.amqp.connect.AMQPConnectParameter.USERNAME;
import static com.neotys.amqp.connect.AMQPConnectParameter.VIRTUALHOST;
import static com.neotys.amqp.connect.AMQPConnectParameter.WORKPOOLTIMEOUT;

import java.io.File;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.neotys.amqp.common.AMQPActionEngine;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.Logger;
import com.neotys.extensions.action.engine.SampleResult;
import com.rabbitmq.client.ConnectionFactory;

public final class AMQPConnectActionEngine extends AMQPActionEngine {

	private static final String STATUS_CODE_INVALID_PARAMETER = "NL-AMQP-CONNECT-ACTION-01";
	private static final String STATUS_CODE_ERROR_CONNECTION = "NL-AMQP-CONNECT-ACTION-02";
	private static final String NEOLOAD_KEYSTORE_FORMAT = "PKCS12";
	private static final String NEOLOAD_CERTIFICATE_ALGORITHM = "SunX509";

	@Override
	public SampleResult execute(final com.neotys.extensions.action.engine.Context context, final List<ActionParameter> parameters) {

		final Map<String, com.google.common.base.Optional<String>> parsedArgs;
		try {
			parsedArgs = parseArguments(parameters, AMQPConnectParameter.getOptions());
		} catch (final IllegalArgumentException iae) {
			return newErrorResult(context, "Executing AMQP Connect action.", STATUS_CODE_INVALID_PARAMETER, "Could not parse arguments: ", iae);
		}
		final String request = "Executing AMQP Connect action with parameters: " + getArgumentLogString(parsedArgs, AMQPConnectParameter.getOptions())
				+ ".";
		final Logger logger = context.getLogger();
		if (logger.isDebugEnabled()) {
			logger.debug(request);
		}
		final String connectionName = getArgument(parsedArgs, CONNECTIONNAME).orElse("");
		if (AMQPActionEngine.getConnection(context, connectionName) != null) {
			return newErrorResult(context, request, STATUS_CODE_INVALID_PARAMETER,
					"A AMQP connection already exists with name " + connectionName + ".");
		}
		try {
			final ConnectionFactory connectionFactory = new ConnectionFactory();
			connectionFactory.setHost(getArgument(parsedArgs, HOSTNAME).orElse(""));
			connectionFactory.setPort(Integer.parseInt(getArgument(parsedArgs, PORT).orElse("")));
			getArgument(parsedArgs, USERNAME).ifPresent(connectionFactory::setUsername);
			getArgument(parsedArgs, PASSWORD).ifPresent(connectionFactory::setPassword);
			getArgument(parsedArgs, VIRTUALHOST).ifPresent(connectionFactory::setVirtualHost);
			final Optional<String> sslProtocol = getArgument(parsedArgs, SSLPROTOCOL);
			if (sslProtocol.isPresent()) {
				final String sslProtocolValue = sslProtocol.get();
				final String certificateName = context.getCertificateManager().getCertificateName();
				if (certificateName == null) {
					// No certificate specified in NeoLoad. Use default SSL.
					if ("".equals(sslProtocolValue)) {
						connectionFactory.useSslProtocol();
					} else {
						connectionFactory.useSslProtocol(sslProtocolValue);
					}
				} else {
					// Use certificate as specified in NeoLoad Certificates Manager*
					final char[] certificatePassword = context.getCertificateManager().getCertificatePassword().toCharArray();
					final String certificateFolder = context.getCertificateManager().getCertificateFolder();
					final String certificatePath = certificateFolder + File.separator + certificateName;
					final KeyStore keystore = KeyStore.getInstance(NEOLOAD_KEYSTORE_FORMAT);
					final InputStream certificateInputStream = context.getFileManager().getFileInputStream(certificatePath);
					keystore.load(certificateInputStream, certificatePassword);
					final KeyManagerFactory kmf = KeyManagerFactory.getInstance(NEOLOAD_CERTIFICATE_ALGORITHM);
					kmf.init(keystore, certificatePassword);
					final SSLContext sslContext = SSLContext.getInstance(sslProtocolValue);
					final TrustManager[] trustAllCerts = new TrustManager[] {
							new X509TrustManager() {
								@Override
								public X509Certificate[] getAcceptedIssuers() {
									return new X509Certificate[0];
								}
								@Override
								@SuppressWarnings("squid:S4424")
								public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
									// Trust all client certificates
								}
								@Override
								@SuppressWarnings("squid:S4424")
								public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
									// Trust all server certificates
								}
							}
					};
					sslContext.init(kmf.getKeyManagers(), trustAllCerts, new java.security.SecureRandom());
					connectionFactory.useSslProtocol(sslContext);
				}
			}
			getArgument(parsedArgs, REQUESTEDCHANNELMAX).map(Integer::parseInt).ifPresent(connectionFactory::setRequestedChannelMax);
			getArgument(parsedArgs, REQUESTEDFRAMEMAX).map(Integer::parseInt).ifPresent(connectionFactory::setRequestedFrameMax);
			getArgument(parsedArgs, CONNECTIONTIMEOUT).map(Integer::parseInt).ifPresent(connectionFactory::setConnectionTimeout);
			getArgument(parsedArgs, HANDSHAKETIMEOUT).map(Integer::parseInt).ifPresent(connectionFactory::setHandshakeTimeout);
			getArgument(parsedArgs, SHUTDOWNTIMEOUT).map(Integer::parseInt).ifPresent(connectionFactory::setShutdownTimeout);
			getArgument(parsedArgs, SHUTDOWNTIMEOUT).map(Boolean::parseBoolean).ifPresent(connectionFactory::setTopologyRecoveryEnabled);
			getArgument(parsedArgs, NETWORKRECOVERYINTERVAL).map(Long::parseLong).ifPresent(connectionFactory::setNetworkRecoveryInterval);
			getArgument(parsedArgs, CHANNELSHOULDCHECKRPCRESPONSETYPE).map(Boolean::parseBoolean).ifPresent(connectionFactory::setChannelShouldCheckRpcResponseType);
			getArgument(parsedArgs, WORKPOOLTIMEOUT).map(Integer::parseInt).ifPresent(connectionFactory::setWorkPoolTimeout);
			getArgument(parsedArgs, CHANNELRPCTIMEOUT).map(Integer::parseInt).ifPresent(connectionFactory::setChannelRpcTimeout);
			if(!getArgument(parsedArgs, DISABLENIO).map(Boolean::parseBoolean).orElse(false)){
				connectionFactory.useNio();
			}			
			final int consumerThredPoolSize = getArgument(parsedArgs, CONSUMERTHREADPOOLSIZE).map(Integer::parseInt).orElse(1);
			final ExecutorService executor = Executors.newFixedThreadPool(consumerThredPoolSize);
			AMQPActionEngine.setConnection(context, connectionName, connectionFactory.newConnection(executor));
		} catch (final Exception e) {
			return newErrorResult(context, request, STATUS_CODE_ERROR_CONNECTION, "Cannot create connection to AMQP server.", e);
		}
		return newOkResult(context, request, "Connected to AMQP server.");
	}

	private static final Optional<String> getArgument(Map<String, com.google.common.base.Optional<String>> parsedArgs,
			final AMQPConnectParameter parameter) {
		return Optional.ofNullable(parsedArgs.get(parameter.getOption().getName()).orNull());
	}
}
