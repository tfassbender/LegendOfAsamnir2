package net.jfabricationgames.gdx.attack;

/**
 * An attack handler that doesn't do anything, but is only used to prevent NPEs.
 */
public class DummyAttackHandler extends AttackHandler {
	
	public DummyAttackHandler() {
		super(null, null, null);
	}
	
	protected void loadAttackConfig(String attackConfigFile) {}
}
