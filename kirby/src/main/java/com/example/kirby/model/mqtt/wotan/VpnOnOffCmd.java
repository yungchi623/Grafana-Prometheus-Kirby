package com.example.kirby.model.mqtt.wotan;

public class VpnOnOffCmd {
	private int vpnonoff;
	private String devip;

	public int getVpnonoff() {
		return vpnonoff;
	}

	public void setVpnonoff(int vpnonoff) {
		this.vpnonoff = vpnonoff;
	}

	public String getDevip() {
		return devip;
	}

	public void setDevip(String devip) {
		this.devip = devip;
	}
}
