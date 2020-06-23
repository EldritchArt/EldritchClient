package net.eldritch.client.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.eldritch.client.addons.Censor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.LiteralText;
import net.minecraft.text.SelectorText;
import net.minecraft.text.Text;

import org.apache.logging.log4j.Logger;

@Mixin(ChatHud.class)
public class CensorChatMixin {
	@Shadow
	private static Logger LOGGER;

	@Shadow
	MinecraftClient client;

	@Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;I)V", cancellable = true)
	public void addMessage(Text text, int messageId, CallbackInfo callback) {
		if (Censor.enabled()) {
			String original;
			if (Censor.colourEnabled())
				original = text.asFormattedString();
			else
				original = text.getString();
			String censored = original;
			if (censored == null)
				callback.cancel();
			if (!censored.equals(original)) {
				text = new LiteralText(censored);

				shadow$addMessage(text, messageId, client.inGameHud.getTicks(), false);
				LOGGER.info("[CHAT] {}", text.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
				callback.cancel();
			}
		}
	}

	@Shadow
	private void shadow$addMessage(Text text, int messageId, int ticks, boolean b) {
	}
}
