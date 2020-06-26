package net.eldritch.client.addons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import net.eldritch.client.EldritchClient;
import net.minecraft.client.MinecraftClient;

//could not get the detection for this to work
public class AutoEZ {
	private static HashMap<String, String> options;
	private static Random rand = new Random();

	public static void autoEZInit() {
		String[] initOptions = { "Enabled(y/n):n", "Messages:EZ" };
		EldritchClient.config.initializeOptions("AutoEZ", initOptions);
		options = EldritchClient.config.getOptionGroup("AutoEZ");

	}

	public static void sayEZ() {
		MinecraftClient.getInstance().player.sendChatMessage("<AutoEZ> " + getMessage());
	}

	public static String getMessage() {
		ArrayList<String> messages = divideIntoMessages(options.get("Messages"));
		if (messages.size() == 0)
			return "";
		int index = rand.nextInt(messages.size());
		return messages.get(index);
	}

	public static ArrayList<String> divideIntoMessages(String format) {
		ArrayList<String> list = new ArrayList<String>();
		format = format + " ";

		String toAdd = "";
		for (int i = 0; i < format.length(); i++) {
			char adding = format.charAt(i);
			switch (adding) {
			case ' ':
				if (toAdd.length() == 0)
					break;
				list.add(toAdd);
				toAdd = "";
				break;
			case '\\':
				toAdd += format.charAt(++i);
				break;
			case '_':
				toAdd += ' ';
				break;
			default:
				toAdd += adding;
			}
		}

		return list;
	}
}
