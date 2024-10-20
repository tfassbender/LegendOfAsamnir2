package net.jfabricationgames.gdx.condition.execution;

import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.event.global.GlobalEventConfig;

public enum ConditionExecutableType {
	
	NO_EXECUTION {
		
		@Override
		public void execute(ConditionExecutable conditionExecutable) {}
	},
	CONDITION {
		
		@Override
		public void execute(ConditionExecutable conditionExecutable) {
			conditionExecutable.conditionalExecution.execute();
		}
	},
	EVENT {
		
		@Override
		public void execute(ConditionExecutable conditionExecutable) {
			GlobalEventConfig event = conditionExecutable.eventConfig;
			event.executionType.execute(event);
		}
	},
	CUTSCENE {
		
		private static final String MAP_KEY_CONDITION_CASE = "conditionCase";
		
		@Override
		public void execute(ConditionExecutable conditionExecutable) {
			String conditionCase = conditionExecutable.executionParameters.get(MAP_KEY_CONDITION_CASE);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.CUTSCENE_CONDITION).setStringValue(conditionCase));
		}
	},
	FIRE_EVENT {
		
		@Override
		public void execute(ConditionExecutable conditionExecutable) {
			EventHandler.getInstance().fireEvent(conditionExecutable.eventConfig.event);
		}
	},
	SET_GLOBAL_VALUE {
		
		private static final String MAP_KEY_GLOBAL_VALUE_KEY = "globalValueKey";
		private static final String MAP_KEY_GLOBAL_VALUE = "globalValue";
		
		@Override
		public void execute(ConditionExecutable eventConfig) {
			String key = eventConfig.executionParameters.get(MAP_KEY_GLOBAL_VALUE_KEY);
			String value = eventConfig.executionParameters.get(MAP_KEY_GLOBAL_VALUE);
			
			ObjectMap<String, Object> parameterObject = new ObjectMap<>();
			parameterObject.put("key", key);
			parameterObject.put("value", value);
			
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.SET_GLOBAL_CONDITION_VALUE).setParameterObject(parameterObject));
		}
	};
	
	public abstract void execute(ConditionExecutable conditionExecutable);
}
