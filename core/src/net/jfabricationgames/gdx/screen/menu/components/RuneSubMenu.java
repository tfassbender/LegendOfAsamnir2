package net.jfabricationgames.gdx.screen.menu.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.rune.RuneType;

public class RuneSubMenu extends ItemSubMenu {
	
	private static final int RUNE_MENU_ITEMS_PER_LINE = 9;
	private static final int RUNE_MENU_LINES = 1;
	
	private static final String RUNE_NOT_FOUND_YET = "You have not revealed this rune yet";
	
	private RuneType hoveredRune;
	private boolean[] runeFound;
	private boolean runeKenazForged;
	
	public RuneSubMenu() {
		super(RUNE_MENU_ITEMS_PER_LINE, RUNE_MENU_LINES, RuneType.getNamesAsList());
	}
	
	@Override
	public void updateStateAfterMenuShown() {
		runeFound = new boolean[RuneType.values().length];
		for (RuneType rune : RuneType.values()) {
			runeFound[rune.order] = rune.isCollected();
		}
		
		runeKenazForged = GlobalValuesDataHandler.getInstance().getAsBoolean(RuneType.GLOBAL_VALUE_KEY_RUNE_KENAZ_FORGED);
	}
	
	@Override
	protected boolean isItemKnown(int index) {
		return runeFound[index];
	}
	
	@Override
	protected void drawItem(SpriteBatch batch, float posX, float posY, float scaledWidth, float scaledHeight, float borderFactor, float sizeFactor, TextureRegion itemTexture, int index) {
		if (index == RuneType.KENAZ.order && !runeKenazForged) {
			//the rune kenaz needs to be forged after use; if it's not forged, the texture is marked in gray
			batch.setColor(Color.LIGHT_GRAY);
			super.drawItem(batch, posX, posY, scaledWidth, scaledHeight, borderFactor, sizeFactor, itemTexture, index);
			batch.setColor(Color.WHITE); // reset to default color
		}
		else {
			super.drawItem(batch, posX, posY, scaledWidth, scaledHeight, borderFactor, sizeFactor, itemTexture, index);
		}
	}
	
	@Override
	public void setHoveredIndex(int hoveredIndex) {
		super.setHoveredIndex(hoveredIndex);
		
		if (hoveredIndex == -1) {
			hoveredRune = null;
		}
		else {
			hoveredRune = RuneType.getByOrder(hoveredIndex);
		}
	}
	
	public String getHoveredRuneDescription() {
		if (hoveredRune == null) {
			return "";
		}
		if (!runeFound[hoveredRune.order]) {
			return RUNE_NOT_FOUND_YET;
		}
		
		if (hoveredRune == RuneType.KENAZ) {
			if (runeKenazForged) {
				return RuneType.KENAZ.description + RuneType.RUNE_KENAZ_DESCRIPTION_POSTFIX_FORGED;
			}
			else {
				return RuneType.KENAZ.description + RuneType.RUNE_KENAZ_DESCRIPTION_POSTFIX_UNFORGED;
			}
		}
		
		return hoveredRune.description;
	}
}
