package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;

public class FireTotemDummy extends Enemy {
	
	private static final String MAP_PROPERTY_KEY_FACING_LEFT = "facing_left";
	
	private boolean initialized = false;
	
	private String unitId;
	
	private CharacterState attack;
	
	public FireTotemDummy(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
		unitId = properties.get("unitId", String.class);
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		// no AI to move or attack - attacks will be triggered by the player attacking the fire totem (because it's not a real enemy)
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		if (!initialized) {
			initialized = true;
			
			attack = stateMachine.getState("attack_dummy");
			
			boolean facingLeft = Boolean.parseBoolean(properties.get(MAP_PROPERTY_KEY_FACING_LEFT, "false", String.class));
			if (facingLeft) {
				moveToDirection(-0.1f, 0f);
			}
			else {
				moveToDirection(0.1f, 0f);
			}
			
			Vector2 attackDirection = new Vector2(facingLeft ? -1f : 1f, 0f);
			attack.setAttackDirection(attackDirection);
		}
	}
	
	@Override
	public void takeDamage(float damage, AttackInfo attackInfo) {
		// the fire totem doesn't take damage, but only reacts to magic attacks
		
		AttackType attackType = attackInfo.getAttackType();
		if (attackType.isSubTypeOf(AttackType.MAGIC)) {
			stateMachine.setState(attack);
			
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.FIRE_TOTEM_ACTIVATED) //
					.setStringValue(unitId));
		}
	}
}
