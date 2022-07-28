package com.example.kirby.model.server;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class LatencyThresholdForm {
	@NotBlank
	@Size(min = 3, max = 50)
	private String servername;

	private Long timestamp;

	@NotBlank
	@Size(min = 1, max = 10)
	private String region;

	private float latency = 0;

	private float avg = 0;

	public String getServername() {
		return servername;
	}

	public void setServername(String servername) {
		this.servername = servername;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public float getLatency() {
		return latency;
	}

	public void setLatency(float latency) {
		this.latency = latency;
	}

	public float getAvg() {
		return avg;
	}

	public void setAvg(float avg) {
		this.avg = avg;
	}

	public LocalDateTime getDateTime() {
		LocalDateTime localDateTime = null;
		if (timestamp != null) {
			localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
		}
		return localDateTime;
	}
}
