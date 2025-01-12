package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.attack.Hittable;
import net.jfabricationgames.gdx.map.PositionedObject;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;

public abstract class Projectile implements ContactListener, Hittable, PositionedObject {
	
	private static final String SOUND_SET_PROJECTILE = "projectile";
	private static final String EXPLOSION_PROJECTILE_TYPE = "explosion";
	
	protected Body body;
	protected ProjectileTypeConfig typeConfig;
	protected PhysicsCollisionType collisionType;
	
	protected ExplosionFactory explosionFactory;
	protected ProjectileMap gameMap;
	
	protected Body playerBody;
	
	protected float distanceTraveled;
	protected float timeActive;
	protected boolean attackPerformed;
	protected boolean reflected;
	
	protected float damage;
	protected float pushForce;
	protected float pushForceWhenBlocked;
	protected boolean pushForceAffectedByBlock = true;
	
	protected float explosionDamage;
	protected float explosionPushForce;
	protected boolean explosionPushForceAffectedByBlock;
	
	protected float imageOffsetX;
	protected float imageOffsetY;
	
	protected float animationRotation;
	
	protected AnimationDirector<TextureRegion> animation;
	protected Sprite sprite;
	
	protected String unitId;
	
	public Projectile(ProjectileTypeConfig typeConfig, Sprite sprite, ProjectileMap gameMap) {
		this.typeConfig = typeConfig;
		this.sprite = sprite;
		this.gameMap = gameMap;
		if (!typeConfig.textureScaleGrowing) {
			scaleSprite();
		}
		
		initialize();
	}
	
	public Projectile(ProjectileTypeConfig typeConfig, AnimationDirector<TextureRegion> animation, ProjectileMap gameMap) {
		this.typeConfig = typeConfig;
		this.animation = animation;
		this.gameMap = gameMap;
		
		sprite = new Sprite(animation.getKeyFrame());
		if (!typeConfig.textureScaleGrowing) {
			scaleSprite();
		}
		
		initialize();
	}
	
	private void scaleSprite() {
		sprite.setScale(sprite.getScaleX() * typeConfig.textureScale, sprite.getScaleY() * typeConfig.textureScale);
	}
	
	private void initialize() {
		distanceTraveled = 0;
		if (typeConfig.damping > 0 && isRangeRestricted()) {
			throw new IllegalStateException("A Projectile can not be restricted to be removed after a given range AND use linear damping");
		}
		attackPerformed = false;
		reflected = false;
		
		if (typeConfig.sound != null) {
			SoundSet soundSet = SoundManager.getInstance().loadSoundSet(SOUND_SET_PROJECTILE);
			soundSet.playSound(typeConfig.sound);
		}
		
		registerAsContactListener();
	}
	
	private void registerAsContactListener() {
		PhysicsWorld physicsWorld = PhysicsWorld.getInstance();
		physicsWorld.registerContactListener(this);
	}
	
	protected void setExplosionFactory(ExplosionFactory explosionFactory) {
		this.explosionFactory = explosionFactory;
	}
	
	protected void createPhysicsBody(Vector2 position, Vector2 direction, PhysicsCollisionType collisionType) {
		this.collisionType = collisionType;
		PhysicsBodyProperties bodyProperties = createShapePhysicsBodyProperties() //
				.setType(BodyType.DynamicBody) //
				.setX(position.x + getInitialOffsetX(direction)) //
				.setY(position.y) //
				.setCollisionType(collisionType) //
				.setLinearDamping(typeConfig.damping);
		body = PhysicsBodyCreator.createBody(bodyProperties);
		body.setUserData(this);
		addAdditionalPhysicsParts();
	}
	
	protected float getInitialOffsetX(Vector2 direction) {
		return 0f;
	}
	
	protected void addAdditionalPhysicsParts() {}
	
	protected abstract PhysicsBodyProperties createShapePhysicsBodyProperties();
	
	protected void startProjectile(Vector2 direction) {
		Vector2 movement = direction.nor().scl(typeConfig.speed);
		
		if (typeConfig.rotateTextureToMovementDirection) {
			rotateProjectile(direction);
		}
		
		//apply the movement force only once because the linear damping is set to zero
		body.applyForceToCenter(movement.scl(body.getMass() * 10), true);
	}
	
