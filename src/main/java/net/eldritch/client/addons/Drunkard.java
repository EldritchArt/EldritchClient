package net.eldritch.client.addons;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import net.eldritch.client.EldritchClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;

public class Drunkard {
	private static HashMap<String, String> options;
	private static ClientPlayerEntity player;
	private static int lastHotbarSlot;
	private static ClientPlayerInteractionManager interactor;
	
	public static void drunkardInit() {
		String[] initOptions = { "Enabled(y/n):n","Seconds before drink:4"};
		EldritchClient.config.initializeOptions("Drunkard", initOptions);
		options = EldritchClient.config.getOptionGroup("Drunkard");
	}
	
	public static void stayDrunk() {
		player = MinecraftClient.getInstance().player;
		interactor = MinecraftClient.getInstance().interactionManager;
		lastHotbarSlot = -1;
		
		StatusEffect lunch = whatsForLunch();
		if (lunch==null) return;
		if (!holdingBeer(lunch)) {
			lastHotbarSlot = getPotionSlot(lunch);
			if (lastHotbarSlot == -1) return;
			Caravan.swapSlots(MinecraftClient.getInstance().interactionManager, player, lastHotbarSlot, getHotbarSlot());
		}
		enjoyFineSpirits();
		barFight();
		if (lastHotbarSlot != -1)
			Caravan.swapSlots(MinecraftClient.getInstance().interactionManager, player, lastHotbarSlot, getHotbarSlot());
	}
	
	public static void enjoyFineSpirits() {
		
		interactor.interactItem(player, MinecraftClient.getInstance().world, player.preferredHand);
	}
	
	public static void barFight() {
		interactor.clickSlot(0, getHotbarSlot(), 1, SlotActionType.THROW, player);
	}
	
	public static int getPotionSlot(StatusEffect type) {
		for (int i = 9; i < 45; i++) {
			if (isBeer(type, i)) return i;
		}
		return -1;
	}
	
	public static int getHotbarSlot() {
		return player.inventory.selectedSlot + 36;
	}
	
	public static boolean holdingBeer(StatusEffect brew) {
		return isBeer(brew, getHotbarSlot());
	}
	
	public static boolean isBeer(StatusEffect brew, int slot) {
		ItemStack currentItem = player.inventory.getInvStack(slot);
		if (currentItem.getItem() != Items.POTION) return false;
		for (StatusEffectInstance effect : PotionUtil.getPotionEffects(currentItem)) {
			if (effect.getEffectType() == brew) return true;
		}
		return false;
	}
	
	public static boolean enabled() {
		return options.get("Enabled(y/n)").equals("y");
	}
	
	public static StatusEffect whatsForLunch() {
		Collection<StatusEffectInstance> effects = player.getStatusEffects();
		Iterator<StatusEffectInstance> effectsIterator = effects.iterator();
		
		while (effectsIterator.hasNext()) {
			StatusEffectInstance currentEffect = effectsIterator.next();
			if (currentEffect.getDuration() < Integer.parseInt(options.get("Seconds before drink"))) {
				return currentEffect.getEffectType();
			}
		}
		
		return null;
	}
}
