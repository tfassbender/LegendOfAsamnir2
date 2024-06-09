package net.jfabricationgames.gdx.character.player.implementation;

import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.cutscene.CutsceneHandler;
import net.jfabricationgames.gdx.input.InputActionListener;
import net.jfabricationgames.gdx.input.InputContext;
import net.jfabricationgames.gdx.input.InputManager;
import net.jfabricationgames.gdx.interaction.InteractionManager;

class CharacterInputProcessor implements InputActionListener {
	
	private static final float SQRT_0_5 = (float) Math.sqrt(0.5f);
	
	private static final float TIME_TILL_IDLE_ANIMATION = 4.0f;
	private static final float TIME_TILL_SPIN_ATTACK = 1.5f;
	
	private static final float MOVING_SPEED = 300f;
	private static final float MOVING_SPEED_JUMP = 425f;
	private static final float MOVING_SPEED_SPRINT = 425f;
	private static final float MOVING_SPEED_ATTACK = 150f;
	private static final float MOVING_SPEED_BLOCK = 100f;
	
	private static final float SPRINT_INPUT_ACTIONS_MAX_DELTA = 0.5f;
	
	private static final float SPIN_ATTACK_CHARGED_ENDURANCE_COSTS_PER_SECOND = 20f; // resulting costs are reduced by regeneration (15f)
	private static final String SOUND_SPIN_ATTACK_CHARGED = "spin_attack_charged";
	
	private static final String INPUT_MOVE_UP = "up";
	private static final String INPUT_MOVE_DOWN = "down";
	private static final String INPUT_MOVE_LEFT = "left";
	private static final String INPUT_MOVE_RIGHT = "right";
	private static final String INPUT_SPECIAL = "special";
	private static final String INPUT_ATTACK = "attack";
	private static final String INPUT_SPRINT = "sprint";
	private static final String INPUT_BLOCK = "block";
	
	private static final String ACTION_INTERACT = "interact";
	private static final String ACTION_BLOCK = "block_action";
	private static final String ACTION_PREVIOUS_SPECIAL_ACTION = "previousSpecialAction";
	private static final String ACTION_NEXT_SPECIAL_ACTION = "nextSpecialAction";
	
	private static final String ACTION_UP = "up_action";
	private static final String ACTION_DOWN = "down_action";
	private static final String ACTION_LEFT = "left_action";
	private static final String ACTION_RIGHT = "right_action";
	
	private Dwarf player;
	
	private boolean moveUp = false;
	private boolean moveDown = false;
	private boolean moveLeft = false;
	private boolean moveRight = false;
	private boolean special = false;
	private boolean attack = false;
	private boolean sprint = false;
	private boolean block = false;
	private boolean startBlock = false;
	private boolean changeSprint = false;
	private boolean spinAttack = false;
	
	private boolean attackReleased = false;
	
	private float idleTime;
	private float timeTillIdleAnimation;
	private float attackHeldTime;
	private float timeTillSpinAttack;
	
	private MovingDirection jumpDirection;
	private MovingDirection lastMoveDirection;
	
	private InputContext inputContext;
	
	private boolean spinAttackCharged;
	
	private MovingDirection lastInputActionDirection = MovingDirection.NONE;
	private float lastInputActionDelta = 0f;
	
	public CharacterInputProcessor(Dwarf player) {
		this.player = player;
		timeTillIdleAnimation = TIME_TILL_IDLE_ANIMATION;
		timeTillSpinAttack = TIME_TILL_SPIN_ATTACK;
		jumpDirection = MovingDirection.NONE;
		lastMoveDirection = MovingDirection.NONE;
		inputContext = InputManager.getInstance().getInputContext();
		inputContext.addListener(this);
	}
	
