package com.example.kirby.model.server;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class LatencyForm {
	@NotBlank
	@Size(min = 3, max = 50)
	private String servername;

	private Long timestamp;

	@NotBlank
	@Size(min = 1, max = 10)
	private String region;

	private float latency = 0;

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
}
