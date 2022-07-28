package com.example.kirby.mqtt;

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

/**
 * @author gaoyf
 * @since 2020/4/9 0009 16:02
 */
@Component
public class MQTTPublisher extends MQTTConfig implements MqttCallback, IMQTTPublisher {
	private String ipUrl = null;

	final private String colon = ":";// 冒号分隔符
	final private String clientId = "mqtt_server_pub";// 客户端ID 这里可以随便定义

	private MqttClient mqttClient = null;
	private MqttConnectionOptions connectionOptions = null;
	private MemoryPersistence persistence = null;

	private static final Logger logger = LoggerFactory.getLogger(MQTTPublisher.class);

	/**
	 * Private default constructor
	 */
	private MQTTPublisher() {
		this.config();
	}

	/**
	 * Private constructor
	 */
	private MQTTPublisher(String ip, Integer port, Boolean ssl, Boolean withUserNamePass) {
		this.config(ip, port, ssl, withUserNamePass);
	}

	/**
	 * Factory method to get instance of MQTTPublisher
	 *
	 * @return MQTTPublisher
	 */
	public static MQTTPublisher getInstance() {
		return new MQTTPublisher();
	}

	/**
	 * 获取MQTTPublisher实例的工厂方法
	 *
	 * @param ip               ip地址
	 * @param port             断开
	 * @param ssl              是否ssl
	 * @param withUserNamePass 用户名密码
	 * @return MQTTPublisher
	 */
	public static MQTTPublisher getInstance(String ip, Integer port, Boolean ssl, Boolean withUserNamePass) {
		return new MQTTPublisher(ip, port, ssl, withUserNamePass);
	}

	@Override
	protected void config() {
		this.ipUrl = this.TCP + this.ip + colon + this.port;
		// logger.info("ipUrl: " + ipUrl);
		this.persistence = new MemoryPersistence();
		this.connectionOptions = new MqttConnectionOptions();

		try {
			this.mqttClient = new MqttClient(ipUrl, clientId, persistence);
			this.connectionOptions.setCleanStart(true);
			this.mqttClient.connect(this.connectionOptions);
			this.mqttClient.setCallback(this);
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
		this.ipUrl = protocal + this.ip + colon + port;
		// logger.info("ipUrl: " + ipUrl);
		this.persistence = new MemoryPersistence();
		this.connectionOptions = new MqttConnectionOptions();

		try {
			this.mqttClient = new MqttClient(ipUrl, clientId, persistence);
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
			logger.error("ERROR", me);
		}
	}

	@Override
	public void publishMessage(String topic, String message) {

		try {
			MqttMessage mqttmessage = new MqttMessage(message.getBytes());
			mqttmessage.setQos(this.qos);
			this.mqttClient.publish(topic, mqttmessage);
			logger.info("[訊息傳送] Topic: " + topic + "  Message: " + message);
		} catch (MqttException me) {
			logger.error("ERROR", me);
		}

	}

	@Override
	public void messageArrived(String arg0, MqttMessage arg1) {
		// Leave it blank for Publisher
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
	public void disconnected(MqttDisconnectResponse disconnectResponse) {
		// TODO Auto-generated method stub

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