	public void handleInputs(float delta) {
		if (CutsceneHandler.getInstance().isCutsceneActive()) {
			resetInputFlags();
			return;
		}
		
		lastInputActionDelta += delta;
		readInputs(delta);
		
		boolean move = moveUp || moveDown || moveLeft || moveRight;
		boolean characterActionSet = false;
		
		if (spinAttackCharged) {
			player.propertiesDataHandler.reduceEndurance(SPIN_ATTACK_CHARGED_ENDURANCE_COSTS_PER_SECOND * delta);
			if (player.propertiesDataHandler.getEndurancePercentual() <= 0f) {
				// release the spin attack if the player runs out of endurance
				spinAttack = true;
				spinAttackCharged = false;
				attackHeldTime = 0f;
				attackReleased = true;
			}
		}
		
		if (player.isAlive()) {
			if (!characterActionSet && spinAttack) {
				//hit and shield hit can only be interrupted by spin attacks (to free from near enemies)
				if (player.action.isInterruptable() || player.action == CharacterAction.HIT || player.action == CharacterAction.SHIELD_HIT) {
					player.changeAction(CharacterAction.ATTACK_SPIN);
				}
			}
			if (!characterActionSet && attack) {
				if (player.action.isInterruptable()) {
					if (move && sprint) {
						lastMoveDirection = getDirectionFromInputs();
						jumpDirection = getDirectionFromInputs();
						characterActionSet = player.changeAction(CharacterAction.ATTACK_JUMP);
					}
					else {
						characterActionSet = player.changeAction(CharacterAction.ATTACK);
					}
				}
			}
			if (!characterActionSet && startBlock) {
				if (player.action.isInterruptable() && player.hasEnoughEnduranceToBlock()) {
					startBlock = false;
					characterActionSet = player.changeAction(CharacterAction.BLOCK);
				}
			}
			if (!characterActionSet && special) {
				if (player.action.isInterruptable()) {
					jumpDirection = getDirectionFromInputs();
					characterActionSet = player.executeSpecialAction();
				}
			}
			if (!characterActionSet && move) {
				lastMoveDirection = getDirectionFromInputs();
				if (block && (player.hasEnoughEnduranceToBlock() && startBlock //
						|| player.action == CharacterAction.BLOCK || player.action == CharacterAction.BLOCK_MOVE)) { // no endurance is needed to start moving while blocking
					if (player.action.isInterruptable() && player.action != CharacterAction.BLOCK_MOVE) {
						characterActionSet = player.changeAction(CharacterAction.BLOCK_MOVE);
					}
				}
				else {
					if (player.action.isInterruptable() && !player.action.isMoveBlocking() && player.action != CharacterAction.RUN) {
						characterActionSet = player.changeAction(CharacterAction.RUN);
					}
				}
			}
			else {
				if (player.action == CharacterAction.RUN || player.action == CharacterAction.BLOCK_MOVE) {
					if (block && (player.hasEnoughEnduranceToBlock() //
							|| player.action == CharacterAction.BLOCK_MOVE)) { // no endurance is needed if the block was already started
						characterActionSet = player.changeAction(CharacterAction.BLOCK);
					}
					else {
						characterActionSet = player.changeAction(CharacterAction.NONE);
					}
				}
			}
			
			if (player.isBlocking()) {
				if (player.action == CharacterAction.BLOCK) {
					player.propertiesDataHandler.reduceEnduranceForBlocking(delta);
				}
				else if (player.action == CharacterAction.BLOCK_MOVE) {
					player.propertiesDataHandler.reduceEnduranceForBlockMoving(delta);
				}
				if (!block || player.propertiesDataHandler.isExhausted()) {
					characterActionSet = player.changeAction(CharacterAction.NONE);
				}
			}
			
			if (player.action == CharacterAction.NONE) {
				sprint = false;
				idleTime += delta;
				
				if (idleTime > timeTillIdleAnimation) {
					if (player.action != CharacterAction.IDLE) {
						player.changeAction(CharacterAction.IDLE);
					}
					else if (player.isAnimationFinished()) {
						player.changeAction(CharacterAction.NONE);
						idleTime = 0;
					}
				}
			}
			else {
				idleTime = 0;
			}
		}
	}
	
