package com.example.kirby.model.server;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class MonitorForm {
	@NotBlank
	@Size(min = 3, max = 50)
	private String servername;

	private String service;

	private Long timestamp;

	private int alive = 0;

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

	public int getAlive() {
		return alive;
	}

	public void setAlive(int alive) {
		this.alive = alive;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}
}
