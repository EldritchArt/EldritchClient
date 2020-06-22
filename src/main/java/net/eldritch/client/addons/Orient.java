package net.eldritch.client.addons;

import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import net.eldritch.client.EldritchClient;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

public class Orient {
	private static HashMap<String, String> options;

	public static void initOrient() {
		String[] defaultList = { "YawInterval:15", "Set pitch(y/n):y", "Pitch:0" };
		EldritchClient.config.initializeOptions("Orient", defaultList);
		options = EldritchClient.config.getOptionGroup("Orient");

		FabricKeyBinding toggleHotkey = FabricKeyBinding.Builder.create(new Identifier("eldritchclient", "orient"),
				InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_3, "eldritchclient").build();
		KeyBindingRegistry.INSTANCE.register(toggleHotkey);

		ClientTickCallback.EVENT.register(e -> {
			if (toggleHotkey.wasPressed())
				orient();
		});
	}

	public static void orient() {
		float newYaw = MinecraftClient.getInstance().player.yaw;
		float interval = Float.parseFloat(options.get("YawInterval"));
		newYaw += 180;
		newYaw += interval/2;
		newYaw = newYaw / interval;
		newYaw = (int)newYaw;
		newYaw = newYaw * interval;
		newYaw -= 180;
		if (newYaw == 180) newYaw = -180;
		MinecraftClient.getInstance().player.yaw = newYaw;

		if (options.get("Set pitch(y/n)").equals("y"))
			MinecraftClient.getInstance().player.pitch = Float.parseFloat(options.get("Pitch"));
	}
}
