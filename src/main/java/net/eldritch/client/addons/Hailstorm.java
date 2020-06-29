package net.eldritch.client.addons;

import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import net.eldritch.client.Armoury;
import net.eldritch.client.EldritchClient;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Identifier;

public class Hailstorm {
	private static HashMap<String, String> options;
	private static MinecraftClient mc;

	public static void hailstormInit() {
		String[] initOptions = { "Potions Enabled(y/n):n" };
		EldritchClient.config.initializeOptions("Hailstorm", initOptions);
		options = EldritchClient.config.getOptionGroup("Hailstorm");

		FabricKeyBinding toggleHotkey = FabricKeyBinding.Builder
				.create(new Identifier("eldritchclient", "toggle_potions"), InputUtil.Type.KEYSYM, -1, "eldritchclient")
				.build();
		KeyBindingRegistry.INSTANCE.register(toggleHotkey);

		ClientTickCallback.EVENT.register(e -> {
			if (toggleHotkey.wasPressed())
				toggleHailstormPotions();
		});
	}

	private static void toggleHailstormPotions() {
		if (enabledPotions()) {
			options.put("Potions Enabled(y/n)", "n");
		} else {
			options.put("Potions Enabled(y/n)", "y");
		}
	}

	public static void onTick() {
		mc = MinecraftClient.getInstance();
		if (enabledPotions() && Armoury.safeScreen(mc.currentScreen)) {
			if (mc.player.inventory.getInvStack(mc.player.inventory.selectedSlot).getItem() == Items.AIR) {
				int potSlot = getHarmPotionIndex();
				if (potSlot != -1)
					Caravan.swapSlots(mc.interactionManager, mc.player, potSlot, mc.player.inventory.selectedSlot);
			}
		}
	}

	private static int getHarmPotionIndex() {
		for (int i = 0; i < 36; i++) {
			for (StatusEffectInstance e : PotionUtil.getPotionEffects(mc.player.inventory.getInvStack(i))) {
				if (e.getEffectType() == StatusEffects.INSTANT_DAMAGE) {
					return i;
				}
			}
		}
		return -1;
	}

	private static boolean enabledPotions() {
		return options.get("Potions Enabled(y/n)").equals("y");
	}
}
