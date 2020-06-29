package net.eldritch.client.addons;

import java.util.HashMap;

import net.eldritch.client.EldritchClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;

//TODO better detection of logging in to a server
public class AutoLog {
	private static HashMap<String, String> options;
	private static PlayerInventory inv;
	
	public static void autologInit() {
		String[] initOptions = { "Enabled(y/n):n", "Totems:0", "Elytra:0", "Test:0" };
		EldritchClient.config.initializeOptions("AutoLog", initOptions);
		options = EldritchClient.config.getOptionGroup("AutoLog");
	}

	public static void update() {
		if (!enabled())
			return;
		inv = MinecraftClient.getInstance().player.inventory;

		logIfInsufficient(countNumber(Items.TOTEM_OF_UNDYING), "Totems","[Eldritch Client] Less than %d totems");
		logIfInsufficient(countElytra(), "Elytra","[Eldritch Client] Less than %d Elytra");
		logIfInsufficient((int)MinecraftClient.getInstance().player.getY(),"Test", "Quitting %d");
	}

	private static boolean enabled() {
		return options.get("Enabled(y/n)").equals("y");
	}

	public static void logIfInsufficient(int itemCount, String itemId, String cause) {
		if (itemCount < Integer.parseInt(options.get(itemId))) {
			cause = cause.replace(" %d ", " "+Integer.parseInt(options.get(itemId))+" ");
			MinecraftClient.getInstance().getNetworkHandler().getConnection().disconnect(new LiteralText(cause));
			options.put("Enabled(y/n)", "n");
		}
	}

	public static int countNumber(Item item) {
		int retval = 0;
		for (int i = 0; i < 36; i++) {
			ItemStack stack = inv.getStack(i);
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
			ItemStack stack = inv.getStack(i);
			if (stack.getItem().equals(Items.ELYTRA) && stack.getDamage() < 20)
				retval++;
		}
		if (inv.getArmorStack(2).getItem().equals(Items.ELYTRA) && inv.getArmorStack(2).getDamage() < 20) {
			retval++;
		}
		if (inv.getCursorStack().getItem().equals(Items.ELYTRA) && inv.getCursorStack().getDamage() < 20) {
			retval++;
		}
		return retval;
	}
}
