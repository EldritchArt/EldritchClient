package net.eldritch.client.addons;

import java.util.HashMap;

import net.eldritch.client.EldritchClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class AutoLog {
	private static HashMap<String, String> options;
	private static PlayerInventory inv;

	public static void autologInit() {
		String[] initOptions = { "Enabled(y/n):n", "Totems:0", "Elytra:0" };
		EldritchClient.config.initializeOptions("AutoLog", initOptions);
		options = EldritchClient.config.getOptionGroup("AutoLog");
	}

	public static void update() {
		if (!enabled())
			return;
		if (MinecraftClient.getInstance().currentScreen != null)
			return;
		inv = MinecraftClient.getInstance().player.inventory;

		logIfInsufficient(countNumber(Items.TOTEM_OF_UNDYING), "Totems");
		logIfInsufficient(countElytra(), "Elytra");
	}

	private static boolean enabled() {
		return options.get("Enabled(y/n)").equals("y");
	}

	public static void logIfInsufficient(int itemCount, String itemId) {
		if (itemCount < Integer.parseInt(options.get(itemId))) {
			options.put("Enabled(y/n)", "n");
			MinecraftClient.getInstance().world.disconnect();
		}
	}

	public static int countNumber(Item item) {
		int retval = 0;
		for (int i = 0; i < 36; i++) {
			ItemStack stack = inv.getInvStack(i);
			if (stack.getItem().equals(item))
				retval++;
		}
		for (int i = 0; i < inv.offHand.size(); i++)
			if (inv.offHand.get(i).getItem() == item)
				retval++;
		return retval;
	}

	public static int countElytra() {
		int retval = 0;
		for (int i = 0; i < 36; i++) {
			ItemStack stack = inv.getInvStack(i);
			if (stack.getItem().equals(Items.ELYTRA) && stack.getDamage() < 20)
				retval++;
		}
		if (inv.getArmorStack(2).getItem().equals(Items.ELYTRA) && inv.getArmorStack(2).getDamage() < 20) {
			retval++;
		}
		return retval;
	}
}
