package net.eldritch.client.addons;

import java.util.HashMap;

import net.eldritch.client.EldritchClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Whisper {
	
	private static HashMap<String, String> options;
	private static String currentPrefix = "";
	
	public static void WhisperInit() {
		String[] initOptions = {"Enabled(y/n):y","Formats:/msg_{} /pc","Cancel Prefix:/say"};
		EldritchClient.config.initializeOptions("Whisper", initOptions);
		options = EldritchClient.config.getOptionGroup("Whisper");
	}
	
	public static String doWork(String typedMessage) {
		if (!(((String)options.get("Enabled(y/n)")).equals("y"))) return typedMessage;
		
		String[] formats = ((String)options.get("Formats")).split(" ");
		for (String format : formats) {
			int checker = specialStartsWith(format, typedMessage);
			if (checker == -1) continue;
			currentPrefix = typedMessage.substring(0, checker);
			return typedMessage;
		}
		
		if (currentPrefix.length() == 0) {
			return typedMessage;
		} else {
			//check for the cancel prefix
			String prefix = (String) options.get("Cancel Prefix");
			if (typedMessage.startsWith(prefix)) {
				currentPrefix="";
				return typedMessage.substring(prefix.length()).trim();
			} else {
				return currentPrefix + " " + typedMessage;
			}
		}
	}
	
	/**
	 * Returns an index if the strings match, where the regex has underscores rather than spaces (and no spaces) and {} in the regex counts as any string without spaces
	 * Returns -1 otherwise
	 * The index is the length of the total matching string, as {} may make this not the length of regex
	 */
	public static int specialStartsWith(String regex, String string) {
		int ri = 0;
		int si = 0;
		while (true) {
			if (ri == regex.length() && (si==string.length() || string.charAt(si)==' ')) return si;
			if (si == string.length()) return -1;
			
			if (ri != regex.length()-1) //needed for an edge case
			if (regex.charAt(ri)=='{' && regex.charAt(ri+1)=='}' && string.charAt(si)!=' ') {
				ri+=2;
				while (string.charAt(++si)!=' ');
				continue;
			}
			if (regex.charAt(ri)==string.charAt(si)) {ri++; si++; continue;}
			if (string.charAt(si)==' ' && regex.charAt(ri)=='_') {ri++; si++; continue;}
			
			return -1;
		}
	}
}
