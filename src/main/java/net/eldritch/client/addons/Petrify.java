package net.eldritch.client.addons;

import java.util.HashMap;

import net.eldritch.client.EldritchClient;

public class Petrify {
	private static HashMap<String, String> options;

	public static void PetrifyInit() {
		String[] initOptions = {"Enabled(y/n):n", "Vanilla Range(y/n):n", "Custom Range:4.25"};
		EldritchClient.config.initializeOptions("Petrify", initOptions);
		options = EldritchClient.config.getOptionGroup("Petrify");
	}
	
	
}
