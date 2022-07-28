package com.example.kirby.model.mqtt.router;

public class CheckFwRsp {
	private Integer status = -1;
	private CheckFw content;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public CheckFw getContent() {
		return content;
	}

	public void setContent(CheckFw content) {
		this.content = content;
	}
}
