package net.eldritch.client.addons;

import java.util.HashMap;

import net.eldritch.client.EldritchClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class LogOutSpot {
	private static HashMap<String, String> options;
	private static int lastX;
	private static int lastY;
	private static int lastZ;
	private static boolean shouldRecord = false;

	public static void logSpotInit() {
		String[] defaultList = { "Enabled(y/n):n", "Last X:", "Last Y:", "Last Z:" };
		EldritchClient.config.initializeOptions("LogOutSpot", defaultList);
		options = EldritchClient.config.getOptionGroup("LogOutSpot");
	}

	public static boolean enabled() {
		return options.get("Enabled(y/n)").equals("y");
	}

	public static void recordCoords() {
		if (MinecraftClient.getInstance().player == null) {
			if (shouldRecord) {
				options.put("Last X", "" + lastX);
				options.put("Last Y", "" + lastY);
				options.put("Last Z", "" + lastZ);
				EldritchClient.config.saveConfigFile();
				shouldRecord = false;
			}
		} else {
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			lastX = (int) player.getX();
			lastY = (int) player.getY();
			lastZ = (int) player.getZ();
			shouldRecord = true;
		}
	}
}
