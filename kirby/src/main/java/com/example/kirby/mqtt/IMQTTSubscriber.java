package com.example.kirby.mqtt;

import com.example.kirby.model.util.CmdType;

/**
 * @author gaoyf
 * @since 2020/4/9 0009 16:04
 *        <p>
 *        订阅者接口
 */
public interface IMQTTSubscriber {

	/**
	 * 订阅消息
	 *
	 * @param topic
	 */
	public void subscribeMessage(String topic);

	public String getMsg(String topic, CmdType cmd);

	public void delMsg(String topic, CmdType cmd);

	/**
	 * 断开MQTT客户端
	 */
	public void disconnect();
}