	private void readInputs(float delta) {
		resetInputFlags();
		if (inputContext.isStateActive(INPUT_MOVE_UP)) {
			moveUp = true;
		}
		if (inputContext.isStateActive(INPUT_MOVE_DOWN)) {
			moveDown = true;
		}
		if (inputContext.isStateActive(INPUT_MOVE_LEFT)) {
			moveLeft = true;
		}
		if (inputContext.isStateActive(INPUT_MOVE_RIGHT)) {
			moveRight = true;
		}
		if (inputContext.isStateActive(INPUT_SPECIAL)) {
			special = true;
		}
		if (inputContext.isStateActive(INPUT_ATTACK)) {
			if (attackReleased) {
				attack = true;
				attackReleased = false;
			}
			
			attackHeldTime += delta;
			if (attackHeldTime >= timeTillSpinAttack && !spinAttackCharged) {
				spinAttackCharged = true;
				player.soundHandler.playSound(SOUND_SPIN_ATTACK_CHARGED);
			}
		}
		else {
			if (spinAttackCharged) {
				spinAttack = true;
				spinAttackCharged = false;
			}
			attackHeldTime = 0f;
			attackReleased = true;
		}
		if (inputContext.isStateActive(INPUT_BLOCK)) {
			block = true;
		}
		if (inputContext.isStateActive(INPUT_SPRINT)) {
			if (!changeSprint) {
				sprint = !sprint;
			}
			changeSprint = true;
		}
		else {
			changeSprint = false;
		}
		
		if (moveUp && moveDown) {
			moveUp = false;
			moveDown = false;
		}
		if (moveLeft && moveRight) {
			moveLeft = false;
			moveRight = false;
		}
	}
	
	private void resetInputFlags() {
		moveUp = false;
		moveDown = false;
		moveLeft = false;
		moveRight = false;
		attack = false;
		special = false;
		block = false;
		spinAttack = false;
		//attack is not reset, because the attackReleased flag does this
		//sprint is not reset here, but in the handleInputs method (when idle)
	}
	
	/**
	 * Get the current input direction (where horizontal directions have a higher priority than vertical directions)
	 * 
	 * @return The current direction from the inputs.
	 */
	private MovingDirection getDirectionFromInputs() {
		if (moveUp && moveRight) {
			return MovingDirection.UP_RIGHT;
		}
		if (moveUp && moveLeft) {
			return MovingDirection.UP_LEFT;
		}
		if (moveDown && moveRight) {
			return MovingDirection.DOWN_RIGHT;
		}
		if (moveDown && moveLeft) {
			return MovingDirection.DOWN_LEFT;
		}
		if (moveLeft) {
			return MovingDirection.LEFT;
		}
		if (moveRight) {
			return MovingDirection.RIGHT;
		}
		if (moveUp) {
			return MovingDirection.UP;
		}
		if (moveDown) {
			return MovingDirection.DOWN;
		}
		return MovingDirection.NONE;
	}
	
	public void move(float delta) {
		if (!player.action.isMoveBlocking()) {
			//reduce the endurance for sprinting before requesting the movement speed
			if (sprint) {
				//the endurance is increased at the same time (see CharacterAction.getEnduranceRecharge())
				player.propertiesDataHandler.reduceEnduranceForSprinting(delta);
				if (player.propertiesDataHandler.isExhausted()) {
					sprint = false;
				}
			}
			
			float moveSpeedPerDirection = getMovingSpeed();
			if ((moveUp || moveDown) && (moveLeft || moveRight)) {
				moveSpeedPerDirection = getMovingSpeed() * SQRT_0_5;
			}
			
			float speedX = 0;
			float speedY = 0;
			
			if (moveUp) {
				speedY = moveSpeedPerDirection;
			}
			if (moveDown) {
				speedY = -moveSpeedPerDirection;
			}
			if (moveLeft) {
				speedX = -moveSpeedPerDirection;
			}
			if (moveRight) {
				speedX = moveSpeedPerDirection;
			}
			move(speedX, speedY, delta);
		}
		else if (player.action == CharacterAction.JUMP || player.action == CharacterAction.ATTACK_JUMP) {
			float movingSpeed = getMovingSpeed();
			float speedX = 0;
			float speedY = 0;
			
			if (jumpDirection.isCombinedDirection()) {
				movingSpeed *= SQRT_0_5;
			}
			
			if (jumpDirection.containsDirection(MovingDirection.UP)) {
				speedY = movingSpeed;
			}
			if (jumpDirection.containsDirection(MovingDirection.DOWN)) {
				speedY = -movingSpeed;
			}
			if (jumpDirection.containsDirection(MovingDirection.LEFT)) {
				speedX = -movingSpeed;
			}
			if (jumpDirection.containsDirection(MovingDirection.RIGHT)) {
				speedX = movingSpeed;
			}
			move(speedX, speedY, delta);
		}
		else if (player.action == CharacterAction.BLOCK) {
			if (moveLeft) {
				lastMoveDirection = MovingDirection.LEFT;
			}
			if (moveRight) {
				lastMoveDirection = MovingDirection.RIGHT;
			}
		}
	}
	
