package net.eldritch.client.addons;

import java.util.HashMap;

import net.eldritch.client.EldritchClient;

public class Insomnia {
	private static HashMap<String,String> options;
	
	public static void insomniaInit() {
		String[] defaultList = {"Enabled(y/n):n", "Adventure(y/n):n"};
		EldritchClient.config.initializeOptions("Insomnia", defaultList);
		options = EldritchClient.config.getOptionGroup("Insomnia");
	}
	
	public static boolean enabled() {
		return (options.get("Enabled(y/n)").equals("y"));
	}
	public static boolean adventureEnabled() {
		return (options.get("Adventure(y/n)").equals("y"));
	}
}
