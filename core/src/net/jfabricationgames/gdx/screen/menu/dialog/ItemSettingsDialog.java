package net.jfabricationgames.gdx.screen.menu.dialog;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.item.SpecialAction;
import net.jfabricationgames.gdx.screen.menu.components.FocusButton;
import net.jfabricationgames.gdx.screen.menu.components.FocusButton.FocusButtonBuilder;
import net.jfabricationgames.gdx.screen.menu.components.FocusCheckbox;
import net.jfabricationgames.gdx.screen.menu.components.MenuBox;
import net.jfabricationgames.gdx.texture.TextureLoader;

public class ItemSettingsDialog extends InGameMenuDialog {
	
	private static final String GLOBAL_VALUE_KEY_SPECIAL_ACTION_QUICK_SELECT_ENABLED = "special_action_quick_select_enabled__";
	private static final String ITEM_TEXTURE_CONFIG_FILE = "config/menu/items/items.json";
	
	private Array<TextureRegion> itemTextures;
	private Array<FocusCheckbox> itemCheckboxes;
	private TextureLoader itemTextureLoader;
	
	private MenuBox backgroundItemExplanation;
	private MenuBox backgroundQuickSelectExplanation;
	
	private String itemExplanationText;
	
	public ItemSettingsDialog(OrthographicCamera camera) {
		super(camera);
		
		itemTextureLoader = new TextureLoader(ITEM_TEXTURE_CONFIG_FILE);
		loadItemTextures();
		createControls();
	}
	
	private void loadItemTextures() {
		Array<String> items = SpecialAction.getNamesAsList();
		itemTextures = new Array<>(items.size);
		for (String item : items) {
			if (item != null) {
				itemTextures.add(itemTextureLoader.loadTexture(item));
			}
			else {
				itemTextures.add(null);
			}
		}
	}
	
	private void createControls() {
		background = new MenuBox(12, 8, MenuBox.TextureType.YELLOW_PAPER);
		banner = new MenuBox(6, 2, MenuBox.TextureType.BIG_BANNER);
		buttonBackToMenu = new FocusButtonBuilder() //
				.setNinePatchConfig(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
				.setPosition(760, 555) //
				.setSize(110, 40) //
				.build();
		buttonBackToMenu.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonBackToMenu.setFocused(true);
		
		backgroundItemExplanation = new MenuBox(10, 4, MenuBox.TextureType.YELLOW_PAPER);
		backgroundQuickSelectExplanation = new MenuBox(10, 4, MenuBox.TextureType.YELLOW_PAPER);
		
		itemCheckboxes = new Array<>();
		Array<String> items = SpecialAction.getNamesAsList();
		for (int i = 0; i < items.size; i++) {
			FocusCheckbox checkbox = new FocusCheckbox.FocusCheckboxBuilder() //
					.setNinePatchConfig(FocusCheckbox.BUTTON_YELLOW_NINEPATCH_CONFIG) //
					.setNinePatchConfigFocused(FocusCheckbox.BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED) //
					.setPosition(400, 465 - i * 70) //
					.setSize(35) //
					.setSelectionSupplier(createSelectionSupplier(items.get(i))) //
					.setOnSelectionChangeConsumer(createSelectionConsumer(items.get(i))) //
					.build();
			checkbox.scaleBy(FocusCheckbox.DEFAULT_CHECKBOX_SCALE);
			itemCheckboxes.add(checkbox);
		}
	}
	
	private Supplier<Boolean> createSelectionSupplier(String itemName) {
		GlobalValuesDataHandler globalValuesDataHandler = GlobalValuesDataHandler.getInstance();
		return () -> globalValuesDataHandler.getAsBoolean(GLOBAL_VALUE_KEY_SPECIAL_ACTION_QUICK_SELECT_ENABLED + itemName);
	}
	
	private Consumer<Boolean> createSelectionConsumer(String itemName) {
		GlobalValuesDataHandler globalValuesDataHandler = GlobalValuesDataHandler.getInstance();
		return (selected) -> globalValuesDataHandler.put(GLOBAL_VALUE_KEY_SPECIAL_ACTION_QUICK_SELECT_ENABLED + itemName, selected);
	}
	
	public void draw() {
		if (visible) {
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			
			background.draw(batch, 200, 50, 750, 600);
			backgroundItemExplanation.draw(batch, 500, 285, 550, 260);
			backgroundQuickSelectExplanation.draw(batch, 500, 100, 550, 210);
			banner.draw(batch, 150, 525, 550, 200);
			buttonBackToMenu.draw(batch);
			
			drawCheckboxes();
			drawText();
			
			batch.end();
			
			// items need to be drawn in a separate batch
			batch.begin();
			drawItemTextures();
			batch.end();
		}
	}
	
	private void drawCheckboxes() {
		// checkboxes need to be drawn in a separate batch
		batch.flush();
		for (FocusCheckbox checkbox : itemCheckboxes) {
			checkbox.draw(batch);
		}
		batch.flush();
	}
	
	private void drawText() {
		screenTextWriter.setBatchProjectionMatrix(camera.combined);
		
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(1.2f);
		screenTextWriter.drawText("Item Settings", 240, 641);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonBackToMenu) + "Back", 795, 598);
		
