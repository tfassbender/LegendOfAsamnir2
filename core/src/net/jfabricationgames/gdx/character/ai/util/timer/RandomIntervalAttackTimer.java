package net.jfabricationgames.gdx.character.ai.util.timer;

import java.util.Random;

public class RandomIntervalAttackTimer implements AttackTimer {
	
	private final float minTimeBetweenAttacks;
	private final float maxTimeBetweenAttacks;
	
	private float timeSinceLastAttack;
	private float timeTillNextAttack;
	
	private Random random;
	
	public RandomIntervalAttackTimer(float minTimeBetweenAttacks, float maxTimeBetweenAttacks) {
		this.minTimeBetweenAttacks = minTimeBetweenAttacks;
		this.maxTimeBetweenAttacks = maxTimeBetweenAttacks;
		
		random = new Random();
	}
	
	@Override
	public void reset() {
		timeSinceLastAttack = 0;
		timeTillNextAttack = minTimeBetweenAttacks + ((maxTimeBetweenAttacks - minTimeBetweenAttacks) * random.nextFloat());
	}
	
	@Override
	public boolean timeToAttack() {
		return timeSinceLastAttack >= timeTillNextAttack;
	}
	
	@Override
	public void incrementTime(float delta) {
		timeSinceLastAttack += delta;
	}
}
