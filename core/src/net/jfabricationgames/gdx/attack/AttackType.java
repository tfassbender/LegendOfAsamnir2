package net.jfabricationgames.gdx.attack;

public enum AttackType {
	
	ATTACK(null), //
	MELEE(ATTACK), //
	HIT(MELEE), //
	SPIN_ATTACK(HIT), //
	NOVA(MELEE), //
	PROJECTILE(ATTACK), //
	ARROW(PROJECTILE), //
	BOMB(PROJECTILE), //
	EXPLOSION(BOMB), //
	WEB(PROJECTILE), //
	FIREBALL(PROJECTILE), //
	ROCK(PROJECTILE), //
	BOOMERANG(PROJECTILE), //
	MAGIC(PROJECTILE), //
	COIN_BAG(PROJECTILE), //
	DWARVEN_GUARDIAN_CONSTRUCT_FIST(PROJECTILE), //
	DWARVEN_GUARDIAN_CONSTRUCT_FIRE(PROJECTILE), //
	ANIMATED_HIT(PROJECTILE), // a hit with an animation, that is not part of the character animation - handled as a projectile that doesn't move
	SLINGSHOT(PROJECTILE), //
	FROST_GIANT_AXE_THROW(PROJECTILE), //
	HOOKSHOT(PROJECTILE), //
	WAND(MAGIC), //
	MAGIC_WAVE(MAGIC), //
	FORCE_FIELD(MAGIC), //
	HADOUKEN(MAGIC), //
	BEAM(ATTACK), //
	CONTINUOUS_MAP_DAMAGE(ATTACK); // 
	
	private final AttackType superType;
	
	private AttackType(AttackType superType) {
		this.superType = superType;
	}
	
	public AttackType getSuperType() {
		return superType;
	}
	
	public boolean isSubTypeOf(AttackType type) {
		if (type == this) {
			return true;
		}
		
		AttackType currentType = superType;
		while (currentType != null) {
			if (currentType == type) {
				return true;
			}
			currentType = currentType.superType;
		}
		
		return false;
	}
}