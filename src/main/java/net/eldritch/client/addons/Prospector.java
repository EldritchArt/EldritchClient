package net.eldritch.client.addons;

import java.util.HashMap;

import net.eldritch.client.EldritchClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class Prospector {
	private static HashMap<String, String> options;

	public static void prospectorInit() {
		String[] initOptions = {"Enabled(y/n):y"};
		EldritchClient.config.initializeOptions("Prospector", initOptions);
		options = EldritchClient.config.getOptionGroup("Prospector");
	}
	
	public static boolean enabled() {
		return (((String)options.get("Enabled(y/n)")).equals("y"));
	}
	
	//TO-DO check for enchantments that have higher level than vanilla limit
	public static boolean hasConflictingEnchants(ItemStack item) {
		if (!item.hasEnchantments()) return false;
		ListTag enchants = item.getEnchantments();
		if (enchants.size() == 1) return false;
		HashMap<String, Boolean> enchantMap = new HashMap<String, Boolean>(enchants.size()*2);
		
		for (int i = 0; i < enchants.size(); i++) {
			Tag tag = enchants.get(i);
			String id = getEnchID(tag.asString());
			if (enchantMap.containsKey(id)) return true;
			enchantMap.put(id, true);
		}
		//check for conflicting enchants like multishot and piercing
		String[] conflictBow = {"infinity","mending"};
		String[] conflictXBow = {"piercing","multishot"};
		String[] conflictArmour = {"protection","fire_protection","blast_protection","projectile_protection"};
		String[] conflictSwords = {"sharpness","smite","bane_of_arthropods"};
		String[] conflictTridents = {"riptide","loyalty"};
		if (countIn(conflictBow,enchantMap) > 1) return true;
		if (countIn(conflictXBow,enchantMap) > 1) return true;
		if (countIn(conflictArmour,enchantMap) > 1) return true;
		if (countIn(conflictSwords,enchantMap) > 1) return true;
		if (countIn(conflictTridents,enchantMap) > 1) return true;
		return false;
	}

	public static String getEnchID(String tag) {
		String start = tag.substring(tag.indexOf("id:\"")+4);
		int endIndex = start.indexOf("\""); //remove stuff afterwards
		int startIndex = (start.startsWith("minecraft:")) ? 10 : 0; //remove minecraft: prefix
		return start.substring(startIndex, endIndex);
	}
	
	private static int countIn(String[] ids, HashMap<String, Boolean> enchantMap) {
		int retval = 0;
		for (String id : ids)
			if (enchantMap.containsKey(id))
				retval++;
		return retval;
	}
}
