package com.drewchaseproject.mc.fabric.osp.mixin;

import com.drewchaseproject.mc.fabric.osp.globals;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public class ServerMixin {

	@Inject(method = "shutdown", at = @At("HEAD"))
	private void shutdown(CallbackInfo info) {
		globals.Instance.PlayersData.DisconnectAllPlayers();
	}
}
