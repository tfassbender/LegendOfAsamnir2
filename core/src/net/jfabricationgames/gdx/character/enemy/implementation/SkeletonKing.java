package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.RayCastFollowAI;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.ai.util.timer.FixedAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.SkeletonKingAttackAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.state.GameStateManager;

public class SkeletonKing extends Enemy {
	
	public SkeletonKing(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		ai = createMovementAI(ai);
		ai = createFightAI(ai);
	}
	
	private ArtificialIntelligence createMovementAI(ArtificialIntelligence ai) {
		CharacterState idleState = stateMachine.getState(STATE_NAME_IDLE);
		CharacterState moveState = stateMachine.getState(STATE_NAME_MOVE);
		
		// follow the player if the health is high
		RayCastFollowAI followAI = new RayCastFollowAI(ai, moveState, idleState);
		followAI.setMinDistanceToTarget(2f);
		
		return followAI;
	}
	
	private ArtificialIntelligence createFightAI(ArtificialIntelligence ai) {
		String stateNameAttackSwing = "attack_swing";
		String stateNameAttackLeap = "attack_leap";
		String stateNameCommand = "command";
		
		CharacterState characterStateAttackSwing = stateMachine.getState(stateNameAttackSwing);
		CharacterState characterStateAttackLeap = stateMachine.getState(stateNameAttackLeap);
		CharacterState characterStateCommand = stateMachine.getState(stateNameCommand);
		
		ArrayMap<String, CharacterState> attackStates = new ArrayMap<>();
		attackStates.put(stateNameAttackSwing, characterStateAttackSwing);
		attackStates.put(stateNameAttackLeap, characterStateAttackLeap);
		attackStates.put(stateNameCommand, characterStateCommand);
		
		ArrayMap<CharacterState, Float> attackDistances = new ArrayMap<>();
		attackDistances.put(characterStateAttackSwing, 2f);
		attackDistances.put(characterStateAttackLeap, 5f);
		attackDistances.put(characterStateCommand, 100f); // no range restriction
		
		ArrayMap<CharacterState, AttackTimer> attackTimers = new ArrayMap<>();
		attackTimers.put(characterStateAttackSwing, new FixedAttackTimer(2f));
		attackTimers.put(characterStateAttackLeap, new FixedAttackTimer(10f));
		attackTimers.put(characterStateCommand, new FixedAttackTimer(20f));
		
		return new SkeletonKingAttackAI(ai, attackStates, attackDistances, attackTimers);
	}
	
	@Override
	protected void die() {
		super.die();
		GameStateManager.fireQuickSaveEvent();
		playMapBackgroundMusicAfterBossDefeated();
		
		unlockBossGates();
	}
	
	private void unlockBossGates() {
		// this will trigger a config object that unlocks the gates after a few seconds
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.CONFIG_GAME_OBJECT_ACTION) //
				.setStringValue("loa2_l4_helheim__config_object__open_key_wall_after_skeleton_king_defeated"));
	}
}
