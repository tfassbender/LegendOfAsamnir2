package net.jfabricationgames.gdx.character.ai.util.timer;

public class FixedAttackTimer implements AttackTimer {
	
	private final float fixedTime;
	private float timeSinceLastAttack;
	
	public FixedAttackTimer(float fixedTime) {
		this.fixedTime = fixedTime;
	}
	
	@Override
	public void reset() {
		timeSinceLastAttack = 0;
	}
	
	@Override
	public boolean timeToAttack() {
		return timeSinceLastAttack >= fixedTime;
	}
	
	@Override
	public void incrementTime(float delta) {
		timeSinceLastAttack += delta;
	}
}
