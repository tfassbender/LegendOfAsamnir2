package net.jfabricationgames.gdx.event.coded;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;

public class CastleOfTheChaosWizardBossFightStage3EventHandler extends CodedEventHandler {
	
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
			}
			else if ("loa2_l5_castle_of_the_chaos_wizard__spire__magic_obelisk_top_right".equals(event.stringValue)) {
				animationObjectIdToTurnOff = "loa2_l5_castle_of_the_chaos_wizard__spire__magic_energy_line_top_right";
			}
			else if ("loa2_l5_castle_of_the_chaos_wizard__spire__magic_obelisk_bottom_left".equals(event.stringValue)) {
				animationObjectIdToTurnOff = "loa2_l5_castle_of_the_chaos_wizard__spire__magic_energy_line_bottom_left";
			}
			else if ("loa2_l5_castle_of_the_chaos_wizard__spire__magic_obelisk_bottom_right".equals(event.stringValue)) {
				animationObjectIdToTurnOff = "loa2_l5_castle_of_the_chaos_wizard__spire__magic_energy_line_bottom_right";
			}
			
			if (animationObjectIdToTurnOff != null) {
				// turn off the magic energy line
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.DISPLAY_ANIMATION_GAME_OBJECT) //
						.setStringValue(animationObjectIdToTurnOff) //
						.setBooleanValue(false));
			}
		}
	}
}
