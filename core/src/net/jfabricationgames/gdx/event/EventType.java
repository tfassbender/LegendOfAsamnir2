package net.jfabricationgames.gdx.event;

public enum EventType {
	
	//*******************************
	//*** General
	//*******************************
	PRE_NEW_GAME_STARTED, //
	NEW_GAME_STARTED, //
	GAME_STARTED, //
	GAME_LOADED, //
	MAP_ENTERED, //
	SHOW_IN_GAME_SHOP_MENU, //
	SHOW_IN_GAME_SKILL_MENU, //
	START_CUTSCENE, //
	ON_SCREEN_TEXT_ENDED, //
	CHANGE_MAP, // 
	RESTART_FROM_SVARTALFHEIM, //
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
	PLAYER_STATS_BELOW_THRESHOLD, //
	TAKE_PLAYERS_COINS, //
	TAKE_PLAYERS_METAL_INGOTS, //
	GIVE_COINS_TO_PLAYER, //
	GIVE_ITEM_TO_PLAYER, //
	GIVE_METAL_INGOTS_TO_PLAYER, //
	PLAYER_BUY_ITEM, //
	ITEM_BOUGHT_BUT_ALREADY_OWNED, //
	CHANGE_HEALTH, //
	CHANGE_SHIELD, //
	CHANGE_MANA, //
	SET_HEALTH, //
	KILL_PLAYER, //
	HOOKSHOT_ATTACK_STARTED, //
	HOOKSHOT_ATTACK_FINISHED, //
	//*******************************
	//*** Game Objects
	//*******************************
	EVENT_OBJECT_TOUCHED, //
	UPDATE_MAP_OBJECT_STATES, //
	STATE_SWITCH_ACTION, //
	STATE_SWITCH_ACTIVATED, //
	STATE_SWITCH_DEACTIVATED, //
	INTERACTED_WITH_LOCKED_OBJECT, //
	OPEN_LOCK, //
	CLOSE_LOCK, //
	SET_STATE_SWITCH_STATE, //
	CHANGE_SIGNBOARD_TEXT, //
	DESTROYABLE_OBJECT_DESTROYED, //
	DISTRIBUTED_SPAWN, //
	ACTIVATE_MOVING_OBJECT, //
	DISSOLVE_DRAGGABLE_OBJECT_JOINTS, //
	CONFIG_GAME_OBJECT_ACTION, //
	TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SENSOR, //
	TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SOLID_OBJECT, //
	ROTATING_PUZZLE_INTERACTION, //
	ROTATING_PUZZLE_STATE_REQUEST, //
	//*******************************
	//*** Items
	//*******************************
	ITEM_PICKED_UP, //
	EVENT_ITEM_PICKED_UP, //
	SPECIAL_KEY_ITEM_PICKED_UP, //
	SET_ITEM, //
	SET_TOKENS_RELATIVE, //
	RUNE_FOUND, //
	RUNE_NEEDED, //
	RUNE_USED, //
	SPECIAL_ACTION_ITEM_PICKED_UP, //
	DELIVER_TRIFORCE_PIECES, //
	//*******************************
	//*** Cutscenes
	//*******************************
	CUTSCENE_CONDITION, //
	CUTSCENE_EVENT, //
	CUTSCENE_PLAYER_CHOICE, //
	CUSTCENE_SPAWN_UNIT, //
	CUSTCENE_SPAWN_UNIT_2, //
	CUTSCENE_CREATE_ATTACK, //
	CUTSCENE_REMOVE_UNIT, //
	CUTSCENE_SHOW_TITLE_BANNER, //
	//*******************************
	//*** Level Setup
	//*******************************
	// add multiple level setup events here, to activate different spawn points
	// usually from cutscenes, because there you can easily use conditions
	LEVEL_SETUP_1, //
	LEVEL_SETUP_2, //
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
	FIRE_TOTEM_ACTIVATED, //
	ENEMY_DEFEATED, //
	ENEMY_TAKE_DAMAGE, //
	ENEMY_DIE, //
	GOBLIN_KING_COMMAND, //
	BOSS_ENEMY_SHOW, //
	BOSS_ENEMY_APPEARED, //
	HIDE_BOSS_STATUS_BAR, //
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
	//*** Background Music
	//*******************************
	PLAY_BACKGROUND_MUSIC, // play a background music that is selected by the event
	PLAY_MAP_BACKGROUND_MUSIC, // play the background music that is configured in the map properties
	ADD_BACKGROUND_MUSIC_TO_QUEUE, // 
	ADD_MAP_BACKGROUND_MUSIC_TO_QUEUE, //
	CLEAR_BACKGROUND_MUSIC_QUEUE, // does not stop the current music
	STOP_BACKGROUND_MUSIC, //
	//*******************************
	//*** Debug Config Events
	//*******************************
	COLLECT_RUNE, //
}
