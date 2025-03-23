package net.jfabricationgames.gdx.attack;

import net.jfabricationgames.gdx.projectile.ProjectileTypeConfig;

public class AttackInfo {
	
	private AttackType attackType;
	
	public static AttackInfo from(ProjectileTypeConfig projectileTypeConfig) {
		return new AttackInfo(projectileTypeConfig.attackType);
	}
	
	public static AttackInfo from(AttackConfig config) {
		return new AttackInfo(config.type);
	}
	
	public AttackInfo(AttackType attackType) {
		this.attackType = attackType;
	}
	
	public AttackType getAttackType() {
		return attackType;
	}
	
	public boolean canBeBlocked() {
		return !attackType.isSubTypeOf(AttackType.BEAM) //
				&& !attackType.isSubTypeOf(AttackType.MAGIC) //
				&& !attackType.isSubTypeOf(AttackType.CONTINUOUS_MAP_DAMAGE);
	}
	
	public boolean canBeBlockedCompletely() {
		return attackType.isSubTypeOf(AttackType.BLOCKABLE_FIREBALL);
	}
}
