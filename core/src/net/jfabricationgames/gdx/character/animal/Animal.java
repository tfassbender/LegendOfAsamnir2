package net.jfabricationgames.gdx.character.animal;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.attack.Hittable;
import net.jfabricationgames.gdx.character.AbstractCharacter;
import net.jfabricationgames.gdx.character.CharacterTypeConfig;
import net.jfabricationgames.gdx.character.state.CharacterStateMachine;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsUtil;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public class Animal extends AbstractCharacter implements Hittable {
	
	private static PhysicsBodyProperties physicsBodyProperties = createDefaultPhysicsBodyProperties();
	
	private static PhysicsBodyProperties createDefaultPhysicsBodyProperties() {
		return physicsBodyProperties = new PhysicsBodyProperties() //
				.setType(BodyType.DynamicBody) //
				.setSensor(false) //
				.setCollisionType(PhysicsCollisionType.MAP_OBJECT) //
				.setDensity(10f) //
				.setLinearDamping(10f);
	}
	
	protected AnimalTypeConfig typeConfig;
	
	private AnimalCharacterMap gameMap;
	
	private Runnable onRemoveFromMap;
	
	public Animal(AnimalTypeConfig typeConfig, MapProperties properties) {
		super(properties);
		this.typeConfig = typeConfig;
		
		readTypeConfig();
		initializeStates();
		initializeMovingState();
		initializeIdleState();
		
		createAI();
		ai.setCharacter(this);
		
		setImageOffset(typeConfig.imageOffsetX, typeConfig.imageOffsetY);
	}
	
	private void readTypeConfig() {
		movingSpeed = typeConfig.movingSpeed;
	}
	
	private void initializeStates() {
		stateMachine = new CharacterStateMachine(typeConfig.stateConfig, typeConfig.initialState, null);
	}
	
	protected void createAI() {
		createAiFromConfiguration(typeConfig.aiConfig);
	}
	
	public void setGameMap(AnimalCharacterMap gameMap) {
		this.gameMap = gameMap;
	}
	
	@Override
	protected PhysicsBodyProperties definePhysicsBodyProperties() {
		return physicsBodyProperties.setRadius(typeConfig.bodyRadius) //
				.setWidth(typeConfig.bodyWidth) //
				.setHeight(typeConfig.bodyHeight) //
				.setPhysicsBodyShape(typeConfig.bodyShape) //
				.setDensity(typeConfig.bodyDensity);
	}
	
	@Override
	protected void addAdditionalPhysicsParts() {
		if (typeConfig.addSensor) {
			PhysicsUtil.addEnemySensor(body, typeConfig.sensorRadius);
		}
	}
	
	@Override
	public void act(float delta) {
		stateMachine.updateState(delta);
		
		if (!cutsceneHandler.isCutsceneActive()) {
			ai.calculateMove(delta);
			ai.executeMove(delta);
		}
	}
	
	@Override
	protected CharacterTypeConfig getTypeConfig() {
		return typeConfig;
	}
	
	@Override
	public void removeFromMap() {
		ai.characterRemovedFromMap();
		gameMap.removeAnimal(this, body);
		PhysicsWorld.getInstance().removeContactListener(this);
		body = null;// set the body to null to avoid strange errors in native Box2D methods
		
		if (onRemoveFromMap != null) {
			onRemoveFromMap.run();
		}
	}
	
	public void setOnRemoveFromMap(Runnable onRemoveFromMap) {
		this.onRemoveFromMap = onRemoveFromMap;
	}
	
	@Override
	public void takeDamage(float damage, AttackInfo attackInfo) {
		if (damage > 0 && typeConfig.damageState != null) {
			stateMachine.setState(typeConfig.damageState);
		}
	}
	
	@Override
	public void pushByHit(Vector2 hitCenter, float force, float forceWhenBlocked, boolean blockAffected) {
		if (typeConfig.damageState != null) {
			Vector2 pushDirection = getPushDirection(getPosition(), hitCenter);
			force *= 500f * body.getMass(); // this factor that works for most animals
			body.applyForceToCenter(pushDirection.x * force, pushDirection.y * force, true);
		}
	}
}