	private void move(float speedX, float speedY, float delta) {
		player.move(speedX * delta, speedY * delta);
	}
	
	private float getMovingSpeed() {
		float speed;
		speed = MOVING_SPEED;
		if (sprint) {
			speed = MOVING_SPEED_SPRINT;
		}
		if (player.action == CharacterAction.ATTACK) {
			speed = MOVING_SPEED_ATTACK;
		}
		if (player.action == CharacterAction.JUMP) {
			speed = MOVING_SPEED_JUMP;
		}
		if (player.action == CharacterAction.BLOCK_MOVE) {
			speed = MOVING_SPEED_BLOCK;
		}
		
		return speed;
	}
	
	public boolean isDrawDirectionRight() {
		return lastMoveDirection.isDrawingDirectionRight();
	}
	
	public MovingDirection getMovingDirection() {
		return lastMoveDirection;
	}
	
	public void setMovingDirection(Vector2 moveDirection) {
		if (moveDirection.len2() > 0) {
			final float angle = moveDirection.angleDeg();
			final float mid = 22.5f;
			if (angle < mid) {
				lastMoveDirection = MovingDirection.RIGHT;
			}
			else if (angle < 90f - mid) {
				lastMoveDirection = MovingDirection.UP_RIGHT;
			}
			else if (angle < 135f - mid) {
				lastMoveDirection = MovingDirection.UP;
			}
			else if (angle < 180f - mid) {
				lastMoveDirection = MovingDirection.UP_LEFT;
			}
			else if (angle < 225f - mid) {
				lastMoveDirection = MovingDirection.LEFT;
			}
			else if (angle < 270f - mid) {
				lastMoveDirection = MovingDirection.DOWN_LEFT;
			}
			else if (angle < 315f - mid) {
				lastMoveDirection = MovingDirection.DOWN;
			}
			else if (angle < 360f - mid) {
				lastMoveDirection = MovingDirection.DOWN_RIGHT;
			}
			else {
				lastMoveDirection = MovingDirection.RIGHT;
			}
		}
	}
	
	@Override
	public boolean onAction(String action, Type type, Parameters parameters) {
		if (type == Type.KEY_DOWN || type == Type.CONTROLLER_BUTTON_PRESSED || type == Type.BUTTON_PRESSED || type == Type.CONTROLLER_AXIS_THRESHOLD_PASSED) {
			if (action.equals(ACTION_INTERACT)) {
				InteractionManager.getInstance().interact(player.getPosition());
			}
			else if (action.equals(ACTION_BLOCK)) {
				startBlock = true;
			}
			else if (action.equals(ACTION_PREVIOUS_SPECIAL_ACTION)) {
				selectNextSpecialAction(-1);
			}
			else if (action.equals(ACTION_NEXT_SPECIAL_ACTION)) {
				selectNextSpecialAction(1);
			}
			else if (action.equals(ACTION_UP)) {
				handleSprintByInputAction(MovingDirection.UP);
			}
			else if (action.equals(ACTION_DOWN)) {
				handleSprintByInputAction(MovingDirection.DOWN);
			}
			else if (action.equals(ACTION_LEFT)) {
				handleSprintByInputAction(MovingDirection.LEFT);
			}
			else if (action.equals(ACTION_RIGHT)) {
				handleSprintByInputAction(MovingDirection.RIGHT);
			}
		}
		return false;
	}
	
	private void selectNextSpecialAction(int delta) {
		if (delta != 1 && delta != -1) {
			throw new IllegalArgumentException("delta must be 1 or -1");
		}
		
		SpecialAction specialAction = SpecialAction.getNextSpecialAction(player.getActiveSpecialAction(), delta);
		while (!specialAction.canBeUsed()) {
			//search on till a special action can be used (should not be an infinite loop, since the jump action can always be used)
			specialAction = SpecialAction.getNextSpecialAction(specialAction, delta);
		}
		
		player.setActiveSpecialAction(specialAction);
	}
	
	private void handleSprintByInputAction(MovingDirection direction) {
		if (moveUp || moveDown || moveLeft || moveRight) {
			//don't activate the sprint when the player is already moving (in a different direction)
			return;
		}
		
		if (lastInputActionDirection == direction && lastInputActionDelta < SPRINT_INPUT_ACTIONS_MAX_DELTA) {
			sprint = true;
		}
		lastInputActionDirection = direction;
		lastInputActionDelta = 0f;
	}
}
