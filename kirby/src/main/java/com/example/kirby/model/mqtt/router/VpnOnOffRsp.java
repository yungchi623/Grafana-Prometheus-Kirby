package com.example.kirby.model.mqtt.router;

public class VpnOnOffRsp {
	private Integer status = -1;
	private VpnOnOff content;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public VpnOnOff getContent() {
		return content;
	}

	public void setContent(VpnOnOff content) {
		this.content = content;
	}
}
