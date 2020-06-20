package net.eldritch.client.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.eldritch.client.addons.Whisper;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

@Mixin(ClientPlayerEntity.class)
public class WhisperChatMixin {
	@Shadow @Final ClientPlayNetworkHandler networkHandler;
	
	@Inject(at = @At("HEAD"), method="sendChatMessage", cancellable=true)
	public void sendChatMessage(String string, CallbackInfo callback) {
		string = Whisper.doWork(string);
		if (string.length()!=0)
		networkHandler.sendPacket(new ChatMessageC2SPacket(string));
		callback.cancel();
	}
}
