package net.jfabricationgames.gdx.event.coded;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;

public class CastleOfTheChaosWizardBossFightStage3EventHandler extends CodedEventHandler {
	
	@Override
	public void handleEvent(EventConfig event) {
		// TODO delete after tests
		if (EventType.EVENT_OBJECT_TOUCHED.equals(event.eventType)) {
			if ("loa2_l5_castle_of_the_chaos_wizard__spire__test".equals(event.stringValue)) {
				// restore the magic obelisks
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.CONFIG_GENERATED_EVENT) //
						.setStringValue("restore_magic_obelisks"));
			}
		}
	}
}
