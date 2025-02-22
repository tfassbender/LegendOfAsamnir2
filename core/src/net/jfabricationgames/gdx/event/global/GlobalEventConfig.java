package net.jfabricationgames.gdx.event.global;

import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.event.EventConfig;

public class GlobalEventConfig {
	
	public EventConfig event;
	public boolean exactMatch = true; // if configured to false, all event properties that are default values (null or 0) are ignored 
	public GlobalEventExecutionType executionType;
	public String conditionalExecutionId;
	
	public ObjectMap<String, String> executionParameters;
	public Object parameterObject;
}
