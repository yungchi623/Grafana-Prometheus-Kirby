package com.example.kirby.model.mqtt.router;

public class DeviceInfo {
	private Integer devType;
	private Integer devStatus;
	private String devName;
	private String devIP;

	public Integer getDevType() {
		return devType;
	}

	public void setDevType(Integer devType) {
		this.devType = devType;
	}

	public Integer getDevStatus() {
		return devStatus;
	}

	public void setDevStatus(Integer devStatus) {
		this.devStatus = devStatus;
	}

	public String getDevName() {
		return devName;
	}

	public void setDevName(String devName) {
		this.devName = devName;
	}

	public String getDevIP() {
		return devIP;
	}

	public void setDevIP(String devIP) {
		this.devIP = devIP;
	}
}
