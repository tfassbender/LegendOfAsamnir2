package net.jfabricationgames.gdx.event;

/**
 * An {@link EventListener} instance that can be used to listen to one specific event.
 * The handleEvent method needs to be overwritten to react to the event.
 * After instantiation the listener will register itself to the {@link EventHandler}.
 */
public abstract class EventListenerInstance implements EventListener {
	
	public EventListenerInstance() {
		EventHandler.getInstance().registerEventListener(this);
	}
}
