package net.jfabricationgames.gdx.hud.implementation;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;

import net.jfabricationgames.gdx.condition.ConditionHandler;
import net.jfabricationgames.gdx.cutscene.variable.CutsceneVariable;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.input.InputActionListener.Parameters;
import net.jfabricationgames.gdx.input.InputActionListener.Type;
import net.jfabricationgames.gdx.text.ScreenTextWriter;

public class OnScreenTextRenderer {
	
	private OnScreenTextBox onScreenTextBox;
	
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private ScreenTextWriter screenTextWriter;
	
	private GlyphLayout textLayout;
	
	private String text;
	private boolean showNextPageIcon;
	
	private float nextPageIndicatorTimer = 0;
	private final float nextPageIndicatorBlinkingTime = 0.75f;
	private boolean displayNextPageIndicator = false;
	
	private int firstDisplayedLine = 0;
	private int[] displayedTextIndices;
	
	private int displayedCharacters = 0;
	private float timeSincePageStarted = 0;
	private float displayCharactersPerSecond = 25f;
	
	public OnScreenTextRenderer(OnScreenTextBox onScreenTextBox, SpriteBatch batch, ShapeRenderer shapeRenderer, ScreenTextWriter screenTextWriter) {
		this.onScreenTextBox = onScreenTextBox;
		this.batch = batch;
		this.shapeRenderer = shapeRenderer;
		this.screenTextWriter = screenTextWriter;
	}
	
	protected boolean isDisplaying() {
		return text != null;
	}
	
	protected void render(float delta) {
		renderText();
		
		nextPageIndicatorTimer += delta;
		if (nextPageIndicatorTimer > nextPageIndicatorBlinkingTime) {
			nextPageIndicatorTimer -= nextPageIndicatorBlinkingTime;
			displayNextPageIndicator = !displayNextPageIndicator;
		}
		
		if (!allCharactersDisplayed()) {
			timeSincePageStarted += delta;
			displayedCharacters = Math.min((int) (displayCharactersPerSecond * timeSincePageStarted), displayedTextIndices[1] - displayedTextIndices[0]);
		}
	}
	
	private void renderText() {
		batch.begin();
		
		screenTextWriter.setScale(OnScreenTextBox.TEXT_SCALE);
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.drawText(text, onScreenTextBox.textBoxX + onScreenTextBox.textOffsetX, onScreenTextBox.textBoxY + onScreenTextBox.textBoxHeight - onScreenTextBox.textOffsetY, displayedTextIndices[0], displayedTextIndices[0] + displayedCharacters, onScreenTextBox.textWidth, Align.left, true);
		
		if ((hasNextPage() || showNextPageIcon) && displayNextPageIndicator) {
			drawNextPageIndicator();
		}
		
		batch.end();
	}
	
	protected boolean hasNextPage() {
		return textLayout.runs.size > firstDisplayedLine + OnScreenTextBox.DISPLAYABLE_LINES;
	}
	
	private void drawNextPageIndicator() {
		shapeRenderer.begin(ShapeType.Filled);
		
		shapeRenderer.setColor(Color.BLACK);
		//draw an arrow in the lower right corner
		shapeRenderer.triangle(1200f, 55f, 1230f, 55f, 1215f, 35f);
		
		shapeRenderer.end();
	}
	
	protected boolean allCharactersDisplayed() {
		return displayedCharacters == displayedTextIndices[1] - displayedTextIndices[0];
	}
	
	protected void showAllCharacters() {
		displayedCharacters = displayedTextIndices[1] - displayedTextIndices[0];
	}
	
	private int[] getDisplayedTextIndices() {
		int[] indices = new int[2];
		
		//find the start index
		for (int i = 0; i < firstDisplayedLine; i++) {
			//increase the start index by the number of characters in the previous (not displayed) lines
			indices[0] += textLayout.runs.get(i).glyphs.size + 1;//+1 for line breaks (I think...)
		}
		
		//add the start index to the end index, to only count the remaining displayed line's characters
		indices[1] = indices[0];
		
		//find the end index
		for (int i = 0; i < OnScreenTextBox.DISPLAYABLE_LINES && i + firstDisplayedLine < textLayout.runs.size; i++) {
			//increase the end index by the number of characters in the displayed lines
			indices[1] += textLayout.runs.get(i + firstDisplayedLine).glyphs.size + 1;//+1 for line breaks (I think...)
		}
		indices[1] -= 1;//remove the last line break index
		
		return indices;
	}
	
