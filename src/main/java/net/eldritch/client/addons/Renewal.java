package net.eldritch.client.addons;

import java.util.HashMap;

import net.eldritch.client.Armoury;
import net.eldritch.client.EldritchClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
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
		boolean needsMove;
		if (isOffhand) {
			needsMove = inv.offHand.size() > 0 && inv.offHand.get(0).isDamageable()
					&& inv.offHand.get(0).getDamage() == 0;
		} else {
			ItemStack handStack = inv.getInvStack(inv.selectedSlot);
			needsMove = handStack.isDamageable() && handStack.getDamage() == 0;
		}

		if (!needsMove)
			return;

		int movingSlot = getMendingItemSlot(inv);
		if (-1 == movingSlot) {
			// move it to an empty inventory slot if we have one, to allow say autototem to
			// do its magic
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
			ItemStack stack = inv.getInvStack(freeSlot);
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
			ItemStack nextItem = inv.getInvStack(i);
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
