package net.jfabricationgames.gdx.condition;

import java.util.function.Function;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.jfabricationgames.gdx.cutscene.action.CutsceneControlledUnit;
import net.jfabricationgames.gdx.cutscene.variable.CutsceneVariable;
import net.jfabricationgames.gdx.data.handler.CharacterItemDataHandler;
import net.jfabricationgames.gdx.data.handler.CharacterPropertiesDataHandler;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.item.TriforceItem;
import net.jfabricationgames.gdx.item.TriforceItem.TriforceItemType;
import net.jfabricationgames.gdx.map.GameMapManager;
import net.jfabricationgames.gdx.rune.RuneType;

public enum ConditionType {
	
	AND {
		
		@Override
		public boolean check(Condition condition) {
			Condition condition1 = condition.conditionalParameters.get(CONDITIONAL_PARAMETERS_KEY_CONDITION_1);
			Condition condition2 = condition.conditionalParameters.get(CONDITIONAL_PARAMETERS_KEY_CONDITION_2);
			return condition1.check() && condition2.check();
		}
	},
	OR {
		
		@Override
		public boolean check(Condition condition) {
			Condition condition1 = condition.conditionalParameters.get(CONDITIONAL_PARAMETERS_KEY_CONDITION_1);
			Condition condition2 = condition.conditionalParameters.get(CONDITIONAL_PARAMETERS_KEY_CONDITION_2);
			return condition1.check() || condition2.check();
		}
	},
	NOT {
		
		@Override
		public boolean check(Condition condition) {
			Condition negated = condition.conditionalParameters.get(CONDITIONAL_PARAMETERS_KEY_CONDITION_1);
			return !negated.check();
		}
	},
	HAS_ITEM {
		
		private static final String PARAMETERS_ITEM_ID = "itemId";
		
		@Override
		public boolean check(Condition condition) {
			String itemId = condition.parameters.get(PARAMETERS_ITEM_ID);
			return CharacterItemDataHandler.getInstance().containsSpecialItem(itemId);
		}
	},
	HAS_KEY {
		
		private static final String PARAMETERS_KEY_ID = "numKeys";
		
		@Override
		public boolean check(Condition condition) {
			int numKeys = Integer.parseInt(condition.parameters.get(PARAMETERS_KEY_ID, "1"));
			return CharacterItemDataHandler.getInstance().getNumNormalKeys() >= numKeys;
		}
	},
	HAS_COINS {
		
		private static final String PARAMETERS_COINS = "atLeast";
		
		@Override
		public boolean check(Condition condition) {
			int coinsNeeded = Integer.parseInt(condition.parameters.get(PARAMETERS_COINS));
			return CharacterPropertiesDataHandler.getInstance().getCoins() >= coinsNeeded;
		}
	},
	STATE_SWITCH_ACTIVE {
		
		private static final String PARAMETER_STATE_SWITCH_ID = "stateSwitchId";
		
		@Override
		public boolean check(Condition condition) {
			String stateSwitchId = condition.parameters.get(PARAMETER_STATE_SWITCH_ID);
			return isStateSwitchActive.apply(stateSwitchId);
		}
	},
	GLOBAL_VALUE_SET {
		
		private static final String PARAMETER_VALUE_KEY = "key";
		private static final String PARAMETER_EXPECTED_VALUE = "expectedValue";
		
		@Override
		public boolean check(Condition condition) {
			String key = condition.parameters.get(PARAMETER_VALUE_KEY);
			String expectedValue = condition.parameters.get(PARAMETER_EXPECTED_VALUE);
			
			return GlobalValuesDataHandler.getInstance().isValueEqual(key, expectedValue);
		}
	},
	GLOBAL_VALUE_SET_OR_NULL {
		
		private static final String PARAMETER_VALUE_KEY = "key";
		private static final String PARAMETER_EXPECTED_VALUE = "expectedValue";
		
		@Override
		public boolean check(Condition condition) {
			String key = condition.parameters.get(PARAMETER_VALUE_KEY);
			String expectedValue = condition.parameters.get(PARAMETER_EXPECTED_VALUE);
			
			return GlobalValuesDataHandler.getInstance().isValueEqual(key, expectedValue) || GlobalValuesDataHandler.getInstance().get(key) == null;
		}
	},
	GATE_OPENED {
		
		private static final String PARAMETER_GATE_ID = "gateId";
		private static final String GLOBAL_VALUE_KEY_GATE_OPENED_PREFIX = "gate_opened__";
		
		@Override
		public boolean check(Condition condition) {
			String gateId = condition.parameters.get(PARAMETER_GATE_ID);
			return GlobalValuesDataHandler.getInstance().getAsBoolean(GLOBAL_VALUE_KEY_GATE_OPENED_PREFIX + gateId);
		}
	},
	RUNE_COLLECTED {
		
		private static final String PARAMETER_RUNE_NAME = "rune";
		
		@Override
		public boolean check(Condition condition) {
			String runeName = condition.parameters.get(PARAMETER_RUNE_NAME);
			RuneType rune = RuneType.getByContainingName(runeName);
			return rune.isCollected();
		}
	},
	TOKENS_COLLECTED {
		
		private static final String PARAMETER_TOKEN_NAME = "tokenName";
		private static final String PARAMETER_TOKEN_AMOUNT = "neededAmount";
		
		@Override
		public boolean check(Condition condition) {
			String token = condition.parameters.get(PARAMETER_TOKEN_NAME, "<token_name_not_set>");
			int amount = Integer.parseInt(condition.parameters.get(PARAMETER_TOKEN_AMOUNT, "-1"));
			
			Gdx.app.debug(ConditionType.class.getSimpleName(), "Checking if the player has collected at least " + amount + " tokens of type " + token);
			if (amount < 0) {
				Gdx.app.error(ConditionType.class.getSimpleName(), "The amount of tokens (of type: " + token //
						+ ") for the condition is not set correctly or not set at all. Using -1 as fallback.");
			}
			
			return CharacterPropertiesDataHandler.getInstance().getTokens(token) >= amount;
		}
	},
	TRIFORCE_PIECES_COLLECTED {
		
		private static final String PARAMETER_AMOUNT = "neededAmount";
		
		@Override
		public boolean check(Condition condition) {
			int pieces = Integer.parseInt(condition.parameters.get(PARAMETER_AMOUNT, "1"));
			return TriforceItem.getNumCarriedTriforcePieces() >= pieces;
		}
	},
	TRIFORCE_PIECES_DELIVERED {
		
		private static final String PARAMETER_AMOUNT = "neededAmount";
		
		@Override
		public boolean check(Condition condition) {
			int pieces = Integer.parseInt(condition.parameters.get(PARAMETER_AMOUNT, "1"));
			return TriforceItem.getNumDeliveredTriforcePieces() >= pieces;
		}
	},
	TRIFORCE_PIECE_COLLECTED_OR_DELIVERED {
		
		private static final String PARAMETER_ID = "triforceId";
		
		@Override
		public boolean check(Condition condition) {
			String triforceId = condition.parameters.get(PARAMETER_ID);
			return TriforceItemType.getByContainingName(triforceId).isCollectedOrDelivered();
		}
	},
	CHICKENS_MISSING {
		
		private static final String PARAMETER_NUM_CHICKENS = "numChickens";
		
		@Override
		public boolean check(Condition condition) {
			String numChickens = condition.parameters.get(PARAMETER_NUM_CHICKENS, "0");
			return CutsceneVariable.NUM_CHICKENS_MISSING.evaluate().equals(numChickens);
		}
	},
	OBJECT_IN_POSITION {
		
		private static final String PARAMETER_OBJECT_ID = "objectId";
		private static final String PARAMETER_TARGET_LOWER_LEFT_OBJECT_ID = "targetLowerLeftObjectId";
		private static final String PARAMETER_TARGET_UPPER_RIGHT_OBJECT_ID = "targetUpperRightObjectId";
		private static final String PARAMETER_OBJECT_CAN_BE_REMOVED = "objectCanBeRemoved";
		
		@Override
		public boolean check(Condition condition) {
			String objectId = condition.parameters.get(PARAMETER_OBJECT_ID);
			String targetLowerLeftObjectId = condition.parameters.get(PARAMETER_TARGET_LOWER_LEFT_OBJECT_ID);
			String targetUpperRightObjectId = condition.parameters.get(PARAMETER_TARGET_UPPER_RIGHT_OBJECT_ID);
			boolean objectCanBeRemoved = Boolean.parseBoolean(condition.parameters.get(PARAMETER_OBJECT_CAN_BE_REMOVED, "false"));
			
			if (objectId == null || targetLowerLeftObjectId == null || targetUpperRightObjectId == null) {
				Gdx.app.error(getDeclaringClass().getSimpleName(), "The parameters for the OBJECT_IN_POSITION condition are not set correctly: " //
						+ "objectId: " + objectId + ", targetLowerLeftObjectId: " + targetLowerLeftObjectId + ", targetUpperRightObjectId: " + targetUpperRightObjectId);
				return false;
			}
			
			CutsceneControlledUnit object = GameMapManager.getInstance().getMap().getUnitById(objectId);
			CutsceneControlledUnit lowerLeftEventObject = GameMapManager.getInstance().getMap().getUnitById(targetLowerLeftObjectId);
			CutsceneControlledUnit upperRightEventObject = GameMapManager.getInstance().getMap().getUnitById(targetUpperRightObjectId);
			
			if (lowerLeftEventObject == null || upperRightEventObject == null) {
				Gdx.app.error(getDeclaringClass().getSimpleName(), "The object or the target area for the OBJECT_IN_POSITION condition cannot be found: " //
						+ "object: " + object + ", lowerLeftObject: " + lowerLeftEventObject + ", upperRightObject: " + upperRightEventObject);
				return false;
			}
			if (object == null) {
				if (!objectCanBeRemoved) {
					Gdx.app.error(getDeclaringClass().getSimpleName(), "The object for the OBJECT_IN_POSITION condition cannot be found: " + objectId //
							+ " (maybe the parameter \"objectId\" is configured wrong? - " //
							+ "if it can be removed during the game: set \"objectCanBeRemoved\" to true to prevent this error message)");
				}
				
				return false;
			}
			
			Vector2 lowerLeft = lowerLeftEventObject.getPosition();
			Vector2 upperRight = upperRightEventObject.getPosition();
			
			return object.getPosition().x >= lowerLeft.x && object.getPosition().x <= upperRight.x //
					&& object.getPosition().y >= lowerLeft.y && object.getPosition().y <= upperRight.y;
		}
	},
	ROTATING_PUZZLE_COMBINATION {
		
		// all parameters are checked, so no specific key is needed (keys are the ids of the rotating puzzle, values are the expected values)
		
		@Override
		public boolean check(Condition condition) {
			EventHandler eventHandler = EventHandler.getInstance();
			for (Entry<String, String> pair : condition.parameters.entries()) {
				String objectId = pair.key;
				int expectedValue = Integer.parseInt(pair.value);
				
				// the rotating puzzle sets the result into the boolean value of the event
				// (if an object with the id is not found, the boolean value remains false and the condition fails)
				EventConfig requestEvent = new EventConfig() //
						.setEventType(EventType.ROTATING_PUZZLE_STATE_REQUEST) //
						.setStringValue(objectId) //
						.setIntValue(expectedValue);
				
				eventHandler.fireEvent(requestEvent);
				
				if (!requestEvent.booleanValue) {
					return false; // all conditions have to match
				}
			}
			
			return true;
		}
	};
	
	private static final String CONDITIONAL_PARAMETERS_KEY_CONDITION_1 = "condition";
	private static final String CONDITIONAL_PARAMETERS_KEY_CONDITION_2 = "condition2";
	
	private static Function<String, Boolean> isStateSwitchActive;
	
	public static void setIsStateSwitchActive(Function<String, Boolean> isStateSwitchActive) {
		ConditionType.isStateSwitchActive = isStateSwitchActive;
	}
	
	public abstract boolean check(Condition condition);
}
