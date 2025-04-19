package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.state.GameStateManager;

public class SkeletonKing extends Enemy {
	
	public SkeletonKing(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
		health = 1; // TODO remove after tests
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
	}
	
	@Override
	protected void die() {
		super.die();
		GameStateManager.fireQuickSaveEvent();
		playMapBackgroundMusicAfterBossDefeated();
		
		unlockBossGates();
	}
	
	private void unlockBossGates() {
		// this will trigger a config object that unlocks the gates after a few seconds
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.CONFIG_GAME_OBJECT_ACTION) //
				.setStringValue("loa2_l4_helheim__config_object__open_key_wall_after_skeleton_king_defeated"));
	}
}
