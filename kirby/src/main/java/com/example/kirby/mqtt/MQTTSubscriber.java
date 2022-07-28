package com.example.kirby.mqtt;

import java.text.ParseException;
import java.util.Set;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.kirby.model.mqtt.router.CmdResponse;
import com.example.kirby.model.util.CmdType;
import com.example.kirby.model.util.JsonUtil;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

/**
 * @author gaoyf
 * @since 2020/4/9 0009 16:05
 */
@Component
public class MQTTSubscriber extends MQTTConfig implements MqttCallback, IMQTTSubscriber {

	private String brokerUrl = null;
	final private String colon = ":";// 冒号分隔符
	final private String clientId = "mqtt_server_sub";// 客户端ID 这里可以随便定义

	private MqttClient mqttClient = null;
	private MqttConnectionOptions connectionOptions = null;
	private MemoryPersistence persistence = null;
	private Table<String, CmdType, String> map = null;

	private static final Logger logger = LoggerFactory.getLogger(MQTTSubscriber.class);

	public MQTTSubscriber() {
		this.config();
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws ParseException {
		// Called when a message arrives from the server that matches any subscription
		// made by the client

		// String time = new Timestamp(System.currentTimeMillis()).toString();

		// System.out.println();
		// System.out.println("***********************************************************************");
		// System.out.println("消息到达时间：" + time + " Topic: " + topic + " Message: " + new
		// String(message.getPayload()));
		// System.out.println("***********************************************************************");
		// System.out.println();

		logger.info("[訊息到達] Topic: " + topic + "  Message: " + new String(message.getPayload()));

		CmdResponse cmdRsp = null;
		try {
			cmdRsp = JsonUtil.parseJsonToObj(new String(message.getPayload()), CmdResponse.class);
		} catch (Exception me) {
			logger.error("ERROR-cmd:", me);
		}

		// parseResult(topic, cmdRsp.getCmd(), cmdRsp.getResult());

		if (this.map.contains(topic, cmdRsp.getCmd())) {
			// System.out.println("rm");
			this.map.remove(topic, cmdRsp.getCmd());
		}

		this.map.put(topic, cmdRsp.getCmd(), cmdRsp.getResult());

		// System.out.println("add");
		// print();
	}

	@Override
	public String getMsg(String topic, CmdType cmd) {
		if (!this.map.isEmpty() && this.map.contains(topic, cmd)) {
			return this.map.get(topic, cmd);
		}
		return null;
	}

	public void print() {
		Set<Cell<String, CmdType, String>> p = this.map.cellSet();
		p.forEach(System.out::println);
	}

	@Override
	public void delMsg(String topic, CmdType cmd) {
		if (!this.map.isEmpty() && this.map.contains(topic, cmd)) {
			// logger.info("del:" + topic);
			this.map.remove(topic, cmd);
			// print();
		}

		/*
		 * try { this.mqttClient.unsubscribe(topic); } catch (MqttException e) { // TODO
		 * Auto-generated catch block logger.error("ERROR", e); }
		 */
	}

	@Override
	public void subscribeMessage(String topic) {
		try {
			this.mqttClient.subscribe(topic, this.qos);
		} catch (MqttException me) {
			logger.error("ERROR", me);
		}
	}

	@Override
	public void disconnect() {
		try {
			this.mqttClient.disconnect();
		} catch (MqttException me) {
			logger.error("ERROR", me);
		}
	}

	@Override
	protected void config(String ip, Integer port, Boolean ssl, Boolean withUserNamePass) {
		String protocal = this.TCP;
		if (ssl) {
			protocal = this.SSL;
		}
		this.brokerUrl = protocal + this.ip + colon + port;
		// logger.info("brokerUrl: " + brokerUrl);
		this.persistence = new MemoryPersistence();
		this.connectionOptions = new MqttConnectionOptions();
		this.map = HashBasedTable.create();

		try {
			this.mqttClient = new MqttClient(brokerUrl, clientId, persistence);
			this.connectionOptions.setCleanStart(true);
			if (withUserNamePass) {
				if (password != null) {
					this.connectionOptions.setPassword(this.password.getBytes());
				}
				if (username != null) {
					this.connectionOptions.setUserName(this.username);
				}
			}
			this.mqttClient.connect(this.connectionOptions);
			this.mqttClient.setCallback(this);
		} catch (MqttException me) {
			me.printStackTrace();
		}

	}

	@Override
	protected void config() {

		this.brokerUrl = this.TCP + this.ip + colon + this.port;
		// logger.info("brokerUrl: " + brokerUrl);
		this.persistence = new MemoryPersistence();
		this.connectionOptions = new MqttConnectionOptions();
		this.map = HashBasedTable.create();

		try {
			this.mqttClient = new MqttClient(brokerUrl, clientId, persistence);
			this.connectionOptions.setCleanStart(true);
			this.mqttClient.connect(this.connectionOptions);
			this.mqttClient.setCallback(this);
		} catch (MqttException me) {
			me.printStackTrace();
		}

	}

	@Override
	public void disconnected(MqttDisconnectResponse disconnectResponse) {
		// TODO Auto-generated method stub
		logger.info("disconnect");
	}

	@Override
	public void mqttErrorOccurred(MqttException exception) {
		// TODO Auto-generated method stub
		logger.info("mqttErrorOccurred");
	}

	@Override
	public void deliveryComplete(IMqttToken token) {
		// TODO Auto-generated method stub
		// logger.info("delivery completed");
	}

	@Override
	public void connectComplete(boolean reconnect, String serverURI) {
		// TODO Auto-generated method stub

	}

	@Override
	public void authPacketArrived(int reasonCode, MqttProperties properties) {
		// TODO Auto-generated method stub

	}
}