	protected void rotateProjectile(Vector2 direction) {
		float angle = direction.angleDeg();
		float rotation = angle - typeConfig.textureInitialRotation + getSpriteVectorAngleOffset();
		body.setTransform(body.getPosition().x, body.getPosition().y, MathUtils.degreesToRadians * angle);
		sprite.setRotation(rotation);
		animationRotation = rotation;
	}
	
	public float getRotation() {
		return (sprite.getRotation() + typeConfig.textureInitialRotation) % 360f;
	}
	
	protected float getSpriteVectorAngleOffset() {
		return 0;
	}
	
	public void setDamage(float damage) {
		this.damage = damage;
	}
	
	public void setPushForce(float pushForce) {
		this.pushForce = pushForce;
	}
	
	public void setPushForceWhenBlocked(float pushForceWhenBlocked) {
		this.pushForceWhenBlocked = pushForceWhenBlocked;
	}
	
	public void setPushForceAffectedByBlock(boolean pushForceAffectedByBlock) {
		this.pushForceAffectedByBlock = pushForceAffectedByBlock;
	}
	
	public void update(float delta) {
		distanceTraveled += delta * typeConfig.speed;
		timeActive += delta;
		
		if (animation != null) {
			animation.increaseStateTime(delta);
			sprite = new Sprite(animation.getKeyFrame());
			sprite.setRotation(animationRotation);
			// scaling the sprite is needed if the animation is a growing animation instead of a multiple-texture animation
			animation.scaleSprite(sprite);
			
			if (animation.isAnimationFinished() && typeConfig.removeAfterAnimationFinished) {
				removeFromMap();
			}
		}
		
		if (reachedMaxRange()) {
			if (isRangeRestricted()) {
				removeFromMap();
			}
			else if (isDampedAfterRangeExceeds()) {
				stopProjectileAfterRangeExceeds();
			}
		}
		if (activeTimeOver()) {
			removeFromMap();
		}
		if (explosionTimeReached()) {
			explode();
		}
		if (reflected) {
			removeFromMap();
		}
	}
	
	private boolean reachedMaxRange() {
		return distanceTraveled > typeConfig.range;
	}
	
	private boolean isRangeRestricted() {
		return typeConfig.range > 0 && typeConfig.removeAfterRangeExceeded;
	}
	
	private boolean isDampedAfterRangeExceeds() {
		return typeConfig.range > 0 && typeConfig.dampingAfterRangeExceeded > 0;
	}
	
	private boolean activeTimeOver() {
		return timeActive > typeConfig.timeActive && isTimeRestricted();
	}
	
	private boolean isTimeRestricted() {
		return typeConfig.timeActive >= 0;
	}
	
	private boolean explosionTimeReached() {
		return timeActive > typeConfig.timeTillExplosion && isExplosive();
	}
	
	private boolean isExplosive() {
		return typeConfig.timeTillExplosion > 0;
	}
	
	protected void stopProjectileAfterObjectHit() {
		if (hasBody()) {
			startBodyLinearDamping();
			attackPerformed = true;
		}
	}
	
	protected void stopProjectileAfterRangeExceeds() {
		if (hasBody()) {
			body.setLinearDamping(typeConfig.dampingAfterRangeExceeded);
			attackPerformed = true;
		}
	}
	
	protected void changeBodyToSensor() {
		if (hasBody()) {
			for (Fixture fixture : body.getFixtureList()) {
				fixture.setSensor(true);
			}
		}
	}
	
	protected boolean hasBody() {
		return body != null;
	}
	
	private void explode() {
		Projectile explosion = explosionFactory.createExplosion(EXPLOSION_PROJECTILE_TYPE, body.getPosition(), Vector2.Zero, collisionType);
		explosion.damage = explosionDamage;
		explosion.pushForce = explosionPushForce;
		explosion.pushForceAffectedByBlock = explosionPushForceAffectedByBlock;
		removeFromMap();
	}
	
