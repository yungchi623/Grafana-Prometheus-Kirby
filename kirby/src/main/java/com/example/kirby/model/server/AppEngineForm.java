package com.example.kirby.model.server;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AppEngineForm {
	@NotBlank
	@Size(min = 3, max = 50)
	private String servername;

	private Long timestamp;

	private Long responsetime;

	/*
	 * @NotBlank
	 * 
	 * @Size(min = 1, max = 10) private String region;
	 */

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

	public Long getResponsetime() {
		return responsetime;
	}

	public void setResponsetime(Long responsetime) {
		this.responsetime = responsetime;
	}

	/*
	 * public String getRegion() { return region; }
	 * 
	 * public void setRegion(String region) { this.region = region; }
	 */
}
