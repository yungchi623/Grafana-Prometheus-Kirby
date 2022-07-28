package com.example.kirby.model.mqtt.wotan;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class LoginForm {
	@NotBlank
	@Size(min = 3, max = 50)
	private String serialno;

	private String username;

	public String getSerialno() {
		return serialno;
	}

	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