		screenTextWriter.setScale(0.6f);
		screenTextWriter.drawText(itemExplanationText, 540, 505, 500, Align.left, true);
		screenTextWriter.drawText("Check items in this menu to be able to equip them with the 'R' Key on the keyboard or the 'X' button on the controller.\n" //
				+ "Other items can be equipped in the pause menu.", 540, 280, 500, Align.left, true);
		
		screenTextWriter.setScale(1f);
		screenTextWriter.setColor(Color.BROWN);
		screenTextWriter.drawText("Quick Select Items", 270, 570);
	}
	
	private void drawItemTextures() {
		float itemWidth = 40;
		float itemHeight = 40;
		float itemX = 320;
		float itemY = 470;
		float itemDistance = 70;
		
		for (int i = 0; i < itemTextures.size; i++) {
			TextureRegion itemTexture = itemTextures.get(i);
			if (itemTexture != null) {
				batch.draw(itemTexture, itemX, itemY - i * itemDistance, itemWidth, itemHeight);
			}
		}
	}
	
	public void setFocusTo(String stateName) {
		unfocusAll();
		
		FocusButton button = null;
		FocusCheckbox checkbox = null;
		switch (stateName) {
			case "itemsettings_dialog_button_back":
				button = buttonBackToMenu;
				itemExplanationText = "";
				break;
			case "itemsettings_dialog_checkbox_bow":
				checkbox = itemCheckboxes.get(0);
				itemExplanationText = "[#C8441B]Bow:\n[#000000]A ranged weapon that can be used to shoot arrows into the direction you are facing.\nAmmunition is needed to use this item.";
				break;
			case "itemsettings_dialog_checkbox_bomb":
				checkbox = itemCheckboxes.get(1);
				itemExplanationText = "[#C8441B]Bomb:\n[#000000]A throwable item that explodes after a short time.\nCan be used to attack enemies or destroy obstacles.\nAmmunition is needed to use this item";
				break;
			case "itemsettings_dialog_checkbox_boomerang":
				checkbox = itemCheckboxes.get(2);
				itemExplanationText = "[#C8441B]Boomerang:\n[#000000]A throwable item that flies in the direction you're facing and returns to you.\nCan be used to attack enemies or move obstacles.\nMana is needed to use this item.";
				break;
			case "itemsettings_dialog_checkbox_wand":
				checkbox = itemCheckboxes.get(3);
				itemExplanationText = "[#C8441B]Wand:\n[#000000]A magical item that can be used to shoot magical projectiles in the direction you're facing.\nMana is needed to use this item.";
				break;
			case "itemsettings_dialog_checkbox_feather":
				checkbox = itemCheckboxes.get(4);
				itemExplanationText = "[#C8441B]Feather:\n[#000000]A magical tool that can be used to change the text on some signboards.\nMana is needed to use this item.";
				break;
			case "itemsettings_dialog_checkbox_lantern":
				checkbox = itemCheckboxes.get(5);
				itemExplanationText = "[#C8441B]Lantern:\n[#000000]A light source that can be used to light up dark areas like caves or dungeons.\nMana is needed to use this item.";
				break;
			default:
				throw new IllegalStateException("Unexpected button state identifier: " + stateName);
		}
		
		if (button != null) {
			button.setFocused(true);
		}
		if (checkbox != null) {
			checkbox.setFocused(true);
		}
	}
	
	public void unfocusAll() {
		buttonBackToMenu.setFocused(false);
		for (FocusCheckbox checkbox : itemCheckboxes) {
			checkbox.setFocused(false);
		}
	}
	
	public void toggleSelectedCheckbox() {
		for (FocusCheckbox checkbox : itemCheckboxes) {
			if (checkbox.hasFocus()) {
				checkbox.toggle();
			}
		}
	}
}
