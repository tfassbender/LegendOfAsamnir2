package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.attack.AttackConfig;

public class FireWall extends ForceField {
	
	public FireWall(ProjectileTypeConfig typeConfig, AttackConfig attackConfig, AnimationDirector<TextureRegion> animation, ProjectileMap gameMap) {
		super(typeConfig, attackConfig, animation, gameMap);
	}
	
	public void draw(float delta, SpriteBatch batch) {
		sprite.setScale(0.05f); // the animation needs to be scaled down - don't know why this is not done automatically here
		
		// arrange the sprite on multiple positions in a circle to create a wall effect
		for (int i = 0; i < 32; i++) {
			float angle = (float) (i * (2 * Math.PI / 32));
			float imageOffsetX = (float) Math.cos(angle) * typeConfig.radius * 0.9f;
			float imageOffsetY = (float) Math.sin(angle) * typeConfig.radius * 0.9f + 0.5f; // y-offset to better center the fire wall
			
			sprite.setPosition(body.getPosition().x - sprite.getOriginX() + imageOffsetX + typeConfig.imageOffsetX, //
					body.getPosition().y - sprite.getOriginY() + imageOffsetY + typeConfig.imageOffsetY);
			sprite.draw(batch);
		}
	}
}
