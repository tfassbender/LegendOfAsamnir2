package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class DwarvenGuardianConstruct extends Enemy {
	
	private static final String MAP_PROPERTY_KEY_FACING_LEFT = "facing_left";
	
	private boolean initialized = false;
	
	private CharacterState attackFist;
	private CharacterState attackFire;
	
	public DwarvenGuardianConstruct(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		// no AI to move or attack - attacks will be triggered by the player attacking the construct (because it's not a real enemy)
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		if (!initialized) {
			initialized = true;
			
			attackFist = stateMachine.getState("attack_fist");
			attackFire = stateMachine.getState("attack_fire");
			
			boolean facingLeft = Boolean.parseBoolean(properties.get(MAP_PROPERTY_KEY_FACING_LEFT, "false", String.class));
			if (facingLeft) {
				moveToDirection(-0.1f, 0f);
			}
			else {
				moveToDirection(0.1f, 0f);
			}
			
			Vector2 attackDirection = new Vector2(facingLeft ? -1f : 1f, 0f);
			attackFist.setAttackDirection(attackDirection);
			attackFire.setAttackDirection(attackDirection);
		}
	}
	
	@Override
	public void takeDamage(float damage, AttackType attackType) {
		// the dwarven guardian construct doesn't take damage 
		
		if (attackType.isSubTypeOf(AttackType.MELEE)) {
			stateMachine.setState(attackFist);
		}
		else if (attackType.isSubTypeOf(AttackType.MAGIC)) {
			stateMachine.setState(attackFire);
		}
	}
}
