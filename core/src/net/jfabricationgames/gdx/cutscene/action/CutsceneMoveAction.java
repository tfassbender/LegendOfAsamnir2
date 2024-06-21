package net.jfabricationgames.gdx.cutscene.action;

public class CutsceneMoveAction extends AbstractCutsceneMoveAction {
	
	private CutsceneMoveableUnit controlledUnit;
	
	public CutsceneMoveAction(CutsceneUnitProvider unitProvider, CutsceneControlledActionConfig actionConfig) {
		super(unitProvider, actionConfig);
		// the controlled unit has to be initialized lazy, because it might be spawned from within the cutscene 
	}
	
	@Override
	public void execute(float delta) {
		if (actionConfig.updatePositionRelativeToTarget) {
			if (controlledUnit == null) {
				controlledUnit = getControlledUnitAs(CutsceneMoveableUnit.class);
			}
			findTarget();
		}
		
		moveControlledUnitToTarget();
	}
	
	private void moveControlledUnitToTarget() {
		if (controlledUnit == null) {
			controlledUnit = getControlledUnitAs(CutsceneMoveableUnit.class);
			findTarget();
		}
		
		controlledUnit.changeToMovingState();
		controlledUnit.moveTo(target.cpy(), actionConfig.speedFactor);
	}
	
	@Override
	public boolean isExecutionFinished() {
		return getDistanceToTarget() < MAX_DISTANCE_TO_TARGET_POINT;
	}
	
	@Override
	public boolean isMoveAction() {
		return true;
	}
	
	private float getDistanceToTarget() {
		if (controlledUnit == null) {
			controlledUnit = getControlledUnitAs(CutsceneMoveableUnit.class);
			findTarget();
		}
		
		return target.cpy().sub(controlledUnit.getPosition()).len();
	}
}
