package net.jfabricationgames.gdx.attack;

import net.jfabricationgames.gdx.projectile.ProjectileTypeConfig;

public class AttackInfo {
	
	private AttackType attackType;
	private boolean canBeBlocked;
	private boolean canBeBlockedCompletely;
	private boolean shieldDamagedWhenBlocked;
	
	public static AttackInfo from(ProjectileTypeConfig projectileTypeConfig, AttackConfig attackConfig) {
		return AttackInfo.from(attackConfig)
				// the projectile type config overwrites some values from the attack config (if they are set)
				.setAttackType(projectileTypeConfig.attackType != null ? projectileTypeConfig.attackType : attackConfig.type);
	}
	
	public static AttackInfo from(AttackConfig attackConfig) {
		return new AttackInfo(attackConfig.type) //
				.setCanBeBlocked(attackConfig.canBeBlocked) //
				.setCanBeBlockedCompletely(attackConfig.canBeBlockedCompletely) //
				.setShieldDamagedWhenBlocked(attackConfig.shieldDamagedWhenBlocked);
	}
	
	public static AttackInfo dummy() {
		return new AttackInfo(AttackType.ATTACK) //
				.setCanBeBlocked(true) //
				.setCanBeBlockedCompletely(true) //
				.setShieldDamagedWhenBlocked(false);
	}
	
	public AttackInfo(AttackType attackType) {
		this.attackType = attackType;
	}
	
	public AttackType getAttackType() {
		return attackType;
	}
	
	private AttackInfo setAttackType(AttackType attackType) {
		this.attackType = attackType;
		return this;
	}
	
	public boolean canBeBlocked() {
		return canBeBlocked //
				// some attacks cannot be blocked because of their type
				&& !attackType.isSubTypeOf(AttackType.BEAM) //
				&& !attackType.isSubTypeOf(AttackType.MAGIC) //
				&& !attackType.isSubTypeOf(AttackType.CONTINUOUS_MAP_DAMAGE);
	}
	
	public AttackInfo setCanBeBlocked(boolean canBeBlocked) {
		this.canBeBlocked = canBeBlocked;
		return this;
	}
	
	public boolean canBeBlockedCompletely() {
		return canBeBlockedCompletely;
	}
	
	public AttackInfo setCanBeBlockedCompletely(boolean canBeBlockedCompletely) {
		this.canBeBlockedCompletely = canBeBlockedCompletely;
		return this;
	}
	
	public boolean shieldDamagedWhenBlocked() {
		return shieldDamagedWhenBlocked;
	}
	
	public AttackInfo setShieldDamagedWhenBlocked(boolean shieldDamagedWhenBlocked) {
		this.shieldDamagedWhenBlocked = shieldDamagedWhenBlocked;
		return this;
	}
}
