package com.example.kirby.mqtt;

import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.kirby.model.mqtt.router.CheckFw;
import com.example.kirby.model.mqtt.router.CheckFwRsp;
import com.example.kirby.model.mqtt.router.CheckLogin;
import com.example.kirby.model.mqtt.router.CheckLoginRsp;
import com.example.kirby.model.mqtt.router.DeviceInfo;
import com.example.kirby.model.mqtt.router.DeviceInfoRsp;
import com.example.kirby.model.mqtt.router.HostInfo;
import com.example.kirby.model.mqtt.router.HostInfoRsp;
import com.example.kirby.model.mqtt.router.LoginRsp;
import com.example.kirby.model.mqtt.router.RemoteControlRsp;
import com.example.kirby.model.mqtt.router.UpgradeFw;
import com.example.kirby.model.mqtt.router.VpnOnOff;
import com.example.kirby.model.mqtt.router.VpnOnOffRsp;
import com.example.kirby.model.mqtt.wotan.CmdMessage;
import com.example.kirby.model.mqtt.wotan.LoginCmd;
import com.example.kirby.model.mqtt.wotan.LoginForm;
import com.example.kirby.model.mqtt.wotan.RequestForm;
import com.example.kirby.model.mqtt.wotan.VpnOnOffCmd;
import com.example.kirby.model.mqtt.wotan.VpnOnOffForm;
import com.example.kirby.model.util.Auth;
import com.example.kirby.model.util.CmdType;
import com.example.kirby.model.util.JsonUtil;
import com.example.kirby.model.util.SHAUtils;
import com.example.kirby.prometheus.PrometheusUrlOpt;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/mqtt")
public class MQTTAPIs {

	@Autowired
	IMQTTPublisher publisher;

	@Autowired
	IMQTTSubscriber subscriber;

	private static final Logger LOGGER = LoggerFactory.getLogger(MQTTAPIs.class);

	private long limitTime = 5 * 1000;
	private long vpnonofflimitTime = 30 * 1000;
	private long removeupdateTime = 3 * 60 * 1000; // router更新大約2分多鐘結束
	private long removeloginTime = 60 * 1000;
	private String testString = "test";
	private String testConent = "content";
	private Map<String, Long> isUpdating = new HashMap<String, Long>();
	private Map<String, Long> isLogin = new HashMap<String, Long>();

	static public List<String> onlinePlayrer = new ArrayList<String>();

	@RequestMapping(value = "/get-token", method = RequestMethod.GET, produces = "text/html")
	@ResponseBody
	public ResponseEntity<?> GetToken() throws InterruptedException {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
		String secret = "ilove4gamers";
		String timestamp = LocalDateTime.now().format(dateTimeFormatter);
		String sha256Str = timestamp + "-" + SHAUtils.getSHA256Str(timestamp + secret);

		return ResponseEntity.ok(sha256Str);
	}

