package net.jfabricationgames.gdx.event;

public enum EventType {
	
	//*******************************
	//*** General
	//*******************************
	NEW_GAME_STARTED, //
	GAME_STARTED, //
	GAME_LOADED, //
	MAP_ENTERED, //
	SHOW_IN_GAME_SHOP_MENU, //
	START_CUTSCENE, //
	ON_SCREEN_TEXT_ENDED, //
	CHANGE_MAP, // 
	//*******************************
	//*** Save / load game
	//*******************************
	QUICKSAVE, //
	SAVE_GAME, //
	BEFORE_PERSIST_STATE, //
	UPDATE_MAP_AFTER_LOADING_GAME_STATE, //
	BACK_TO_MAIN_MENU, //
	//*******************************
	//*** Player Events
	//*******************************
	PLAYER_RESPAWNED, //
	OUT_OF_AMMO, //
	TAKE_PLAYERS_COINS, //
	GIVE_COINS_TO_PLAYER, //
	GIVE_ITEM_TO_PLAYER, //
	PLAYER_BUY_ITEM, //
	ITEM_BOUGHT_BUT_ALREADY_OWNED, //
	CHANGE_HEALTH, //
	CHANGE_SHIELD, //
	CHANGE_MANA, //
	//*******************************
	//*** Game Objects
	//*******************************
	EVENT_OBJECT_TOUCHED, //
	UPDATE_MAP_OBJECT_STATES, //
	STATE_SWITCH_ACTION, //
	INTERACTED_WITH_LOCKED_OBJECT, //
	OPEN_LOCK, //
	CLOSE_LOCK, //
	SET_STATE_SWITCH_STATE, //
	CHANGE_SIGNBOARD_TEXT, //
	//*******************************
	//*** Items
	//*******************************
	EVENT_ITEM_PICKED_UP, //
	SPECIAL_KEY_ITEM_PICKED_UP, //
	SET_ITEM, //
	RUNE_FOUND, //
	RUNE_NEEDED, //
	RUNE_USED, //
	SPECIAL_ACTION_ITEM_PICKED_UP, //
	//*******************************
	//*** Cutscenes
	//*******************************
	CUTSCENE_CONDITION, //
	CUTSCENE_EVENT, //
	CUTSCENE_PLAYER_CHOICE, //
	CUSTCENE_SPAWN_UNIT, //
	//*******************************
	//*** Fast Travel
	//*******************************
	FAST_TRAVEL_POINT_REGISTERED, //
	FAST_TRAVEL_POINT_ENABLED, //
	FAST_TRAVEL_TO_MAP_POSITION, //
	//*******************************
	//*** Enemy, NPC, AI
	//*******************************
	NPC_INTERACTION, //
	AI_TEAM_CALL, //
	ENEMY_DEFEATED, //
	GOBLIN_KING_COMMAND, //
	BOSS_ENEMY_SHOW, //
	BOSS_ENEMY_APPEARED, //
	//*******************************
	//*** Conditions
	//*******************************
	SET_GLOBAL_CONDITION_VALUE, //
	INCREASE_GLOBAL_CONDITION_VALUE, //
	//*******************************
	//*** Configuration
	//*******************************
	CONFIG_GENERATED_EVENT, //
	//*******************************
	//*** Debug Config Events
	//*******************************
	COLLECT_RUNE, //
}
