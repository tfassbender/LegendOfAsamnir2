package net.jfabricationgames.gdx.character.ai.util.timer;

/**
 * An AttackTimer that allows for multiple attacks by combining multiple attack timers. 
 * If one of the attack timers is ready to attack, the MultipleAttackTimer is ready to attack as well.
 */
public class MultipleAttackTimer implements AttackTimer {
	
	private final AttackTimer[] attackTimers;
	
	public MultipleAttackTimer(AttackTimer... attackTimers) {
		this.attackTimers = attackTimers;
	}
	
	@Override
	public void reset() {
		for (AttackTimer timer : attackTimers) {
			if (timer.timeToAttack()) {
				timer.reset();
				return; // reset only one timer (the first in the array that is ready to attack)
			}
		}
	}
	
	@Override
	public boolean timeToAttack() {
		for (AttackTimer timer : attackTimers) {
			if (timer.timeToAttack()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void incrementTime(float delta) {
		for (AttackTimer timer : attackTimers) {
			timer.incrementTime(delta);
		}
	}
}
