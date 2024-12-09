package net.jfabricationgames.gdx.condition;

import java.util.function.Function;

import com.badlogic.gdx.Gdx;

import net.jfabricationgames.gdx.data.handler.CharacterItemDataHandler;
import net.jfabricationgames.gdx.data.handler.CharacterPropertiesDataHandler;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.item.TriforceItem;
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
			
			Gdx.app.debug(getClass().getSimpleName(), "Checking if the player has collected at least " + amount + " tokens of type " + token);
			if (amount < 0) {
				Gdx.app.error(getClass().getSimpleName(), "The amount of tokens (of type: " + token //
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
	};
	
	private static final String CONDITIONAL_PARAMETERS_KEY_CONDITION_1 = "condition";
	private static final String CONDITIONAL_PARAMETERS_KEY_CONDITION_2 = "condition2";
	
	private static Function<String, Boolean> isStateSwitchActive;
	
	public static void setIsStateSwitchActive(Function<String, Boolean> isStateSwitchActive) {
		ConditionType.isStateSwitchActive = isStateSwitchActive;
	}
	
	public abstract boolean check(Condition condition);
}
