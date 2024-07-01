package net.jfabricationgames.gdx.skill;

import net.jfabricationgames.gdx.attack.AttackWeaponType;

public enum WeaponSkillType {
	
	AXE, // increases the damage
	SHIELD, // increases the amount of damage the player can block with the shield (in percent)
	ARROW, // increases the damage and number of arrows
	BOMB, // increases the damage and number of bombs
	BOOMERANG, // increases the damage 
	WAND; // increases the damage
	
	public static WeaponSkillType fromAttackWeaponType(AttackWeaponType weaponType) {
		if (weaponType == null) {
			return null;
		}
		
		switch (weaponType) {
			case AXE:
				return AXE;
			case ARROW:
				return ARROW;
			case BOMB:
				return BOMB;
			case BOOMERANG:
				return BOOMERANG;
			case WAND:
				return WAND;
			default:
				throw new IllegalArgumentException("The weapon type " + weaponType + " is not a weapon skill type");
		}
	}
}
