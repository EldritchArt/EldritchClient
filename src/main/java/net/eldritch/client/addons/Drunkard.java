package net.eldritch.client.addons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import net.eldritch.client.EldritchClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Hand;

public class Drunkard {
	private static HashMap<String, String> options;
	private static ClientPlayerEntity player;
	private static int lastHotbarSlot;
	private static ClientPlayerInteractionManager interactor;
	private static int isDrinking = 0;
	private static final int startIndex = 0;
	private static final int endIndex = 36;

	public static void drunkardInit() {
		String[] initOptions = { "Enabled(y/n):n", "Seconds before drink:4" };
		EldritchClient.config.initializeOptions("Drunkard", initOptions);
		options = EldritchClient.config.getOptionGroup("Drunkard");
	}

	public static void stayDrunk() {
		if (!enabled()) {
			isDrinking = 0;
			return;
		}
		if (isDrinking < 1) {
			player = MinecraftClient.getInstance().player;
			interactor = MinecraftClient.getInstance().interactionManager;
			lastHotbarSlot = -1;

			ArrayList<StatusEffect> menu = whatsForLunch();
			if (menu.isEmpty())
				return;
			if (!holdingBeer(menu)) {
				lastHotbarSlot = getPotionSlot(menu);
				if (lastHotbarSlot == -1)
					return;
				Caravan.swapSlots(MinecraftClient.getInstance().interactionManager, player, lastHotbarSlot,
						getHotbarSlot());
			}
			compactBottles();
			enjoyFineSpirits();
			isDrinking = 100;
		} else {
			isDrinking--;
		}
	}

	public static void enjoyFineSpirits() {
		MinecraftClient.getInstance().options.keyUse.setPressed(true);
		interactor.interactItem(player, MinecraftClient.getInstance().world, Hand.MAIN_HAND);
	}

	public static void barFight() {
		MinecraftClient.getInstance().options.keyUse.setPressed(false);
	}

	public static int getPotionSlot(ArrayList<StatusEffect> menu) {
		for (int i = startIndex; i < endIndex; i++) {
			if (isBeer(menu, i))
				return i;
		}
		return -1;
	}

	public static int getHotbarSlot() {
		return player.inventory.selectedSlot;
	}

	public static boolean holdingBeer(ArrayList<StatusEffect> menu) {
		return isBeer(menu, getHotbarSlot());
	}

	public static boolean isBeer(ArrayList<StatusEffect> brew, int slot) {
		ItemStack currentItem = player.inventory.getInvStack(slot);
		if (currentItem.getItem() != Items.POTION)
			return false;
		for (StatusEffectInstance effect : PotionUtil.getPotionEffects(currentItem)) {
			for (StatusEffect menuOption : brew) {
				if (effect.getEffectType().getName().equals(menuOption.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean enabled() {
		return options.get("Enabled(y/n)").equals("y");
	}

	public static ArrayList<StatusEffect> whatsForLunch() {
		Collection<StatusEffectInstance> effects = player.getStatusEffects();
		Iterator<StatusEffectInstance> effectsIterator = effects.iterator();
		ArrayList<StatusEffect> retval = new ArrayList<StatusEffect>();

		while (effectsIterator.hasNext()) {
			StatusEffectInstance currentEffect = effectsIterator.next();
			if (currentEffect.getDuration() < getTime()) {
				retval.add(currentEffect.getEffectType());
			}
		}

		return retval;
	}

	public static void compactBottles() {
		int bottleSlot = -1;
		for (int i = startIndex; i < endIndex; i++) {
			if (player.inventory.getInvStack(i).getItem() == Items.GLASS_BOTTLE) {
				if (bottleSlot == -1)
					bottleSlot = i;
				else {
					Caravan.swapSlots(interactor, player, i, bottleSlot);
				}
			}
		}
	}

	public static int getTime() {
		return 20 * Integer.parseInt(options.get("Seconds before drink"));
	}
}
