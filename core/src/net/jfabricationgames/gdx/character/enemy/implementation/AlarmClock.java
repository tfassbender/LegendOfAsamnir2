package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.attack.DummyAttackHandler;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.util.timer.FixedAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.AlarmClockAI;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class AlarmClock extends Enemy {
	
	public static final String STATE_NAME_RING = "attack";
	
	public AlarmClock(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		
		CharacterState attackState = stateMachine.getState(STATE_NAME_RING);
		ai = new AlarmClockAI(ai, attackState, new FixedAttackTimer(1.5f));
	}
	
	@Override
	protected void initializeAttackHandler() {
		attackHandler = new DummyAttackHandler();
	}
}
