package com.example.kirby.model.mqtt.wotan;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class RequestForm {
	@NotBlank
	@Size(min = 3, max = 50)
	private String serialno;

	public String getSerialno() {
		return serialno;
	}

	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}
}
