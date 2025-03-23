package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.attack.AttackConfig;
import net.jfabricationgames.gdx.map.ground.GameMapGroundType;
import net.jfabricationgames.gdx.map.ground.GameMapGroundTypeContainer;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsUtil;

public class Web extends Projectile implements GameMapGroundTypeContainer {
	
	private static final String GROUND_TYPE_WEB = "web";
	
	private boolean touchingPlayer;
	
	public Web(ProjectileTypeConfig typeConfig, AttackConfig attackConfig, AnimationDirector<TextureRegion> animation, ProjectileMap gameMap) {
		super(typeConfig, attackConfig, animation, gameMap);
		setImageOffset(0f, 0.5f);
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(0.1f);
	}
	
	@Override
	protected void addAdditionalPhysicsParts() {
		PhysicsUtil.addCircularGroundFixture(body, 1f, GROUND_TYPE_WEB);
	}
	
	@Override
	protected void stopProjectileAfterObjectHit() {
		super.stopProjectileAfterObjectHit();
		changeBodyToSensor();
	}
	
	@Override
	protected void changeBodyToSensor() {
		if (hasBody()) {
			for (Fixture fixture : body.getFixtureList()) {
				if (!isMapGroundFixture(fixture)) {
					fixture.setSensor(true);
				}
			}
		}
	}
	
	private boolean isMapGroundFixture(Fixture fixture) {
		return fixture.getFilterData().categoryBits == PhysicsCollisionType.MAP_GROUND.category;
	}
	
	@Override
	public void removeFromMap() {
		if (!touchingPlayer) {
			super.removeFromMap();
		}
	}
	
	@Override
	public GameMapGroundType getGameMapGroundType() {
		return gameMap.getGroundTypeByName(GROUND_TYPE_WEB);
	}
}
