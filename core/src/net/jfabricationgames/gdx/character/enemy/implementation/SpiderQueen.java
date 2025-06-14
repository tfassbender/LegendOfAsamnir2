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
import net.jfabricationgames.gdx.character.enemy.ai.SpiderQueenAttackAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.state.GameStateManager;

public class SpiderQueen extends Enemy {
	
	public SpiderQueen(EnemyTypeConfig typeConfig, MapProperties properties) {
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
		
		RayCastFollowAI followAI = new RayCastFollowAI(ai, moveState, idleState);
		followAI.setMinDistanceToTarget(2f);
		
		return followAI;
	}
	
	private ArtificialIntelligence createFightAI(ArtificialIntelligence ai) {
		String stateNameAttackMelee = "attack_melee";
		String stateNameAttackWeb = "attack_web";
		String stateNameAttackCocoon = "attack_cocoon";
		String stateNameAttackCocoonProjectile = "attack_cocoon_projectile";
		
		CharacterState characterStateAttackMelee = stateMachine.getState(stateNameAttackMelee);
		CharacterState characterStateAttackWeb = stateMachine.getState(stateNameAttackWeb);
		CharacterState characterStateAttackCocoon = stateMachine.getState(stateNameAttackCocoon);
		CharacterState characterStateAttackCocoonProjectile = stateMachine.getState(stateNameAttackCocoonProjectile);
		
		ArrayMap<String, CharacterState> attackStates = new ArrayMap<>();
		attackStates.put(stateNameAttackMelee, characterStateAttackMelee);
		attackStates.put(stateNameAttackWeb, characterStateAttackWeb);
		attackStates.put(stateNameAttackCocoon, characterStateAttackCocoon);
		attackStates.put(stateNameAttackCocoonProjectile, characterStateAttackCocoonProjectile);
		
		ArrayMap<CharacterState, Float> attackDistances = new ArrayMap<>();
		attackDistances.put(characterStateAttackMelee, 2f);
		attackDistances.put(characterStateAttackWeb, 8f);
		attackDistances.put(characterStateAttackCocoon, 100f); // no distance restriction because it's not a direct attack
		attackDistances.put(characterStateAttackCocoonProjectile, 6f);
		
		ArrayMap<CharacterState, AttackTimer> attackTimers = new ArrayMap<>();
		attackTimers.put(characterStateAttackMelee, new FixedAttackTimer(2.5f));
		attackTimers.put(characterStateAttackWeb, new FixedAttackTimer(6f));
		attackTimers.put(characterStateAttackCocoon, new FixedAttackTimer(15f));
		attackTimers.put(characterStateAttackCocoonProjectile, new FixedAttackTimer(4f));
		
		return new SpiderQueenAttackAI(ai, attackStates, attackDistances, attackTimers);
	}
	
	@Override
	protected void die() {
		super.die();
		GameStateManager.fireQuickSaveEvent();
		playMapBackgroundMusicAfterBossDefeated();
		
		// triggers a config object that unlocks the gates after a few seconds
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.CONFIG_GAME_OBJECT_ACTION) //
				.setStringValue("loa2_l4_helheim__config_object__open_key_wall_after_spider_queen_defeated"));
		
		// kill all spawned bloodsilk spiders when the spider queen is defeated
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.ENEMY_DIE));
	}
}
