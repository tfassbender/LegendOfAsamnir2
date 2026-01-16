package net.jfabricationgames.gdx.event.coded;

import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.condition.Condition;
import net.jfabricationgames.gdx.condition.ConditionType;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.map.GameMapManager;
import net.jfabricationgames.gdx.map.GameMapProcessable;

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
	public void process(float delta) {
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
	
	@Override
	public void handleEvent(EventConfig event) {
		// TODO delete after tests
		//		if (EventType.EVENT_OBJECT_TOUCHED.equals(event.eventType)) {
		//			if ("loa2_l5_castle_of_the_chaos_wizard__spire__test".equals(event.stringValue)) {
		//				EventHandler.getInstance().fireEvent(new EventConfig() //
		//						.setEventType(EventType.CONFIG_GENERATED_EVENT) //
		//						.setStringValue("loa2_l5_castle_of_the_chaos_wizard__bomb_repelling_dummy__active") //
		//						.setBooleanValue(true));
		//			}
		//		}
		
		if (EventType.DESTROYABLE_OBJECT_DESTROYED.equals(event.eventType)) {
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
			}
			else if ("loa2_l5_castle_of_the_chaos_wizard__spire__setup".equals(event.stringValue)) {
				GameMapManager.getInstance().getMap().registerGameMapProcessable(this); // unregistered when player dies
			}
			else if ("loa2_l5_castle_of_the_chaos_wizard__spire__change_battle_stage".equals(event.stringValue)) {
				battleStage = event.intValue;
			}
		}
		else if (EventType.PLAYER_DIED.equals(event.eventType)) {
			GameMapManager.getInstance().getMap().unregisterGameMapProcessable(this); // unregister to prevent being added twice
		}
		else if (EventType.EVENT_OBJECT_TOUCHED.equals(event.eventType)) {
			boolean playerChangedToLeftSide = "loa2_l5_castle_of_the_chaos_wizard__spire__left_side".equals(event.stringValue);
			boolean playerChangedToRightSide = "loa2_l5_castle_of_the_chaos_wizard__spire__right_side".equals(event.stringValue);
			if (battleStage >= 5 && (playerChangedToLeftSide || playerChangedToRightSide)) {
				// from battle stage 5 on, the nearest lich minion fires a targeting projectile when a player changes the side
				String eventString = "loa2_l5_castle_of_the_chaos_wizard_spire__lich_minion_fire_targeting_projectile__";
				if (isPlayerInBottomArea()) {
					eventString += "bottom";
				}
				else if (isPlayerInTopArea()) {
					eventString += "top";
				}
				else {
					// the player should not be able to move through the center in this stage - just in case, do nothing
					return;
				}
				
				// fire the event to inform the lich minion
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.CONFIG_GENERATED_EVENT) //
						.setStringValue(eventString));
				
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
	
	//************************************************
	//*** TODO test methods - to be deleted later
	//************************************************
	
	//	
	//	private int countFlameskullsOnLeftSide() {
	//		return countUnitsOnMap("loa2_l5_castle_of_the_chaos_wizard__spire__flameskull_spawned_left");
	//	}
	//	
	//	private int countFlameskullsOnRightSide() {
	//		return countUnitsOnMap("loa2_l5_castle_of_the_chaos_wizard__spire__flameskull_spawned_right");
	//	}
	//	
	//	private int countUnitsOnMap(String unitId) {
	//		Array<CutsceneControlledUnit> allUnitsWithId = GameMapManager.getInstance().getMap().getAllUnitsWithId(unitId);
	//		return allUnitsWithId.size;
	//	}
}
