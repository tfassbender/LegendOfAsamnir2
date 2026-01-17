package net.jfabricationgames.gdx.character.enemy.ai;

import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.implementation.MultiAttackAI;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.condition.Condition;
import net.jfabricationgames.gdx.condition.ConditionType;

public class ChaosWizardAttackAI extends MultiAttackAI {
	
	private CharacterState attackMagicFireBall;
	private CharacterState attackMagicFireSoil;
	
	public ChaosWizardAttackAI(ArtificialIntelligence subAI, //
			ArrayMap<String, CharacterState> attackStates, //
			ArrayMap<CharacterState, Float> attackDistances, //
			ArrayMap<CharacterState, AttackTimer> attackTimers) {
		
		super(subAI, attackStates, attackDistances, attackTimers);
		
		attackMagicFireBall = attackStates.get("attackMagicFireBall");
		attackMagicFireSoil = attackStates.get("attackMagicFireSoil");
		
		setMoveToPlayerWhileAttacking(false);
	}
	
	@Override
	protected CharacterState chooseAttack() {
		float distanceToTarget = distanceToTarget();
		
		if (isInRangeForAttack(attackMagicFireSoil, distanceToTarget) && attackTimers.get(attackMagicFireSoil).timeToAttack() && !isPlayerInSaveArea()) {
			return attackMagicFireSoil;
		}
		if (isInRangeForAttack(attackMagicFireBall, distanceToTarget) && attackTimers.get(attackMagicFireBall).timeToAttack()) {
			return attackMagicFireBall;
		}
		
		return null;
	}
	
	private boolean isPlayerInSaveArea() {
		// if the player is hiding behind the wall the attack has to be aborted (otherwise there is no way to avoid this attack or the laser)
		return isPlayerInArea("config_object__chaos_wizard_spire__save_area_left") //
				|| isPlayerInArea("config_object__chaos_wizard_spire__save_area_right");
	}
	
	private boolean isPlayerInArea(String areaConfigObjectUnitId) {
		Condition condition = new Condition();
		condition.parameters = new ObjectMap<>();
		condition.parameters.put("objectId", "PLAYER");
		condition.parameters.put("targetAreaObjectId", areaConfigObjectUnitId);
		
		return ConditionType.OBJECT_IN_POSITION.check(condition);
	}
}
