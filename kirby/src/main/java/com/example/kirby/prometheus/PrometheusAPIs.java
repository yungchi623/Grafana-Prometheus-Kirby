package com.example.kirby.prometheus;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.kirby.model.server.AppEngineForm;
import com.example.kirby.model.server.BandwithForm;
import com.example.kirby.model.server.FirmwareVersionForm;
import com.example.kirby.model.server.GamedetectorVersionForm;
import com.example.kirby.model.server.GamelistVersionForm;
import com.example.kirby.model.server.LatencyForm;
import com.example.kirby.model.server.MonitorForm;
import com.example.kirby.model.server.OnlinePlayer;
import com.example.kirby.model.server.PlayerCountForm;
import com.example.kirby.model.server.VPNCountForm;
import com.example.kirby.model.util.Auth;
import com.google.common.util.concurrent.AtomicDouble;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/prometheus")
public class PrometheusAPIs {
	private static final Logger LOGGER = LoggerFactory.getLogger(PrometheusAPIs.class);

	@Autowired
	private MeterRegistry registry;

	HashMap<String, AtomicDouble> adList = new HashMap<String, AtomicDouble>();
	HashMap<String, Counter> coList = new HashMap<String, Counter>();

	Timer timer;

	@PostConstruct
	private void init() {
	}

	@Auth
	@RequestMapping(value = "/isp-server/vpn-count", method = RequestMethod.POST, produces = "text/html")
	public ResponseEntity<?> VPNCount(@Valid @RequestBody VPNCountForm loginRequest) throws InterruptedException {
		String proName = MeterRegistryName.ISP_SERVER_VPN_COUNT.toString().toLowerCase() + "_"
				+ loginRequest.getServername();
		String adProName = "ad_" + proName;
		/*
		 * try { registry.get(adProName).gauge(); } catch (Exception e) {
		 * adList.put(adProName, registry.gauge(adProName, new AtomicDouble(0))); }
		 */

		if (adList.containsKey(adProName)) {
			registry.get(adProName).gauge();
		} else {
			adList.put(adProName, registry.gauge(adProName, new AtomicDouble(0)));
		}

		AtomicDouble n = adList.get(adProName);
		n.set(loginRequest.getVpncount());
		return ResponseEntity.ok("ok");
	}

	@Auth
	@RequestMapping(value = "/isp-server/player-count", method = RequestMethod.POST, produces = "text/html")
	public ResponseEntity<?> PlayerCount(@Valid @RequestBody PlayerCountForm loginRequest) throws InterruptedException {
		String proName = MeterRegistryName.ISP_SERVER_PLAYER_COUNT.toString().toLowerCase() + "_"
				+ loginRequest.getServername();
		String adProName = "ad_" + proName;
		/*
		 * try { registry.get(adProName).gauge(); } catch (Exception e) {
		 * adList.put(adProName, registry.gauge(adProName, new AtomicDouble(0))); }
		 */

		if (adList.containsKey(adProName)) {
			registry.get(adProName).gauge();
		} else {
			adList.put(adProName, registry.gauge(adProName, new AtomicDouble(0)));
		}

		AtomicDouble n = adList.get(adProName);
		n.set(loginRequest.getPlayercount());
		return ResponseEntity.ok("ok");
	}

	@Auth
	@RequestMapping(value = "/isp-server/monitor", method = RequestMethod.POST, produces = "text/html")
	public ResponseEntity<?> monitor(@Valid @RequestBody MonitorForm loginRequest) throws InterruptedException {
		String proName = MeterRegistryName.ISP_SERVER_MONITOR.toString().toLowerCase() + "_"
				+ loginRequest.getServername() + "_" + loginRequest.getService();
		String adProName = "ad_" + proName;
		/*
		 * try { registry.get(adProName).gauge(); } catch (Exception e) {
		 * adList.put(adProName, registry.gauge(adProName, new AtomicDouble(0))); }
		 */

		if (adList.containsKey(adProName)) {
			registry.get(adProName).gauge();
		} else {
			adList.put(adProName, registry.gauge(adProName, new AtomicDouble(0)));
		}

		AtomicDouble n = adList.get(adProName);
		n.set(loginRequest.getAlive());
		return ResponseEntity.ok("ok");
	}

