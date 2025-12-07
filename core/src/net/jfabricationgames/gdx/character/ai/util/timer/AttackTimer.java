package net.jfabricationgames.gdx.character.ai.util.timer;

public interface AttackTimer {
	
	public void reset();
	
	public boolean timeToAttack();
	
	public void incrementTime(float delta);
}
