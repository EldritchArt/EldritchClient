package net.eldritch.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.eldritch.client.Armoury;
import net.eldritch.client.addons.AutoEZ;
import net.eldritch.client.addons.AutoLog;
import net.eldritch.client.addons.Drunkard;
import net.eldritch.client.addons.Renewal;
import net.eldritch.client.addons.Winged;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntity.class)
public class PlayerTickMixin {

	@Environment(EnvType.CLIENT)
	@Inject(at = @At("HEAD"), method = "tick")
	public void tick(CallbackInfo callback) {
		if (MinecraftClient.getInstance().player != null) {
			Drunkard.stayDrunk();
			AutoLog.update();
			Winged.update();
			Renewal.update();
			Armoury.update();
		}
	}
}