	private void resetDisplayedCharacters() {
		displayedCharacters = 0;
		timeSincePageStarted = 0;
	}
	
	protected void nextPage() {
		if (hasNextPage()) {
			firstDisplayedLine += OnScreenTextBox.DISPLAYABLE_LINES;
			displayedTextIndices = getDisplayedTextIndices();
			timeSincePageStarted = 0;
			displayedCharacters = 0;
		}
	}
	
	protected void setText(String text, boolean showNextPageIcon) {
		this.text = resolveVariablesInText(text);
		this.showNextPageIcon = showNextPageIcon;
		calculateTextLayout();
		calculateDisplayedText();
	}
	
	private String resolveVariablesInText(String text) {
		Map<String, String> variablesAndValues = findVariables(text);
		variablesAndValues.putAll(findTernaryVariables(text));
		
		if (!variablesAndValues.isEmpty()) {
			String jointVariables = variablesAndValues.entrySet().stream() //
					.map(e -> e.getKey() + " -> '" + e.getValue() + "'") //
					.collect(Collectors.joining(", "));
			Gdx.app.debug(getClass().getSimpleName(), "Replacing variables in on screen text: " + jointVariables);
		}
		
		// replace all variables in the text
		for (Map.Entry<String, String> entry : variablesAndValues.entrySet()) {
			text = text.replace(entry.getKey(), entry.getValue());
		}
		
		return text;
	}
	
	private Map<String, String> findVariables(String text) {
		Map<String, String> variablesAndValues = new HashMap<>();
		
		// find all variables in the text
		Pattern replacementVariables = Pattern.compile("#\\{[^}]*\\}");
		Matcher matcher = replacementVariables.matcher(text);
		while (matcher.find()) {
			String variable = matcher.group();
			String variableName = variable.substring(2, variable.length() - 1);
			String replacement;
			
			// try to replace the variables with values from the CutsceneVariables enum
			if (CutsceneVariable.doesVariableExist(variableName)) {
				replacement = CutsceneVariable.valueOf(variableName).evaluate();
			}
			else {
				// if no enum value was defined, a global value is used instead
				replacement = GlobalValuesDataHandler.getInstance().get(variableName);
			}
			
			if (replacement == null) {
				Gdx.app.error(getClass().getSimpleName(), "Variable for on screen text not found: " + variableName);
				replacement = "<" + variableName + ">";
			}
			
			variablesAndValues.put(variable, replacement);
		}
		
		return variablesAndValues;
	}
	
	private Map<String, String> findTernaryVariables(String text) {
		Map<String, String> variablesAndValues = new HashMap<>();
		
		// find all ternary operator variables in the text
		Pattern ternaryOperatorVariables = Pattern.compile("\\?\\{.+\\|.*\\|.*\\}");
		Matcher matcher = ternaryOperatorVariables.matcher(text);
		while (matcher.find()) {
			String variable = matcher.group();
			String variableName = variable.substring(2, variable.length() - 1);
			String[] parts = variableName.split("\\|");
			
			if (ConditionHandler.getInstance().isConditionMet(parts[0])) {
				variablesAndValues.put(variable, parts[1]);
			}
			else {
				if (parts.length > 2) {
					variablesAndValues.put(variable, parts[2]);
				}
				else {
					// ?{condition|text|} will result in only two parts
					variablesAndValues.put(variable, "");
				}
			}
		}
		
		return variablesAndValues;
	}
	
	private void calculateTextLayout() {
		screenTextWriter.setScale(OnScreenTextBox.TEXT_SCALE);
		textLayout = screenTextWriter.createGlyphLayout(text, onScreenTextBox.textWidth, Align.left, true);
	}
	
	private void calculateDisplayedText() {
		firstDisplayedLine = 0;
		displayedTextIndices = getDisplayedTextIndices();
		resetDisplayedCharacters();
	}
	
	protected void clear() {
		text = null;
		showNextPageIcon = false;
	}
	
	protected boolean onAction(String action, Type type, Parameters parameters, boolean canBeSkipped) {
		if (isDisplaying()) {
			if (action.equals("interact") && (type == Type.KEY_DOWN || type == Type.CONTROLLER_BUTTON_PRESSED)) {
				if (!allCharactersDisplayed()) {
					if (canBeSkipped) {
						showAllCharacters();
					}
				}
				else if (hasNextPage()) {
					nextPage();
				}
				else {
					onScreenTextBox.close();
				}
				return true;
			}
		}
		return false;
	}
}
