package net.jfabricationgames.gdx.attack;

import net.jfabricationgames.gdx.attack.hit.AttackType;

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
	public float explosionDamage;
	public float explosionPushForce;
	public boolean explosionPushForceAffectedByBlock = false;
}
