package com.example.kirby.prometheus;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.example.kirby.model.util.SHAUtils;

public class PrometheusUrlOpt {

	public static void postKirbyApi(Map<String, Object> map, String baseUrl) throws URISyntaxException {
		RestTemplate restTemplate = new RestTemplate();

		URI uri = new URI(baseUrl);

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
		String timestamp = LocalDateTime.now(ZoneOffset.UTC).format(dateTimeFormatter);
		String secret = "ilove4gamers";
		String AUTH_HEADER = "Authorization";
		String sha256Str = SHAUtils.getSHA256Str(timestamp + secret);

		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
		headers.set(AUTH_HEADER, timestamp + "-" + sha256Str);

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

		ResponseEntity<?> result = restTemplate.postForEntity(uri, entity, String.class);
	}
}
