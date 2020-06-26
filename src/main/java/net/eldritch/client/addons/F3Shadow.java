package net.eldritch.client.addons;

import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Random;

import org.lwjgl.glfw.GLFW;

import net.eldritch.client.EldritchClient;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class F3Shadow {

	public static FabricKeyBinding toggleCoords;
	private static HashMap<String, String> options;
	private static BlockPos startPos;
	private static float[] randOffset;
	public static int xScreenRender = 5;
	public static int yScreenRender = 5;
	
	public static void F3ShadowInit() {
		String[] initOptions = {"Enabled(y/n):n","Show Direction(y/n):y","Show Biome(y/n):y",
				"X formula:x&pm1000000","Y formula:y&pm10","Z formula:z&pm1000000",
				"Screen X:5", "Screen Y:5"};
		EldritchClient.config.initializeOptions("F3Shadow", initOptions);
		options = EldritchClient.config.getOptionGroup("F3Shadow");
		options.put("Enabled(y/n)", "n"); //must start off off
		
		toggleCoords = FabricKeyBinding.Builder.create(
				new Identifier("eldritchclient","toggle_coords"),
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_KP_1,
				"eldritchclient").build();
		KeyBindingRegistry.INSTANCE.register(toggleCoords);
		
		ClientTickCallback.EVENT.register(e -> {
			if (toggleCoords.wasPressed()) {
				resetScreenPosition();
				String enabled = (String)options.get("Enabled(y/n)");
				if ("y".equals(enabled)) options.put("Enabled(y/n)", "n");
				else {
					options.put("Enabled(y/n)", "y");
					MinecraftClient client = MinecraftClient.getInstance();
					Entity cameraEntity = client.getCameraEntity();
					startPos = new BlockPos(cameraEntity.getX(), cameraEntity.getBoundingBox().getMin(Direction.Axis.Y), cameraEntity.getZ());
					Random rand = new Random();
					randOffset = new float[3];
					for (int i = 0; i<3; i++) randOffset[i]= rand.nextFloat()*2-1;
				}
			}
		});
	}
	
	private static void resetScreenPosition() {
		xScreenRender = Integer.parseInt(options.get("Screen X"));
		yScreenRender = Integer.parseInt(options.get("Screen Y"));
	}

	public static String getPositionString(BlockPos currentPos) {
		String retval = "";
		
		retval += computeFormula(options.get("X formula"),currentPos,startPos,randOffset[0]) + " ";
		retval += computeFormula(options.get("Y formula"),currentPos,startPos,randOffset[1]) + " ";
		retval += computeFormula(options.get("Z formula"),currentPos,startPos,randOffset[2]);
		
		return retval;
	}
	
	public static HashMap<String,String> getOptions() {
		return options;
	}
	
	private static int computeFormula(String formula, BlockPos currentPos, BlockPos pastPos, float randCoeff) {
		int retval = 0;
		String[] formulaSections = formula.split("&");
		for (String s : formulaSections) {
			int toAdd;
			if (s.contains("pm")) {
				s = s.replace("pm", "");
				float offset = (Math.abs(Integer.parseInt(s))*randCoeff);
				toAdd = (int)offset;
			} else if (s.contains("sx")) {
				toAdd = pastPos.getX();
			} else if (s.contains("sy")) {
				toAdd = pastPos.getY();
			} else if (s.contains("sz")) {
				toAdd = pastPos.getZ();
			} else if (s.contains("x")) {
				toAdd = currentPos.getX();
			} else if (s.contains("y")) {
				toAdd = currentPos.getY();
			} else if (s.contains("z")) {
				toAdd = currentPos.getZ();
			} else {
				toAdd = Math.abs(Integer.parseInt(s));
			}
			if (s.contains("-")) toAdd = -toAdd;
			retval+=toAdd;
		}
		return retval;
	}
}
