package net.eldritch.client.addons;

import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import net.eldritch.client.EldritchClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class Caravan {
	
	public static FabricKeyBinding row1;
	public static FabricKeyBinding row2;
	public static FabricKeyBinding row3;
	
	private static HashMap<String, String> options;
	
	public static void caravanInit() {
		String[] initOptions = {"Enabled(y/n):n","Slots:123456789"};
		EldritchClient.config.initializeOptions("Caravan", initOptions);
		options = EldritchClient.config.getOptionGroup("Caravan");
		
		row1 = initRowKey(1, -1);
		row2 = initRowKey(2, -1);
		row3 = initRowKey(3, GLFW.GLFW_KEY_LEFT_ALT);
		
		ClientTickCallback.EVENT.register(e->{if(row1.wasPressed()) swapRow(1);});
		ClientTickCallback.EVENT.register(e->{if(row2.wasPressed()) swapRow(2);});
		ClientTickCallback.EVENT.register(e->{if(row3.wasPressed()) swapRow(3);});
	}
	
	public static void swapRow(int row) {
		String enabled = (String)options.get("Enabled(y/n)");
		if (!"y".equals(enabled)) return;
		if (MinecraftClient.getInstance().currentScreen!=null) return;
		
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		ClientPlayerInteractionManager interactionManager = MinecraftClient.getInstance().interactionManager;
		
		String slotsToMove = (String)options.get("Slots");
		for (int i = 0; i<slotsToMove.length(); i++) {
			int foundChar = slotsToMove.charAt(i)-'1';
			if (foundChar<0 || foundChar > 8) return;
			swapSlots(interactionManager, player, foundChar, 9*row+foundChar);
		}
	}
	
	//Huge thanks to Wurst client for helping me figure this out, its a very arcane process
	public static void swapSlots(ClientPlayerInteractionManager i, ClientPlayerEntity player, int slot1, int slot2) {
		if (slot1==slot2) return;
		slot1 = (slot1<9)?slot1+36:slot1;
		slot2 = (slot2<9)?slot2+36:slot2;
		i.clickSlot(0, slot1, 0, SlotActionType.PICKUP, player);
		i.clickSlot(0, slot2, 0, SlotActionType.PICKUP, player);
		i.clickSlot(0, slot1, 0, SlotActionType.PICKUP, player);
	}
	
	private static FabricKeyBinding initRowKey(int row, int defaultKeyId) {
		FabricKeyBinding hotkey = FabricKeyBinding.Builder.create(
				new Identifier("eldritchclient","swap_row_" + row),
				InputUtil.Type.KEYSYM,
				defaultKeyId,
				"eldritchclient").build();
		KeyBindingRegistry.INSTANCE.register(hotkey);
		return hotkey;
	}
}
