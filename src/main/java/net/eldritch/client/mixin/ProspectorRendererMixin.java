package net.eldritch.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.eldritch.client.addons.Prospector;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@Mixin(ItemRenderer.class)
public class ProspectorRendererMixin {
	//Spent a few hours getting this to work, turns out I need more control otherwise items render ugly with durability
	/*@ModifyVariable(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"))
	public String testChangeText(String amountText, TextRenderer font, ItemStack item) {
		if (Prospector.enabled() && Prospector.hasConflictingEnchants(item))
			return "Con";
		return amountText;
	}*/
	
	@Shadow
	public float zOffset;
	
	@Inject(at=@At("HEAD"), method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
	public void drawConflict(TextRenderer fontRenderer, ItemStack stack, int x, int y, String amountText, CallbackInfo callback) {
		if (Prospector.enabled() && Prospector.hasConflictingEnchants(stack)) {
			MatrixStack matrixStack = new MatrixStack();
	            matrixStack.translate(0.0D, 0.0D, (double)(this.zOffset + 200.0F));
	            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
	            fontRenderer.draw("CON", (float)(x + 19 - 2 - fontRenderer.getStringWidth("CON")), (float)(y + 1), 16777215, true, matrixStack.peek().getModel(), immediate, false, 0, 15728880);
	            immediate.draw();
		}
	}
}
