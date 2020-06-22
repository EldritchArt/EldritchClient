package net.eldritch.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.eldritch.client.addons.Nescience;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.util.SelectionManager;

@Mixin(SignEditScreen.class)
public class NescienceSignMixin {
	@Shadow
	private SelectionManager selectionManager;
	
	@Shadow
	private int currentRow;

	@Shadow
	public void shadow$onClose() {
	}

	@Inject(at = @At("TAIL"), method = "init")
	protected void init(CallbackInfo callback) {
		if (Nescience.enabled()) {
			String toWrite = Nescience.getText();
			toWrite = toWrite+" ";
			for (int i = 0; i < toWrite.length()-1; i++) {
				//check for newline
				if (toWrite.charAt(i) == '\\') {
					if (toWrite.charAt(++i)=='n') currentRow = (currentRow+1)&3;
					else selectionManager.insert(toWrite.charAt(i));
				} else selectionManager.insert(toWrite.charAt(i));
			}
			
			shadow$onClose();
		}
	}
}
