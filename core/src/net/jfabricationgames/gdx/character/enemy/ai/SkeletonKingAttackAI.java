package net.jfabricationgames.gdx.character.enemy.ai;

import java.util.Objects;
import java.util.stream.IntStream;

import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.implementation.MultiAttackAI;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.map.GameMapManager;

public class SkeletonKingAttackAI extends MultiAttackAI {
	
	private CharacterState attackSwing;
	private CharacterState stateCommand;
	
	public SkeletonKingAttackAI(ArtificialIntelligence subAI, //
			ArrayMap<String, CharacterState> attackStates, //
			ArrayMap<CharacterState, Float> attackDistances, //
			ArrayMap<CharacterState, AttackTimer> attackTimers) {
		
		super(subAI, attackStates, attackDistances, attackTimers);
		
		attackSwing = attackStates.get("attack_swing");
		stateCommand = attackStates.get("command");
		
		setMoveToPlayerWhileAttacking(false);
	}
	
	@Override
	protected CharacterState chooseAttack() {
		float distanceToTarget = distanceToTarget();
		
		if (isInRangeForAttack(attackSwing, distanceToTarget) && attackTimers.get(attackSwing).timeToAttack()) {
			return attackSwing;
		}
		else if (isInRangeForAttack(stateCommand, distanceToTarget) && attackTimers.get(stateCommand).timeToAttack()) {
			return stateCommand;
		}
		
		return null;
	}
	
	@Override
	protected boolean changeToAttackState(CharacterState state) {
		boolean stateChanged = super.changeToAttackState(state);
		
		if (stateChanged && Objects.equals(state, stateCommand)) {
			// spawn a random magic totem in the command state
			String totemToSpawn = chooseRandomTotemPosition();
			if (totemToSpawn != null) { // can be null if all positions are already occupied
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.CUSTCENE_SPAWN_UNIT) //
						.setStringValue(totemToSpawn));
			}
		}
		
		return stateChanged;
	}
	
	private String chooseRandomTotemPosition() {
		String totemUnitIdPrefix = "loa2_l4_helheim_skeleton_zone__boss_totem__";
		String totemSpawnPrefix = "l4_helheim_skeleton_zone__magic_totem_boss_";
		String[] totemPositionEndings = {"up", "up_right", "down_right", "left"};
		boolean[] stillExist = new boolean[totemPositionEndings.length];
		
		// check whether the totems still exist
		for (int i = 0; i < totemPositionEndings.length; i++) {
			if (GameMapManager.getInstance().getMap().getUnitById(totemUnitIdPrefix + totemPositionEndings[i]) != null) {
				stillExist[i] = true;
			}
		}
		
		// check whether all positions are already occupied
		boolean allOccupied = IntStream.range(0, stillExist.length).allMatch(idx -> stillExist[idx]);
		if (allOccupied) {
			return null;
		}
		
		// spawn a totem at a random position that is not occupied
		int randomPosition = (int) (Math.random() * totemPositionEndings.length);
		while (stillExist[randomPosition]) {
			randomPosition = (int) (Math.random() * totemPositionEndings.length);
		}
		
		return totemSpawnPrefix + totemPositionEndings[randomPosition];
	}
}
