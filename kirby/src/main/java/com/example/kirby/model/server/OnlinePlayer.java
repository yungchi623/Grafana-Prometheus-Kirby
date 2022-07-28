package com.example.kirby.model.server;

public class OnlinePlayer {

	private Long timestamp;

	private int playercount = 0;

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
}
