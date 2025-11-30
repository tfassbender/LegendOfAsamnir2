package net.jfabricationgames.gdx.event.coded;

import java.util.ArrayList;
import java.util.List;

import net.jfabricationgames.gdx.event.EventListener;

/**
 * A base class for event handlers that are not configured in files but are hard-coded, because the events are too specific and complex.
 */
public abstract class CodedEventHandler implements EventListener {
	
	protected CodedEventHandler() {}
	
	public static List<EventListener> getCodedEventListeners() {
		List<EventListener> codedEventListeners = new ArrayList<EventListener>();
		
		codedEventListeners.add(new CastleOfTheChaosWizardBossFightEventHandler());
		
		return codedEventListeners;
	}
}
