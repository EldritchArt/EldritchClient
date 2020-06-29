package net.eldritch.client.addons;

import java.util.HashMap;

import net.eldritch.client.EldritchClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class Renewal {
	private static HashMap<String, String> options;

	public static void RenewalInit() {
		String[] initOptions = { "Enabled(y/n):n", "Offhand(y/n):n" };
		EldritchClient.config.initializeOptions("Renewal", initOptions);
		options = EldritchClient.config.getOptionGroup("Renewal");
	}

	public static void update() {
		if (!enabled())
			return;

		MinecraftClient mc = MinecraftClient.getInstance();
		PlayerInventory inv = mc.player.inventory;

		if (!Armoury.safeScreen(mc.currentScreen))
			return;

		boolean isOffhand = options.get("Offhand(y/n)").equals("y");
		ItemStack currentStack;
		if (isOffhand) {
			currentStack = (inv.offHand.size() > 0) ? inv.offHand.get(0) : null;
		} else {
			currentStack = inv.getStack(inv.selectedSlot);
		}
		boolean needsMove = currentStack.isDamageable() && currentStack.getDamage() == 0;
		Item[] keepInOffhand = {Items.FLINT_AND_STEEL, Items.SHIELD, Items.DIAMOND_HOE, Items.BOW, Items.CROSSBOW};
		for (Item keep : keepInOffhand)
			if (keep == currentStack.getItem())
				needsMove = false;

		if (!needsMove)
			return;

		int movingSlot = getMendingItemSlot(inv);
		if (-1 == movingSlot) {
			removeOffhand();
			return;
		}
		if (isOffhand) {
			Caravan.swapSlots(mc.interactionManager, mc.player, movingSlot, Caravan.OFFHAND);
		} else {
			Caravan.swapSlots(mc.interactionManager, mc.player, movingSlot, inv.selectedSlot);
		}
	}

	private static void removeOffhand() {
		MinecraftClient mc = MinecraftClient.getInstance();
		PlayerInventory inv = mc.player.inventory;

		int freeSlot;
		for (freeSlot = 35; freeSlot >= 0; freeSlot--) {
			ItemStack stack = inv.getStack(freeSlot);
			if (stack.getItem() == Items.AIR || stack.getItem() == Items.TOTEM_OF_UNDYING) {
				break;
			}
		}

		if (freeSlot == -1)
			return;
		Caravan.swapSlots(mc.interactionManager, mc.player, freeSlot, Caravan.OFFHAND);
	}

	private static boolean enabled() {
		return options.get("Enabled(y/n)").equals("y");
	}

	private static int getMendingItemSlot(PlayerInventory inv) {
		for (int i = 0; i < 36; i++) {
			ItemStack nextItem = inv.getStack(i);
			if (!nextItem.hasEnchantments())
				continue;
			if (nextItem.getDamage() == 0)
				continue;
			ListTag enchants = nextItem.getEnchantments();

			for (int j = 0; j < enchants.size(); j++) {
				Tag tag = enchants.get(j);
				String id = Prospector.getEnchID(tag.asString());
				if (id.equals("mending"))
					return i;
			}
		}
		return -1;
	}
}
