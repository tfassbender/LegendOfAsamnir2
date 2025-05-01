package net.jfabricationgames.gdx.character.state;

import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.sound.SoundPlayConfig;

public class CharacterStateConfig {
	
	public String id;
	public String animation;
	public String attack;
	public String attackAlternative; // if configured in the map properties, this attack will be used instead of the normal attack
	
	public String stateEnteringSound;
	public float soundDelay = SoundPlayConfig.DEFAULT_SETTING; // will overwrite the delay of the sound config (does not add up)
	public float soundVolume = SoundPlayConfig.DEFAULT_SETTING;
	public boolean abortSoundWhenStateInterrupted = false;
	
	public boolean endsWithAnimation = true;
	public boolean endsAfterAttackFinishes = false;
	public float changeStateAfterAnimationDelay = 0f;
	public String followingState;
	
	public Array<String> interruptingStates;
	
	public boolean flipAnimationToMovingDirection = true;
	public boolean flipAnimationOnEnteringOnly = false;
	public boolean initialAnimationDirectionRight = true;
}
