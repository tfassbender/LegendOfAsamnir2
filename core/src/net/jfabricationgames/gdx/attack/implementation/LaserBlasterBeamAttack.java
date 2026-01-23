package net.jfabricationgames.gdx.attack.implementation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

import net.jfabricationgames.gdx.attack.Attack;
import net.jfabricationgames.gdx.attack.AttackConfig;
import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.attack.Hittable;
import net.jfabricationgames.gdx.character.player.PlayableCharacter;
import net.jfabricationgames.gdx.cutscene.action.CutsceneControlledUnit;
import net.jfabricationgames.gdx.map.GameMapManager;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public class LaserBlasterBeamAttack extends Attack {
	
	private static final float LASER_BEAM_HEIGHT = 0.15f;
	private static final float LASER_MAX_RANGE = 25f;
	
	private Vector2 beamDirection;
	private Vector2 beamStart;
	private float beamLength;
	private float beamAngleRad;
	private float lastDeltaTime;
	private boolean leftSideLaser;
	private Vector2 laserBeginOffset; // there are two lasers on the map - each needs to have its own offset because of the direction they're facing
	
	public LaserBlasterBeamAttack(AttackConfig config, Body body, PhysicsCollisionType collisionType) {
		super(config, Vector2.Zero, body, collisionType);
	}
	
	@Override
	protected void start() {
		started = true;
		
		// determine if this is the left or right laser by checking on which side of the chaos wizard it is located
		CutsceneControlledUnit chaosWizard = GameMapManager.getInstance().getMap().getUnitById("loa2_l5_castle_of_the_chaos_wizard__chaos_wizard");
		if (chaosWizard != null) {
			leftSideLaser = body.getPosition().x < chaosWizard.getPosition().x;
			laserBeginOffset = leftSideLaser ? new Vector2(-0.4f, -0.1f) : new Vector2(0.4f, -0.1f);
		}
	}
	
	private void updateHitFixture() {
		// remove the old hit fixture
		if (hitFixture != null) {
			PhysicsWorld.getInstance().removeFixture(hitFixture, hitFixtureProperties.body);
			hitFixture = null;
			hitFixtureProperties = null;
		}
		
		// calculate the new beam position and length
		beamStart = body.getPosition().cpy().add(laserBeginOffset);
		
		Vector2 targetPos = targetPositionSupplier.get();
		beamDirection = targetPos.cpy().sub(beamStart).nor();
		beamAngleRad = beamDirection.angleRad();
		
		if (beamDirection.x < 0 && !leftSideLaser || beamDirection.x > 0 && leftSideLaser) {
			// the laser would fire to the wrong side - abort the attack
			aborted = true;
			return;
		}
		
		beamLength = performRaycast(beamStart, beamDirection);
		
		Vector2 center = beamStart.cpy().add(beamDirection.cpy().scl(beamLength * 0.5f));
		
		hitFixtureProperties = new PhysicsBodyCreator.PhysicsBodyProperties() //
				.setBody(body) //
				.setCollisionType(collisionType) //
				.setSensor(true) //
				.setPhysicsBodyShape(PhysicsBodyCreator.PhysicsBodyShape.RECTANGLE) //
				.setWidth(beamLength) //
				.setHeight(LASER_BEAM_HEIGHT) //
				.setFixturePosition(center.sub(body.getPosition())) //
				.setAngle(beamAngleRad);
		
		hitFixture = PhysicsBodyCreator.addFixture(hitFixtureProperties);
	}
	
	private float performRaycast(Vector2 start, Vector2 dir) {
		Vector2 end = start.cpy().add(dir.cpy().scl(LASER_MAX_RANGE));
		final float[] hitDistance = {LASER_MAX_RANGE};
		
		PhysicsWorld.getInstance().rayCast(new RayCastCallback() {
			
			@Override
			public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
				// Ignore sensors completely
				if (fixture.isSensor()) {
					return 1f;
				}
				
				// Identify collision type via filter
				PhysicsCollisionType type = PhysicsCollisionType.getByFilter(fixture.getFilterData());
				
				// Ignore unreachable areas and let the ray continue
				if (type == PhysicsCollisionType.MAP_UNREACHABLE_AREA || type == PhysicsCollisionType.OBSTACLE_ENEMY) {
					return 1f;
				}
				
				// Player hit -> extend slightly for visual clarity
				if (fixture.getUserData() instanceof PlayableCharacter) {
					hitDistance[0] = LASER_MAX_RANGE * fraction + 0.2f;
					return fraction;
				}
				
				// Any other solid object stops the ray
				hitDistance[0] = LASER_MAX_RANGE * fraction;
				return fraction;
			}
			
		}, start, end);
		
		return hitDistance[0];
	}
	
	@Override
	protected void render(float delta, SpriteBatch batch) {
		if (aborted || timer < config.delay) {
			return;
		}
		
		lastDeltaTime = delta; // needed to calculate the damage if the player is hit
		updateHitFixture();
	}
	
	@Override
	protected void render(ShapeRenderer shapeRenderer) {
		if (aborted || hitFixtureProperties == null || hitFixtureProperties.body == null || timer < config.delay) {
			return;
		}
		
		// World-space center of the fixture
		Vector2 bodyPos = hitFixtureProperties.body.getPosition();
		Vector2 localCenter = hitFixtureProperties.fixturePosition;
		Vector2 center = bodyPos.cpy().add(localCenter);
		
		float length = hitFixtureProperties.width;
		float thickness = hitFixtureProperties.height;
		float angleDeg = hitFixtureProperties.angle * MathUtils.radiansToDegrees;
		
		// -------------------------
		// Pulsating energy
		// -------------------------
		float pulseSpeed = 10f; // higher = faster pulse
		float pulse = 0.5f + 0.5f * MathUtils.sin(timer * pulseSpeed);
		
		// Edge darkens and brightens
		float edgeBrightness = 0.7f + 0.3f * pulse; // 0.7 -> 1.0
		float halfThickness = thickness * 0.5f;
		float x = center.x - length * 0.5f;
		
		// -------------------------
		// Colors
		// -------------------------
		// White-hot edges (pulse-controlled)
		Color edge = new Color(1f, edgeBrightness, 1f, 0.1f);
		// Purple core
		Color core = new Color(0.6f, 0f, 0.9f + 0.1f * pulse, 1f);
		
		// -------------------------
		// Upper half (edge -> core)
		// -------------------------
		float upperY = center.y;
		
		shapeRenderer.rect(x, upperY, // bottom-left
				length * 0.5f, 0f, // origin (center horizontally)
				length, halfThickness, // size
				1f, 1f, angleDeg, core, // bottom-left (center)
				core, // top-left
				edge, // top-right (edge)
				edge // bottom-right
		);
		
		// -------------------------
		// Lower half (edge -> core, mirrored)
		// -------------------------
		float lowerY = center.y - halfThickness;
		
		shapeRenderer.rect(x, lowerY, // bottom-left
				length * 0.5f, halfThickness, // origin
				length, halfThickness, 1f, 1f, angleDeg, edge, // bottom-left (edge)
				edge, // top-left
				core, // top-right (center)
				core // bottom-right
		);
	}
	
	@Override
	protected void remove() {
		if (hitFixture != null) {
			PhysicsWorld.getInstance().removeFixture(hitFixture, hitFixtureProperties.body);
			hitFixture = null;
			hitFixtureProperties = null;
		}
	}
	
	@Override
	protected void dealAttackDamage(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		if (fixtureA.isSensor() && fixtureB.isSensor()) {
			return;
		}
		
		if (CollisionUtil.containsCollisionType(collisionType, fixtureA, fixtureB)) {
			Object attackUserData = CollisionUtil.getCollisionTypeUserData(collisionType, fixtureA, fixtureB);
			Object attackedObjectUserData = CollisionUtil.getOtherTypeUserData(collisionType, fixtureA, fixtureB);
			
			if (hitFixtureProperties != null && hitFixtureProperties.body != null //
					&& attackUserData == hitFixtureProperties.body.getUserData() //
					&& attackedObjectUserData instanceof Hittable) {
				
				Hittable target = (Hittable) attackedObjectUserData;
				// the laser doesn't push the target, but only deals damage over time
				target.takeDamage(config.damage * lastDeltaTime, new AttackInfo(AttackType.LASER_BLASTER_BEAM));
			}
		}
	}
}