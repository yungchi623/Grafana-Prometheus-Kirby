package com.example.kirby.model.mqtt.router;

import com.example.kirby.model.util.CmdType;

public class CmdResponse {
	private CmdType cmd;
	private Long timestamp;
	private String result;

	public CmdType getCmd() {
		return cmd;
	}

	public void setCmd(CmdType cmd) {
		this.cmd = cmd;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "CmdResponse{" + "cmd=" + cmd + ", timestamp='" + timestamp + ", result='" + result + '\'' + '}';
	}
}
