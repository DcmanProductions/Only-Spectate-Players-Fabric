package com.drewchaseproject.mc.fabric.osp;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.client.util.math.Vector3d;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.source.BiomeAccess;

public class OnlySpectatePlayers {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("osp").then(CommandManager.argument("player", EntityArgumentType.players()).executes(context -> SpectatePlayer(context.getSource(), EntityArgumentType.getPlayer(context, "player")))).then(CommandManager.literal("test").executes(context -> {
            ServerCommandSource src = context.getSource();
            ServerPlayerEntity player;
            try {
                player = src.getPlayer();
            } catch (CommandSyntaxException e) {
                player = null;
                src.sendError(Text.Serializer.fromJson("{\"text\": \"This Command Only Works as Player\"}"));
                return 1;
            }
            return 0;
        })));
    }

    private static int SpectatePlayer(ServerCommandSource src, ServerPlayerEntity otherPlayer) {
        ServerPlayerEntity player;
        try {
            player = src.getPlayer();
        } catch (CommandSyntaxException e) {
            player = null;
            src.sendError(Text.Serializer.fromJson("{\"text\": \"This Command Only Works as Player\"}"));
            return 1;
        }
        OSP_PlayerData data = globals.Instance.PlayersData.GetOrCreatePlayerData(player);
        if (data.IsCurrentlySpectating) {
            player.sendMessage(Text.Serializer.fromJson("{\"text\": \"Command can't be run while Spectating\"}"), true);
        } else if (otherPlayer.canBeSpectated(player) || !otherPlayer.isSpectator()) {
            data.IsPlayerEnteringSpectator = true;
            data.IsNonOpSpectator = true;
            data.Location = new Vector3d(player.getX(), player.getY(), player.getZ());
            data.World = player.getWorld();
            data.Yaw = player.getYaw();
            data.Pitch = player.getPitch();

            player.changeGameMode(GameMode.SPECTATOR);
            player.teleport(otherPlayer.getWorld(), otherPlayer.getX(), otherPlayer.getY(), otherPlayer.getZ(), otherPlayer.getYaw(), otherPlayer.getPitch());
        }

        return 0;
    }
}