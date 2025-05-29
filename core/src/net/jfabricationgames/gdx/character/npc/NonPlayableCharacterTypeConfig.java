package net.jfabricationgames.gdx.character.npc;

import net.jfabricationgames.gdx.character.CharacterTypeConfig;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligenceConfig;

public class NonPlayableCharacterTypeConfig extends CharacterTypeConfig {
	
	public ArtificialIntelligenceConfig aiConfig;
	
	// the graphics configuration can either be done in the main config file, or in a separated file that is referenced by the graphicsConfigFile field.
	// the graphics configuration is loaded automatically in both cases.
	public String graphicsConfigFile;
	public NonPlayableCharacterGraphicsConfig graphicsConfig;
	
	public boolean interactByContact = false;
	public String interactionEventId;
	
	public boolean isSensor = false; // set to true if the NPC should not get into contact by any other objects 
	public boolean addSensor = true;
	public float sensorRadius = 1f;
	
	public boolean interactionPossible = true;
	public float movingSpeed = 1f;
}
