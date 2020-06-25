package net.eldritch.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.eldritch.client.addons.LogOutSpot;
import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftRenderMixin {
	@Inject(at = @At("HEAD"), method = "render(Z)V")
	public void getLogOutSpot(boolean b, CallbackInfo info) {
		if (LogOutSpot.enabled())
			LogOutSpot.recordCoords();
	}
}
