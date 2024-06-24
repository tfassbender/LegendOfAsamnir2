package net.jfabricationgames.gdx.event.global;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.event.PlayerChoice;
import net.jfabricationgames.gdx.util.GameUtil;

public enum GlobalEventExecutionType {
	
	SHOW_ON_SCREEN_TEXT {
		
		private static final String MAP_KEY_DISPLAY_TEXT = "displayText";
		private static final String MAP_KEY_DISPLAY_TEXT_HEADER = "displayTextHeader";
		private static final String MAP_KEY_COLOR_HEADER = "colorHeader";
		private static final String MAP_KEY_SHOW_NEXT_PAGE_ICON = "showNextPageIcon";
		private static final String MAP_KEY_SHOW_ON_BLACK_SCREEN = "showOnBlackScreen";
		private static final String MAP_KEY_CAN_BE_SKIPPED = "canBeSkipped";
		
		@Override
		public void execute(GlobalEventConfig eventConfig) {
			String displayText = eventConfig.executionParameters.get(MAP_KEY_DISPLAY_TEXT);
			String displayTextHeader = eventConfig.executionParameters.get(MAP_KEY_DISPLAY_TEXT_HEADER);
			String colorHeader = eventConfig.executionParameters.get(MAP_KEY_COLOR_HEADER);
			boolean showNextPageIcon = Boolean.parseBoolean(eventConfig.executionParameters.get(MAP_KEY_SHOW_NEXT_PAGE_ICON));
			boolean showOnBlackScreen = Boolean.parseBoolean(eventConfig.executionParameters.get(MAP_KEY_SHOW_ON_BLACK_SCREEN));
			boolean canBeSkipped = Boolean.parseBoolean(eventConfig.executionParameters.get(MAP_KEY_CAN_BE_SKIPPED, "true"));
			
			textBox.setHeaderText(displayTextHeader, GameUtil.getColorFromRGB(colorHeader, Color.RED));
			textBox.setText(displayText, showNextPageIcon);
			textBox.setCanBeSkipped(canBeSkipped);
			textBox.setShowOnBlackScreen(showOnBlackScreen);
		}
	},
	SHOW_PLAYER_CHOICE {
		
		private static final String MAP_KEY_HEADER = "header";
		private static final String MAP_KEY_HEADER_COLOR = "headerColor";
		private static final String MAP_KEY_DESCRIPTION = "description";
		private static final String MAP_KEY_PREFIX_OPTION = "option_";
		
		@Override
		public void execute(GlobalEventConfig eventConfig) {
			if (eventConfig.parameterObject != null && eventConfig.parameterObject instanceof PlayerChoice) {
				textBox.showPlayerChoice((PlayerChoice) eventConfig.parameterObject);
			}
			else {
				PlayerChoice playerChoice = new PlayerChoice();
				playerChoice.header = eventConfig.executionParameters.get(MAP_KEY_HEADER);
				playerChoice.headerColor = eventConfig.executionParameters.get(MAP_KEY_HEADER_COLOR);
				playerChoice.description = eventConfig.executionParameters.get(MAP_KEY_DESCRIPTION);
				playerChoice.options = new Array<>();
				for (int i = 0; i < PlayerChoice.MAX_OPTIONS; i++) {
					String option = eventConfig.executionParameters.get(MAP_KEY_PREFIX_OPTION + i);
					if (option != null) {
						playerChoice.options.add(option);
					}
				}
				
				textBox.showPlayerChoice(playerChoice);
			}
		}
	},
	START_CUTSCENE {
		
		private static final String MAP_KEY_CUTSCENE_ID = "cutsceneId";
		
		@Override
		public void execute(GlobalEventConfig eventConfig) {
			String cutsceneId = eventConfig.executionParameters.get(MAP_KEY_CUTSCENE_ID);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.START_CUTSCENE).setStringValue(cutsceneId));
		}
	},
	CHANGE_MAP {
		
		private static final String MAP_KEY_TARGET_MAP = "map";
		private static final String MAP_KEY_STARTING_POINT_ID = "startingPointId";
		
		@Override
		public void execute(GlobalEventConfig eventConfig) {
			String targetMap = eventConfig.executionParameters.get(MAP_KEY_TARGET_MAP);
			int targetPointId = Integer.parseInt(eventConfig.executionParameters.get(MAP_KEY_STARTING_POINT_ID, "0"));
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.CHANGE_MAP).setStringValue(targetMap).setIntValue(targetPointId));
		}
	},
	CONDITIONAL_EVENT {
		
		@Override
		public void execute(GlobalEventConfig eventConfig) {
			conditionalExecutor.executeConditional(eventConfig.conditionalExecutionId);
		}
	},
	SET_ITEM {
		
		private static final String MAP_KEY_ITEM_NAME = "item";
		
		@Override
		public void execute(GlobalEventConfig eventConfig) {
			String item = eventConfig.executionParameters.get(MAP_KEY_ITEM_NAME);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.SET_ITEM).setStringValue(item));
		}
	},
	SET_GLOBAL_VALUE {
		
		private static final String MAP_KEY_GLOBAL_VALUE_KEY = "globalValueKey";
		private static final String MAP_KEY_GLOBAL_VALUE = "globalValue";
		
		@Override
		public void execute(GlobalEventConfig eventConfig) {
			String key = eventConfig.executionParameters.get(MAP_KEY_GLOBAL_VALUE_KEY);
			String value = eventConfig.executionParameters.get(MAP_KEY_GLOBAL_VALUE);
			
			ObjectMap<String, Object> parameterObject = new ObjectMap<>();
			parameterObject.put("key", key);
			parameterObject.put("value", value);
			
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.SET_GLOBAL_CONDITION_VALUE).setParameterObject(parameterObject));
		}
	},
	INCREASE_GLOBAL_VALUE {
		
		private static final String MAP_KEY_GLOBAL_VALUE_KEY = "globalValueKey";
		
		@Override
		public void execute(GlobalEventConfig eventConfig) {
			String key = eventConfig.executionParameters.get(MAP_KEY_GLOBAL_VALUE_KEY);
			
			ObjectMap<String, Object> parameterObject = new ObjectMap<>();
			parameterObject.put("key", key);
			
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.INCREASE_GLOBAL_CONDITION_VALUE).setParameterObject(parameterObject));
		}
	},
	CHANGE_HEALTH {
		
		private static final String MAP_KEY_AMOUNT = "amount";
		
		@Override
		public void execute(GlobalEventConfig eventConfig) {
			String amount = eventConfig.executionParameters.get(MAP_KEY_AMOUNT);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.CHANGE_HEALTH).setFloatValue(Float.parseFloat(amount)));
		}
	},
	CHANGE_SHIELD {
		
		private static final String MAP_KEY_AMOUNT = "amount";
		
		@Override
		public void execute(GlobalEventConfig eventConfig) {
			String amount = eventConfig.executionParameters.get(MAP_KEY_AMOUNT);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.CHANGE_SHIELD).setFloatValue(Float.parseFloat(amount)));
		}
	},
	CHANGE_MANA {
		
		private static final String MAP_KEY_AMOUNT = "amount";
		
		@Override
		public void execute(GlobalEventConfig eventConfig) {
			String amount = eventConfig.executionParameters.get(MAP_KEY_AMOUNT);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.CHANGE_MANA).setFloatValue(Float.parseFloat(amount)));
		}
	},
	OPEN_LOCK {
		
		private static final String MAP_KEY_LOCK_ID = "lockId";
		private static final String MAP_KEY_DELAY_IN_SECONDS = "delayInSeconds";
		
		@Override
		public void execute(GlobalEventConfig eventConfig) {
			String lockId = eventConfig.executionParameters.get(MAP_KEY_LOCK_ID);
			String delayInSecondsMapProperty = eventConfig.executionParameters.get(MAP_KEY_DELAY_IN_SECONDS, "0f");
			
			float delayInSeconds = Float.parseFloat(delayInSecondsMapProperty);
			new Thread(() -> {
				try {
					Thread.sleep((long) (delayInSeconds * 1000));
				}
				catch (InterruptedException e) {
					// interrupt can be ignored
				}
				EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.OPEN_LOCK).setStringValue(lockId));
			}).start();
		}
	},
	CLOSE_LOCK {
		
		private static final String MAP_KEY_LOCK_ID = "lockId";
		
		@Override
		public void execute(GlobalEventConfig eventConfig) {
			String lockId = eventConfig.executionParameters.get(MAP_KEY_LOCK_ID);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.CLOSE_LOCK).setStringValue(lockId));
		}
	},
	RECEIVE_ITEM {
		
		private static final String MAP_KEY_ITEM_NAME = "item";
		private static final String MAP_KEY_SUPPRESS_SOUND = "suppressSound";
		
		@Override
		public void execute(GlobalEventConfig eventConfig) {
			String item = eventConfig.executionParameters.get(MAP_KEY_ITEM_NAME);
			boolean suppressSound = Boolean.parseBoolean(eventConfig.executionParameters.get(MAP_KEY_SUPPRESS_SOUND));
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.GIVE_ITEM_TO_PLAYER) //
					.setStringValue(item) //
					.setBooleanValue(suppressSound));
		}
	};
	
	private static GlobalEventTextBox textBox;
	private static GlobalEventConditionalExecutor conditionalExecutor;
	
	public static void setConditionalExecutor(GlobalEventConditionalExecutor conditionalExecutor) {
		GlobalEventExecutionType.conditionalExecutor = conditionalExecutor;
	}
	
	public static void setGlobalEventTextBox(GlobalEventTextBox textBox) {
		GlobalEventExecutionType.textBox = textBox;
	}
	
	public abstract void execute(GlobalEventConfig eventConfig);
}
