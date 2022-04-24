package com.drewchaseproject.mc.fabric.osp;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.network.ServerPlayerEntity;

public class AllPlayersData {

	private List<OSP_PlayerData> datas = new ArrayList();

	public OSP_PlayerData GetOrCreatePlayerData(ServerPlayerEntity player) {
		for (OSP_PlayerData data : datas) {
			if (data.Player.equals(player))
				return data;
		}
		return AddPlayerData(new OSP_PlayerData(player));
	}

	public OSP_PlayerData AddPlayerData(OSP_PlayerData data) {
		datas.add(data);
		return data;
	}

	public void DisconnectAllPlayers() {
		for (OSP_PlayerData player : datas) {
			if (player.IsCurrentlySpectating) {
				player.Disconnect();
			}
		}
	}

}
