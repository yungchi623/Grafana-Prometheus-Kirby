package com.example.kirby.mqtt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.kirby.model.mqtt.router.CheckFwRsp;
import com.example.kirby.model.mqtt.router.CheckLoginRsp;
import com.example.kirby.model.mqtt.router.DeviceInfoRsp;
import com.example.kirby.model.mqtt.router.HostInfoRsp;
import com.example.kirby.model.mqtt.router.LoginRsp;
import com.example.kirby.model.mqtt.router.LogoutRsp;
import com.example.kirby.model.mqtt.router.RemoteControlRsp;
import com.example.kirby.model.mqtt.router.UpgradeFw;
import com.example.kirby.model.mqtt.router.VpnOnOffRsp;
import com.example.kirby.model.util.CmdType;
import com.example.kirby.model.util.JsonUtil;

public class MqttOpt {
	private static final Logger LOGGER = LoggerFactory.getLogger(MqttOpt.class);

	public static Object parseResult(String topic, CmdType cmdtype, String result) {
		Object rtn = null;
		switch (cmdtype) {
		case isRemoteControl:
			RemoteControlRsp remoteControlRsp = null;
			try {
				remoteControlRsp = JsonUtil.parseJsonToObj(result, RemoteControlRsp.class);
				rtn = remoteControlRsp;
				// LOGGER.info("parse remoteControl: " +
				// JsonUtil.parseObjToJson(remoteControlRsp));
			} catch (Exception me) {
				LOGGER.error("ERROR-remoteControl:", me);
			}
			break;
		case checkLogin:
			CheckLoginRsp checkLoginRsp = null;
			try {
				checkLoginRsp = JsonUtil.parseJsonToObj(result, CheckLoginRsp.class);
				rtn = checkLoginRsp;
				// LOGGER.info("parse checkLoginRsp: " +
				// JsonUtil.parseObjToJson(checkLoginRsp));
			} catch (Exception me) {
				LOGGER.error("ERROR-checkLoginRsp:", me);
			}
			break;
		case getDevInfo:
			DeviceInfoRsp deviceInfoRsp = null;
			try {
				deviceInfoRsp = JsonUtil.parseJsonToObj(result, DeviceInfoRsp.class);
				rtn = deviceInfoRsp;
				// LOGGER.info("parse device info: " + JsonUtil.parseObjToJson(deviceInfoRsp));
			} catch (Exception me) {
				LOGGER.error("ERROR-deviceInfo:", me);
			}
			break;
		case login:
			LoginRsp loginRsp = null;
			try {
				loginRsp = JsonUtil.parseJsonToObj(result, LoginRsp.class);
				rtn = loginRsp;
				// LOGGER.info("parse login: " + JsonUtil.parseObjToJson(loginRsp));
			} catch (Exception me) {
				LOGGER.error("ERROR-login:", me);
			}
			break;
		case vpnOnOff:
			VpnOnOffRsp vpnOnOffRsp = null;
			try {
				vpnOnOffRsp = JsonUtil.parseJsonToObj(result, VpnOnOffRsp.class);
				rtn = vpnOnOffRsp;
				// LOGGER.info("parse vpnOnOffRsp: " + JsonUtil.parseObjToJson(vpnOnOffRsp));
			} catch (Exception me) {
				LOGGER.error("ERROR-vpnOnOffRsp:", me);
			}
			break;
		case checkFw:
			CheckFwRsp checkFwRsp = null;
			try {
				checkFwRsp = JsonUtil.parseJsonToObj(result, CheckFwRsp.class);
				rtn = checkFwRsp;
				// LOGGER.info("parse checkFwRsp: " + JsonUtil.parseObjToJson(checkFwRsp));
			} catch (Exception me) {
				LOGGER.error("ERROR-checkFwRsp:", me);
			}
			break;
		case upgradeFw:
			UpgradeFw upgradeFw = null;
			try {
				upgradeFw = JsonUtil.parseJsonToObj(result, UpgradeFw.class);
				rtn = upgradeFw;
				// LOGGER.info("parse upgradeFw: " + JsonUtil.parseObjToJson(upgradeFw));
			} catch (Exception me) {
				LOGGER.error("ERROR-upgradeFw:", me);
			}
			break;
		case logout:
			LogoutRsp logoutRsp = null;
			try {
				logoutRsp = JsonUtil.parseJsonToObj(result, LogoutRsp.class);
				rtn = logoutRsp;
				// LOGGER.info("parse logout: " + JsonUtil.parseObjToJson(logoutRsp));
			} catch (Exception me) {
				LOGGER.error("ERROR-logout:", me);
			}
			break;
		case getHostInfo:
			HostInfoRsp hostInfoRsp = null;
			try {
				hostInfoRsp = JsonUtil.parseJsonToObj(result, HostInfoRsp.class);
				rtn = hostInfoRsp;
				// LOGGER.info("parse hostinfo: " + JsonUtil.parseObjToJson(hostInfoRsp));
			} catch (Exception me) {
				LOGGER.error("ERROR-hostinfo:", me);
			}
			break;
		default:
			break;
		}

		return rtn;
	}
}
