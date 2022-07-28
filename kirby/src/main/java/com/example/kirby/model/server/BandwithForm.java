package com.example.kirby.model.server;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class BandwithForm {
	@NotBlank
	@Size(min = 3, max = 50)
	private String servername;

	private Long timestamp;

	private float upbandwith = 0;

	private float downbandwith = 0;

	@NotBlank
	@Size(min = 1, max = 10)
	private String region;

	public float getUpbandwith() {
		return upbandwith;
	}

	public void setUpbandwith(float upbandwith) {
		this.upbandwith = upbandwith;
	}

	public float getDownbandwith() {
		return downbandwith;
	}

	public void setDownbandwith(float downbandwith) {
		this.downbandwith = downbandwith;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

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

	public LocalDateTime getDateTime() {
		LocalDateTime localDateTime = null;
		if (timestamp != null) {
			localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
		}
		return localDateTime;
	}
}
