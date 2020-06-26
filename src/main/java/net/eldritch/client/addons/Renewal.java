package net.eldritch.client.addons;

import java.util.HashMap;

import net.eldritch.client.EldritchClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class Renewal {
	private static HashMap<String, String> options;

	public static void RenewalInit() {
		String[] initOptions = {"Enabled(y/n):n", "Offhand(y/n):n"};
		EldritchClient.config.initializeOptions("Renewal", initOptions);
		options = EldritchClient.config.getOptionGroup("Renewal");
	}
	
	public static void update() {
		MinecraftClient mc = MinecraftClient.getInstance();
		PlayerInventory inv = mc.player.inventory;
		
		boolean isOffhand = options.get("Offhand(y/n)").equals("y");
		boolean needsMove;
		if (isOffhand) {
			needsMove = inv.offHand.size() > 0 && inv.offHand.get(0).getDamage() == 0;
		} else {
			ItemStack handStack = inv.getInvStack(inv.selectedSlot);
			needsMove = handStack.isDamageable() && handStack.getDamage() == 0;
		}
		if (!needsMove) return;
		int movingSlot = getMendingItemSlot(inv);
		if (-1 == movingSlot) {
			options.put("Enabled(y/n)", "n");
			return;
		}
		if (isOffhand) {
			Caravan.swapSlots(mc.interactionManager, mc.player, movingSlot, Caravan.OFFHAND);
		} else {
			Caravan.swapSlots(mc.interactionManager, mc.player, movingSlot, inv.selectedSlot);
		}
	}

	private static int getMendingItemSlot(PlayerInventory inv) {
		for (int i = 0; i < 36; i++) {
			ItemStack nextItem = inv.getInvStack(i);
			if (!nextItem.hasEnchantments()) continue;
			if (nextItem.getDamage() == 0) continue;
			ListTag enchants = nextItem.getEnchantments();
			
			for (int j = 0; j < enchants.size(); j++) {
				Tag tag = enchants.get(j);
				String id = Prospector.getEnchID(tag.asString());
				if (id.equals("mending")) return i;
			}
		}
		return -1;
	}
}
