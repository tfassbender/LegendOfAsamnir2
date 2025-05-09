package net.jfabricationgames.gdx.character.enemy.statsbar;

import com.badlogic.gdx.graphics.Color;

public class EnemyTimerBarRenderer extends EnemyStatsBarRenderer {
	
	@Override
	protected Color getColor(float timer) {
		return Color.ORANGE;
	}
	
	@Override
	protected float getStatsBarHeight() {
		return 1f;
	}
}