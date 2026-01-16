package net.jfabricationgames.gdx.data.handler;

import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.data.container.CharacterDataContainer;
import net.jfabricationgames.gdx.data.container.GameDataContainer;
import net.jfabricationgames.gdx.data.handler.type.DataCharacterAction;

public class CharacterPropertiesDataHandler implements DataHandler {
	
	private static CharacterPropertiesDataHandler instance;
	
	public static synchronized CharacterPropertiesDataHandler getInstance() {
		if (instance == null) {
			instance = new CharacterPropertiesDataHandler();
		}
		return instance;
	}
	
	private CharacterDataContainer properties;
	
	private CharacterPropertiesDataHandler() {}
	
	@Override
	public void updateData(GameDataContainer dataContainer) {
		properties = dataContainer.characterDataContainer;
	}
	
	public void updateStats(float delta, DataCharacterAction action) {
		//recharge endurance by time
		properties.endurance = Math.min(properties.endurance + delta * action.getEnduranceRecharge(), properties.maxEndurance);
		
		//increase health, mana and endurance
		if (properties.increaseHealth > 0f) {
			float increaseStep = Math.min(delta * properties.healthIncreasePerSecond, properties.increaseHealth);
			properties.increaseHealth -= increaseStep;
			properties.health = Math.min(properties.health + increaseStep, properties.maxHealth);
			
			if (properties.health >= properties.maxHealth) {
				properties.increaseHealth = 0;
			}
		}
		if (properties.increaseMana > 0f) {
			float increaseStep = Math.min(delta * properties.manaIncreasePerSecond, properties.increaseMana);
			properties.increaseMana -= increaseStep;
			properties.mana = Math.min(properties.mana + increaseStep, properties.maxMana);
			
			if (properties.mana >= properties.maxMana) {
				properties.increaseMana = 0;
			}
		}
		if (properties.increaseEndurance > 0f) {
			//only used when endurance is increased by an event or an item
			float increaseStep = Math.min(delta * properties.enduranceIncreasePerSecond, properties.increaseEndurance);
			properties.increaseEndurance -= increaseStep;
			properties.endurance = Math.min(properties.endurance + increaseStep, properties.maxEndurance);
			
			if (properties.endurance >= properties.maxEndurance) {
				properties.increaseEndurance = 0;
			}
		}
		if (properties.increaseArmor > 0f) {
			float increaseStep = Math.min(delta * properties.armorIncreasePerSecond, properties.increaseArmor);
			properties.increaseArmor -= increaseStep;
			properties.armor = Math.min(properties.armor + increaseStep, properties.maxArmor);
			
			if (properties.armor >= properties.maxArmor) {
				properties.increaseArmor = 0;
			}
		}
		if (properties.decreaseCoins > 0f) {
			float decreaseStep = Math.min(delta * properties.coinsDecreasePerSecond, properties.decreaseCoins);
			properties.decreaseCoins -= decreaseStep;
			properties.coins = Math.max(Math.min(properties.coins - decreaseStep, properties.maxCoins), 0);
			if (properties.coins == 0 && properties.decreaseCoins > 0) {
				properties.decreaseCoins = 0;
			}
		}
	}
	
	public void resetAllToNull() {
		properties.health = 0.1f; // prevent dying instantly
		properties.armor = 0f;
		properties.mana = 0f;
		properties.endurance = 0f;
	}
	
	public void increaseStatsToMinimumAfterRespawn() {
		if (properties.health + properties.increaseHealth < properties.maxHealth * 0.5f) {
			properties.increaseHealth = 0;
			increaseHealthTo(properties.maxHealth * 0.5f);
		}
		if (properties.armor + properties.increaseArmor < properties.maxArmor * 0.25f) {
			properties.increaseArmor = 0;
			increaseArmorTo(properties.maxArmor * 0.25f);
		}
		if (properties.mana + properties.increaseMana < properties.maxMana * 0.5f) {
			properties.increaseMana = 0;
			increaseManaTo(properties.maxMana * 0.5f);
		}
	}
	
	public boolean hasEnoughEndurance(DataCharacterAction action) {
		return hasEnoughEndurance(action.getEnduranceCosts());
	}
	public boolean hasEnoughEndurance(float endurance) {
		return properties.endurance >= endurance;
	}
	
	public void reduceEnduranceForAction(DataCharacterAction action) {
		reduceEndurance(action.getEnduranceCosts());
	}
	public void reduceEndurance(float endurance) {
		properties.endurance = Math.max(0, properties.endurance - endurance);
	}
	
	public boolean hasBlock() {
		return properties.armor > 0;
	}
	
	public boolean isSlowedDown() {
		return properties.slowedDown;
	}
	public void setSlowedDown(boolean slowedDown) {
		properties.slowedDown = slowedDown;
	}
	