	@Auth
	@RequestMapping(value = "/tunnel/latency", method = RequestMethod.POST, produces = "text/html")
	public ResponseEntity<?> latency(@Valid @RequestBody LatencyForm loginRequest) throws InterruptedException {
		String proName = MeterRegistryName.TUNNEL_LATENCY.toString().toLowerCase() + "_" + loginRequest.getServername()
				+ "_" + loginRequest.getRegion().toString().toLowerCase();
		String adProName = "ad_" + proName;

		/*
		 * try { registry.get(adProName).gauge(); } catch (Exception e) {
		 * adList.put(adProName, registry.gauge(adProName, new AtomicDouble(0))); }
		 */

		if (adList.containsKey(adProName)) {
			registry.get(adProName).gauge();
		} else {
			adList.put(adProName, registry.gauge(adProName, new AtomicDouble(0)));
		}

		if (loginRequest.getRegion().toString().toLowerCase().equals("us")
				|| loginRequest.getRegion().toString().toLowerCase().equals("use")) {
			LOGGER.info(loginRequest.toString());
		}

		AtomicDouble n = adList.get(adProName);
		n.set(loginRequest.getLatency());
		return ResponseEntity.ok("ok");
	}

	@Auth
	@RequestMapping(value = "/tunnel/bandwith", method = RequestMethod.POST, produces = "text/html")
	public ResponseEntity<?> bandwith(@Valid @RequestBody BandwithForm loginRequest) throws InterruptedException {
		String proName = MeterRegistryName.TUNNEL_BANDWITH.toString().toLowerCase() + "_" + loginRequest.getServername()
				+ "_" + loginRequest.getRegion().toString().toLowerCase();
		String upAdProName = "ad_up_" + proName;
		String downAdProName = "ad_down_" + proName;

		/*
		 * try { registry.get(upAdProName).gauge(); registry.get(downAdProName).gauge();
		 * } catch (Exception e) { adList.put(upAdProName, registry.gauge(upAdProName,
		 * new AtomicDouble(0))); adList.put(downAdProName,
		 * registry.gauge(downAdProName, new AtomicDouble(0))); }
		 */

		if (adList.containsKey(upAdProName)) {
			registry.get(upAdProName).gauge();
		} else {
			adList.put(upAdProName, registry.gauge(upAdProName, new AtomicDouble(0)));
		}

		if (adList.containsKey(downAdProName)) {
			registry.get(downAdProName).gauge();
		} else {
			adList.put(downAdProName, registry.gauge(downAdProName, new AtomicDouble(0)));
		}

		AtomicDouble up = adList.get(upAdProName);
		AtomicDouble down = adList.get(downAdProName);
		up.set(loginRequest.getUpbandwith());
		down.set(loginRequest.getDownbandwith());
		return ResponseEntity.ok("ok");
	}

	@Auth
	@Timed
	@RequestMapping(value = "/nwarpmgr/appenginersptime", method = RequestMethod.POST, produces = "text/html")
	public ResponseEntity<?> AppEngineRspTime(@Valid @RequestBody AppEngineForm loginRequest)
			throws InterruptedException {
		String proName = MeterRegistryName.NWARP_MGR_APPENGINE_RSP.toString().toLowerCase() + "_"
				+ loginRequest.getServername().toString().toLowerCase();
		String tProName = "timer_" + proName;
		/*
		 * try { registry.get(tProName).timer(); } catch (Exception e) { timer =
		 * registry.timer(tProName); }
		 */

		if (timer != null) {
			registry.get(tProName).timer();
		} else {
			timer = registry.timer(tProName);
		}

		timer.record(loginRequest.getResponsetime(), TimeUnit.NANOSECONDS);
		return ResponseEntity.ok("ok");
	}

