package com.example.kirby.model.mqtt.wotan;

import javax.validation.constraints.NotBlank;

public class LoginCmd {

	@NotBlank
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
