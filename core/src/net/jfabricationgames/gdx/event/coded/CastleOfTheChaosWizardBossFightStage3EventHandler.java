package net.jfabricationgames.gdx.event.coded;

import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.npc.NonPlayableCharacter;
import net.jfabricationgames.gdx.condition.Condition;
import net.jfabricationgames.gdx.condition.ConditionType;
import net.jfabricationgames.gdx.cutscene.CutsceneHandler;
import net.jfabricationgames.gdx.cutscene.action.CutsceneControlledUnit;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.map.GameMapManager;
import net.jfabricationgames.gdx.map.GameMapProcessable;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public class CastleOfTheChaosWizardBossFightStage3EventHandler extends CodedEventHandler implements GameMapProcessable {
	
	private boolean obeliskDestroyedTopLeft = false;
	private boolean obeliskDestroyedTopRight = false;
	private boolean obeliskDestroyedBottomLeft = false;
	private boolean obeliskDestroyedBottomRight = false;
	
	private int battleStage = 1;
	
	// push away the player before the laser blaster appears on the other side
	private float timeTillChaosWizardPushNova = 0f;
	// there must only be one laser blaster at a time, so it has to disappear before the other appears
	private float timeTillLaserBlasterDisappearesOnTheLeftSide = 0f;
	private float timeTillLaserBlasterDisappearesOnTheRightSide = 0f;
	// let the laser blaster appear a bit later than it disappears on the other side
	private float timeTillLaserBlasterChangedToLeftSide = 0f;
	private float timeTillLaserBlasterChangedToRightSide = 0f;
	
	@Override
	public void handleEvent(EventConfig event) {
		if (EventType.PLAYER_RESPAWNED.equals(event.eventType)) {
			// reset the obelisk states after the game is restarted
			obeliskDestroyedTopLeft = false;
			obeliskDestroyedTopRight = false;
			obeliskDestroyedBottomLeft = false;
			obeliskDestroyedBottomRight = false;
			timeTillChaosWizardPushNova = 0f;
			timeTillLaserBlasterDisappearesOnTheRightSide = 0.1f; // removes the laser blaster - should not be needed but just in case
			timeTillLaserBlasterDisappearesOnTheLeftSide = 0.1f; // removes the laser blaster - should not be needed but just in case
			timeTillLaserBlasterChangedToLeftSide = 0f;
			timeTillLaserBlasterChangedToRightSide = 0f;
			battleStage = 1;
		}
		else if (EventType.ENEMY_DIE.equals(event.eventType)) {
			// the chaos wizard was defeated -> reset all timers, turn off the magic flames and remove the laser blaster
			timeTillChaosWizardPushNova = 0f;
			timeTillLaserBlasterDisappearesOnTheRightSide = 0.1f;
			timeTillLaserBlasterDisappearesOnTheLeftSide = 0.1f;
			timeTillLaserBlasterChangedToLeftSide = 0f;
			timeTillLaserBlasterChangedToRightSide = 0f;
			
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SENSOR) //
					.setIntValue(1));
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SENSOR) //
					.setIntValue(2));
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SENSOR) //
					.setIntValue(3));
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SENSOR) //
					.setIntValue(4));
		}
		else if (EventType.DESTROYABLE_OBJECT_DESTROYED.equals(event.eventType)) {
			String animationObjectIdToTurnOff = null;
			if ("loa2_l5_castle_of_the_chaos_wizard__spire__magic_obelisk_top_left".equals(event.stringValue)) {
				animationObjectIdToTurnOff = "loa2_l5_castle_of_the_chaos_wizard__spire__magic_energy_line_top_left";
				obeliskDestroyedTopLeft = true;
			}
			else if ("loa2_l5_castle_of_the_chaos_wizard__spire__magic_obelisk_top_right".equals(event.stringValue)) {
				animationObjectIdToTurnOff = "loa2_l5_castle_of_the_chaos_wizard__spire__magic_energy_line_top_right";
				obeliskDestroyedTopRight = true;
			}
			else if ("loa2_l5_castle_of_the_chaos_wizard__spire__magic_obelisk_bottom_left".equals(event.stringValue)) {
				animationObjectIdToTurnOff = "loa2_l5_castle_of_the_chaos_wizard__spire__magic_energy_line_bottom_left";
				obeliskDestroyedBottomLeft = true;
			}
			else if ("loa2_l5_castle_of_the_chaos_wizard__spire__magic_obelisk_bottom_right".equals(event.stringValue)) {
				animationObjectIdToTurnOff = "loa2_l5_castle_of_the_chaos_wizard__spire__magic_energy_line_bottom_right";
				obeliskDestroyedBottomRight = true;
			}
			
			if (animationObjectIdToTurnOff != null) {
				// turn off the magic energy line
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.DISPLAY_ANIMATION_GAME_OBJECT) //
						.setStringValue(animationObjectIdToTurnOff) //
						.setBooleanValue(false));
			}
			
			if (obeliskDestroyedTopLeft && obeliskDestroyedTopRight && obeliskDestroyedBottomLeft && obeliskDestroyedBottomRight) {
				// all obelisks destroyed -> remove the magic shield around the Chaos Wizard
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SENSOR) //
						.setIntValue(3));
				
				// also remove the magic shield that is behind the chaos wizard in the final stage
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SENSOR) //
						.setIntValue(4));
			}
		}
		else if (EventType.CONFIG_GENERATED_EVENT.equals(event.eventType)) {
			if ("restore_magic_obelisks".equals(event.stringValue)) {
				obeliskDestroyedTopLeft = false;
				obeliskDestroyedTopRight = false;
				obeliskDestroyedBottomLeft = false;
				obeliskDestroyedBottomRight = false;
			}
			else if ("activate_vorpal_laser_blaster_of_pittenweem".equals(event.stringValue)) {
				if (isPlayerOnLeftSide()) {
					EventHandler.getInstance().fireEvent(new EventConfig() //
							.setEventType(EventType.CONFIG_GENERATED_EVENT) //
							.setStringValue("loa2_l5_castle_of_the_chaos_wizard_spire__activate_vorpal_laser_blaster_of_pittenweem") //
							.setParameterObject("loa2_l5_castle_of_the_chaos_wizard_spire__laser_blaster_left"));
				}
				else {
					EventHandler.getInstance().fireEvent(new EventConfig() //
							.setEventType(EventType.CONFIG_GENERATED_EVENT) //
							.setStringValue("loa2_l5_castle_of_the_chaos_wizard_spire__activate_vorpal_laser_blaster_of_pittenweem") //
							.setParameterObject("loa2_l5_castle_of_the_chaos_wizard_spire__laser_blaster_right"));
				}
			}
			else if ("spawn_magic_flames_near_vorpal_laser_blaster_of_pittenweem".equals(event.stringValue)) {
				spawnMagicFlamesForLaserBlaster();
			}
			else if ("loa2_l5_castle_of_the_chaos_wizard__spire__setup".equals(event.stringValue)) {
				GameMapManager.getInstance().getMap().registerGameMapProcessable(this); // unregistered when player dies
			}
			else if ("loa2_l5_castle_of_the_chaos_wizard__spire__change_battle_stage".equals(event.stringValue)) {
				battleStage = event.intValue;
			}
			else if ("loa2_l5_castle_of_the_chaos_wizard__spire__spawn_magic_flame_circle__final_stage".equals(event.stringValue)) {
				spawnMagicFlamesForLaserBlaster();
			}
			else if ("loa2_l5_castle_of_the_chaos_wizard__spire__move_laser_blaster_to_player_side".equals(event.stringValue)) {
				if (isPlayerOnLeftSide()) {
					timeTillLaserBlasterDisappearesOnTheRightSide = 0.1f; // make the laser blaster disappear on one side first 
					timeTillLaserBlasterDisappearesOnTheLeftSide = 0f; // reset the other timer
					timeTillLaserBlasterChangedToLeftSide = 2.5f; // let the laser blaster appear shortly after it disappeared on the other side
					timeTillLaserBlasterChangedToRightSide = 0f; // reset the other timer
				}
				else {
					timeTillLaserBlasterDisappearesOnTheLeftSide = 0.1f; // make the laser blaster disappear on one side first 
					timeTillLaserBlasterDisappearesOnTheRightSide = 0f; // reset the other timer
					timeTillLaserBlasterChangedToRightSide = 2.5f; // let the laser blaster appear shortly after it disappeared on the other side
					timeTillLaserBlasterChangedToLeftSide = 0f; // reset the other timer
				}
			}
			else if ("loa2_l5_castle_of_the_chaos_wizard__spire__spawn_flameskulls_on_player_side".equals(event.stringValue)) {
				spawnFlameskullsOnPlayerSide(3);
			}
			else if ("loa2_l5_castle_of_the_chaos_wizard__spire__link_chaos_wizard_to_griffin".equals(event.stringValue)) {
				// link the chaos wizard to the griffin with a joint
				Enemy chaosWizard = (Enemy) GameMapManager.getInstance().getMap().getUnitById("loa2_l5_castle_of_the_chaos_wizard__chaos_wizard");
				NonPlayableCharacter griffin = (NonPlayableCharacter) GameMapManager.getInstance().getMap().getUnitById("loa2_l5_castle_of_the_chaos_wizard__spire__griffin");
				
				// connect the bodies of the chaos wizard and the griffin with a weld joint, so they move together during the cutscene
				WeldJointDef jointDef = new WeldJointDef();
				jointDef.initialize(griffin.getBody(), chaosWizard.getBody(), griffin.getBody().getWorldCenter());
				jointDef.collideConnected = false;
				
				PhysicsWorld.getInstance().createJoint(jointDef, null);
			}
		}
		else if (EventType.PLAYER_DIED.equals(event.eventType)) {
			GameMapManager.getInstance().getMap().unregisterGameMapProcessable(this); // unregister to prevent being added twice
		}
		else if (EventType.EVENT_OBJECT_TOUCHED.equals(event.eventType)) {
			boolean playerChangedToLeftSide = "loa2_l5_castle_of_the_chaos_wizard__spire__left_side".equals(event.stringValue);
			boolean playerChangedToRightSide = "loa2_l5_castle_of_the_chaos_wizard__spire__right_side".equals(event.stringValue);
			if (battleStage >= 5 && (playerChangedToLeftSide || playerChangedToRightSide)) {
				// from battle stage 5 on, the lich minions fire a targeting projectile when a player changes the side
				String eventString = "loa2_l5_castle_of_the_chaos_wizard_spire__lich_minion_fire_targeting_projectile__";
				if (isPlayerInBottomArea()) {
					if (battleStage == 5) {
						// in battle stage 5 the near lich fires the targeting projectile
						eventString += "bottom";
					}
					else {
						// in battle stage 6 and 7 the far lich fires the targeting projectile
						eventString += "top";
					}
				}
				else if (isPlayerInTopArea()) {
					if (battleStage == 5) {
						// in battle stage 5, the far lich fires the targeting projectile
						eventString += "top";
					}
					else {
						// in battle stage 6 and 7, the near lich fires the targeting projectile
						eventString += "bottom";
					}
				}
				else {
					// the player should not be able to move through the center in this stage - just in case, do nothing
					return;
				}
				
				// fire the event to inform the lich minion
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.CONFIG_GENERATED_EVENT) //
						.setStringValue(eventString));
				
				if (battleStage >= 6) {
					// in battle stage 6 and 7, the near lich also casts a spell to invert the player's controls
					String eventStringInvertControls = "loa2_l5_castle_of_the_chaos_wizard_spire__lich_minion_invert_player_controls__";
					if (isPlayerInBottomArea()) {
						eventStringInvertControls += "bottom";
					}
					else if (isPlayerInTopArea()) {
						eventStringInvertControls += "top";
					}
					
					EventHandler.getInstance().fireEvent(new EventConfig() //
							.setEventType(EventType.CONFIG_GENERATED_EVENT) //
							.setStringValue(eventStringInvertControls));
				}
				
				if (battleStage == 7) {
					spawnFlameskullsOnPlayerSide(3);
				}
				
				if (playerChangedToLeftSide) {
					timeTillChaosWizardPushNova = 9.5f; // push the player away before the laser blaster changes side
					timeTillLaserBlasterDisappearesOnTheRightSide = 8.5f; // make the laser blaster disappear on one side first 
					timeTillLaserBlasterDisappearesOnTheLeftSide = 0f; // reset the other timer
					timeTillLaserBlasterChangedToLeftSide = 10f; // let the laser blaster appear shortly after it disappeared on the other side
					timeTillLaserBlasterChangedToRightSide = 0f; // reset the other timer
				}
				else if (playerChangedToRightSide) {
					timeTillChaosWizardPushNova = 9.5f; // push the player away before the laser blaster changes side
					timeTillLaserBlasterDisappearesOnTheLeftSide = 8.5f; // make the laser blaster disappear on one side first
					timeTillLaserBlasterDisappearesOnTheRightSide = 0f; // reset the other timer
					timeTillLaserBlasterChangedToRightSide = 10f; // let the laser blaster appear shortly after it disappeared on the other side
					timeTillLaserBlasterChangedToLeftSide = 0f; // reset the other timer
				}
			}
		}
	}
	
	private void spawnMagicFlamesForLaserBlaster() {
		if (isPlayerOnLeftSide()) {
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SOLID_OBJECT) //
					.setIntValue(1));
		}
		else {
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SOLID_OBJECT) //
					.setIntValue(2));
		}
		
		if (battleStage == 7) {
			// in the final stage: also spawn some flames behind the chaos wizard, that will stay active till the obelisks are destroyed
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SOLID_OBJECT) //
					.setIntValue(4));
		}
	}
	
	private boolean isPlayerOnLeftSide() {
		return isPlayerInArea("config_object__chaos_wizard_spire__area_left");
	}
	
	private boolean isPlayerInBottomArea() {
		return isPlayerInArea("config_object__chaos_wizard_spire__area_bottom");
	}
	
	private boolean isPlayerInTopArea() {
		return isPlayerInArea("config_object__chaos_wizard_spire__area_top");
	}
	
	private boolean isPlayerInArea(String areaConfigObjectUnitId) {
		Condition condition = new Condition();
		condition.parameters = new ObjectMap<>();
		condition.parameters.put("objectId", "PLAYER");
		condition.parameters.put("targetAreaObjectId", areaConfigObjectUnitId);
		
		return ConditionType.OBJECT_IN_POSITION.check(condition);
	}
	
	private void spawnFlameskullsOnPlayerSide(int maxFlameskullsPerSide) {
		if (isPlayerOnLeftSide()) {
			if (countFlameskullsOnLeftSide() < maxFlameskullsPerSide) {
				String spawnType = "flameskull__castle_of_the_chaos_wizard_spire__no_charge_attack__left";
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.CUSTCENE_SPAWN_UNIT) //
						.setStringValue(spawnType));
			}
		}
		else {
			// the map is divided in left and right - the sides take up the whole reachable area -> the player has to be on the right side
			if (countFlameskullsOnRightSide() < maxFlameskullsPerSide) {
				String spawnType = "flameskull__castle_of_the_chaos_wizard_spire__no_charge_attack__right";
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.CUSTCENE_SPAWN_UNIT) //
						.setStringValue(spawnType));
			}
		}
	}
	
	private int countFlameskullsOnLeftSide() {
		return countUnitsOnMap("loa2_l5_castle_of_the_chaos_wizard__spire__flameskull_spawned_left");
	}
	
	private int countFlameskullsOnRightSide() {
		return countUnitsOnMap("loa2_l5_castle_of_the_chaos_wizard__spire__flameskull_spawned_right");
	}
	
	private int countUnitsOnMap(String unitId) {
		Array<CutsceneControlledUnit> allUnitsWithId = GameMapManager.getInstance().getMap().getAllUnitsWithId(unitId);
		return allUnitsWithId.size;
	}
	
	@Override
	public void process(float delta) {
		if (CutsceneHandler.getInstance().isCutsceneActive()) {
			return; // do not process during cutscenes
		}
		
		// chaos wizard push nova (before the laser blaster appears on the other side)
		if (timeTillChaosWizardPushNova > 0f) {
			timeTillChaosWizardPushNova -= delta;
			if (timeTillChaosWizardPushNova <= 0f) {
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.CONFIG_GENERATED_EVENT) //
						.setStringValue("loa2_l5_castle_of_the_chaos_wizard_spire__chaos_wizard__fire_push_nova_attack"));
			}
		}
		
		// laser blaster disappears on one side
		
		if (timeTillLaserBlasterDisappearesOnTheLeftSide > 0f) {
			timeTillLaserBlasterDisappearesOnTheLeftSide -= delta;
			if (timeTillLaserBlasterDisappearesOnTheLeftSide <= 0f) {
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.CONFIG_GENERATED_EVENT) //
						.setStringValue("loa2_l5_castle_of_the_chaos_wizard_spire__deactivate_vorpal_laser_blaster_of_pittenweem") //
						.setParameterObject("loa2_l5_castle_of_the_chaos_wizard_spire__laser_blaster_left"));
			}
		}
		
		if (timeTillLaserBlasterDisappearesOnTheRightSide > 0f) {
			timeTillLaserBlasterDisappearesOnTheRightSide -= delta;
			if (timeTillLaserBlasterDisappearesOnTheRightSide <= 0f) {
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.CONFIG_GENERATED_EVENT) //
						.setStringValue("loa2_l5_castle_of_the_chaos_wizard_spire__deactivate_vorpal_laser_blaster_of_pittenweem") //
						.setParameterObject("loa2_l5_castle_of_the_chaos_wizard_spire__laser_blaster_right"));
			}
		}
		
		// laser blaster appears on the other side
		
		if (timeTillLaserBlasterChangedToLeftSide > 0f) {
			timeTillLaserBlasterChangedToLeftSide -= delta;
			if (timeTillLaserBlasterChangedToLeftSide <= 0f) {
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.CONFIG_GENERATED_EVENT) //
						.setStringValue("loa2_l5_castle_of_the_chaos_wizard_spire__activate_vorpal_laser_blaster_of_pittenweem") //
						.setParameterObject("loa2_l5_castle_of_the_chaos_wizard_spire__laser_blaster_left"));
				
				// change the active magic fires to block the path of the player
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SENSOR) //
						.setIntValue(2));
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SOLID_OBJECT) //
						.setIntValue(1));
			}
		}
		
		if (timeTillLaserBlasterChangedToRightSide > 0f) {
			timeTillLaserBlasterChangedToRightSide -= delta;
			if (timeTillLaserBlasterChangedToRightSide <= 0f) {
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.CONFIG_GENERATED_EVENT) //
						.setStringValue("loa2_l5_castle_of_the_chaos_wizard_spire__activate_vorpal_laser_blaster_of_pittenweem") //
						.setParameterObject("loa2_l5_castle_of_the_chaos_wizard_spire__laser_blaster_right"));
				
				// change the active magic fires to block the path of the player
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SENSOR) //
						.setIntValue(1));
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SOLID_OBJECT) //
						.setIntValue(2));
			}
		}
	}
}
