package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Shape.Type;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.character.state.CharacterStateChangeListener;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;

public class LaserBlaster extends Enemy implements CharacterStateChangeListener {
	
	private boolean inDisabledState = true; // starts disabled
	
	public LaserBlaster(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
		stateMachine.addChangeListener(this);
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
		
		if (EventType.CONFIG_GENERATED_EVENT.equals(event.eventType) //
				&& "loa2_l5_castle_of_the_chaos_wizard_spire__activate_vorpal_laser_blaster_of_pittenweem".equals(event.stringValue) //
				&& getUnitId().equals(event.parameterObject)) { // use the parameter object as second string parameter
			stateMachine.setState("appear");
		}
		else if (EventType.CONFIG_GENERATED_EVENT.equals(event.eventType) //
				&& "loa2_l5_castle_of_the_chaos_wizard_spire__deactivate_vorpal_laser_blaster_of_pittenweem".equals(event.stringValue) //
				&& getUnitId().equals(event.parameterObject)) { // use the parameter object as second string parameter
			stateMachine.setState("disappear");
		}
	}
	
	@Override
	public void removeFromMap() {
		super.removeFromMap();
		
		stateMachine.removeChangeListener(this);
	}
}
