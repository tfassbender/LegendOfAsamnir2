package net.jfabricationgames.gdx.cutscene.action;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.condition.execution.ConditionalSwitchExecution;

public class CutsceneSwitchConditionAction extends AbstractCutsceneAction {
	
	private boolean conditionExecuted = false;
	
	private String conditionResultingAction;
	
	public CutsceneSwitchConditionAction(CutsceneUnitProvider unitProvider, CutsceneControlledActionConfig actionConfig) {
		super(unitProvider, actionConfig);
	}
	
	@Override
	public void execute(float delta) {
		if (!conditionExecuted) {
			for (ConditionalSwitchExecution switchCondition : actionConfig.switchConditions) {
				if (switchCondition.isConditionMet()) {
					conditionResultingAction = switchCondition.executedStateName;
					break;
				}
			}
			
			conditionExecuted = true;
		}
	}
	
	@Override
	public boolean isExecutionFinished() {
		return conditionExecuted;
	}
	
	@Override
	public Array<String> getFollowingActions() {
		if (conditionResultingAction == null) {
			Gdx.app.log(getClass().getSimpleName(), "No condition met (no default case given) -> no further actions will be executed");
			return null;
		}
		
		Array<String> chosenAction = new Array<>();
		chosenAction.add(conditionResultingAction);
		
		return chosenAction;
	}
}
