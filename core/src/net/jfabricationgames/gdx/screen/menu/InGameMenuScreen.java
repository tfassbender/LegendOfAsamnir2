package net.jfabricationgames.gdx.screen.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.character.player.PlayableCharacter;
import net.jfabricationgames.gdx.character.player.Player;
import net.jfabricationgames.gdx.data.GameDataService;
import net.jfabricationgames.gdx.data.properties.FastTravelPointProperties;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.input.InputManager;
import net.jfabricationgames.gdx.screen.ScreenManager;
import net.jfabricationgames.gdx.screen.game.GameScreen;
import net.jfabricationgames.gdx.screen.menu.control.ControlledMenu;

public abstract class InGameMenuScreen<T extends ControlledMenu<T>> extends MenuScreen<T> {
	
	protected MenuGameScreen gameScreen;
	protected FrameBuffer gameSnapshotFrameBuffer;
	protected Sprite gameSnapshotSprite;
	
	protected PlayableCharacter player;
	
	public InGameMenuScreen(MenuGameScreen gameScreen, String... stateConfigFiles) {
		super(stateConfigFiles);
		this.gameScreen = gameScreen;
		
		gameSnapshotFrameBuffer = new FrameBuffer(Format.RGB888, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false);
		
		AnimationManager.getInstance().loadAnimations(ANIMATION_CONFIG_FILE);
		
		player = Player.getInstance();
	}
	
	protected void takeGameSnapshot() {
		gameSnapshotFrameBuffer.bind();
		gameScreen.render(0f);
		FrameBuffer.unbind();
		
		Texture gameSnapshotTexture = gameSnapshotFrameBuffer.getColorBufferTexture();
		gameSnapshotSprite = new Sprite(gameSnapshotTexture);
		gameSnapshotSprite.flip(false, true);
		gameSnapshotSprite.setColor(Color.GRAY);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		gameSnapshotFrameBuffer.dispose();
	}
	
	//****************************************************************
	//*** State machine methods (called via reflection)
	//****************************************************************
	
	public void backToGame() {
		removeInputListener();
		InputManager.getInstance().changeInputContext(ScreenManager.INPUT_CONTEXT_NAME);
		ScreenManager.getInstance().changeToGameScreen();
	}
	
	public void backToSvartalfheim() {
		Gdx.app.debug(getClass().getSimpleName(), "'Back to Svartalfheim' selected");
		
		hideBossStatusBar();
		
		GameScreen.loadAndShowGameScreen(() -> {
			// the boolean value determines that the cause of the respawn was that the player died
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.RESTART_FROM_SVARTALFHEIM).setBooleanValue(true));
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.PLAYER_RESPAWNED));
			backToGame();
		});
	}
	
	public void restartFromLastCheckpoint() {
		Gdx.app.debug(getClass().getSimpleName(), "'Restart from last checkpoint' selected");
		
		hideBossStatusBar();
		
		GameDataService gameDataService = new GameDataService();
		if (gameDataService.isQuickSaveGameDataSlotExisting()) {
			gameDataService.loadGameDataFromQuicksaveSlot();
			GameScreen.loadAndShowGameScreen(() -> {
				EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.GAME_LOADED));
				EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.PLAYER_RESPAWNED));
				player.afterLoadMap();
				backToGame();
			});
		}
		else {
			playMenuSound(ControlledMenu.SOUND_ERROR);
		}
	}
	
	private void hideBossStatusBar() {
		// the boss status bar needs to be removed if the player died in a boss fight
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.HIDE_BOSS_STATUS_BAR));
	}
	
	//*********************************************************************
	//*** interfaces
	//*********************************************************************
	
	public interface MenuGameScreen extends Screen {
		
		public String getGameMapConfigPath();
		
		public float getMapHeight();
		public float getMapWidth();
		
		public Vector2 getPlayersPositionOnMap();
		public Array<FastTravelPointProperties> getFastTravelPositions();
		
		public boolean isBossFightActive();
	}
}
