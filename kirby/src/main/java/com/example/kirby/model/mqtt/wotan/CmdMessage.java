package com.example.kirby.model.mqtt.wotan;

import com.example.kirby.model.util.CmdType;

public class CmdMessage {
	private CmdType cmd;
	private Long timestamp;
	private Object data;

	public CmdType getCmd() {
		return cmd;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public void setCmd(CmdType cmd) {
		this.cmd = cmd;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "CmdMessage{" + "cmd=" + cmd + ", timestamp='" + timestamp + ", data='" + data + '}';
	}
}