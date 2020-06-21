package net.eldritch.client;

import org.lwjgl.glfw.GLFW;

import net.eldritch.client.addons.Caravan;
import net.eldritch.client.addons.Censor;
import net.eldritch.client.addons.F3Shadow;
import net.eldritch.client.addons.Ruinous;
import net.eldritch.client.addons.Whisper;
import net.eldritch.client.gui.EldritchClickGui;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EldritchClient implements ModInitializer {

	public static EldritchConfig config;
	public static FabricKeyBinding optionsMenuKey;
	
	@Override
	public void onInitialize() {
		config = new EldritchConfig();
		setupOptionsHotkey();
		
		F3Shadow.F3ShadowInit();
		Whisper.WhisperInit();
		Caravan.caravanInit();
		Censor.censorInit();
		Ruinous.ruinousInit();
		
		config.saveConfigFile();
	}
	
	public void setupOptionsHotkey() {
		optionsMenuKey = FabricKeyBinding.Builder.create(
				new Identifier("eldritchclient","open_options"),
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_KP_0,
				"eldritchclient").build();
		KeyBindingRegistry.INSTANCE.register(optionsMenuKey);
		
		ClientTickCallback.EVENT.register(e -> {
			if (optionsMenuKey.wasPressed()) {
				MinecraftClient.getInstance().openScreen(new EldritchClickGui(new LiteralText("")));
			}
		});
	}

}
