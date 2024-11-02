package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.rune.RuneType;

public class SpecialActionItem extends Item {
	
	private ItemSpecialAction action;
	
	public SpecialActionItem(String itemName, ItemTypeConfig typeConfig, Sprite sprite, AnimationDirector<TextureRegion> animation, MapProperties properties, ItemSpecialAction itemSpecialAction) {
		super(itemName, typeConfig, sprite, animation, properties);
		this.action = itemSpecialAction;
		
		if (Math.abs(action.getScaleFactor() - 1f) > 0.01f) {
			//scale down the images, since these images are larger than the other
			sprite.setScale(Constants.WORLD_TO_SCREEN * action.getScaleFactor());
		}
	}
	
	@Override
	public void pickUp(boolean playSound) {
		if (canUseSpecialActions()) {
			super.pickUp(playSound);
			GlobalValuesDataHandler.getInstance().put(action.getActionAvailableGlobalValueKey(), true);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.SPECIAL_ACTION_ITEM_PICKED_UP).setStringValue(itemName));
		}
	}
	
	private boolean canUseSpecialActions() {
		return RuneType.OTHALA.isCollected();
	}
}
