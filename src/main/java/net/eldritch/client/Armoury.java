package net.eldritch.client;

import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import net.eldritch.client.addons.Caravan;
import net.eldritch.client.addons.Prospector;
import net.eldritch.client.addons.Winged;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;

public class Armoury {
	private static HashMap<String, String> options;
	private static PlayerInventory inv;
	private static MinecraftClient mc;
	private static int armorRetryCooldown = 0;

	public static void ArmouryInit() {
		String[] initOptions = { "Enabled(y/n):y", "Equip on Tick(y/n):n", "Auto Totem(y/n):y" };
		EldritchClient.config.initializeOptions("Armoury", initOptions);
		options = EldritchClient.config.getOptionGroup("Armoury");

		FabricKeyBinding swapWings = FabricKeyBinding.Builder.create(new Identifier("eldritchclient", "armoury_swap"),
				InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_DOWN, "eldritchclient").build();
		KeyBindingRegistry.INSTANCE.register(swapWings);

		ClientTickCallback.EVENT.register(e -> {
			if (swapWings.wasPressed())
				swapElytra();
		});

		FabricKeyBinding getTotem = FabricKeyBinding.Builder
				.create(new Identifier("eldritchclient", "armoury_totem"), InputUtil.Type.KEYSYM, -1, "eldritchclient")
				.build();
		KeyBindingRegistry.INSTANCE.register(getTotem);

		ClientTickCallback.EVENT.register(e -> {
			if (getTotem.wasPressed())
				autoTotem(true);
		});
	}

	private static void swapElytra() {
		if (!enabled())
			return;
		inv = mc.player.inventory;
		mc = MinecraftClient.getInstance();

		// if no elytra, get an elytra. Otherwise, get a chestplate
		if (inv.getArmorStack(2).getItem() == Items.ELYTRA) {
			// get the best chestplate
			int bestSlot = -1;
			int bestPoints = 0;
			for (int i = 0; i < 36; i++) {
				ItemStack stack = inv.getInvStack(i);
				if (stack.getItem() == Items.ELYTRA)
					continue;
				IntPair stackInfo = getArmorInfo(stack);
				if (stackInfo.a != 2)
					continue;
				if (stackInfo.b > bestPoints) {
					bestSlot = i;
					bestPoints = stackInfo.b;
				}
			}
			if (bestSlot != -1)
				Caravan.swapSlots(mc.interactionManager, mc.player, bestSlot, Caravan.CHESTPLATE);
		} else {
			int slot = Winged.findBestElytraSlot(inv);
			Caravan.swapSlots(mc.interactionManager, mc.player, slot, Caravan.CHESTPLATE);
		}
	}

	private static int[] indexes = { -1, -1, -1, -1 };

	public static void update() {
		mc = MinecraftClient.getInstance();
		if (!safeScreen(mc.currentScreen)) {
			armorRetryCooldown = 0;
			return;
		}

		if (!enabled())
			return;

		inv = mc.player.inventory;

		if (shouldTotem())
			autoTotem(false);
		if (shouldArmor())
			autoArmor();
	}

