package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.player.Player;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.character.state.CharacterStateChangeListener;
import net.jfabricationgames.gdx.condition.Condition;
import net.jfabricationgames.gdx.condition.ConditionType;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;

public class LaserBlaster extends Enemy implements CharacterStateChangeListener {
	
	private boolean inDisabledState = true; // starts disabled
	private final boolean leftBlaster;
	
	public LaserBlaster(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
		stateMachine.addChangeListener(this);
		
		// the direction for the attack has to be set (otherwise an exception will be thrown)
		stateMachine.getState("attack_beam").setTargetDirectionSupplier(this::getDirectionToPlayer);
		stateMachine.getState("attack_beam").setAttackTargetPositionSupplier(Player.getInstance()::getPosition);
		
		leftBlaster = "loa2_l5_castle_of_the_chaos_wizard_spire__laser_blaster_left".equals(getUnitId());
	}
	
	private Vector2 getDirectionToPlayer() {
		return Player.getInstance().getPosition().sub(getPosition()).nor();
	}
	
	@Override
	protected PhysicsBodyProperties definePhysicsBodyProperties() {
		PhysicsBodyProperties properties = super.definePhysicsBodyProperties();
		properties.setDensity(Constants.DENSITY_IMMOVABLE);
		return properties;
	}
	
	@Override
	public void createPhysicsBody(float x, float y) {
		super.createPhysicsBody(x, y);
		
		changeBodyToSensor(); // starts in "disabled" state, so the body should be a sensor initially
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		// no AI - the laser blaster is controlled by the Chaos Wizard
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		if (stateMachine.isInState("attack") || stateMachine.isInState("attack_loop")) {
			if (leftBlaster != isPlayerOnLeftSide()) {
				// stop the attack state if the player is not in the blaster's target area
				stateMachine.setState("idle");
			}
		}
	}
	private boolean isPlayerOnLeftSide() {
		return isPlayerInArea("config_object__chaos_wizard_spire__area_left");
	}
	
	private boolean isPlayerInArea(String areaConfigObjectUnitId) {
		Condition condition = new Condition();
		condition.parameters = new ObjectMap<>();
		condition.parameters.put("objectId", "PLAYER");
		condition.parameters.put("targetAreaObjectId", areaConfigObjectUnitId);
		
		return ConditionType.OBJECT_IN_POSITION.check(condition);
	}
	
	@Override
	public void draw(float delta, SpriteBatch batch) {
		if (inDisabledState) {
			// draw only the first frame of the "appear" animation when disabled
			AnimationDirector<TextureRegion> animation = getAnimation();
			if (animation != null) {
				animation.resetStateTime();
			}
		}
		
		super.draw(delta, batch);
	}
	
	@Override
	public void onCharacterStateChange(CharacterState oldState, CharacterState newState) {
		if ("disabled".equals(newState.getStateName())) {
			inDisabledState = true;
			changeBodyToSensor();
		}
		else {
			inDisabledState = false;
			changeBodyToSolid();
		}
	}
	
	private void changeBodyToSolid() {
		for (Fixture fixture : body.getFixtureList()) {
			if (fixture.getShape().getType() != Type.Circle) { // keep any circular sensor fixtures (for detecting the player)
				fixture.setSensor(false);
			}
		}
	}
	
	@Override
	public void takeDamage(float damage, AttackInfo attackInfo) {
		// the laser blaster is immune to damage
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		super.handleEvent(event);
		
		if (EventType.CONFIG_GENERATED_EVENT.equals(event.eventType)) {
			if ("loa2_l5_castle_of_the_chaos_wizard_spire__activate_vorpal_laser_blaster_of_pittenweem".equals(event.stringValue)) {
				if (getUnitId().equals(event.parameterObject)) { // use the parameter object as second string parameter
					stateMachine.setState("appear");
				}
			}
			else if ("loa2_l5_castle_of_the_chaos_wizard_spire__deactivate_vorpal_laser_blaster_of_pittenweem".equals(event.stringValue)) {
				if (getUnitId().equals(event.parameterObject)) { // use the parameter object as second string parameter
					stateMachine.setState("disappear");
				}
			}
			else if ("loa2_l5_castle_of_the_chaos_wizard_spire__fire_vorpal_laser_blaster_of_pittenweem".equals(event.stringValue)) {
				if (stateMachine.getCurrentState().getStateName().equals("idle") //
						&& (leftBlaster == isPlayerOnLeftSide())) { // only activate the attack if the player is in the blaster's target area
					stateMachine.setState("attack_beam");
				}
			}
			else if ("loa2_l5_castle_of_the_chaos_wizard_spire__pause_vorpal_laser_blaster_of_pittenweem".equals(event.stringValue)) {
				if (stateMachine.getCurrentState().getStateName().equals("attack_beam")) {
					stateMachine.setState("idle");
				}
			}
		}
	}
	
	@Override
	public void removeFromMap() {
		super.removeFromMap();
		
		stateMachine.removeChangeListener(this);
	}
}
