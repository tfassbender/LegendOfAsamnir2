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
import net.jfabricationgames.gdx.character.enemy.ai.FastAttackFightAI;
import net.jfabricationgames.gdx.character.enemy.ai.SkeletonKingAttackAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.state.GameStateManager;

public class SkeletonKing extends Enemy {
	
	public static final String GLOBAL_VALUE_KEY_SKELETON_KING_DEFEATED = "loa2_l4_helheim__skeleton_king_defeated";
	
	public SkeletonKing(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
		health = 1f; // TODO remove after tests
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
		attackStates.put(stateNameCommand, characterStateCommand);
		
		ArrayMap<CharacterState, Float> attackDistances = new ArrayMap<>();
		attackDistances.put(characterStateAttackSwing, 2f);
		attackDistances.put(characterStateCommand, 100f); // no range restriction
		
		ArrayMap<CharacterState, AttackTimer> attackTimers = new ArrayMap<>();
		attackTimers.put(characterStateAttackSwing, new FixedAttackTimer(1.5f));
		attackTimers.put(characterStateCommand, new FixedAttackTimer(20f));
		
		SkeletonKingAttackAI skeletonKingAttackAI = new SkeletonKingAttackAI(ai, attackStates, attackDistances, attackTimers);
		FastAttackFightAI fastAttackAI = new FastAttackFightAI(skeletonKingAttackAI, characterStateAttackLeap, new FixedAttackTimer(10f), 5f, 2.5f);
		fastAttackAI.setAttackSpeedDelay(0.1f);
		fastAttackAI.setAttackSpeedMaxTime(0.7f);
		
		return fastAttackAI;
	}
	
	@Override
	protected void die() {
		super.die();
		
		// set the global value that indicates that the skeleton king was defeated, so the red key is not spawned anymore
		GlobalValuesDataHandler.getInstance().put(GLOBAL_VALUE_KEY_SKELETON_KING_DEFEATED, true);
		
		// kill all remaining magic totems that the skeleton king summoned
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.ENEMY_DIE) //
				.setStringValue("loa2_l4_helheim_skeleton_zone__boss_totem__up"));
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.ENEMY_DIE) //
				.setStringValue("loa2_l4_helheim_skeleton_zone__boss_totem__up_right"));
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.ENEMY_DIE) //
				.setStringValue("loa2_l4_helheim_skeleton_zone__boss_totem__down_right"));
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.ENEMY_DIE) //
				.setStringValue("loa2_l4_helheim_skeleton_zone__boss_totem__left"));
		
		GameStateManager.fireQuickSaveEvent();
		playMapBackgroundMusicAfterBossDefeated();
		
		// this will trigger a config object that unlocks the gates after a few seconds
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.CONFIG_GAME_OBJECT_ACTION) //
				.setStringValue("loa2_l4_helheim__config_object__open_key_wall_after_skeleton_king_defeated"));
	}
}
