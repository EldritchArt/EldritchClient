package net.eldritch.client.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.eldritch.client.addons.Fumble;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;

@Mixin(ContainerScreen.class)
public class FumbleContainerMixin {
	@Shadow
	@Final
	protected Container container;

	@Shadow
	protected Slot focusedSlot;

	@Inject(at = @At("HEAD"), method = "keyPressed")
	public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable callback) {
		if (Fumble.enabled() && MinecraftClient.getInstance().options.keyDrop.matchesKey(keyCode, scanCode)
				&& Screen.hasShiftDown()) {
			if (focusedSlot != null) {
				MinecraftClient mc = MinecraftClient.getInstance();
				ClientPlayerInteractionManager manager = mc.interactionManager;

				List<Slot> slots = container.slots;
				Slot originSlot = focusedSlot;

				if (!Fumble.protectPlayer() || !(focusedSlot.inventory == mc.player.inventory)) {

					// Huge thank you to Mouse Wheelie for showing me how to do this
					for (int i = 0; i < slots.size(); i++) {
						if (slots.get(i).inventory == originSlot.inventory)
							if (Fumble.shouldDropRares() || !Fumble.isRare(slots.get(i).getStack())) {
								manager.clickSlot(container.syncId, i, 1, SlotActionType.THROW, mc.player);
							}
					}

					if (Fumble.shouldCloseInventory())
						mc.player.closeContainer();
				}
			}
		}

	}
}
