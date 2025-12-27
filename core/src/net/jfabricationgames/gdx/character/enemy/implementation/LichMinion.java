package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;

/**
 * A Lich that is used as a minion in the battle with the Chaos Wizard. Has no AI, but is controlled by the Chaos Wizard's AI.
 */
public class LichMinion extends Lich {
	
	public LichMinion(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		// no AI of itself - controlled by the Chaos Wizard's AI
	}
	
	@Override
	public void takeDamage(float damage, AttackInfo attackInfo) {
		// the Lich Minion cannot take damage
	}
}
