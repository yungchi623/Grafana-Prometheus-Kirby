package com.example.kirby.model.mqtt.router;

import java.util.ArrayList;

public class DeviceInfoRsp {
	private Integer status = -1;
	private ArrayList<DeviceInfo> content = new ArrayList<DeviceInfo>();

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public ArrayList<DeviceInfo> getContent() {
		return content;
	}

	public void setContent(ArrayList<DeviceInfo> content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "DeviceInfoRsp{" + "status=" + status + ", content='" + content + '}';
	}
}