	@Auth
	@RequestMapping(value = "/version/firmware", method = RequestMethod.POST, produces = "text/html")
	public ResponseEntity<?> firmware(@Valid @RequestBody FirmwareVersionForm loginRequest)
			throws InterruptedException {

		String proName = MeterRegistryName.VERSION_FIRMWARE.toString().toLowerCase() + "_"
				+ loginRequest.getModelname();
		String adProName = "ad_" + proName;
		/*
		 * try { registry.get(adProName).gauge(); } catch (Exception e) {
		 * adList.put(adProName, registry.gauge(adProName, new AtomicDouble(0))); }
		 */

		if (adList.containsKey(adProName)) {
			registry.get(adProName).gauge();
		} else {
			adList.put(adProName, registry.gauge(adProName, new AtomicDouble(0)));
		}

		AtomicDouble n = adList.get(adProName);
		n.set(loginRequest.getVersion());
		return ResponseEntity.ok("ok");
	}

	@Auth
	@RequestMapping(value = "/version/gamelist", method = RequestMethod.POST, produces = "text/html")
	public ResponseEntity<?> gamelist(@Valid @RequestBody GamelistVersionForm loginRequest)
			throws InterruptedException {
		String proName = MeterRegistryName.VERSION_GAMELIST.toString().toLowerCase();
		String adProName = "ad_" + proName;
		/*
		 * try { registry.get(adProName).gauge(); } catch (Exception e) {
		 * adList.put(adProName, registry.gauge(adProName, new AtomicDouble(0))); }
		 */

		if (adList.containsKey(adProName)) {
			registry.get(adProName).gauge();
		} else {
			adList.put(adProName, registry.gauge(adProName, new AtomicDouble(0)));
		}

		AtomicDouble n = adList.get(adProName);
		n.set(loginRequest.getVersion());
		return ResponseEntity.ok("ok");
	}

	@Auth
	@RequestMapping(value = "/version/gamedetector", method = RequestMethod.POST, produces = "text/html")
	public ResponseEntity<?> gamedetector(@Valid @RequestBody GamedetectorVersionForm loginRequest)
			throws InterruptedException {
		String proName = MeterRegistryName.VERSION_GAMEDETECTOR.toString().toLowerCase();
		String adProName = "ad_" + proName;
		/*
		 * try { registry.get(adProName).gauge(); } catch (Exception e) {
		 * adList.put(adProName, registry.gauge(adProName, new AtomicDouble(0))); }
		 */

		if (adList.containsKey(adProName)) {
			registry.get(adProName).gauge();
		} else {
			adList.put(adProName, registry.gauge(adProName, new AtomicDouble(0)));
		}

		AtomicDouble n = adList.get(adProName);
		n.set(loginRequest.getVersion());
		return ResponseEntity.ok("ok");
	}

	@Auth
	@RequestMapping(value = "/mqtt/online-player", method = RequestMethod.POST, produces = "text/html")
	public ResponseEntity<?> onlineplayer(@Valid @RequestBody OnlinePlayer loginRequest) throws InterruptedException {
		String proName = MeterRegistryName.MQTT_ONLINE_PLAYER.toString().toLowerCase();
		String adProName = "ad_" + proName;
		/*
		 * try { registry.get(adProName).gauge(); } catch (Exception e) {
		 * adList.put(adProName, registry.gauge(adProName, new AtomicDouble(0))); }
		 */

		if (adList.containsKey(adProName)) {
			registry.get(adProName).gauge();
		} else {
			adList.put(adProName, registry.gauge(adProName, new AtomicDouble(0)));
		}

		AtomicDouble n = adList.get(adProName);
		n.set(loginRequest.getPlayercount());
		return ResponseEntity.ok("ok");
	}
}
