package com.example.kirby.schedulingtasks;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.kirby.model.util.SHAUtils;
import com.example.kirby.mqtt.MQTTAPIs;
import com.example.kirby.prometheus.PrometheusUrlOpt;

@Component
public class ScheduledTasks {
	private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

	String nwarpUrl = "https://nwarp-prod.appspot.com"; // "https://gamersgir.appspot.com";

	@Value("${prometheus.url}")
	String prometheusUrl;

	public void postAppEngine(String servername, Long responsetime) throws URISyntaxException {
		final String baseUrl = prometheusUrl + "/nwarpmgr/appenginersptime";
		Map<String, Object> map = new HashMap<>();
		map.put("servername", servername);
		// map.put("region", region);
		map.put("responsetime", responsetime);
		map.put("timestamp", LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		// logger.info("App Engine Task :: Execution Time - {}",
		// LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		PrometheusUrlOpt.postKirbyApi(map, baseUrl);
	}

	@Scheduled(fixedRate = 1000 * 60 * 30)
	// @Scheduled(fixedRate = 1000 * 2)
	public void scheduleTaskWithAppengine() throws URISyntaxException {
		long startTime = System.nanoTime();
		long totalTime = 0;
		final String baseUrl = nwarpUrl + "/sys/contentver";
		try {
			List<String> versionList = getVersion(baseUrl);
			totalTime = System.nanoTime() - startTime;
		} catch (Exception e) {
			totalTime = 0;
		}
		postAppEngine("abc", totalTime);
	}

	@Scheduled(cron = "0 0 * * * *")
	public void scheduleTaskWithoOnlinePlayer() throws URISyntaxException {
		MQTTAPIs.cleanOnlineList();
	}

	public void postFirwareVersion(String modelname, int version) throws URISyntaxException {
		final String baseUrl = prometheusUrl + "/version/firmware";
		Map<String, Object> map = new HashMap<>();
		map.put("modelname", modelname);
		map.put("timestamp", LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		map.put("version", version);
		PrometheusUrlOpt.postKirbyApi(map, baseUrl);
	}

	public void postGamelistVersion(int version) throws URISyntaxException {
		final String baseUrl = prometheusUrl + "/version/gamelist";
		Map<String, Object> map = new HashMap<>();
		map.put("timestamp", LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		map.put("version", version);
		PrometheusUrlOpt.postKirbyApi(map, baseUrl);
	}

	public void postGameDetectorVersion(int version) throws URISyntaxException {
		final String baseUrl = prometheusUrl + "/version/gamedetector";
		Map<String, Object> map = new HashMap<>();
		map.put("timestamp", LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		map.put("version", version);
		PrometheusUrlOpt.postKirbyApi(map, baseUrl);
	}

	public List<String> getVersion(String baseUrl) throws URISyntaxException {
		RestTemplate restTemplate = new RestTemplate();
		List<String> rtn = new ArrayList<String>();
		URI uri = new URI(baseUrl);

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
		String timestamp = LocalDateTime.now(ZoneOffset.UTC).format(dateTimeFormatter);
		String secret = "4gamersnwarp";
		String AUTH_HEADER = "KIRBYTOKEN";
		String sha256Str = SHAUtils.getSHA256Str(timestamp + secret);
		// logger.info("Version Task :: Execution Time - {}", timestamp);

		final HttpHeaders headers = new HttpHeaders();
		headers.set(AUTH_HEADER, timestamp + "-" + sha256Str);

		final HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, entity, Map.class);
		// logger.info("FW:" + response.getBody().get("fw") + " GL:" +
		// response.getBody().get("gamelist") + " GD:"
		// + response.getBody().get("gd"));
		List<String> fwList = getTokens(response.getBody().get("fw").toString());
		List<String> glList = getTokens(response.getBody().get("gamelist").toString());
		List<String> gdList = getTokens(response.getBody().get("gd").toString());
		int[] fwP = { 2, 2, 4 };
		int[] glP = { 4 };
		int[] gdP = { 2, 4 };

		rtn.add(fullDigital(fwP, fwList));
		rtn.add(fullDigital(glP, glList));
		rtn.add(fullDigital(gdP, gdList));

		return rtn;
	}

	public String fullDigital(int[] pattern, List<String> version) {
		int tlen = pattern.length - version.size();
		String rtn = "";
		int index = 0;

		for (int i = 0; i < tlen; i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < pattern[index]; j++) {
				sb.insert(0, "0");
			}
			rtn += sb.toString();
			index++;
		}

		for (String v : version) {
			int inDig = pattern[index] - v.length();
			StringBuilder sb = new StringBuilder(v);
			for (int i = 0; i < inDig; i++) {
				sb.insert(0, "0");
			}
			rtn += sb.toString();
			index++;
		}

		return rtn;
	}

	public List<String> getTokens(String str) {
		List<String> tokens = new ArrayList<>();
		StringTokenizer tokenizer = new StringTokenizer(str, ".‧");
		while (tokenizer.hasMoreElements()) {
			tokens.add(tokenizer.nextToken());
		}
		return tokens;
	}

	@Scheduled(cron = "0 0 0 * * ?")
	// @Scheduled(cron = "0/15 * * * * *")
	public void scheduleTaskWithVersion() throws URISyntaxException {
		final String baseUrl = nwarpUrl + "/sys/contentver";
		List<String> versionList = getVersion(baseUrl);
		logger.info("Get Version FW:" + versionList.get(0) + " GL:" + versionList.get(1) + " GD:" + versionList.get(2));
		postFirwareVersion("N501", Integer.parseInt(versionList.get(0)));
		postGamelistVersion(Integer.parseInt(versionList.get(1)));
		postGameDetectorVersion(Integer.parseInt(versionList.get(2)));
	}
}