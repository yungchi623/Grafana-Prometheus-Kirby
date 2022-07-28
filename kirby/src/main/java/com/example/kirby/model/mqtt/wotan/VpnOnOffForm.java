package com.example.kirby.model.mqtt.wotan;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

public class VpnOnOffForm {
	@NotBlank
	@Size(min = 3, max = 50)
	private String serialno;
	@Range(min = 0, max = 1)
	private int vpnonoff;
	@NotBlank
	private String devip;

	public String getSerialno() {
		return serialno;
	}

	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}

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
