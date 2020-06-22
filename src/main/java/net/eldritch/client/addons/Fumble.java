package net.eldritch.client.addons;

import java.util.HashMap;

import net.eldritch.client.EldritchClient;
import net.minecraft.item.CommandBlockItem;
import net.minecraft.item.DebugStickItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.EnchantedGoldenAppleItem;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.item.NetherStarItem;
import net.minecraft.item.SkullItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.TridentItem;
import net.minecraft.item.WrittenBookItem;

public class Fumble {
	private static HashMap<String, String> options;

	public static void fumbleInit() {
		String[] initOptions = { "Enabled(y/n):y", "Dump rares(y/n):n", "Close container after(y/n):n","Protect Player's Inventory(y/n):y" };
		EldritchClient.config.initializeOptions("Fumble", initOptions);
		options = EldritchClient.config.getOptionGroup("Fumble");
	}

	public static void chuckItem() {

	}

	public static boolean enabled() {
		return (options.get("Enabled(y/n)").equals("y"));
	}

	public static boolean shouldDropRares() {
		return (options.get("Dump rares(y/n)").equals("y"));
	}

	public static boolean shouldCloseInventory() {
		return (options.get("Close container after(y/n)").equals("y"));
	}
	public static boolean protectPlayer() {
		return options.get("Protect Player's Inventory(y/n)").equals("y");
	}

	public static boolean isRare(ItemStack item) {
		if (item.getEnchantments().size() > 1)
			return true;
		if (item.hasCustomName())
			return true;
		Class[] rareList = { ElytraItem.class, DebugStickItem.class, EndCrystalItem.class, EnchantedGoldenAppleItem.class,
				HorseArmorItem.class, NameTagItem.class, NetherStarItem.class, SkullItem.class, SpawnEggItem.class, TridentItem.class,
				WrittenBookItem.class, CommandBlockItem.class};
		for (Class rare : rareList)
			if (item.getItem().getClass().equals(rare))
				return true;
		// TODO more sophisticated, such as searching for conflicting enchantments
		return false;
	}
}
