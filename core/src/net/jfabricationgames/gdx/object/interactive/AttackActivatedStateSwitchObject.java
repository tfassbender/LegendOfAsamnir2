package net.jfabricationgames.gdx.object.interactive;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.attack.hit.AttackType;
import net.jfabricationgames.gdx.object.GameObjectMap;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;

/**
 * A state switch object that is activated by a certain type of attack.
 */
public class AttackActivatedStateSwitchObject extends StateSwitchObject {
	
	private AttackType activationAttackType;
	
	public AttackActivatedStateSwitchObject(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties properties, GameObjectMap gameMap, AttackType activationAttackType) {
		super(typeConfig, sprite, properties, gameMap);
		this.activationAttackType = activationAttackType;
	}
	
	@Override
	public void takeDamage(float damage, AttackType attackType) {
		/*
		 * An attack of the correct type can activate the switch but not deactivate it.
		 * It can be deactivated by an event of type SET_STATE_SWITCH_STATE (see StateSwitchObject.handleEvent) 
		 * if the configuration allows to deactivate the switch.
		 */
		if (!active && attackType.isSubTypeOf(activationAttackType)) {
			executeInteraction();
		}
	}
}
