package net.eldritch.client.gui;

import net.eldritch.client.EldritchClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.text.LiteralText;

public class GroupOpenerButtonWidget extends AbstractPressableButtonWidget {

	private String group;
	
	public GroupOpenerButtonWidget(int i, int j, int k, int l, String string) {
		super(i, j, k, l, string);
		this.group=string;
	}

	@Override
	public void onPress() {
		EldritchClient.config.saveConfigFile();
		MinecraftClient.getInstance().openScreen(new EldritchClickGui(new LiteralText(""), group));
	}

}
