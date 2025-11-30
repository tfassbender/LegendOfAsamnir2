package net.jfabricationgames.gdx.cutscene.action;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;

public abstract class AbstractCutsceneMoveAction extends AbstractCutsceneAction {
	
	public static final float MAX_DISTANCE_TO_TARGET_POINT = 0.1f;
	public static final String FUNCTION_CALL = "_function_call(";
	
	protected Vector2 target;
	
	public AbstractCutsceneMoveAction(CutsceneUnitProvider unitProvider, CutsceneControlledActionConfig actionConfig) {
		super(unitProvider, actionConfig);
	}
	
	protected void findTarget() {
		if (actionConfig.controlledUnitId != null && actionConfig.controlledUnitId.equals(actionConfig.targetPositionRelativeToUnitId) && actionConfig.updatePositionRelativeToTarget) {
			throw new IllegalStateException("The target position can't be relative to the controlled unit AND be updated. The unit will never reach the target.");
		}
		
		CutscenePositioningUnit unit;
		if (actionConfig.targetPositionRelativeToUnitId != null) {
			if (actionConfig.targetPositionRelativeToUnitId.startsWith(FUNCTION_CALL)) {
				actionConfig.targetPositionRelativeToUnitId = resolveFunctionCall(actionConfig.targetPositionRelativeToUnitId);
			}
			
			unit = getUnitAs(actionConfig.targetPositionRelativeToUnitId, CutscenePositioningUnit.class);
		}
		else {
			unit = getControlledUnitAs(CutscenePositioningUnit.class);
		}
		
		target = unit.getPosition().cpy().add(actionConfig.controlledUnitTarget);
	}
	
	private String resolveFunctionCall(String functionCallWithParameter) {
		// get the parameter from the string of the form _function_call(parameter)
		String parameter = functionCallWithParameter.substring(FUNCTION_CALL.length(), functionCallWithParameter.lastIndexOf(')'));
		
		// fire an event to get the result of the function call (the result will be stored in the stringValue of the eventConfig)
		EventConfig eventConfig = new EventConfig().setEventType(EventType.CUTSCENE_FUNCTION_CALL).setStringValue(parameter);
		EventHandler.getInstance().fireEvent(eventConfig);
		
		Gdx.app.log(getClass().getSimpleName(), "Resolved function call '" + functionCallWithParameter + "' to value: " + eventConfig.stringValue);
		
		return eventConfig.stringValue;
	}
}