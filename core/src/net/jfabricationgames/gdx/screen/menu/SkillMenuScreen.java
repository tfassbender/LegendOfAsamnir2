package net.jfabricationgames.gdx.screen.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.screen.menu.components.FocusButton;
import net.jfabricationgames.gdx.screen.menu.components.FocusButton.FocusButtonBuilder;
import net.jfabricationgames.gdx.screen.menu.components.ItemSubMenu;
import net.jfabricationgames.gdx.screen.menu.components.MenuBox;
import net.jfabricationgames.gdx.skill.WeaponSkill;
import net.jfabricationgames.gdx.skill.WeaponSkillType;
import net.jfabricationgames.gdx.sound.SoundHandler;

public class SkillMenuScreen extends InGameMenuScreen<SkillMenuScreen> {
	
	private static final String SHOP_MENU_STATES_CONFIG = "config/menu/skill_menu_states.json";
	private static final String SOUND_IMPROVE_SKILL = "forge_rune";
	private static final String INPUT_CONTEXT_NAME = "shopMenu";
	
	private static final String ACTION_BACK_TO_GAME = "backToGame";
	private static final String ACTION_BACK_TO_GAME_INTERACT_BUTTON = "backToGame_interactButton";
	private static final String STATE_PREFIX_ITEM = "item_";
	private static final String STATE_PREFIX_BUTTON = "button_";
	
	private SoundHandler playedSound;
	
	private WeaponSkill weaponSkill;
	
	private Array<SkillConfig> skills;
	
	private MenuBox background;
	private MenuBox headerBanner;
	private MenuBox descriptionBox;
	private MenuBox statsBox;
	private ItemSubMenu itemMenu;
	private MenuBox itemMenuBanner;
	private FocusButton buttonBackToGame;
	
	public SkillMenuScreen(MenuGameScreen gameScreen) {
		super(gameScreen, SHOP_MENU_STATES_CONFIG);
		
		initialize();
	}
	
	private void initialize() {
		itemMenu = new ItemSubMenu(3, 2); // needs to be created before loading the item config
		
		weaponSkill = WeaponSkill.loadWeaponSkillFromConfig();
		createComponents();
		createSkillConfigs();
		loadItemGraphics();
		
		stateMachine.changeToInitialState();
	}
	
	private void loadItemGraphics() {
		Array<String> displayedItems = new Array<>(skills.size);
		for (SkillConfig config : skills) {
			displayedItems.add(config.type.name().toLowerCase());
		}
		itemMenu.setDisplayedItems(displayedItems);
	}
	
	private void createComponents() {
		background = new MenuBox(8, 6, MenuBox.TextureType.GREEN_BOARD);
		headerBanner = new MenuBox(6, 2, MenuBox.TextureType.BIG_BANNER);
		descriptionBox = new MenuBox(5, 4, MenuBox.TextureType.YELLOW_PAPER);
		statsBox = new MenuBox(8, 3, MenuBox.TextureType.YELLOW_PAPER);
		
		itemMenuBanner = new MenuBox(4, 2, MenuBox.TextureType.BIG_BANNER);
		
		int buttonWidth = 140;
		int buttonHeight = 95;
		int buttonPosX = 295;
		int lowestButtonY = 205;
		buttonBackToGame = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG).setNinePatchConfigFocused(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG_FOCUSED).setSize(buttonWidth, buttonHeight).setPosition(buttonPosX, lowestButtonY).build();
		
