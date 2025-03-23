package net.jfabricationgames.gdx.attack;

public class AttackConfig {
	
	public String id;
	public AttackType type;
	public String projectileType;
	public float delay;
	public float duration;
	public float damage;
	public float distFromCenter;
	public float hitFixtureRadius;
	public float pushForce;
	public float pushForceWhenBlocked = -1; // sets a different push force when blocked
	public boolean pushForceAffectedByBlock = true; // reduces the push force by a factor when blocked
	public boolean canBeBlocked = true;
	public boolean canBeBlockedCompletely;
	public boolean shieldDamagedWhenBlocked = true;
	public float explosionDamage;
	public float explosionPushForce;
	public boolean explosionPushForceAffectedByBlock = false;
	public AttackWeaponType weaponType; // used to adapt attacks to skill levels
	public float projectileStartOffsetY;
	public boolean startAttackAtPlayerPosition;
}
