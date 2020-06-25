package net.eldritch.client.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.injection.At;

import net.eldritch.client.addons.F3Shadow;
import net.eldritch.client.addons.LogOutSpot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Mixin({GameRenderer.class})
public class SpooferRendererMixin {

@Inject(at = @At(value = "INVOKE", target = "com/mojang/blaze3d/systems/RenderSystem.defaultAlphaFunc()V"), method = "render")
public void render(float float_1, long long_1, boolean boolean_1, CallbackInfo info) {
	
	MinecraftClient client = MinecraftClient.getInstance();
	
	if(!client.options.debugEnabled && F3Shadow.getOptions().get("Enabled(y/n)").equals("y")) {
		Entity cameraEntity = client.getCameraEntity();
		
		RenderSystem.pushMatrix();
		String renderInfo = "";
		
		BlockPos blockPos = new BlockPos(cameraEntity.getX(), cameraEntity.getBoundingBox().getMin(Direction.Axis.Y), cameraEntity.getZ());
		renderInfo += String.format("%s ", F3Shadow.getPositionString(blockPos));
		
		if (F3Shadow.getOptions().get("Show Direction(y/n)").equals("y")) {
			Direction direction = cameraEntity.getHorizontalFacing();
			renderInfo += String.format("%5s ", direction);
		}
		if (F3Shadow.getOptions().get("Show Biome(y/n)").equals("y")) {
			renderInfo += String.format("%s ",client.world.getBiome(blockPos).getName().asString());
		}
		
		float textPosX = 5;

		client.textRenderer.drawWithShadow(renderInfo, textPosX, 5, 0xffffffff);
		RenderSystem.popMatrix();
	}
}}