package net.eldritch.client.addons;

import java.util.HashMap;

import net.eldritch.client.EldritchClient;

//this might be really inneficcient and slow lol
public class Censor {
	private static HashMap<String, String> options;

	public static void censorInit() {
		String[] initOptions = { "Enabled(y/n):y", "Redact:", "Temp:", "Ignore:gnlrlleb whats2+2?",
				"Similar Characters:l!1i| e3 s5 o0", "Trimmed Characters: _-:;'", "See coloured chat(y/n):y"};
		EldritchClient.config.initializeOptions("Censor", initOptions);
		options = EldritchClient.config.getOptionGroup("Censor");
		options.put("Temp", "");
		String[] similarList = ((String) options.get("Similar Characters")).split(" ");

		// Fix a quirk in my implementation that would make certain combinations of
		// redacted and similar characters not show up.
		// This means that when inputting temp ignores you have to also make sure that
		// they use the first character of the list of similar characters
		options.put("Redact", getSimilarString((String) options.get("Redact"), similarList));
		options.put("Ignore", getSimilarString((String) options.get("Ignore"), similarList));
	}

	public static String doCensor(String string) {
		// get all the redacted and ignore sequences
		String[] redactList = ((String) options.get("Redact")).split(" ");
		String[] moreRedact = ((String) options.get("Temp")).split(" ");
		String[] ignoreList = ((String) options.get("Ignore")).split(" ");
		String[] similarList = ((String) options.get("Similar Characters")).split(" ");
		String trimChars = (String) options.get("Trimmed Characters");
		// prepare
		String simString = getSimilarString(string, similarList);
		StringPair group = new StringPair(string, simString);
		// remove offending words without blocking the whole message
		for (String redacted : redactList)
			if (redacted.length() > 0)
				doRedact(group, redacted, trimChars);
		for (String redacted : moreRedact)
			if (redacted.length() > 0)
				doRedact(group, redacted, trimChars);
		// remove the entire message if especially bad
		group.b = snipSnip(group.b, trimChars);
		for (String redacted : ignoreList)
			if (redacted.length() > 0)
				if (group.b.contains(redacted))
					return null;

		if (group.a.length() == 0)
			return null;
		return group.a;
	}

	/**
	 * The most overengineered useless method ever But it was a fun challenge to
	 * write
	 */
	private static void doRedact(StringPair msg, String redact, String trimChars) {
		int suspectIndex = 0;
		int redactIndex = 0;
		for (int i = 0; i < msg.a.length(); i++) {
			if (trimChars.contains(Character.toString(msg.b.charAt(i))))
				continue;
			if (msg.b.charAt(i) != redact.charAt(redactIndex++)) {
				redactIndex = 0;
				suspectIndex = i + 1;
			} else {
				if (redactIndex == redact.length()) {
					msg.a = msg.a.substring(0, suspectIndex) + msg.a.substring(i + 1, msg.a.length());
					msg.b = msg.b.substring(0, suspectIndex) + msg.b.substring(i + 1, msg.b.length());
					i = suspectIndex - 1;
					redactIndex = 0;
				}
			}
		}
	}

	private static String getSimilarString(String string, String[] similar) {
		string = string.toLowerCase();
		for (String sim : similar) {
			String updated = "";
			for (int i = 0; i < string.length(); i++) {
				if (sim.contains(Character.toString(string.charAt(i)))) {
					updated += sim.charAt(0);
				} else
					updated += string.charAt(i);
			}
			string = updated;
		}
		return string;
	}

	private static String snipSnip(String s, String regex) {
		String retval = "";
		for (int i = 0; i < s.length(); i++) {
			if (regex.contains(Character.toString(s.charAt(i))))
				continue;
			retval += s.charAt(i);
		}
		return retval;
	}

	private static class StringPair { // this needs to be static cause java dumb, luckily its not an issue
		public StringPair(String a, String b) {
			this.a = a;
			this.b = b;
		}

		public String a;
		public String b;
	}

	public static boolean enabled() {
		return (options.get("Enabled(y/n)").equals("y"));
	}
	
	public static boolean colourEnabled() {
		return options.get("See coloured chat(y/n)").equals("y");
	}
}
