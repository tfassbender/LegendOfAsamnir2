package net.jfabricationgames.gdx.hud.implementation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;
import net.jfabricationgames.gdx.text.ScreenTextWriter;

public class BossStatusBar implements Disposable, EventListener {
	
	private static final SoundSet SOUND_SET = SoundManager.getInstance().loadSoundSet("enemy");
	
	private static final float HEALTH_BAR_FULL_REDUCTION_TIME = 3f; // the time it takes for the health bar to go from full to empty (if the boss is defeated in one hit)
	
	private final Vector2 size = new Vector2(-1180, -120); //negative values because it's drawn from the top right
	private final Vector2 healthBarUpperRightOffset = new Vector2(-10f, -60f);
	private final Vector2 healthBarSize = new Vector2(size.x - healthBarUpperRightOffset.x * 2, (size.y - (healthBarUpperRightOffset.y * 1.2f)));
	
	private final Color[] backgroundTileColors = new Color[] {//
			new Color(0.3f, 0.3f, 0.3f, 1f), //top-right
			new Color(0.35f, 0.35f, 0.35f, 1f), //top-left
			new Color(0.2f, 0.2f, 0.2f, 1f), //bottom-left
			new Color(0.05f, 0.05f, 0.05f, 1f) //bottom-right
	};
	
	private final Color[] barBackgroundColors = new Color[] {//
			Color.LIGHT_GRAY, //top-right
			Color.LIGHT_GRAY, //top-left
			Color.DARK_GRAY, //bottom-left
			Color.DARK_GRAY //bottom-right
	};
	
	private final Color[] healthBarColorsHigh = new Color[] {//
			new Color(0f, 0.85f, 0f, 1f), //top-right
			Color.GREEN, //top-left
			Color.DARK_GRAY, //bottom-left
			new Color(0.05f, 0.05f, 0.05f, 1f) //bottom-right
	};
	
	private final Color[] healthBarColorsMid = new Color[] {//
			new Color(0.9f, 0.9f, 0.1f, 1f), //top-right
			Color.YELLOW, //top-left
			Color.DARK_GRAY, //bottom-left
			new Color(0.05f, 0.05f, 0.05f, 1f) //bottom-right
	};
	
	private final Color[] healthBarColorsLow = new Color[] {//
			new Color(0.85f, 0f, 0f, 1f), //top-right
			Color.RED, //top-left
			Color.DARK_GRAY, //bottom-left
			new Color(0.05f, 0.05f, 0.05f, 1f) //bottom-right
	};
	
	private Enemy boss;
	private String bossName;
	
	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch batch;
	private ScreenTextWriter screenTextWriter;
	
	private Vector2 tileUpperRight;
	private Color[] healthBarColors;
	
	private float health;
	private float drawnHealth; // slowly reduce the health bar to the actual health
	
	private float timeSinceBossWasDefeated = 0f;
	private boolean bossSoundPlayed = false;
	
	public BossStatusBar(OrthographicCamera camera, float sceneWidth, float sceneHeight) {
		this.camera = camera;
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		screenTextWriter = new ScreenTextWriter();
		screenTextWriter.setFont(Constants.DEFAULT_FONT_NAME);
		
		tileUpperRight = new Vector2(sceneWidth - 50f, sceneHeight - 680f);
		
		EventHandler.getInstance().registerEventListener(this);
	}
	
	public void render(float delta) {
		if (boss != null) {
			queryStats();
			chooseHealthBarColors();
			
			shapeRenderer.setProjectionMatrix(camera.combined);
			batch.setProjectionMatrix(camera.combined);
			screenTextWriter.setBatchProjectionMatrix(camera.combined);
			
			// reduce the drawn health to the actual health
			if (drawnHealth > health) {
				drawnHealth -= delta / HEALTH_BAR_FULL_REDUCTION_TIME;
				if (drawnHealth < health) {
					drawnHealth = health;
				}
			}
			else {
				drawnHealth = health;
			}
			if (drawnHealth < 0) {
				drawnHealth = 0;
			}
			
			// make the health bar disappear a view seconds after the boss was defeated
			if (health <= 0) {
				timeSinceBossWasDefeated += delta;
				if (timeSinceBossWasDefeated > 2f && !bossSoundPlayed) {
					bossSoundPlayed = true;
					SOUND_SET.playSound("boss_defeated");
				}
				if (timeSinceBossWasDefeated > 5f) {
					boss = null;
				}
			}
			
			drawStatusBar();
		}
	}
	
	private void queryStats() {
		health = boss.getPercentualHealth();
	}
	
	private void chooseHealthBarColors() {
		if (drawnHealth > 0.4) {
			healthBarColors = healthBarColorsHigh;
		}
		else if (drawnHealth > 0.2) {
			healthBarColors = healthBarColorsMid;
		}
		else {
			healthBarColors = healthBarColorsLow;
		}
	}
	
	private void drawStatusBar() {
		shapeRenderer.begin(ShapeType.Filled);
		
		// background
		shapeRenderer.rect(tileUpperRight.x, tileUpperRight.y, size.x, size.y, backgroundTileColors[0], backgroundTileColors[1], backgroundTileColors[2], backgroundTileColors[3]);
		// bar background
		shapeRenderer.rect(tileUpperRight.x + healthBarUpperRightOffset.x, tileUpperRight.y + healthBarUpperRightOffset.y, healthBarSize.x, healthBarSize.y, barBackgroundColors[0], barBackgroundColors[1], barBackgroundColors[2], barBackgroundColors[3]);
		// health bar
		shapeRenderer.rect(tileUpperRight.x + healthBarUpperRightOffset.x, tileUpperRight.y + healthBarUpperRightOffset.y, healthBarSize.x * drawnHealth, healthBarSize.y, healthBarColors[0], healthBarColors[1], healthBarColors[2], healthBarColors[3]);
		
		shapeRenderer.end();
		
		screenTextWriter.setScale(1.1f);
		screenTextWriter.setColor(Color.RED);
		screenTextWriter.drawText(bossName, 70, 120, 1140, Align.center, false);
	}
	
	@Override
	public void dispose() {
		shapeRenderer.dispose();
		batch.dispose();
		EventHandler.getInstance().removeEventListener(this);
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.BOSS_ENEMY_APPEARED) {
			boss = (Enemy) event.parameterObject;
			bossName = event.stringValue;
			
			timeSinceBossWasDefeated = 0f;
			bossSoundPlayed = false;
		}
		else if (event.eventType == EventType.HIDE_BOSS_STATUS_BAR) {
			boss = null;
		}
	}
	
	public boolean isBossFightActive() {
		return boss != null;
	}
}
