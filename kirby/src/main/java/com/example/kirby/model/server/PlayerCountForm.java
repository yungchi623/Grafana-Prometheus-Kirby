package com.example.kirby.model.server;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class PlayerCountForm {
	@NotBlank
	@Size(min = 3, max = 50)
	private String servername;

	private Long timestamp;

	private int playercount = 0;

	public String getServername() {
		return servername;
	}

	public void setServername(String servername) {
		this.servername = servername;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public int getPlayercount() {
		return playercount;
	}

	public void setPlayercount(int playercount) {
		this.playercount = playercount;
	}

	public LocalDateTime getDateTime() {
		LocalDateTime localDateTime = null;
		if (timestamp != null) {
			localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
		}
		return localDateTime;
	}
}
