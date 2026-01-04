package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Contact;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.attack.AttackConfig;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class Explosion extends Projectile {
	
	private boolean processedContact = false;
	private boolean explosionDamageDealt = false;
	
	public Explosion(ProjectileTypeConfig typeConfig, AttackConfig attackConfig, AnimationDirector<TextureRegion> animation, ProjectileMap gameMap) {
		super(typeConfig, attackConfig, animation, gameMap);
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(2f).setSensor(true);
	}
	
	@Override
	public void update(float delta) {
		if (processedContact) {
			// explosion damage is dealt multiple times (multiple targets) but only in one update cycle
			explosionDamageDealt = true;
		}
		
		super.update(delta);
	}
	
	@Override
	protected void processContact(Object contactUserData, Contact contact) {
		if (explosionDamageDealt) {
			// prevent dealing damage multiple times in multiple update cycles (but allow multiple targets in one update cycle)
			return;
		}
		
		super.processContact(contactUserData, contact);
		processedContact = true;
	}
}
