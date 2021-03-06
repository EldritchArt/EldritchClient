package net.eldritch.client.gui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.eldritch.client.EldritchClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class EldritchClickGui extends Screen {

	private static String lastOpen = "";

	// private TextFieldWidget testText;
	private ButtonWidget exitButton;
	private String[] groupNames;
	private String openGroup;

	private GroupOpenerButtonWidget[] groupSelectors;
	private Map.Entry[] selectedGroupInfo;
	private TextFieldWidget[] textFields;
	HashMap<String, String> groupOptions;

	private int textX = 260;

	public EldritchClickGui(Text string) {
		super(string);
		openGroup = lastOpen;
	}

	public EldritchClickGui(Text string, String openGroup) {
		super(string);
		this.openGroup = openGroup;
		lastOpen = openGroup;
	}

	@Override
	public Text getTitle() {
		return new LiteralText("Eldritch Config");
	}

	protected void init() {
		// testText = this.addButton(new
		// TextFieldWidget(this.font,this.width/2-100,60,200,20, "Test"));
		exitButton = this
				.addButton(new ButtonWidget(this.width - 105, this.height - 25, 100, 20, new LiteralText("Exit"), (ButtonWidget) -> {
					onClose();
				}));

		// get all the groups in the config file
		groupNames = EldritchClient.config.getOptionGroups();
		// make a button for each group
		groupSelectors = new GroupOpenerButtonWidget[groupNames.length];
		for (int i = 0; i < groupSelectors.length; i++) {
			groupSelectors[i] = this.addButton(new GroupOpenerButtonWidget(20, 20 + 25 * i, 150, 20, groupNames[i], this));
		}

		if (openGroup.length() > 0) {
			groupOptions = EldritchClient.config.getOptionGroup(openGroup);
			textFields = new TextFieldWidget[groupOptions.size()];
			selectedGroupInfo = new Map.Entry[groupOptions.size()];

			Set set = groupOptions.entrySet();
			Iterator iterator = set.iterator();
			int i = 0;
			while (iterator.hasNext()) {
				selectedGroupInfo[i++] = (Map.Entry) iterator.next();
				textFields[i - 1] = this.addButton(new TextFieldWidget(this.textRenderer, textX, i * 25 + 40, 200, 20,
						new LiteralText((String) selectedGroupInfo[i - 1].getKey())));
				textFields[i - 1].setMaxLength(256);
				textFields[i - 1].setText((String) selectedGroupInfo[i - 1].getValue());
			}

		}
	}

	public static final int textcolor = 0xffd0d0d0;

	@Override
	public void render(MatrixStack m, int mouseX, int mouseY, float delta) {
		this.drawCenteredString(m, this.textRenderer, openGroup + " options", this.width / 2, 20, textcolor);
		if (null != selectedGroupInfo)
			for (int i = 0; i < selectedGroupInfo.length; i++) {
				this.drawCenteredString(m, this.textRenderer, (String) selectedGroupInfo[i].getKey(), textX - 8,
						i * 25 + 65 + 7, textcolor);
			}
		super.render(m, mouseX, mouseY, delta);
	}

	@Override
	public void onClose() {
		putNewSettingsIntoConfig();
		super.onClose();
	}

	public void putNewSettingsIntoConfig() {
		if (null != textFields) // fix crash glitch
			for (int i = 0; i < textFields.length; i++) {
				groupOptions.put((String) selectedGroupInfo[i].getKey(), textFields[i].getText());
			}
		EldritchClient.config.saveConfigFile();
	}
}
