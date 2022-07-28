package com.example.kirby.model.mqtt.router;

public class HostInfo {
	private String fwVersion;
	private String devName;
	private Integer devType;
	private String devIP;
	private Integer devStatus;
	private Integer gameId;

	public String getFwVersion() {
		return fwVersion;
	}

	public void setFwVersion(String fwVersion) {
		this.fwVersion = fwVersion;
	}

	public String getDevName() {
		return devName;
	}

	public void setDevName(String devName) {
		this.devName = devName;
	}

	public Integer getDevType() {
		return devType;
	}

	public void setDevType(Integer devType) {
		this.devType = devType;
	}

	public String getDevIP() {
		return devIP;
	}

	public void setDevIP(String devIP) {
		this.devIP = devIP;
	}

	public Integer getDevStatus() {
		return devStatus;
	}

	public void setDevStatus(Integer devStatus) {
		this.devStatus = devStatus;
	}

	public Integer getGameId() {
		return gameId;
	}

	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}
}
