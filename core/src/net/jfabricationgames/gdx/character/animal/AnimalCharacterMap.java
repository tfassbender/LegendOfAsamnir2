package net.jfabricationgames.gdx.character.animal;

import com.badlogic.gdx.physics.box2d.Body;

public interface AnimalCharacterMap {
	
	public void addAnimal(Animal animal);
	public void removeAnimal(Animal animal, Body body);
}
