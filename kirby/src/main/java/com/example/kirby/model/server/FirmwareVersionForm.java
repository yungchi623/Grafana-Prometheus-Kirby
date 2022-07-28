package com.example.kirby.model.server;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class FirmwareVersionForm {
	@NotBlank
	@Size(min = 3, max = 50)
	private String modelname;

	private Long timestamp;

	private int version;

	public String getModelname() {
		return modelname;
	}

	public void setModelname(String modelname) {
		this.modelname = modelname;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}
