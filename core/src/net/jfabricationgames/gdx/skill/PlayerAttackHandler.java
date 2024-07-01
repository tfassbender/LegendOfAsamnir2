package net.jfabricationgames.gdx.skill;

import com.badlogic.gdx.physics.box2d.Body;

import net.jfabricationgames.gdx.attack.AttackConfig;
import net.jfabricationgames.gdx.attack.AttackHandler;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.skill.config.WeaponSkillLevelConfig;

/**
 * A special {@link AttackHandler} for the attacks of the player. 
 * This class is used to change the attack configuration based on the skill level of the player.
 */
public class PlayerAttackHandler extends AttackHandler {
	
	private String attackConfigFile;
	private WeaponSkill weaponSkill;
	
	public PlayerAttackHandler(String attackConfigFile, Body body, PhysicsCollisionType collisionType, WeaponSkill weaponSkill) {
		super(attackConfigFile, body, collisionType);
		this.attackConfigFile = attackConfigFile;
		this.weaponSkill = weaponSkill;
		
		reloadAttackConfigAfterSkillChange();
	}
	
	public void reloadAttackConfigAfterSkillChange() {
		// reload the attack config file because the values are changed based on the player's skill level
		loadAttackConfig(attackConfigFile);
		
		// change the attack config based on the player's skill level
		for (AttackConfig config : configs.values()) {
			if (config.weaponType != null) {
				WeaponSkillLevelConfig skillLevelConfig = weaponSkill.getSkillLevelConfig(WeaponSkillType.fromAttackWeaponType(config.weaponType));
				config.damage *= skillLevelConfig.damageInPercent / 100f;
				config.explosionDamage *= skillLevelConfig.damageInPercent / 100f;
			}
		}
	}
}
