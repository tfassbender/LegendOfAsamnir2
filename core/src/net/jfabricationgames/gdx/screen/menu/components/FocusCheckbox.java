package net.jfabricationgames.gdx.screen.menu.components;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import net.jfabricationgames.gdx.texture.TextureLoader;

public class FocusCheckbox {
	
	public static final String BUTTON_GREEN_NINEPATCH_CONFIG_FOCUSED = "config/menu/buttons/green_button_focused_nine_patch.json";
	public static final String BUTTON_GREEN_NINEPATCH_CONFIG = "config/menu/buttons/green_button_nine_patch.json";
	public static final String BUTTON_YELLOW_NINEPATCH_CONFIG_FOCUSED = "config/menu/buttons/yellow_button_focused_nine_patch.json";
	public static final String BUTTON_YELLOW_NINEPATCH_CONFIG = "config/menu/buttons/yellow_button_nine_patch.json";
	
	public static final float DEFAULT_CHECKBOX_SCALE = 2f;
	
	public static class FocusCheckboxBuilder {
		
		public String ninePatchConfig;
		public String ninePatchConfigFocused;
		public float width;
		public float height;
		public float x;
		public float y;
		public Supplier<Boolean> selectionSupplier;
		public Consumer<Boolean> onSelectionChangeConsumer;
		
		public FocusCheckboxBuilder setNinePatchConfig(String ninePatchConfig) {
			this.ninePatchConfig = ninePatchConfig;
			return this;
		}
		
		public FocusCheckboxBuilder setNinePatchConfigFocused(String ninePatchConfigFocused) {
			this.ninePatchConfigFocused = ninePatchConfigFocused;
			return this;
		}
		
		public FocusCheckboxBuilder setSize(float size) {
			this.width = size;
			this.height = size;
			return this;
		}
		
		public FocusCheckboxBuilder setPosition(float x, float y) {
			this.x = x;
			this.y = y;
			return this;
		}
		
		public FocusCheckboxBuilder setSelectionSupplier(Supplier<Boolean> selectionSupplier) {
			this.selectionSupplier = selectionSupplier;
			return this;
		}
		
		public FocusCheckboxBuilder setOnSelectionChangeConsumer(Consumer<Boolean> onSelectionChangeConsumer) {
			this.onSelectionChangeConsumer = onSelectionChangeConsumer;
			return this;
		}
		
		public FocusCheckbox build() {
			if (selectionSupplier == null) {
				throw new IllegalStateException("The selection supplier must not be null");
			}
			if (onSelectionChangeConsumer == null) {
				throw new IllegalStateException("The on selection change consumer must not be null");
			}
			
			return new FocusCheckbox(ninePatchConfig, ninePatchConfigFocused, x, y, width, height, selectionSupplier, onSelectionChangeConsumer);
		}
	}
	
	private Button button; // the checkbox is modeled as a button
	private TextureRegion checkmark;
	
	private float size;
	private float scale;
	
	private Supplier<Boolean> selectionSupplier;
	private Consumer<Boolean> onSelectionChangeConsumer;
	
	private FocusCheckbox(String ninePatchConfig, String ninePatchConfigFocused, //
			float x, float y, float width, float height, //
			Supplier<Boolean> selectionSupplier, Consumer<Boolean> onSelectionChangeConsumer) {
		NinePatchDrawable buttonTexture = new NinePatchDrawable(NinePatchLoader.load(ninePatchConfig));
		NinePatchDrawable buttonTextureFocused = new NinePatchDrawable(NinePatchLoader.load(ninePatchConfigFocused));
		
		ButtonStyle style = new ButtonStyle();
		style.up = buttonTexture;
		style.checked = buttonTextureFocused;
		
		button = new Button(style);
		button.setSize(width, height);
		button.setPosition(x, y);
		
		this.selectionSupplier = selectionSupplier;
		this.onSelectionChangeConsumer = onSelectionChangeConsumer;
		this.size = width;
		
		TextureLoader checkboxTextureLoader = new TextureLoader("config/menu/checkbox/checkbox.json");
		checkmark = checkboxTextureLoader.loadTexture("checkmark");
	}
	
	public boolean hasFocus() {
		return button.isChecked();
	}
	
	public void setFocused(boolean focused) {
		button.setChecked(focused);
	}
	
	public void scaleBy(float scale) {
		this.scale = scale;
		button.setTransform(true);
		button.scaleBy(scale);
		button.setSize(button.getWidth() / scale, button.getHeight() / scale);
	}
	
	public void toggle() {
		onSelectionChangeConsumer.accept(!checked());
	}
	
	public void draw(SpriteBatch batch) {
		button.draw(batch, 1f);
		
		if (checked()) {
			batch.draw(checkmark, //
					button.getX() + (size * 0.15f) * scale, //
					button.getY() + (size * 0.15f) * scale, //
					button.getWidth() * scale * 1.5f, //
					button.getHeight() * scale * 1.5f);
		}
	}
	
	private boolean checked() {
		return Boolean.TRUE.equals(selectionSupplier.get());
	}
}
