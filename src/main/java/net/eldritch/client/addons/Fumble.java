package net.eldritch.client.addons;

import java.util.HashMap;

import net.eldritch.client.EldritchClient;
import net.minecraft.item.CommandBlockItem;
import net.minecraft.item.DebugStickItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.EnchantedGoldenAppleItem;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
		//optimization idea: use a map for the rare items list
		final Class[] rareClassList = {SkullItem.class, SpawnEggItem.class};
		final Item[] rareItemsList = {Items.ELYTRA, Items.DEBUG_STICK, Items.END_CRYSTAL, Items.END_PORTAL_FRAME, Items.ENCHANTED_GOLDEN_APPLE,
				Items.DIAMOND_HORSE_ARMOR, Items.GOLDEN_HORSE_ARMOR, Items.IRON_HORSE_ARMOR, Items.NETHER_STAR, Items.TRIDENT, Items.WRITTEN_BOOK,
				Items.SHULKER_SHELL, Items.BLACK_SHULKER_BOX, Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.CYAN_SHULKER_BOX,
				Items.GRAY_SHULKER_BOX, Items.GREEN_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX, Items.LIGHT_GRAY_SHULKER_BOX, Items.LIME_SHULKER_BOX,
				Items.MAGENTA_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.PURPLE_SHULKER_BOX, Items.RED_SHULKER_BOX, Items.SHULKER_BOX,
				Items.WHITE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX, Items.FILLED_MAP};
		for (Class rare : rareClassList)
			if (item.getItem().getClass().equals(rare))
				return true;
		for (Item rareItem : rareItemsList)
			if (item.getItem() == rareItem)
				return true;
		// TODO more sophisticated, such as searching for conflicting enchantments
		return false;
	}
}
