package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;

public class ChaosWizard extends Enemy implements EventListener {
	
	private static final String CHAOS_WIZARD_NAME_1 = "The Allmighty Chaos Wizard - Mega\n Supreme Godlike Overlord of Existence";
	private static final String CHAOS_WIZARD_NAME_2 = "The Mighty Chaos Wizard - Supreme\n Author of the Nine Realms";
	private static final String CHAOS_WIZARD_NAME_FINAL = "The Chaos Wizard - Sovereign of Reality";
	
	public ChaosWizard(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
		// needs to be reset if the game is restarted
		typeConfig.bossName = CHAOS_WIZARD_NAME_1;
	}
	
	@Override
	protected PhysicsBodyProperties definePhysicsBodyProperties() {
		PhysicsBodyProperties properties = super.definePhysicsBodyProperties();
		properties.setDensity(Constants.DENSITY_IMMOVABLE);
		return properties;
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		// TODO
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		super.handleEvent(event);
		
		if (EventType.CONFIG_GENERATED_EVENT.equals(event.eventType) && //
				"loa2_l5_castle_of_the_chaos_wizard__spire__change_chaos_wizard_name".equals(event.stringValue)) {
			if (event.intValue == 2) {
				typeConfig.bossName = CHAOS_WIZARD_NAME_2;
			}
			else if (event.intValue == 3) {
				typeConfig.bossName = CHAOS_WIZARD_NAME_FINAL;
			}
			
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.BOSS_ENEMY_APPEARED) //
					.setParameterObject(this) //
					.setStringValue(typeConfig.bossName));
		}
	}
}