	// moves totems to an empty offhand
	private static void autoTotem(boolean force) {
		if (force || (inv.offHand.size() > 0 && inv.offHand.get(0).getItem() == Items.AIR)) {
			for (int i = 0; i < 36; i++) {
				ItemStack stack = inv.getInvStack(i);
				if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
					Caravan.swapSlots(mc.interactionManager, mc.player, i, Caravan.OFFHAND);
					return;
				}
			}
		}
	}

	private static void autoArmor() {
		if (armorRetryCooldown > 0) {
			armorRetryCooldown--;
			return;
		}

		boolean mustEquip = false;
		for (int i = 0; i < 4; i++) {
			if (inv.getArmorStack(i).getItem() == Items.AIR) {
				indexes[i] = -2;
				mustEquip = true;
			}
		}

		if (!mustEquip)
			return;

		getArmourIndexes();

		for (int i = 0; i < 4; i++) {
			if (indexes[i] != -1) {
				Caravan.swapSlots(mc.interactionManager, mc.player, indexes[i], Caravan.BOOTS - i);
			}
		}

		armorRetryCooldown = 20;
	}

	public static boolean safeScreen(Screen currentScreen) {
		if (currentScreen == null)
			return true;
		String name = currentScreen.getTitle().asFormattedString();
		String[] safeNames = { "Game Menu", "Eldritch Config" }; // escape and chat screen are okay, as well as
																	// options
		for (String safeName : safeNames)
			if (name.equals(safeName))
				return true;
		if (currentScreen instanceof ChatScreen)
			return true;
		return false;
	}

	private static void getArmourIndexes() {
		int[] armorPoints = { 0, 0, 0, 0 };
		for (int i = 0; i < 4; i++) {
			if (indexes[i] == -1)
				armorPoints[i] = Integer.MAX_VALUE;
			else
				indexes[i] = -1;
		}

		for (int i = 0; i < 36; i++) {
			ItemStack stack = inv.getInvStack(i);
			IntPair stackInfo = getArmorInfo(stack);
			if (stackInfo.a == -1)
				continue;
			if (stackInfo.b > armorPoints[stackInfo.a]) {
				indexes[stackInfo.a] = i;
				armorPoints[stackInfo.a] = stackInfo.b;
			}
		}
	}

	private static final int LEATHER_TIER = 1;
	private static final int GOLD_TIER = 10;
	private static final int CHAIN_TIER = 11;
	private static final int IRON_TIER = 20;
	private static final int TURTLE_TIER = 29;
	private static final int DIAMOND_TIER = 38;
	private static final int NETHERITE_TIER = 47;
	private static final int PROT_TIER = 8;
	private static final int ELYTRA_TIER = 1000;

	private static IntPair getArmorInfo(ItemStack stack) {
		Item itemType = stack.getItem();
		IntPair retval = new IntPair(-1, 0);
		if (!stack.isDamageable())
			return retval;
		if (itemType == Items.DIAMOND_CHESTPLATE)
			retval.update(2, DIAMOND_TIER);
		else if (itemType == Items.IRON_CHESTPLATE)
			retval.update(2, IRON_TIER);
		else if (itemType == Items.CHAINMAIL_CHESTPLATE)
			retval.update(2, CHAIN_TIER);
		else if (itemType == Items.LEATHER_CHESTPLATE)
			retval.update(2, LEATHER_TIER);
		else if (itemType == Items.GOLDEN_CHESTPLATE)
			retval.update(2, GOLD_TIER);
		else if (itemType == Items.DIAMOND_BOOTS)
			retval.update(0, DIAMOND_TIER);
		else if (itemType == Items.IRON_BOOTS)
			retval.update(0, IRON_TIER);
		else if (itemType == Items.CHAINMAIL_BOOTS)
			retval.update(0, CHAIN_TIER);
		else if (itemType == Items.LEATHER_BOOTS)
			retval.update(0, LEATHER_TIER);
		else if (itemType == Items.GOLDEN_BOOTS)
			retval.update(0, GOLD_TIER);
		else if (itemType == Items.DIAMOND_LEGGINGS)
			retval.update(1, DIAMOND_TIER);
		else if (itemType == Items.IRON_LEGGINGS)
			retval.update(1, IRON_TIER);
		else if (itemType == Items.CHAINMAIL_LEGGINGS)
			retval.update(1, CHAIN_TIER);
		else if (itemType == Items.LEATHER_LEGGINGS)
			retval.update(1, LEATHER_TIER);
		else if (itemType == Items.GOLDEN_LEGGINGS)
			retval.update(1, GOLD_TIER);
		else if (itemType == Items.DIAMOND_HELMET)
			retval.update(3, DIAMOND_TIER);
		else if (itemType == Items.IRON_HELMET)
			retval.update(3, IRON_TIER);
		else if (itemType == Items.CHAINMAIL_HELMET)
			retval.update(3, CHAIN_TIER);
		else if (itemType == Items.LEATHER_HELMET)
			retval.update(3, LEATHER_TIER);
		else if (itemType == Items.GOLDEN_HELMET)
			retval.update(3, GOLD_TIER);
		else if (itemType == Items.ELYTRA) {
			if (stack.getDamage() < Winged.ELYTRA_DANGEROUS)
				retval.update(2, ELYTRA_TIER - stack.getDamage());
		} else if (itemType == Items.TURTLE_HELMET)
			retval.update(3, TURTLE_TIER);

		if (retval.a == -1)
			return retval;
		if (!stack.hasEnchantments())
			return retval;

		ListTag enchants = stack.getEnchantments();

		for (int j = 0; j < enchants.size(); j++) {
			Tag tag = enchants.get(j);
			String id = Prospector.getEnchID(tag.asString());
			if (id.equals("protection")) {
				String lev = getEnchLevel(tag.asString());
				retval.b += PROT_TIER * Integer.parseInt(lev);
			}
		}

		return retval;
	}

	private static String getEnchLevel(String tag) {
		String start = tag.substring(tag.indexOf("lvl:") + 4);
		int endindex;
		for (endindex = 0; !"s},".contains(Character.toString(start.charAt(endindex))); endindex++)
			;
		return start.substring(0, endindex);
	}

	private static boolean enabled() {
		return options.get("Enabled(y/n)").equals("y");
	}

	private static boolean shouldArmor() {
		return options.get("Equip on Tick(y/n)").equals("y");
	}

	private static boolean shouldTotem() {
		return options.get("Auto Totem(y/n)").equals("y");
	}

	private static class IntPair {
		int a;
		int b;

		public IntPair(int a, int b) {
			this.a = a;
			this.b = b;
		}

		public void update(int a, int b) {
			this.a = a;
			this.b = b;
		}
	}
}
