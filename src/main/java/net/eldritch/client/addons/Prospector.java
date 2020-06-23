package net.eldritch.client.addons;

import java.util.HashMap;

import net.eldritch.client.EldritchClient;

public class Prospector {
	private static HashMap<String, String> options;

	public static void prospectorInit() {
		String[] initOptions = {"Enabled(y/n):y"};
		EldritchClient.config.initializeOptions("Prospector", initOptions);
		options = EldritchClient.config.getOptionGroup("Prospector");
	}
}
