package com.example.kirby.model.mqtt.router;

public class HostInfoRsp {
	private Integer status = -1;
	private HostInfo content;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public HostInfo getContent() {
		return content;
	}

	public void setContent(HostInfo content) {
		this.content = content;
	}
}
