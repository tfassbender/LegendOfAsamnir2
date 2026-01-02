package net.jfabricationgames.gdx.event.coded;

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
		//				// turn off the magic energy line
		//				EventHandler.getInstance().fireEvent(new EventConfig() //
		//						.setEventType(EventType.DISPLAY_ANIMATION_GAME_OBJECT) //
		//						.setStringValue("loa2_l5_castle_of_the_chaos_wizard__spire__magic_energy_line_bottom_left") //
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
	}
}
