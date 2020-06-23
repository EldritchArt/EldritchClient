package net.eldritch.client.addons;

import java.util.HashMap;

import net.eldritch.client.EldritchClient;

public class Prospector {
	private static HashMap<String, String> options;

	public static void prospectorInit() {
		String[] initOptions = { "Enabled(y/n):n", "Totems:0", "Elytra:0", "Test:0" };
		EldritchClient.config.initializeOptions("AutoLog", initOptions);
		options = EldritchClient.config.getOptionGroup("AutoLog");
	}
}
