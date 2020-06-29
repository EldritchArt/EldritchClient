package net.eldritch.client.addons;

import java.util.HashMap;

import net.eldritch.client.EldritchClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class Winged {
	private static HashMap<String, String> options;
	private static int delay = 0;
	public static final int ELYTRA_DANGEROUS = 429;

	public static void WingedInit() {
		String[] initOptions = { "Enabled(y/n):n", "Repair(y/n):n" };
		EldritchClient.config.initializeOptions("Winged", initOptions);
		options = EldritchClient.config.getOptionGroup("Winged");
	}

	private static boolean enabled() {
		return options.get("Enabled(y/n)").equals("y");
	}

	private static boolean isRepairMode() {
		return options.get("Repair(y/n)").equals("y");
	}

	public static void update() {
		if (!enabled())
			return;
		if (delay > 0) {
			delay--;
			return;
		}
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		PlayerInventory inv = player.inventory;

		if (inv.getArmorStack(2).getItem() == Items.ELYTRA) {
			if (isRepairMode()) {
				if (inv.getArmorStack(2).getDamage() == 0) { // take off healed wings
					int slot = findElytraSlot(1, 500, inv);
					if (slot != -1) {
						Caravan.swapSlots(MinecraftClient.getInstance().interactionManager, player, slot,
								Caravan.CHESTPLATE);
						freeCursorStack(inv);
					} else
						delay = 10;
				}
			} else {
				if (inv.getArmorStack(2).getDamage() >= 429) { // take off broken wings
					int slot = findBestElytraSlot(inv);
					if (slot != -1) {
						Caravan.swapSlots(MinecraftClient.getInstance().interactionManager, player, slot,
								Caravan.CHESTPLATE);
						freeCursorStack(inv);
					} else
						delay = 10;
				}
			}
		}
	}

	public static void freeCursorStack(PlayerInventory inv) {
		if (inv.getCursorStack().getItem() != Items.AIR) {
			int freeSlot = inv.getEmptySlot();
			if (freeSlot == -1) {
				MinecraftClient.getInstance().openScreen(null);
				return;
			}
			if (freeSlot < 9)
				freeSlot += 36;
			MinecraftClient.getInstance().interactionManager.clickSlot(0, freeSlot, 0, SlotActionType.PICKUP,
					inv.player);
		}
	}

	public static int findBestElytraSlot(PlayerInventory inv) {
		int bestDamage = 431;
		int bestSlot = -1;
		for (int i = 0; i < 36; i++) {
			ItemStack stack = inv.getStack(i);
			if (stack.getItem() == Items.ELYTRA) {
				if (stack.getDamage() < bestDamage) {
					bestDamage = stack.getDamage();
					bestSlot = i;
				}
			}
		}
		return bestSlot;
	}

	private static int findElytraSlot(int minDamage, int maxDamage, PlayerInventory inv) {
		for (int i = 0; i < 36; i++) {
			ItemStack stack = inv.getStack(i);
			if (stack.getItem() == Items.ELYTRA) {
				if (stack.getDamage() < maxDamage && stack.getDamage() > minDamage) {
					return i;
				}
			}
		}
		return -1;
	}
}