	public void reduceEnduranceForSprinting(float delta) {
		properties.endurance -= properties.enduranceCostsSprint * delta;
		if (properties.endurance < 0) {
			properties.endurance = 0;
		}
	}
	public void reduceEnduranceForBlocking(float delta) {
		properties.endurance -= properties.enduranceCostsBlock * delta;
		if (properties.endurance < 0) {
			properties.endurance = 0;
		}
	}
	public void reduceEnduranceForBlockMoving(float delta) {
		properties.endurance -= properties.enduranceCostsBlockMoving * delta;
		if (properties.endurance < 0) {
			properties.endurance = 0;
		}
	}
	public void reduceEnduranceForHitBlocking() {
		properties.endurance -= properties.enduranceCostHitBlocking;
		if (properties.endurance < 0) {
			properties.endurance = 0;
		}
	}
	
	public boolean isExhausted() {
		return properties.endurance < 1e-5;
	}
	
	public float getHealthPercentual() {
		return properties.health / properties.maxHealth;
	}
	
	public float getHealthPlusIncreasePercentual() {
		return (properties.health + properties.increaseHealth) / properties.maxHealth;
	}
	
	public void setHealth(float health) {
		properties.health = health;
	}
	
	public void takeDamage(float damage) {
		properties.health -= damage;
		if (properties.health < 0) {
			properties.health = 0;
		}
	}
	
	public boolean isAlive() {
		return properties.health + properties.increaseHealth > 0;
	}
	
	public void increaseHealth(float amount) {
		properties.increaseHealth += amount;
	}
	
	public void increaseHealthByHalf() {
		properties.increaseHealth += properties.maxHealth * 0.5f;
	}
	
	public void increaseHealthFull() {
		properties.increaseHealth += properties.maxHealth;
	}
	
	public void increaseHealthTo(float max) {
		if (max > properties.health) {
			properties.increaseHealth += max - properties.health;
		}
	}
	
	public void resetIncreaseHealth() {
		properties.increaseHealth = 0;
	}
	
	public float getManaPercentual() {
		return properties.mana / properties.maxMana;
	}
	
	public float getManaPlusIncreasePercentual() {
		return (properties.mana + properties.increaseMana) / properties.maxMana;
	}
	
	public float getMaxMana() {
		return properties.maxMana;
	}
	
	public boolean hasEnoughMana(float manaCost) {
		return properties.mana >= manaCost;
	}
	
	public void increaseMana(float amount) {
		properties.increaseMana += amount;
	}
	
	public void increaseManaFull() {
		properties.increaseMana += properties.maxMana;
	}
	
	public void increaseManaTo(float max) {
		if (max > properties.mana) {
			properties.increaseMana += max - properties.mana;
		}
	}
	
	public void reduceMana(float amount) {
		properties.mana -= amount;
	}
	
	public float getEndurancePercentual() {
		return properties.endurance / properties.maxEndurance;
	}
	
	public void increaseArmor(float amount) {
		properties.increaseArmor += amount;
	}
	
	public float getArmorPercentual() {
		return properties.armor / properties.maxArmor;
	}
	
	public float getArmorPlusIncreasePercentual() {
		return (properties.armor + properties.increaseArmor) / properties.maxArmor;
	}
	
	public void takeArmorDamage(float damage) {
		properties.armor = Math.max(properties.armor - damage, 0);
	}
	
	public void increaseArmorByHalf() {
		properties.increaseArmor += properties.maxArmor * 0.5f;
	}
	
	public void increaseArmorTo(float max) {
		if (max > properties.armor) {
			properties.increaseArmor += max - properties.armor;
		}
	}
	
	public void increaseCoins(int coins) {
		properties.coins += coins;
	}
	public void reduceCoins(int coins) {
		properties.decreaseCoins += coins;
	}
	/**
	 * Shows the decrease of coins if properties.decreaseCoins is greater than 0.
	 */
	public int getCoinsForHud() {
		return Math.round(properties.coins);
	}
	public int getCoins() {
		return Math.round(properties.coins - properties.decreaseCoins);
	}
	
	public void increaseMetalIngots(int ingots) {
		properties.metalIngots += ingots;
	}
	public void reduceMetalIngots(int ingots) {
		properties.metalIngots -= ingots;
	}
	public int getMetalIngots() {
		return Math.round(properties.metalIngots);
	}
	
	public void increaseToken(String token) {
		properties.tokens.put(token, properties.tokens.get(token, 0) + 1);
	}
	public void setTokens(String token, int amount) {
		properties.tokens.put(token, amount);
	}
	public void resetTokens(String token) {
		properties.tokens.put(token, 0);
	}
	public int getTokens(String token) {
		return properties.tokens.get(token, 0);
	}
	
	public Vector2 getRespawnPoint() {
		return properties.respawnPoint;
	}
	public void setRespawnPoint(Vector2 respawnPoint) {
		properties.respawnPoint = respawnPoint;
	}
	
	public void setRespawnWithStartingStats(boolean respawnWithStartingStats) {
		properties.respawnWithStartingStats = respawnWithStartingStats;
	}
	
	public Vector2 getPlayerPosition() {
		return properties.position;
	}
	public void setPlayerPosition(Vector2 position) {
		properties.position = position;
	}
}
