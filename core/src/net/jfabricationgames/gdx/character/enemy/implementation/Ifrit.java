package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.FollowAI;
import net.jfabricationgames.gdx.character.ai.util.timer.FixedAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.FightAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.state.GameStateManager;

public class Ifrit extends Enemy {
	
	public Ifrit(EnemyTypeConfig typeConfig, MapProperties properties) {
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
		FollowAI followAI = new FollowAI(ai, moveState, idleState);
		followAI.setMinDistanceToTarget(2f);
		
		// TODO
		
		return followAI;
	}
	
	private ArtificialIntelligence createFightAI(ArtificialIntelligence ai) {
		CharacterState attackSword = stateMachine.getState("attack_sword");
		CharacterState attackFireBall = stateMachine.getState("attack_throw_fire_ball");
		
		// TODO
		
		FightAI fightAi = new FightAI(ai, attackSword, new FixedAttackTimer(2f), 2f);
		fightAi.setMoveWhileAttacking(false);
		
		return fightAi;
	}
	
	@Override
	public void createPhysicsBody(float x, float y) {
		super.createPhysicsBody(x, y);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		// TODO
	}
	
	@Override
	protected void die() {
		super.die();
		GameStateManager.fireQuickSaveEvent();
	}
}
