package com.example.kirby.model.mqtt.router;

public class CheckLoginRsp {
	private Integer status = -1;
	private CheckLogin content;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public CheckLogin getContent() {
		return content;
	}

	public void setContent(CheckLogin content) {
		this.content = content;
	}
}