	public void draw(float delta, SpriteBatch batch) {
		if (typeConfig.textureScaleGrowing) {
			scaleSprite();
		}
		sprite.setPosition(body.getPosition().x - sprite.getOriginX() + imageOffsetX, body.getPosition().y - sprite.getOriginY() + imageOffsetY);
		sprite.draw(batch);
	}
	
	protected void setImageOffset(float x, float y) {
		this.imageOffsetX = x;
		this.imageOffsetY = y;
	}
	
	@Override
	public void beginContact(Contact contact) {
		if (isAttackOver()) {
			return;
		}
		
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		Object attackUserData = CollisionUtil.getCollisionTypeUserData(collisionType, fixtureA, fixtureB);
		Fixture attackedFixture = CollisionUtil.getOtherTypeFixture(collisionType, fixtureA, fixtureB);
		if (attackUserData == this && !attackedFixture.isSensor()) {
			Object attackedUserData = CollisionUtil.getOtherTypeUserData(collisionType, fixtureA, fixtureB);
			
			processContact(attackedUserData);
		}
	}
	
	public boolean isAttackOver() {
		return reflected || (attackPerformed && !typeConfig.multipleHitsPossible);
	}
	
	protected void processContact(Object contactUserData) {
		if (contactUserData instanceof ProjectileReflector) {
			ProjectileReflector reflector = (ProjectileReflector) contactUserData;
			reflected = reflector.reflectProjectile(this);
		}
		if (!reflected && contactUserData instanceof Hittable) {
			Hittable hittable = (Hittable) contactUserData;
			//enemies define the force themselves; the force parameter is a factor for this self defined force
			hittable.pushByHit(body.getPosition().cpy(), pushForce, pushForceWhenBlocked, pushForceAffectedByBlock);
			hittable.takeDamage(damage, typeConfig.attackType);
			
			if (typeConfig.freezeTarget) {
				hittable.freeze();
			}
		}
		
		startBodyLinearDamping();
		if (typeConfig.changeBodyToSensorAfterHit) {
			changeBodyToSensor();
		}
		attackPerformed = true;
	}
	
	protected void startBodyLinearDamping() {
		body.setLinearDamping(typeConfig.dampingAfterObjectHit);
	}
	
	@Override
	public void endContact(Contact contact) {}
	
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}
	
	@Override
	public void takeDamage(float damage, AttackType melee) {
		// do nothing here - only needed for the Hittable interface
	}
	
	@Override
	public void pushByHit(Vector2 hitCenter, float force, float forceWhenBlocked, boolean blockAffected) {
		if (hasBody()) {
			Vector2 pushDirection = getPushDirection(body.getPosition(), hitCenter);
			force *= 10f * body.getMass();
			body.applyForceToCenter(pushDirection.x * force, pushDirection.y * force, true);
		}
	}
	
	public void removeFromMap() {
		attackPerformed = true;
		gameMap.removeProjectile(this, body);
		PhysicsWorld.getInstance().removeContactListener(this);
		body = null;// set the body to null to avoid strange errors in native Box2D methods
	}
	
	public boolean isRemoved() {
		return body == null;
	}
	
	public void setExplosionDamage(float explosionDamage) {
		this.explosionDamage = explosionDamage;
	}
	
	public void setExplosionPushForce(float explosionPushForce) {
		this.explosionPushForce = explosionPushForce;
	}
	
	public void setExplosionPushForceAffectedByBlock(boolean explosionPushForceAffectedByBlock) {
		this.explosionPushForceAffectedByBlock = explosionPushForceAffectedByBlock;
	}
	
	public void setPlayerBody(Body body) {
		this.playerBody = body;
	}
	
	@Override
	public Vector2 getPosition() {
		if (body != null) {
			return body.getPosition();
		}
		
		return null;
	}
	
	public String getUnitId() {
		return unitId;
	}
	
	@FunctionalInterface
	protected interface ExplosionFactory {
		
		public Projectile createExplosion(String type, Vector2 position, Vector2 direction, PhysicsCollisionType collisionType);
	}
}
