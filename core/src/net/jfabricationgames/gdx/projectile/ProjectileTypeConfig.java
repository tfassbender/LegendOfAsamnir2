package net.jfabricationgames.gdx.projectile;

import net.jfabricationgames.gdx.attack.AttackType;

public class ProjectileTypeConfig {
	
	public AttackType attackType;
	
	public String texture;
	public String animation;
	public boolean textureAnimation = true;
	public boolean removeAfterAnimationFinished = true;
	public float textureScale = 1f;
	public boolean textureScaleGrowing;
	public float textureInitialRotation;
	public boolean rotateTextureToMovementDirection = false;
	public boolean changeBodyToSensorAfterHit = false;
	
	public String sound;
	
	public float range = 0f;
	public boolean removeAfterRangeExceeded = true;
	public float speed = 30f;
	public float damping = 0f;
	public float dampingAfterObjectHit = 5f;
	public float dampingAfterRangeExceeded = 0f;
	
	public float timeTillExplosion = -1f;
	public boolean multipleHitsPossible = false;
	public boolean freezeTarget = false;
	
	public float timeActive = -1f;
	
	public boolean flipAnimationToAttackDirection = true;
	public float hitFixtureRadius = 1f;
	public float distanceFromCenter = 0f;
	
	public float pushForce = 8f; // enough to push the player away if he's running
	public float radius = 3f;
	public boolean affectedByForceField = true;
	
	public boolean sensorBody = false; // if true the body is a sensor (used for delayed hits)
	public float hitDelayInSeconds = 0; // change the body to a sensor when using delayed hits
	
	public float imageOffsetX = 0;
	public float imageOffsetY = 0;
}
