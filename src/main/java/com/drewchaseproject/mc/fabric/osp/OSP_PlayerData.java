package com.drewchaseproject.mc.fabric.osp;

import net.minecraft.client.util.math.Vector3d;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;

public class OSP_PlayerData {
	public final ServerPlayerEntity Player;

	public Vector3d Location;
	public float Pitch;
	public float Yaw;
	public ServerWorld World;
	public GameMode gameMode;
	public boolean IsNonOpSpectator = false;
	public boolean IsPlayerEnteringSpectator = false;
	public boolean IsCurrentlySpectating = false;

	public OSP_PlayerData(ServerPlayerEntity player) {
		Player = player;
		Location = new Vector3d(player.getX(), player.getY(), player.getZ());
		World = player.getWorld();
		gameMode = player.isCreative() ? GameMode.CREATIVE : GameMode.SURVIVAL;
		Yaw = player.getYaw();
		Pitch = player.getPitch();
	}

	public void Disconnect() {
		Player.changeGameMode(GameMode.SURVIVAL);
		IsNonOpSpectator = false;
		Player.teleport(World, Location.x, Location.y, Location.z, Yaw, Pitch);
		IsCurrentlySpectating = false;
	}

}
