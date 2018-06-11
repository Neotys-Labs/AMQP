<p align="center"><img src="/screenshots/logo-amqp.png" width="40%" alt="AMQP Logo" /></p>

# AMQP
The [Advanced Message Queuing Protocol (AMQP)](https://www.amqp.org/) is an open standard application layer protocol for high performance enterprise messaging.

## Overview

This repository contains NeoLoad Advanced Actions that allows performance testers using NeoLoad to send messages using AMQP or AMQPS protocol.

| Property           | Value             |
| ----------------   | ----------------  |
| Maturity           | Experimental      |
| Support            | Supported by Neotys      |
| Author             | Neotys |
| License            | [BSD Simplified](https://www.neotys.com/documents/legal/bsd-neotys.txt) |
| NeoLoad            | 6.6 (Enterprise or Professional Edition w/ Integration & Advanced Usage)|
| Bundled in NeoLoad | No |
| Download Binaries  | See the [latest release](https://github.com/Neotys-Labs/AMQP/releases/latest)

## Installation

1. Download the [latest release](https://github.com/Neotys-Labs/AMQP/releases/latest)
1. Read the NeoLoad documentation to see [How to install a custom Advanced Action](https://www.neotys.com/documents/doc/neoload/latest/en/html/#25928.htm)

## Advanced Actions definitions

### Connect

This Advanced Action establishes a connection to a AMQP server. 
Parameters: 

| Name                     | Description       |
| ---------------          | ----------------- |
| connectionName | Name of the AMQP connection to be refereced for further usage. |
| hostname | AMQP server hostname or IP address. |  
| port | AMQP server port. |
| username | Username to connect to the AMQP server. |
| password | Password to connect to the AMQP server. |
| virtualHost | Virtual host. |
| sslProtocol | SSL protocol to use, e.g. TLSv1, TLSv1.2 or keep value empty for default SSL protocol. |
| requestedChannelMax | Maximum channel number to ask for. |
| requestedFrameMax | Frame-max parameter to ask for (in bytes). |
| connectionTimeout | Timeout setting for connection attempts (in milliseconds). |
| handshakeTimeout | Set the AMQP0-9-1 protocol handshake timeout. |
| shutdownTimeout | Set the shutdown timeout in milliseconds. |
| topologyRecoveryEnabled | Enables or disables topology recovery (true or false)." |
| networkRecoveryInterval | Sets connection recovery interval (milliseconds). |
| channelShouldCheckRpcResponseType | Define if the AMQP channel should check the RPC response type or not (true or false). |
| workPoolTimeout | Timeout in milliseconds for work pool enqueueing. The WorkPool dispatches several types of responses from the broker (e.g. deliveries). A high-traffic client with slow consumers can exhaust the work pool and compromise the whole connection (by e.g. letting the broker saturate the receive TCP buffers). Setting a timeout would make the connection fail early and avoid hard-to-diagnose TCP connection failure. Note this shouldn't happen with clients that set appropriate QoS values.|
| channelRpcTimeout | Continuation timeout in milliseconds for RPC calls in channels. |
| disableNio | Enables or disables the Nio mode. Default is false, it is using Nio. Set to true to disable the Nio Mode.|
| consumerThreadPoolSize | Size of the thread pool for the AMQP Consumer.|  

Example: 
<p align="center"><img src="/screenshots/connect.png" alt="Connect" /></p>

Status Codes:
* NL-AMQP-CONNECT-ACTION-01: Invalid parameter.
* NL-AMQP-CONNECT-ACTION-02: Issue connecting to the AMQP server. 

### Disconnect

This Advanced Action closes all AMQP channel and disconnect from AMQP server.
Parameters: 

| Name                     | Description       |
| ---------------          | ----------------- |
| connectionName | Name of the AMQP connection to disconnect. |
| timeout | Set the shutdown timeout in milliseconds. |  

Example: 
<p align="center"><img src="/screenshots/disconnect.png" alt="Disconnect" /></p>

Status Codes:
* NL-AMQP-DISCONNECT-ACTION-01: Invalid parameter.
* NL-AMQP-DISCONNECT-ACTION-02: Issue disconnecting from the AMQP server. 

### Channel - Create

This Advanced Action creates a new channel on an AMQP connection.
Parameters: 

| Name                     | Description       |
| ---------------          | ----------------- |
| connectionName | Name of the AMQP connection to use to create a channel. |
| channelName | Name of the AMQP channel to be referenced for further usage. |  

Example: 
<p align="center"><img src="/screenshots/create_channel.png" alt="Create Channel" /></p>

Status Codes:
* NL-AMQP-CREATECHANNEL-ACTION-01: Invalid parameter.
* NL-AMQP-CREATECHANNEL-ACTION-02: Issue while creating channel. 

### Channel - Close

This Advanced Action closes the AMQP channel.
Parameters: 

| Name                     | Description       |
| ---------------          | ----------------- |
| channelName | Name of the AMQP channel to close. |  

Example: 
<p align="center"><img src="/screenshots/close_channel.png" alt="Close Channel" /></p>

Status Codes:
* NL-AMQP-CLOSECHANNEL-ACTION-01: Invalid parameter.
* NL-AMQP-CLOSECHANNEL-ACTION-02: Issue while closing channel. 

### Exchange - Declare

This Advanced Action publishes a message on an AMQP channel.
Parameters: 

| Name                     | Description       |
| ---------------          | ----------------- |
| channelName | Name of the AMQP channel. |  
| exchangeName | Name of the AMQP exchange to declare. |
| type | The type of the created exchange. Possible values are: **direct**, **fanout**, **topic** and **headers**. Default value is  **direct**." |
| durable | If set to true, the created exchange will be durable. Default value is false. |
| autoDelete | If set to true, the created exchange will be auto deleted if not used. Default value is false. |
| arguments | The arguments used to create the exchange. An argument must follow the pattern name=[class]value separated by '\\n'. Example : size=[java.lang.Integer]150. One line per argument. |

Example: 
<p align="center"><img src="/screenshots/exchange_declare.png" alt="Exchange Declare" /></p>

Status Codes:
* NL-AMQP-DECLARE-EXCHANGE-ACTION-01: Invalid parameter.
* NL-AMQP-DECLARE-EXCHANGE-ACTION-02: Issue while declaring exchange. 

### Exchange - Publish 

### Exchange - Delete

### Queue - Declare

### Queue - Consume

### Queue - Delete

## User Path examples

### Simple Publish

The following User Path establish a connection and create a channel in the "Init" section. In the "Actions" section, a message is published on an already declared exchange. Finally in the "End" section, the channel is closed and the connection is closed. 
<p align="center"><img src="/screenshots/simple-publish.png" alt="Publish" /></p>

### Publish and then Consume

The following User Path establish a connection, create a channel, create an exchange and create a queue in the "Init" section. In the "Actions" section, a message is published and a response is consumed. Finally in the "End" section, the queue and the exchange are deleted the channel is closed and the connection is closed.
<p align="center"><img src="/screenshots/publish-and-consume.png" alt="Publish and Consume" /></p>
