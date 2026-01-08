package net.jfabricationgames.gdx.event.coded;

import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.condition.Condition;
import net.jfabricationgames.gdx.condition.ConditionType;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;

public class CastleOfTheChaosWizardBossFightStage3EventHandler extends CodedEventHandler {
	
	private boolean obeliskDestroyedTopLeft = false;
	private boolean obeliskDestroyedTopRight = false;
	private boolean obeliskDestroyedBottomLeft = false;
	private boolean obeliskDestroyedBottomRight = false;
	
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
		}
	}
	
	private boolean isPlayerOnLeftSide() {
		return isPlayerInArea("config_object__chaos_wizard_spire__area_left");
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
