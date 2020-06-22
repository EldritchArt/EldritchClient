package net.eldritch.client.addons;

import java.util.HashMap;

import net.eldritch.client.EldritchClient;

public class Nescience {
	private static HashMap<String,String> options;
	
	public static void nescienceInit() {
		String[] defaultList = {"Enabled(y/n):n","Text:"};
		EldritchClient.config.initializeOptions("Nescience", defaultList);
		options = EldritchClient.config.getOptionGroup("Nescience");
	}
	
	public static boolean enabled() {
		return (options.get("Enabled(y/n)").equals("y"));
	}
	public static String getText() {
		return options.get("Text");
	}
}
