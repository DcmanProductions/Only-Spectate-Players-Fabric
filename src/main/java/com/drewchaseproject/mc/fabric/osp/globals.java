package com.drewchaseproject.mc.fabric.osp;

public class globals {
	public AllPlayersData PlayersData = new AllPlayersData();
	public static globals Instance = new globals();

	private globals() {
		Instance = this;
	}
}
