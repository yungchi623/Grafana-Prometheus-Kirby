package com.example.kirby.model.mqtt.router;

public class LoginRsp {
	private Integer status = -1;
	private String content;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
