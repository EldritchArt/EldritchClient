package net.eldritch.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.text.LiteralText;

public class GroupOpenerButtonWidget extends AbstractPressableButtonWidget {

	private String group;
	private EldritchClickGui gui;
	
	
	public GroupOpenerButtonWidget(int i, int j, int k, int l, String string, EldritchClickGui gui) {
		super(i, j, k, l, new LiteralText(string));
		this.group=string;
		this.gui = gui;
	}

	@Override
	public void onPress() {
		gui.putNewSettingsIntoConfig();
		MinecraftClient.getInstance().openScreen(new EldritchClickGui(new LiteralText(""), group));
	}

}
