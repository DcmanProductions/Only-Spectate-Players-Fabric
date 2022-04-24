package com.drewchaseproject.mc.fabric.osp.mixin;

import com.drewchaseproject.mc.fabric.osp.OSP_PlayerData;
import com.drewchaseproject.mc.fabric.osp.globals;
import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.source.BiomeAccess;

@Mixin(ServerPlayerEntity.class)
public class PlayerMixin extends ServerPlayerEntity {

	public PlayerMixin(MinecraftServer server, ServerWorld world, GameProfile profile) {
		super(server, world, profile);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void tick(CallbackInfo cb) {
		ServerPlayerEntity player = (ServerPlayerEntity) ((Object) this);
		OSP_PlayerData data = globals.Instance.PlayersData.GetOrCreatePlayerData(player);
		if (player.isSpectator() && player.getCameraEntity() == player && !data.IsPlayerEnteringSpectator && data.IsNonOpSpectator) {
			data.Disconnect();
		} else if (!player.getCameraEntity().getWorld().equals(player.getWorld()) || !player.getCameraEntity().getPos().equals(player.getPos())) {
			data.IsPlayerEnteringSpectator = true;
			ServerPlayerEntity otherPlayer = (ServerPlayerEntity) player.getCameraEntity();
			player.setCameraEntity(player);
			player.teleport((ServerWorld) otherPlayer.getWorld(), otherPlayer.getX(), otherPlayer.getY(), otherPlayer.getZ(), otherPlayer.getYaw(), otherPlayer.getPitch());
			player.setCameraEntity(otherPlayer);
			data.IsPlayerEnteringSpectator = false;
		} else {
			// player.setCameraEntity(player.getCameraEntity());
		}
	}

	@Inject(method = "teleport", at = @At("HEAD"), cancellable = true)
	public void teleport(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo cli) {
		this.sendMessage(Text.of("Teleporting"), false);
		OSP_PlayerData data = globals.Instance.PlayersData.GetOrCreatePlayerData(this);
		if (data.IsPlayerEnteringSpectator) {
			this.sendMessage(Text.of("Super Secrete Teleporting"), false);
			this.setCameraEntity(this);
			this.stopRiding();
			ServerWorld serverWorld = this.getWorld();
			WorldProperties worldProperties = targetWorld.getLevelProperties();
			this.networkHandler.sendPacket(new PlayerRespawnS2CPacket(targetWorld.method_40134(), targetWorld.getRegistryKey(), BiomeAccess.hashSeed(targetWorld.getSeed()), this.interactionManager.getGameMode(), this.interactionManager.getPreviousGameMode(), targetWorld.isDebugWorld(), targetWorld.isFlat(), true));
			this.networkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
			this.server.getPlayerManager().sendCommandTree(this);
			serverWorld.removePlayer(this, Entity.RemovalReason.CHANGED_DIMENSION);
			this.unsetRemoved();
			this.refreshPositionAndAngles(x, y, z, yaw, pitch);
			this.setWorld(targetWorld);
			targetWorld.onPlayerTeleport(this);
			this.worldChanged(serverWorld);
			this.networkHandler.requestTeleport(x, y, z, yaw, pitch);
			this.server.getPlayerManager().sendWorldInfo(this, targetWorld);
			this.server.getPlayerManager().sendPlayerStatus(this);
			cli.cancel();
		}
	}

	@Inject(method = "onDisconnect", at = @At("HEAD"))
	private void disconnect(CallbackInfo ci) {
		ServerPlayerEntity player = (ServerPlayerEntity) ((Object) this);
		OSP_PlayerData data = globals.Instance.PlayersData.GetOrCreatePlayerData(player);
		if (player.isSpectator() && data.IsNonOpSpectator) {
			data.Disconnect();
		}

	}
}