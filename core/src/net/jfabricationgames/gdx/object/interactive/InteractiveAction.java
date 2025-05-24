package net.jfabricationgames.gdx.object.interactive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Json;

import net.jfabricationgames.gdx.condition.ConditionHandler;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.event.dto.FastTravelPointEventDto;
import net.jfabricationgames.gdx.object.GameObjectTextBox;
import net.jfabricationgames.gdx.object.InteractivePlayer;
import net.jfabricationgames.gdx.util.GameUtil;

public enum InteractiveAction {
	
	SHOW_ON_SCREEN_TEXT {
		
		@Override
		public void execute(InteractiveObject object) {
			MapProperties mapProperties = object.getMapProperties();
			String headerText = mapProperties.get(MAP_PROPERTY_KEY_DISPLAY_TEXT_HEADER, String.class);
			String text = mapProperties.get(MAP_PROPERTY_KEY_DISPLAY_TEXT, String.class);
			String headerColor = mapProperties.get(MAP_PROPERTY_KEY_COLOR_HEADER, String.class);
			
			textBox.setHeaderText(headerText, GameUtil.getColorFromRGB(headerColor, Color.RED));
			textBox.setText(text);
		}
	},
	SHOW_OR_CHANGE_TEXT {
		
		private static final String MAP_PROPERTIES_KEY_DISPLAY_TEXT_CHANGED = "displayTextChanged";
		private static final String MAP_PROPERTIES_KEY_GLOBAL_CONDITION_VALUE = "globalConditionValue";
		private static final String MAP_PROPERTIES_KEY_GLOBAL_CONDITION_KEY = "globalConditionKey";
		private static final String MAP_PROPERTIES_KEY_EVENT_PARAMETER = "eventParameter";
		private static final String MAP_PROPERTIES_KEY_SHOW_TEXT_CHANGE_INFO = "showTextChangeInfo";
		
		@Override
		public void execute(InteractiveObject object) {
			String globalConditionKey = object.getMapProperties().get(MAP_PROPERTIES_KEY_GLOBAL_CONDITION_KEY, String.class);
			String globalConditionValue = object.getMapProperties().get(MAP_PROPERTIES_KEY_GLOBAL_CONDITION_VALUE, String.class);
			String headerColor = object.getMapProperties().get(MAP_PROPERTY_KEY_COLOR_HEADER, String.class);
			String headerText = object.getMapProperties().get(MAP_PROPERTY_KEY_DISPLAY_TEXT_HEADER, String.class);
			String text = object.getMapProperties().get(MAP_PROPERTY_KEY_DISPLAY_TEXT, String.class);
			String changedText = object.getMapProperties().get(MAP_PROPERTIES_KEY_DISPLAY_TEXT_CHANGED, String.class);
			String eventParameter = object.getMapProperties().get(MAP_PROPERTIES_KEY_EVENT_PARAMETER, String.class);
			String showTextChangeInfo = object.getMapProperties().get(MAP_PROPERTIES_KEY_SHOW_TEXT_CHANGE_INFO, String.class);
			
			if (player.isSpecialActionFeatherSelected() && !isValueChanged(globalConditionKey, globalConditionValue)) {
				GlobalValuesDataHandler.getInstance().put(globalConditionKey, globalConditionValue);
				
				if (eventParameter != null) {
					EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.CHANGE_SIGNBOARD_TEXT).setStringValue(eventParameter));
				}
				if (showTextChangeInfo == null || Boolean.parseBoolean(showTextChangeInfo)) { // default is true
					showOnScreenText("The text was changed.", "Text changed", headerColor);
				}
			}
			else {
				if (isValueChanged(globalConditionKey, globalConditionValue)) {
					showOnScreenText(changedText, headerText, headerColor);
				}
				else {
					showOnScreenText(text, headerText, headerColor);
				}
			}
		}
		
		private void showOnScreenText(String text, String header, String headerColor) {
			textBox.setHeaderText(header, GameUtil.getColorFromRGB(headerColor, Color.RED));
			textBox.setText(text);
		}
		
		private boolean isValueChanged(String globalConditionKey, String globalConditionValue) {
			return GlobalValuesDataHandler.getInstance().isValueEqual(globalConditionKey, globalConditionValue);
		}
	},
	OPEN_SHOP_MENU {
		
		@Override
		public void execute(InteractiveObject object) {
			//fire an event that is handled by the GameScreen
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.SHOW_IN_GAME_SHOP_MENU));
		}
	},
	ENABLE_FAST_TRAVEL_POINT {
		
		@Override
		public void execute(InteractiveObject object) {
			FastTravelPointEventDto eventDto = object.createFastTravelPointEventDto();
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.FAST_TRAVEL_POINT_ENABLED).setParameterObject(eventDto));
		}
	},
	TRAVEL_BACK_TO_SVARTALFHEIM {
		
		private static final String MAP_PROPERTY_KEY_CONDITION = "condition";
		private static final String CUTSCENE_ID_LEVEL_NOT_FINISHED = "loa2_level_not_completed_cutscene";
		private static final String CUTSCENE_ID_TRAVEL_BACK_TO_SVARTALFHEIM = "loa2_travel_back_to_svartalfheim_cutscene";
		
		@Override
		public void execute(InteractiveObject object) {
			String condition = object.getMapProperties().get(MAP_PROPERTY_KEY_CONDITION, String.class);
			if (ConditionHandler.getInstance().isConditionMet(condition)) {
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.START_CUTSCENE) //
						.setStringValue(CUTSCENE_ID_TRAVEL_BACK_TO_SVARTALFHEIM));
			}
			else {
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.START_CUTSCENE) //
						.setStringValue(CUTSCENE_ID_LEVEL_NOT_FINISHED));
			}
		}
	},
	START_CUTSCENE {
		
		private static final String MAP_PROPERTY_KEY_CUTSCENE_ID = "cutsceneId";
		
		@Override
		public void execute(InteractiveObject object) {
			String cutsceneId = object.getMapProperties().get(MAP_PROPERTY_KEY_CUTSCENE_ID, String.class);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.START_CUTSCENE).setStringValue(cutsceneId));
		}
	},
	DEAL_DAMAGE_TO_PLAYER {
		
		private static final String MAP_PROPERTY_KEY_DAMAGE = "damage";
		
		@Override
		public void execute(InteractiveObject object) {
			// if not configured in the map, the trap is an insta-kill trap, so the damage is 100
			float damage = object.getMapProperties().get(MAP_PROPERTY_KEY_DAMAGE, 100f, Float.class);
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.CHANGE_HEALTH) //
					.setFloatValue(-damage));
		}
	},
	FIRE_EVENT {
		
		private static final String MAP_PROPERTY_KEY_EVENT = "event";
		
		@Override
		public void execute(InteractiveObject object) {
			String event = object.getMapProperties().get(MAP_PROPERTY_KEY_EVENT, "", String.class);
			if (event == null || event.isEmpty()) {
				Gdx.app.error(getClass().getSimpleName(), "InteractiveAction - FIRE_EVENT: Event is null or empty. No event fired. MapObjectId is " + object.getMapObjectId());
				return;
			}
			
			EventConfig eventConfig = new Json().fromJson(EventConfig.class, event);
			EventHandler.getInstance().fireEvent(eventConfig);
		}
	};
	
	private static final String MAP_PROPERTY_KEY_COLOR_HEADER = "colorHeader";
	private static final String MAP_PROPERTY_KEY_DISPLAY_TEXT_HEADER = "displayTextHeader";
	private static final String MAP_PROPERTY_KEY_DISPLAY_TEXT = "displayText";
	
	private static GameObjectTextBox textBox;
	private static InteractivePlayer player;
	
	public static void setTextBox(GameObjectTextBox textBox) {
		InteractiveAction.textBox = textBox;
	}
	
	public static void setPlayer(InteractivePlayer player) {
		InteractiveAction.player = player;
	}
	
	public abstract void execute(InteractiveObject object);
}
