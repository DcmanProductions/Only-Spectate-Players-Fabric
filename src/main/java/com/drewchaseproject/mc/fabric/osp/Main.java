package com.drewchaseproject.mc.fabric.osp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class Main implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("osp");

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> OnlySpectatePlayers.register(dispatcher));
	}
}
