package net.eldritch.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.eldritch.client.addons.Insomnia;
import net.minecraft.block.BedBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

@Mixin(ClientPlayerInteractionManager.class)
public class InsomniaInteractMixin {
	@Inject(at = @At("HEAD"), method = "interactBlock", cancellable = true)
	public void interactBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult,
			CallbackInfoReturnable<ActionResult> callback) {
		if (Insomnia.enabled()) {
			if (Insomnia.adventureEnabled()) {
				callback.setReturnValue(ActionResult.FAIL);
				callback.cancel();
			} else {
				if (world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof BedBlock) {
					callback.setReturnValue(ActionResult.FAIL);
					callback.cancel();
				}
			}
		}
	}
}