		buttonBackToGame.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
	}
	
	private void createSkillConfigs() {
		skills = new Array<>();
		skills.add(new SkillConfig(WeaponSkillType.AXE));
		skills.add(new SkillConfig(WeaponSkillType.SHIELD));
		skills.add(new SkillConfig(WeaponSkillType.BOW));
		skills.add(new SkillConfig(WeaponSkillType.BOMB));
		skills.add(new SkillConfig(WeaponSkillType.BOOMERANG));
		skills.add(new SkillConfig(WeaponSkillType.WAND));
		updateSkillConfigCosts();
	}
	
	private void updateSkillConfigCosts() {
		skills.get(0).cost = weaponSkill.getNextSkillLevelCost(WeaponSkillType.AXE);
		skills.get(1).cost = weaponSkill.getNextSkillLevelCost(WeaponSkillType.SHIELD);
		skills.get(2).cost = weaponSkill.getNextSkillLevelCost(WeaponSkillType.BOW);
		skills.get(3).cost = weaponSkill.getNextSkillLevelCost(WeaponSkillType.BOMB);
		skills.get(4).cost = weaponSkill.getNextSkillLevelCost(WeaponSkillType.BOOMERANG);
		skills.get(5).cost = weaponSkill.getNextSkillLevelCost(WeaponSkillType.WAND);
	}
	
	@Override
	public boolean onAction(String action, Type type, Parameters parameters) {
		if ((action.equals(ACTION_BACK_TO_GAME) || action.equals(ACTION_BACK_TO_GAME_INTERACT_BUTTON)) && isEventTypeHandled(type)) {
			backToGame();
			return true;
		}
		
		return super.onAction(action, type, parameters);
	}
	
	/**
	 * Called from the MenuStateMachine via reflection.
	 */
	public void selectCurrentItem() {
		int selectedItemIndex = itemMenu.getHoveredIndex();
		if (skills.size > selectedItemIndex) {
			SkillConfig skill = skills.get(selectedItemIndex);
			
			if (skill.cost > 0 // max level sets the cost to -1
					&& player.getMetalIngots() >= skill.cost) {
				playMenuSound(SOUND_IMPROVE_SKILL);
				EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.TAKE_PLAYERS_METAL_INGOTS).setIntValue(skill.cost));
				weaponSkill.increaseSkillLevel(skill.type);
				updateSkillConfigCosts();
			}
			else {
				playMenuSound(InGameMenuScreen.SOUND_ERROR);
			}
		}
	}
	
	@Override
	public void showMenu() {
		super.showMenu();
		takeGameSnapshot();
	}
	
	@Override
	protected String getInputContextName() {
		return INPUT_CONTEXT_NAME;
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		setProjectionMatrixBeforeRendering();
		
		batch.begin();
		drawBackground();
		drawItemMenu();
		drawDescriptionBox();
		drawStatsBox();
		drawButtons();
		drawBanners();
		batch.end();
		
		drawTexts();
	}
	
	protected void playMenuSound(String name) {
		if (playedSound != null) {
			playedSound.stop();
		}
		playedSound = soundSet.playSound(name);
	}
	
	private void drawBackground() {
		gameSnapshotSprite.draw(batch);
		background.draw(batch, 225, 130, 860, 580);
	}
	
	private void drawItemMenu() {
		itemMenu.draw(batch, 290, 390, 350, 200);
	}
	
	private void drawDescriptionBox() {
		descriptionBox.draw(batch, 675, 355, 370, 270);
	}
	
	private void drawStatsBox() {
		statsBox.draw(batch, 520, 150, 520, 250);
	}
	
	private void drawButtons() {
		buttonBackToGame.draw(batch);
	}
	
	private void drawBanners() {
		headerBanner.draw(batch, 100, 550, 1100, 250);
		itemMenuBanner.draw(batch, 305, 507, 270, 150);
	}
	
	private void drawTexts() {
		screenTextWriter.setColor(Color.BLACK);
		
		screenTextWriter.setScale(1.5f);
		screenTextWriter.drawText("Weapon Improvements", 250, 693);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText("Weapons", 360, 595);
		
		screenTextWriter.setScale(1.15f);
		int buttonTextX = 330;
		int buttonTextWidth = 140;
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonBackToGame) + "Leave\nForge", buttonTextX, 316, buttonTextWidth, Align.center, false);
		
		screenTextWriter.setScale(0.7f);
		String itemName = "";
		int itemCosts = 0;
		int playersMetalIngots = player.getMetalIngots();
		
		int selectedItemIndex = itemMenu.getHoveredIndex();
		if (selectedItemIndex >= 0 && skills.size > selectedItemIndex) {
			itemName = skills.get(selectedItemIndex).type.name();
			itemCosts = skills.get(selectedItemIndex).cost;
		}
		if (itemName == null || itemName.isEmpty()) {
			itemName = "---";
		}
		
		String costString = itemCosts >= 0 ? String.valueOf(itemCosts) + " Metal Ingots" : "--- (Max Level)";
		
		String textColor = "[#EB5000]";
		screenTextWriter.drawText("Weapon:\n" + textColor + itemName + "\n[BLACK]Costs:\n" + textColor + costString + //
				"\n[BLACK]You have:\n" + textColor + playersMetalIngots + " Metal Ingots", 730, 575);
		
		String skillDescription = getSkillDescription(selectedItemIndex);
		screenTextWriter.drawText(skillDescription, 570, 345, 450, Align.left, true);
	}
	
	private String getSkillDescription(int selectedItemIndex) {
		if (selectedItemIndex >= 0 && skills.size > selectedItemIndex) {
			String skillDescription = "Level: " //
					+ weaponSkill.getSkillLevel(skills.get(selectedItemIndex).type) + "/" //
					+ weaponSkill.getMaxSkillLevel(skills.get(selectedItemIndex).type) + "\n";
			
			switch (skills.get(selectedItemIndex).type) {
				case AXE:
					skillDescription += "Increases the damage of the axe.";
					break;
				case SHIELD:
					skillDescription += "Increases the ratio of the attack damage that is blocked by the shield.";
					break;
				case BOW:
					skillDescription += "Increases the damage of the bow and the maximum number of arrows.";
					break;
				case BOMB:
					skillDescription += "Increases the damage and the maximum number of bombs.";
					break;
				case BOOMERANG:
					skillDescription += "Increases the damage of the boomerang and reduces the mana cost for using it.";
					break;
				case WAND:
					skillDescription += "Increases the damage of the wand and reduces the mana cost for using it.";
					break;
			}
			
			return skillDescription;
		}
		
		return "Select a weapon to see it's description.";
	}
	
	private String getButtonTextColorEncoding(FocusButton button) {
		return button.hasFocus() ? TEXT_COLOR_ENCODING_FOCUS : TEXT_COLOR_ENCODING_NORMAL;
	}
	
	@Override
	public void setFocusTo(String stateName, String leavingState) {
		unfocusAll();
		if (stateName.startsWith(STATE_PREFIX_ITEM)) {
			int itemIndex = Integer.parseInt(stateName.substring(STATE_PREFIX_ITEM.length())) - 1;
			itemMenu.setHoveredIndex(itemIndex);
		}
		else if (stateName.startsWith(STATE_PREFIX_BUTTON)) {
			String buttonId = stateName.substring(STATE_PREFIX_BUTTON.length());
			FocusButton button = null;
			switch (buttonId) {
				case "backToGame":
					button = buttonBackToGame;
					break;
				default:
					throw new IllegalStateException("Unexpected button state identifier: " + STATE_PREFIX_BUTTON + buttonId);
			}
			if (button != null) {
				button.setFocused(true);
			}
		}
	}
	
	private void unfocusAll() {
		buttonBackToGame.setFocused(false);
		itemMenu.setHoveredIndex(-1);
	}
	
	public static class SkillConfig {
		
		public WeaponSkillType type;
		public int cost;
		
		public SkillConfig() {}
		
		public SkillConfig(WeaponSkillType type) {
			this.type = type;
		}
	}
}
