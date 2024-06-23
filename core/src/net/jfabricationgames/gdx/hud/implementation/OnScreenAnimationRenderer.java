package net.jfabricationgames.gdx.hud.implementation;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.animation.GrowingAnimationDirector;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;

public class OnScreenAnimationRenderer implements Disposable, EventListener {
	
	private static final String ANIMATION_CONFIG_FILE_RUNE = "config/animation/rune.json";
	private static final String ANIMATION_CONFIG_FILE_TITLE_BANNER = "config/animation/title_banner.json";
	private static final String ANIMATION_CONFIG_NAME_TITLE_BANNER = "legend_of_asamnir_title_banner";
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	private AnimationManager animationManager;
	private GrowingAnimationDirector<TextureRegion> animation;
	
	private float animationPositionOffsetFactorY;
	
	private Vector2 screenCenter;
	
	public OnScreenAnimationRenderer(OrthographicCamera camera, float sceneWidth, float sceneHeight) {
		this.camera = camera;
		batch = new SpriteBatch();
		
		screenCenter = new Vector2(sceneWidth * 0.5f, sceneHeight * 0.5f);
		
		animationManager = AnimationManager.getInstance();
		animationManager.loadAnimations(ANIMATION_CONFIG_FILE_RUNE);
		animationManager.loadAnimations(ANIMATION_CONFIG_FILE_TITLE_BANNER);
		
		EventHandler.getInstance().registerEventListener(this);
	}
	
	public void render(float delta) {
		if (animation != null) {
			animation.increaseStateTime(delta);
			
			batch.setProjectionMatrix(camera.combined);
			drawRunes();
			
			if (animation.isAnimationFinished()) {
				animation = null;
			}
		}
	}
	
	private void drawRunes() {
		batch.begin();
		batch.draw(animation.getKeyFrame(), //
				screenCenter.x - animation.getWidth() * 0.5f, //
				screenCenter.y - animation.getHeight() * 0.5f + animation.getHeight() * animationPositionOffsetFactorY, //
				animation.getWidth(), //
				animation.getHeight());
		batch.end();
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.RUNE_USED) {
			animation = animationManager.getGrowingAnimationDirector(event.stringValue);
			animationPositionOffsetFactorY = 0f;
		}
		else if (event.eventType == EventType.CUTSCENE_SHOW_TITLE_BANNER) {
			animation = animationManager.getGrowingAnimationDirector(ANIMATION_CONFIG_NAME_TITLE_BANNER);
			animationPositionOffsetFactorY = 0.5f; // makes the title banner appear a bit higher
		}
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		EventHandler.getInstance().removeEventListener(this);
	}
}
