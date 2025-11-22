package net.jfabricationgames.gdx.character.state;

public interface CharacterStateChangeListener {
	
	void onCharacterStateChange(CharacterState oldState, CharacterState newState);
}
