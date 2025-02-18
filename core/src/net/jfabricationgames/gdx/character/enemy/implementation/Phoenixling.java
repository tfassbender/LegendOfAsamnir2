package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.BackToStartingPointIfPlayerOutOfSightAI;

public class Phoenixling extends Enemy {
	
	private Array<Fixture> nonSensorFixtures = new Array<>(); // remember the fixtures that are no sensors to set them back to non-sensors after rebirth
	
	public Phoenixling(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
		// the createAI method is called in the super constructor - so the AI has to be (re-)created here
		createAI();
		ai.setCharacter(this);
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		ai = new BackToStartingPointIfPlayerOutOfSightAI(ai, movingState, idleState);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		if (stateMachine.isInState("death") && getAnimation().isAnimationFinished() && !isPlayerNear()) {
			rebirth();
		}
	}
	
	private void rebirth() {
		stateMachine.setState("rebirth");
		health = typeConfig.health; // reset to full health
		
		// set the body to be no sensor (so the player cannot move through it anymore)
		for (Fixture fixture : nonSensorFixtures) {
			fixture.setSensor(false);
		}
	}
	
	private boolean isPlayerNear() {
		return playerToPushAway != null; // the player is in sensor range
	}
	
	protected void pushAwayPlayer() {
		// override to do nothing - the player must not be pushed away, but this is the easiest way to recognize the player is near
	}
	
	@Override
	protected String getDieStateName() {
		return "death";
	}
	
	protected void changeBodyToSensor() {
		for (Fixture fixture : body.getFixtureList()) {
			if (!fixture.isSensor()) {
				nonSensorFixtures.add(fixture);
				fixture.setSensor(true);
			}
		}
	}
}
