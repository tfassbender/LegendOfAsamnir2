package net.jfabricationgames.gdx.character.ai;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimerConfig;
import net.jfabricationgames.gdx.character.state.CharacterStateMachine;

public class ArtificialIntelligenceConfig {
	
	public ArtificialIntelligenceType type;
	public ArtificialIntelligenceConfig subAI;
	
	public String stateNameAction = "action";
	public String stateNameAction2;
	public String stateNameMove = "move";
	public String stateNameIdle = "idle";
	public String stateNameAttack = "attack";
	public String stateNameWait = "waiting";
	public String stateNameSurprise = "surprise";
	public String stateNameBlock = "block";
	
	public String followCondition;
	
	public float minDistToEnemy;
	public float maxDistToEnemy;
	public float maxMoveDistance;
	public float timeBetweenActions;
	
	public boolean useRelativePositions = true;
	public boolean changeTargetWhenStaticBodyHit = true;
	public boolean moveWhileAttacking;
	
	public float attackDistance;
	public float attackSpeedFactor;
	public float attackSpeedDelay;
	public float minDistanceToTargetPlayer = 1f;
	public float distanceToKeepFromPlayer = 0f;
	public float distanceToStopRunning = 2f;
	public float distanceToInformTeamMates = 7f;
	public float movementSpeedFactor = 1f;
	public float movementProbability;
	
	// target angles for the AngleRestrictedFightAI
	public Array<Float> angles;
	public float maxAngleDelta;
	
	public AttackTimerConfig attackTimerConfig;
	
	public ObjectMap<String, ArtificialIntelligenceStateConfig> idleStates;
	
	public ArtificialIntelligence buildAI(CharacterStateMachine stateMachine, MapProperties mapProperties) {
		return type.buildAI(this, stateMachine, mapProperties);
	}
}
