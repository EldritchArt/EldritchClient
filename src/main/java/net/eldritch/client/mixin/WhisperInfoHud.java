package net.eldritch.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.eldritch.client.addons.Whisper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@Mixin(InGameHud.class)
public class WhisperInfoHud {
	@Shadow
	int scaledWidth;
	@Shadow
	int scaledHeight;
	@Shadow
	MinecraftClient client;
	@Shadow
	int heldItemTooltipFade;
	@Shadow
	ItemStack currentStack;

	@Inject(at = @At("HEAD"), method = "render")
	public void render(MatrixStack m, float tickDelta, CallbackInfo callback) {
		if (heldItemTooltipFade <= 0)
		if (Whisper.enabled() && Whisper.hudEnabled() && !Whisper.getPrefix().isEmpty()) {
			String prefixString = Whisper.getPrefix();
			int i = (this.scaledWidth - client.textRenderer.getWidth(prefixString)) / 2;
			int j = this.scaledHeight - 59;
			if (!this.client.interactionManager.hasStatusBars()) {
				j += 14;
			}
			//client.textRenderer.drawWithShadow(prefixString, (float)i, (float)j, 16777215);
		}
	}
}