	@Auth
	@RequestMapping(value = "/vpn/is-remote-control", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> IsRemoteControl(@Valid @RequestBody RequestForm mqttRequest) throws InterruptedException {

		if (mqttRequest.getSerialno().equals(testString)) {
			return ResponseEntity.ok(JsonUtil.parseObjToJson(new RemoteControlRsp()));
		}

		if (mqttRequest.getSerialno().equals(testConent)) {
			RemoteControlRsp remoteControlRsp = new RemoteControlRsp();
			remoteControlRsp.setStatus(0);
			return ResponseEntity.ok(JsonUtil.parseObjToJson(remoteControlRsp));
		}

		String TOPIC_MSG = "N501/" + mqttRequest.getSerialno() + "/cmd";
		String TOPIC_RSP = "N501/" + mqttRequest.getSerialno() + "/cmd/result";
		String subRsp = null;

		subscriber.subscribeMessage(TOPIC_RSP);
		CmdMessage cmdMsg = new CmdMessage();
		cmdMsg.setCmd(CmdType.isRemoteControl);
		Instant instant = Instant.now();
		long timeStampSeconds = instant.getEpochSecond();
		cmdMsg.setTimestamp(timeStampSeconds);

		try {
			// LOGGER.info("Topic: " + TOPIC_MSG + ", " + JsonUtil.parseObjToJson(cmdMsg));
			publisher.publishMessage(TOPIC_MSG, JsonUtil.parseObjToJson(cmdMsg));
		} catch (Exception me) {
			LOGGER.error("ERROR", me);
		}

		subscriber.delMsg(TOPIC_RSP, cmdMsg.getCmd());
		long startTime = System.currentTimeMillis();
		do {
			if ((System.currentTimeMillis() - startTime) > limitTime) {
				RemoteControlRsp remoteControlRsp = new RemoteControlRsp();
				remoteControlRsp.setStatus(-1);
				updateOnlineList(mqttRequest.getSerialno(), -1);
				return ResponseEntity.ok(JsonUtil.parseObjToJson(remoteControlRsp));
			}

			subRsp = subscriber.getMsg(TOPIC_RSP, cmdMsg.getCmd());
			Thread.sleep(10);
		} while (isEmptyString(subRsp));
		RemoteControlRsp remoteControlRsp = (RemoteControlRsp) MqttOpt.parseResult(TOPIC_RSP, cmdMsg.getCmd(), subRsp);
		updateOnlineList(mqttRequest.getSerialno(), remoteControlRsp.getStatus());
		subscriber.delMsg(TOPIC_RSP, cmdMsg.getCmd());
		return ResponseEntity.ok(subRsp);
	}

	@Auth
	@RequestMapping(value = "/vpn/check-login", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> CheckLogin(@Valid @RequestBody RequestForm mqttRequest) throws InterruptedException {

		if (mqttRequest.getSerialno().equals(testString)) {
			return ResponseEntity.ok(JsonUtil.parseObjToJson(new CheckLoginRsp()));
		}

		if (mqttRequest.getSerialno().equals(testConent)) {
			CheckLoginRsp checkLoginRsp = new CheckLoginRsp();
			checkLoginRsp.setStatus(0);
			CheckLogin checkLogin = new CheckLogin();
			checkLogin.setLoginErrno(400);
			checkLoginRsp.setContent(checkLogin);
			return ResponseEntity.ok(JsonUtil.parseObjToJson(checkLoginRsp));
		}

		String TOPIC_MSG = "N501/" + mqttRequest.getSerialno() + "/cmd";
		String TOPIC_RSP = "N501/" + mqttRequest.getSerialno() + "/cmd/result";
		String subRsp = null;

		subscriber.subscribeMessage(TOPIC_RSP);
		CmdMessage cmdMsg = new CmdMessage();
		cmdMsg.setCmd(CmdType.checkLogin);
		Instant instant = Instant.now();
		long timeStampSeconds = instant.getEpochSecond();
		cmdMsg.setTimestamp(timeStampSeconds);

		try {
			// LOGGER.info("Topic: " + TOPIC_MSG + ", " + JsonUtil.parseObjToJson(cmdMsg));
			publisher.publishMessage(TOPIC_MSG, JsonUtil.parseObjToJson(cmdMsg));
		} catch (Exception me) {
			LOGGER.error("ERROR", me);
		}

		subscriber.delMsg(TOPIC_RSP, cmdMsg.getCmd());
		long startTime = System.currentTimeMillis();
		do {
			if ((System.currentTimeMillis() - startTime) > limitTime) {
				CheckLoginRsp checkLoginRsp = new CheckLoginRsp();

				if (isLogin.containsKey(mqttRequest.getSerialno())) {
					if (isLogin.get(mqttRequest.getSerialno()) > removeloginTime)
						isLogin.remove(mqttRequest.getSerialno());
					checkLoginRsp.setStatus(254);
				} else
					checkLoginRsp.setStatus(-1);

				updateOnlineList(mqttRequest.getSerialno(), -1);
				return ResponseEntity.ok(JsonUtil.parseObjToJson(checkLoginRsp));
			}

			subRsp = subscriber.getMsg(TOPIC_RSP, cmdMsg.getCmd());
			Thread.sleep(10);
		} while (isEmptyString(subRsp));

		CheckLoginRsp checkLoginRsp = (CheckLoginRsp) MqttOpt.parseResult(TOPIC_RSP, cmdMsg.getCmd(), subRsp);
		if (isLogin.containsKey(mqttRequest.getSerialno()) && checkLoginRsp.getStatus() == 0)
			isLogin.remove(mqttRequest.getSerialno());
		updateOnlineList(mqttRequest.getSerialno(), checkLoginRsp.getStatus());
		subscriber.delMsg(TOPIC_RSP, cmdMsg.getCmd());
		return ResponseEntity.ok(subRsp);
	}

	@Auth
	@RequestMapping(value = "/vpn/get-deviceinfo", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> DeviceInfo(@Valid @RequestBody RequestForm mqttRequest) throws InterruptedException {

		if (mqttRequest.getSerialno().equals(testString)) {
			return ResponseEntity.ok(JsonUtil.parseObjToJson(new DeviceInfoRsp()));
		}

		if (mqttRequest.getSerialno().equals(testConent)) {
			DeviceInfoRsp deviceInfoRsp = new DeviceInfoRsp();
			deviceInfoRsp.setStatus(0);
			DeviceInfo deviceInfo = new DeviceInfo();
			deviceInfo.setDevIP("192.168.47.101");
			deviceInfo.setDevName("abc");
			deviceInfo.setDevStatus(2);
			deviceInfo.setDevType(4);
			ArrayList<DeviceInfo> con = new ArrayList<DeviceInfo>();
			con.add(deviceInfo);
			deviceInfoRsp.setContent(con);
			return ResponseEntity.ok(JsonUtil.parseObjToJson(deviceInfoRsp));
		}

		String TOPIC_MSG = "N501/" + mqttRequest.getSerialno() + "/cmd";
		String TOPIC_RSP = "N501/" + mqttRequest.getSerialno() + "/cmd/result";
		String subRsp = null;

		subscriber.subscribeMessage(TOPIC_RSP);
		CmdMessage cmdMsg = new CmdMessage();
		cmdMsg.setCmd(CmdType.getDevInfo);
		Instant instant = Instant.now();
		long timeStampSeconds = instant.getEpochSecond();
		cmdMsg.setTimestamp(timeStampSeconds);

		try {
			// LOGGER.info("Topic: " + TOPIC_MSG + ", " + JsonUtil.parseObjToJson(cmdMsg));
			publisher.publishMessage(TOPIC_MSG, JsonUtil.parseObjToJson(cmdMsg));
		} catch (Exception me) {
			LOGGER.error("ERROR", me);
		}

		subscriber.delMsg(TOPIC_RSP, cmdMsg.getCmd());
		long startTime = System.currentTimeMillis();
		do {
			if ((System.currentTimeMillis() - startTime) > limitTime) {
				DeviceInfoRsp deviceInfoRsp = new DeviceInfoRsp();
				deviceInfoRsp.setStatus(-1);
				updateOnlineList(mqttRequest.getSerialno(), -1);
				return ResponseEntity.ok(JsonUtil.parseObjToJson(deviceInfoRsp));
			}

			subRsp = subscriber.getMsg(TOPIC_RSP, cmdMsg.getCmd());
			Thread.sleep(10);
		} while (isEmptyString(subRsp));
		DeviceInfoRsp deviceInfoRsp = (DeviceInfoRsp) MqttOpt.parseResult(TOPIC_RSP, cmdMsg.getCmd(), subRsp);
		updateOnlineList(mqttRequest.getSerialno(), deviceInfoRsp.getStatus());
		subscriber.delMsg(TOPIC_RSP, cmdMsg.getCmd());
		return ResponseEntity.ok(subRsp);
	}

	@Auth
	@RequestMapping(value = "/vpn/login", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> Login(@Valid @RequestBody LoginForm mqttRequest) throws InterruptedException {

		if (mqttRequest.getSerialno().equals(testString)) {
			return ResponseEntity.ok(JsonUtil.parseObjToJson(new LoginRsp()));
		}

		if (mqttRequest.getSerialno().equals(testConent)) {
			LoginRsp loginRsp = new LoginRsp();
			loginRsp.setStatus(0);
			return ResponseEntity.ok(JsonUtil.parseObjToJson(loginRsp));
		}

		String TOPIC_MSG = "N501/" + mqttRequest.getSerialno() + "/cmd";
		String TOPIC_RSP = "N501/" + mqttRequest.getSerialno() + "/cmd/result";
		String subRsp = null;

		subscriber.subscribeMessage(TOPIC_RSP);
		CmdMessage cmdMsg = new CmdMessage();
		LoginCmd loginCmd = new LoginCmd();
		cmdMsg.setCmd(CmdType.login);
		Instant instant = Instant.now();
		long timeStampSeconds = instant.getEpochSecond();
		cmdMsg.setTimestamp(timeStampSeconds);

		loginCmd.setUsername(mqttRequest.getUsername());

		cmdMsg.setData(loginCmd);

		try {
			// LOGGER.info("Topic: " + TOPIC_MSG + ", " + JsonUtil.parseObjToJson(cmdMsg));
			publisher.publishMessage(TOPIC_MSG, JsonUtil.parseObjToJson(cmdMsg));
		} catch (Exception me) {
			LOGGER.error("ERROR", me);
		}

		subscriber.delMsg(TOPIC_RSP, cmdMsg.getCmd());
		long startTime = System.currentTimeMillis();
		do {
			if ((System.currentTimeMillis() - startTime) > limitTime) {
				LoginRsp loginRsp = new LoginRsp();
				loginRsp.setStatus(-1);
				updateOnlineList(mqttRequest.getSerialno(), -1);
				return ResponseEntity.ok(JsonUtil.parseObjToJson(loginRsp));
			}

			subRsp = subscriber.getMsg(TOPIC_RSP, cmdMsg.getCmd());
			Thread.sleep(10);
		} while (isEmptyString(subRsp));

		if (isLogin.containsKey(mqttRequest.getSerialno()))
			isLogin.remove(mqttRequest.getSerialno());

		isLogin.put(mqttRequest.getSerialno(), timeStampSeconds);

		LoginRsp loginRsp = (LoginRsp) MqttOpt.parseResult(TOPIC_RSP, cmdMsg.getCmd(), subRsp);
		updateOnlineList(mqttRequest.getSerialno(), loginRsp.getStatus());
		subscriber.delMsg(TOPIC_RSP, cmdMsg.getCmd());
		return ResponseEntity.ok(subRsp);
	}

	@Auth
	@RequestMapping(value = "/vpn/vpn-on-off", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> VpnOnOff(@Valid @RequestBody VpnOnOffForm mqttRequest) throws InterruptedException {

		if (mqttRequest.getSerialno().equals(testString)) {
			return ResponseEntity.ok(JsonUtil.parseObjToJson(new VpnOnOffRsp()));
		}

		if (mqttRequest.getSerialno().equals(testConent)) {
			VpnOnOffRsp vpnOnOffRsp = new VpnOnOffRsp();
			vpnOnOffRsp.setStatus(0);
			VpnOnOff vpnOnOff = new VpnOnOff();
			vpnOnOff.setDevIP("192.168.47.100");
			vpnOnOff.setVpnOnOff(1);
			vpnOnOffRsp.setContent(vpnOnOff);
			return ResponseEntity.ok(JsonUtil.parseObjToJson(vpnOnOffRsp));
		}

		String TOPIC_MSG = "N501/" + mqttRequest.getSerialno() + "/cmd";
		String TOPIC_RSP = "N501/" + mqttRequest.getSerialno() + "/cmd/result";
		String subRsp = null;

		subscriber.subscribeMessage(TOPIC_RSP);
		CmdMessage cmdMsg = new CmdMessage();
		VpnOnOffCmd vpnOnOffCmd = new VpnOnOffCmd();
		cmdMsg.setCmd(CmdType.vpnOnOff);
		Instant instant = Instant.now();
		long timeStampSeconds = instant.getEpochSecond();
		cmdMsg.setTimestamp(timeStampSeconds);
		vpnOnOffCmd.setVpnonoff(mqttRequest.getVpnonoff());
		vpnOnOffCmd.setDevip(mqttRequest.getDevip());
		cmdMsg.setData(vpnOnOffCmd);
		try {
			// LOGGER.info("Topic: " + TOPIC_MSG + ", " + JsonUtil.parseObjToJson(cmdMsg));
			publisher.publishMessage(TOPIC_MSG, JsonUtil.parseObjToJson(cmdMsg));
		} catch (Exception me) {
			LOGGER.error("ERROR", me);
		}

		subscriber.delMsg(TOPIC_RSP, cmdMsg.getCmd());
		long startTime = System.currentTimeMillis();
		do {
			if ((System.currentTimeMillis() - startTime) > vpnonofflimitTime) {
				VpnOnOffRsp vpnOnOffRsp = new VpnOnOffRsp();
				vpnOnOffRsp.setStatus(-1);
				updateOnlineList(mqttRequest.getSerialno(), -1);
				return ResponseEntity.ok(JsonUtil.parseObjToJson(vpnOnOffRsp));
			}

			subRsp = subscriber.getMsg(TOPIC_RSP, cmdMsg.getCmd());
			Thread.sleep(10);
		} while (isEmptyString(subRsp));
		VpnOnOffRsp vpnOnOffRsp = (VpnOnOffRsp) MqttOpt.parseResult(TOPIC_RSP, cmdMsg.getCmd(), subRsp);
		updateOnlineList(mqttRequest.getSerialno(), vpnOnOffRsp.getStatus());
		subscriber.delMsg(TOPIC_RSP, cmdMsg.getCmd());
		return ResponseEntity.ok(subRsp);
	}

	@Auth
	@RequestMapping(value = "/vpn/check-fw", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> CheckFW(@Valid @RequestBody RequestForm mqttRequest) throws InterruptedException {

		if (mqttRequest.getSerialno().equals(testString)) {
			return ResponseEntity.ok(JsonUtil.parseObjToJson(new CheckFwRsp()));
		}

		if (mqttRequest.getSerialno().equals(testConent)) {
			CheckFwRsp checkFwRsp = new CheckFwRsp();
			checkFwRsp.setStatus(0);
			CheckFw checkFw = new CheckFw();
			checkFw.setFwVersion("v1.1.0001");
			checkFwRsp.setContent(checkFw);
			return ResponseEntity.ok(JsonUtil.parseObjToJson(checkFwRsp));
		}

		String TOPIC_MSG = "N501/" + mqttRequest.getSerialno() + "/cmd";
		String TOPIC_RSP = "N501/" + mqttRequest.getSerialno() + "/cmd/result";
		String subRsp = null;

		subscriber.subscribeMessage(TOPIC_RSP);
		CmdMessage cmdMsg = new CmdMessage();
		cmdMsg.setCmd(CmdType.checkFw);
		Instant instant = Instant.now();
		long timeStampSeconds = instant.getEpochSecond();
		cmdMsg.setTimestamp(timeStampSeconds);

		try {
			// LOGGER.info("Topic: " + TOPIC_MSG + ", " + JsonUtil.parseObjToJson(cmdMsg));
			publisher.publishMessage(TOPIC_MSG, JsonUtil.parseObjToJson(cmdMsg));
		} catch (Exception me) {
			LOGGER.error("ERROR", me);
		}

		for (Object key : isUpdating.keySet()) {
			System.out.println(key + " : " + isUpdating.get(key));
		}

		subscriber.delMsg(TOPIC_RSP, cmdMsg.getCmd());
		long startTime = System.currentTimeMillis();
		do {
			if ((System.currentTimeMillis() - startTime) > limitTime) {
				CheckFwRsp checkFwRsp = new CheckFwRsp();

				if (isUpdating.containsKey(mqttRequest.getSerialno())) {
					if (isUpdating.get(mqttRequest.getSerialno()) > removeupdateTime)
						isUpdating.remove(mqttRequest.getSerialno());
					checkFwRsp.setStatus(3);
				} else
					checkFwRsp.setStatus(-1);
				updateOnlineList(mqttRequest.getSerialno(), -1);
				return ResponseEntity.ok(JsonUtil.parseObjToJson(checkFwRsp));
			}

			subRsp = subscriber.getMsg(TOPIC_RSP, cmdMsg.getCmd());
			Thread.sleep(10);
		} while (isEmptyString(subRsp));
		CheckFwRsp checkFwRsp = (CheckFwRsp) MqttOpt.parseResult(TOPIC_RSP, cmdMsg.getCmd(), subRsp);
		updateOnlineList(mqttRequest.getSerialno(), checkFwRsp.getStatus());
		subscriber.delMsg(TOPIC_RSP, cmdMsg.getCmd());

		if (isUpdating.containsKey(mqttRequest.getSerialno()))
			isUpdating.remove(mqttRequest.getSerialno());

		return ResponseEntity.ok(subRsp);
	}

	@Auth
	@RequestMapping(value = "/vpn/upgrade-fw", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> UpgradeFW(@Valid @RequestBody RequestForm mqttRequest) throws InterruptedException {

		if (mqttRequest.getSerialno().equals(testString)) {
			return ResponseEntity.ok(JsonUtil.parseObjToJson(new UpgradeFw()));
		}

		if (mqttRequest.getSerialno().equals(testConent)) {
			UpgradeFw upgradeFw = new UpgradeFw();
			upgradeFw.setStatus(0);
			return ResponseEntity.ok(JsonUtil.parseObjToJson(upgradeFw));
		}

		String TOPIC_MSG = "N501/" + mqttRequest.getSerialno() + "/cmd";
		CmdMessage cmdMsg = new CmdMessage();
		cmdMsg.setCmd(CmdType.upgradeFw);
		Instant instant = Instant.now();
		long timeStampSeconds = instant.getEpochSecond();
		cmdMsg.setTimestamp(timeStampSeconds);

		try {
			// LOGGER.info("Topic: " + TOPIC_MSG + ", " + JsonUtil.parseObjToJson(cmdMsg));
			publisher.publishMessage(TOPIC_MSG, JsonUtil.parseObjToJson(cmdMsg));
			isUpdating.put(mqttRequest.getSerialno(), timeStampSeconds);
		} catch (Exception me) {
			LOGGER.error("ERROR", me);
		}

		UpgradeFw upgradeFw = new UpgradeFw();
		upgradeFw.setStatus(0);
		return ResponseEntity.ok(JsonUtil.parseObjToJson(upgradeFw));
	}

	public static void postOnlinePlayer(int playercount) throws URISyntaxException {
		final String baseUrl = "http://localhost:8080/prometheus/mqtt/online-player";
		Map<String, Object> map = new HashMap<>();
		map.put("timestamp", LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		map.put("playercount", playercount);
		PrometheusUrlOpt.postKirbyApi(map, baseUrl);
	}

	static public void cleanOnlineList() throws URISyntaxException {
		onlinePlayrer.clear();
		MQTTAPIs.postOnlinePlayer(onlinePlayrer.size());
		// LOGGER.info("OnlinePlayer clear!");
	}

	public void updateOnlineList(String serialno, int status) {

		if (onlinePlayrer.contains(serialno))
			onlinePlayrer.remove(serialno);

		if (status == 0)
			onlinePlayrer.add(serialno);

		try {
			MQTTAPIs.postOnlinePlayer(onlinePlayrer.size());
		} catch (URISyntaxException e) {
			LOGGER.error("ERROR:", e);
		}
	}

	/*
	 * @Auth
	 * 
	 * @RequestMapping(value = "/vpn/logout", method = RequestMethod.POST, produces
	 * = "application/json")
	 * 
	 * @ResponseBody public ResponseEntity<?> Logout(@Valid @RequestBody RequestForm
	 * mqttRequest) throws InterruptedException {
	 * 
	 * if (mqttRequest.getSerialno().equals(testString)) { return
	 * ResponseEntity.ok(JsonUtil.parseObjToJson(new LogoutRsp())); }
	 * 
	 * if (mqttRequest.getSerialno().equals(testConent)) { LogoutRsp logoutRsp = new
	 * LogoutRsp(); logoutRsp.setStatus(0); return
	 * ResponseEntity.ok(JsonUtil.parseObjToJson(logoutRsp)); }
	 * 
	 * String TOPIC_MSG = "N501/" + mqttRequest.getSerialno() + "/cmd"; String
	 * TOPIC_RSP = "N501/" + mqttRequest.getSerialno() + "/cmd/result"; String
	 * subRsp = null;
	 * 
	 * subscriber.subscribeMessage(TOPIC_RSP); CmdMessage cmdMsg = new CmdMessage();
	 * cmdMsg.setCmd(CmdType.logout); Instant instant = Instant.now(); long
	 * timeStampSeconds = instant.getEpochSecond();
	 * cmdMsg.setTimestamp(timeStampSeconds);
	 * 
	 * try { LOGGER.info("Topic: " + TOPIC_MSG + ", " +
	 * JsonUtil.parseObjToJson(cmdMsg)); publisher.publishMessage(TOPIC_MSG,
	 * JsonUtil.parseObjToJson(cmdMsg)); } catch (Exception me) {
	 * LOGGER.error("ERROR", me); }
	 * 
	 * subscriber.delMsg(TOPIC_RSP, cmdMsg.getCmd()); long startTime =
	 * System.currentTimeMillis(); do { if ((System.currentTimeMillis() - startTime)
	 * > limitTime) { subRsp = JsonUtil.parseObjToJson("Time out!!"); break; }
	 * 
	 * subRsp = subscriber.getMsg(TOPIC_RSP, cmdMsg.getCmd()); Thread.sleep(10); }
	 * while (isEmptyString(subRsp)); subscriber.delMsg(TOPIC_RSP, cmdMsg.getCmd());
	 * return ResponseEntity.ok(subRsp); }
	 */

	@Auth
	@RequestMapping(value = "/vpn/get-hostinfo", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> HostInfo(@Valid @RequestBody RequestForm mqttRequest) throws InterruptedException {

		if (mqttRequest.getSerialno().equals(testString)) {
			return ResponseEntity.ok(JsonUtil.parseObjToJson(new HostInfoRsp()));
		}

		if (mqttRequest.getSerialno().equals(testConent)) {
			HostInfoRsp hostInfoRsp = new HostInfoRsp();
			hostInfoRsp.setStatus(0);
			HostInfo hostInfo = new HostInfo();
			hostInfo.setDevIP("192.168.47.101");
			hostInfo.setDevName("bbb");
			hostInfo.setDevType(4);
			hostInfo.setDevStatus(3);
			hostInfo.setFwVersion("v1.1.0001");
			hostInfo.setGameId(40);
			hostInfoRsp.setContent(hostInfo);
			return ResponseEntity.ok(JsonUtil.parseObjToJson(hostInfoRsp));
		}

		String TOPIC_MSG = "N501/" + mqttRequest.getSerialno() + "/cmd";
		String TOPIC_RSP = "N501/" + mqttRequest.getSerialno() + "/cmd/result";
		String subRsp = null;

		subscriber.subscribeMessage(TOPIC_RSP);
		CmdMessage cmdMsg = new CmdMessage();
		cmdMsg.setCmd(CmdType.getHostInfo);
		Instant instant = Instant.now();
		long timeStampSeconds = instant.getEpochSecond();
		cmdMsg.setTimestamp(timeStampSeconds);

		try {
			// LOGGER.info("Topic: " + TOPIC_MSG + ", " + JsonUtil.parseObjToJson(cmdMsg));
			publisher.publishMessage(TOPIC_MSG, JsonUtil.parseObjToJson(cmdMsg));
		} catch (Exception me) {
			LOGGER.error("ERROR", me);
		}

		subscriber.delMsg(TOPIC_RSP, cmdMsg.getCmd());
		long startTime = System.currentTimeMillis();
		do {
			if ((System.currentTimeMillis() - startTime) > limitTime) {
				HostInfoRsp hostInfoRsp = new HostInfoRsp();
				hostInfoRsp.setStatus(-1);
				updateOnlineList(mqttRequest.getSerialno(), -1);
				return ResponseEntity.ok(JsonUtil.parseObjToJson(hostInfoRsp));
			}

			subRsp = subscriber.getMsg(TOPIC_RSP, cmdMsg.getCmd());
			Thread.sleep(10);
		} while (isEmptyString(subRsp));
		subscriber.delMsg(TOPIC_RSP, cmdMsg.getCmd());
		return ResponseEntity.ok(subRsp);
	}

	boolean isEmptyString(String string) {
		return string == null || string.isEmpty();
	}

}
