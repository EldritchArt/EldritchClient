package net.eldritch.client.addons;

import java.util.HashMap;
import java.util.Random;

import org.lwjgl.glfw.GLFW;

import net.eldritch.client.EldritchClient;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

public class Ruinous {
	private static HashMap<String, String> options;
	private static Random rand = new Random();
	
	public static void ruinousInit() {
		String[] defaultList = {"Slots:15 16 17 24 25 26","Enabled(y/n):n","Chance of Swap:0.5"};
		EldritchClient.config.initializeOptions("Ruinous", defaultList);
		options = EldritchClient.config.getOptionGroup("Ruinous");
		
		FabricKeyBinding toggleHotkey = FabricKeyBinding.Builder.create(
				new Identifier("eldritchclient","toggle_ruinous"),
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_KP_2,
				"eldritchclient").build();
		KeyBindingRegistry.INSTANCE.register(toggleHotkey);
		
		ClientTickCallback.EVENT.register(e->{if(toggleHotkey.wasPressed()) toggleRuinous();});
	}
	
	public static void toggleRuinous() {
		String enabled = (String)options.get("Enabled(y/n)");
		if ("y".equals(enabled)) options.put("Enabled(y/n)", "n");
		else {
			options.put("Enabled(y/n)", "y");
		}
	}
	
	public static void swapItem() {
		String[] indexes = options.get("Slots").split(" ");
		int swappingIndex = rand.nextInt(indexes.length);
		//get the index
		swappingIndex = Integer.parseInt(indexes[swappingIndex]);
		Caravan.swapSlots(MinecraftClient.getInstance().interactionManager, MinecraftClient.getInstance().player, MinecraftClient.getInstance().player.inventory.selectedSlot, swappingIndex);
	}
	
	public static boolean shouldSwapItem() {
		if (!(((String)options.get("Enabled(y/n)")).equals("y"))) return false;
		float threshold = Float.parseFloat((String)options.get("Chance of Swap"));
		if (rand.nextFloat() < threshold) {
			return true;
		}
		return false;
	}
}
