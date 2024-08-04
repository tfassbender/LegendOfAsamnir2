package net.jfabricationgames.gdx.condition.execution;

import com.badlogic.gdx.Gdx;

import net.jfabricationgames.gdx.condition.ConditionHandler;

public class ConditionalSwitchExecution {
	
	public String conditionId; // empty string or null means no condition (default case)
	public String executedStateName; // the name of the next state to execute if the condition is met
	
	public boolean isConditionMet() {
		if (conditionId == null || conditionId.isEmpty()) {
			Gdx.app.debug(getClass().getSimpleName(), "Condition \"" + conditionId + "\" - default case (no condition)");
			return true;
		}
		
		boolean conditionMet = ConditionHandler.getInstance().isConditionMet(conditionId);
		Gdx.app.debug(getClass().getSimpleName(), "Condition \"" + conditionId + "\" result is: " + conditionMet);
		return conditionMet;
	}
}
