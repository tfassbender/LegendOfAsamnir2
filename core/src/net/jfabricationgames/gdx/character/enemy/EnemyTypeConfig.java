package net.jfabricationgames.gdx.character.enemy;

import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.character.CharacterTypeConfig;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class EnemyTypeConfig extends CharacterTypeConfig {
	
	public String animationsConfig; //the animation config file that is to be loaded (by the factory)
	public String stateConfig; //the state config file that is to be loaded (by the StateMachine)
	public String initialState;
	public String attackConfig;
	public String aiConfig;
	
	public float health;
	public float movingSpeed = 1f;
	
	public float imageOffsetX;
	public float imageOffsetY;
	
	public PhysicsBodyShape bodyShape = PhysicsBodyShape.CIRCLE;
	public float bodyRadius;
	public float bodyWidth;
	public float bodyHeight;
	
	public boolean addSensor = true;
	public float sensorRadius;
	public boolean useSensorAsForceField = false; // the sensor is used so that the player can't get too close to the enemy
	public boolean ignoreForceFieldWhenBlocking = false; // the player can ignore the force field when blocking (using his shield)
	
	public float pushForceDamage; //the force that is applied to the enemy when he takes damage (multiplied with the body mass times 10)
	public float pushForceHit; //the force that is applied to the player when the enemy hits him (multiplied with the body mass times 10)
	
	public boolean usesHealthBar = false;
	public float healthBarOffsetX;
	public float healthBarOffsetY;
	public float healthBarWidthFactor;
	
	public ObjectMap<String, Float> drops;
	public float dropPositionOffsetX;
	public float dropPositionOffsetY;
	public boolean renderDropsAboveObject = false;
	
	public boolean movable = true;
	
	public boolean isBoss = false;
	public String bossName;
	public boolean playBossAppearedSound = true;
}
