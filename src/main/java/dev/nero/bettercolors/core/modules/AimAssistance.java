package dev.nero.bettercolors.core.modules;

import dev.nero.bettercolors.core.events.EventType;
import dev.nero.bettercolors.core.wrapper.Wrapper;
import dev.nero.bettercolors.engine.option.Option;
import dev.nero.bettercolors.engine.option.ToggleOption;
import dev.nero.bettercolors.engine.option.ValueFloatOption;
import dev.nero.bettercolors.engine.option.ValueOption;
import dev.nero.bettercolors.engine.utils.TimeHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AimAssistance extends BetterModule {

    // Prefix for AimAssistance (logging and settings)
    private static final String PREFIX = "AA";

    // Description
    private static final String DESCRIPTION = "Helps you aiming while fighting";

    // Options name
    private static final String STOP_ON_RIGHT_CLICK = "Stop_on_right_click";
    private static final String USE_ON_MOBS = "Use_on_mobs";
    private static final String STOP_WHEN_REACHED = "Stop_when_reached";
    private static final String STICKY = "Sticky";
    private static final String STEP_X = "Force_X";
    private static final String STEP_Y = "Force_Y";
    private static final String RANGE = "Range";
    private static final String RADIUS_X = "Fov_X";
    private static final String RADIUS_Y = "Fov_Y";
    private static final String DURATION = "Duration";
    private static final String CPS_TO_ACTIVATE = "CPS_to_activate";

    // Options index
    private static final int I_STOP_ON_RIGHT_CLICK = 0;
    private static final int I_USE_ON_MOBS = 1;
    private static final int I_STOP_WHEN_REACHED = 2;
    private static final int I_STICKY = 3;
    private static final int I_STEP_X = 4;
    private static final int I_STEP_Y = 5;
    private static final int I_RANGE = 6;
    private static final int I_RADIUS_X = 7;
    private static final int I_RADIUS_Y = 8;
    private static final int I_DURATION = 9;
    private static final int I_CPS_TO_ACTIVATE = 10;

    // Options description
    private static final String DESC_STOP_ON_RIGHT_CLICK = "if enabled, stops the assistance when you right click (it does not turn the module off though).";
    private static final String DESC_USE_ON_MOBS = "If enabled, it will help you while fighting mobs";
    private static final String DESC_STOP_WHEN_REACHED = "If enabled, the assistance only works when you're not aiming at an entity";
    private static final String DESC_STICKY = "If enabled, the assistance only works when you're aiming an entity ";
    private static final String DESC_STEP_X = "The force of the assistance horizontally";
    private static final String DESC_STEP_Y = "The force of the assistance vertically";
    private static final String DESC_RANGE = "The range of the aim assistance";
    private static final String DESC_RADIUS_X = "The assistance will only work if you're less than <?> degrees (horizontally) from an entity";
    private static final String DESC_RADIUS_Y = "The assistance will only work if you're less than <?> degrees (vertically) from an entity";
    private static final String DESC_DURATION = "When the aim assistance detects that you're fighting, how much time should it help you (in milliseconds)";
    private static final String DESC_CPS_TO_ACTIVATE = "If your CPS are higher or equal to that value, then it turns the assistance on (detects that you're fighting)";

    // Default options loading
    private static final ArrayList<Option> DEFAULT_OPTIONS;
    static{
        DEFAULT_OPTIONS = new ArrayList<>();

        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, STOP_ON_RIGHT_CLICK, DESC_STOP_ON_RIGHT_CLICK, false));
        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, USE_ON_MOBS, DESC_USE_ON_MOBS, false));
        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, STOP_WHEN_REACHED, DESC_STOP_WHEN_REACHED, true));
        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, STICKY, DESC_STICKY, false));

        DEFAULT_OPTIONS.add(new ValueFloatOption(PREFIX, STEP_X, DESC_STEP_X, 2.0f, 0, 7, 0.1f, 1));
        DEFAULT_OPTIONS.add(new ValueFloatOption(PREFIX, STEP_Y, DESC_STEP_Y, 2.0f, 0, 7, 0.1f, 1));
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, RANGE, DESC_RANGE, 3, 0, 10, 1, 5));
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, RADIUS_X, DESC_RADIUS_X, 25, 0, 180, 5, 25));
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, RADIUS_Y, DESC_RADIUS_Y, 30, 0, 90, 3, 15));
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, DURATION, DESC_DURATION, 2000, 0, 10000, 200, 1000));

        DEFAULT_OPTIONS.add(new ValueFloatOption(PREFIX, CPS_TO_ACTIVATE, DESC_CPS_TO_ACTIVATE, 4, 0, 10, 1, 5));
    }

    private Entity target;
    private boolean assist;
    private int attackCount;
    private final TimeHelper attackTimer;
    private final TimeHelper activationTimer;

    /**
     * @param toggleKey the toggle key (-1 -> none)
     * @param IsActivated the initial state
     * @param givenOptions the options for the mod
     */
    public AimAssistance(Integer toggleKey, Boolean IsActivated, Map<String, String> givenOptions) {
        super("Aim assistance", DESCRIPTION, toggleKey, IsActivated, "magnet.png", PREFIX);
        this.loadOptionsAccordingTo(DEFAULT_OPTIONS, givenOptions);

        this.target = null;
        this.assist = false;
        this.attackCount = 0;

        this.attackTimer = new TimeHelper();
        this.attackTimer.stop();

        this.activationTimer = new TimeHelper();
        this.activationTimer.stop();
    }

    @Override
    protected void onOptionChange(Option option, Object oldValue) {
        if (option.getName().equals(STICKY) && getOptionB(I_STOP_WHEN_REACHED) ||
                option.getName().equals(STOP_WHEN_REACHED) && getOptionB(I_STICKY)) {
            this.logWarn("Sticky and stop when reached options should not be used together");
        }
    }

    @Override
    protected void onEvent(int code, Object details) {
        if (!this.isActivated()) return;
        if (Wrapper.MC.player == null) return;
        if (Wrapper.isInGui()) return;

        switch (code) {
            case EventType.RENDER:
                assistIfPossible();
                break;

            case EventType.MOUSE_INPUT:
                if (this.playerUses() && this.getOptionB(I_STOP_ON_RIGHT_CLICK)) {
                    this.stop();
                }
                break;

            case EventType.CLIENT_TICK:
                if (this.assist) analyseEnvironment();
                analyseBehaviour();
                break;
        }
    }

    /**
     * This function analyses the player's environment to know what they're aiming at.
     */
    private void analyseEnvironment() {
        // Settings
        final int RANGE = ((ValueOption) this.options.get(I_RANGE)).getVal();
        final Class<? extends Entity> ENTITY_TYPE = (this.getOptionB(I_USE_ON_MOBS) ? LivingEntity.class : Player.class);

        // Get all entities around the player
        List<Entity> entities = Wrapper.getEntitiesAroundPlayer(RANGE, ENTITY_TYPE);

        // Get the closest one to the crosshair
        Entity closest = Wrapper.getClosestEntityToCrosshair(entities, true);

        if (closest != null) {
            this.target = closest;
        }
    }

    /**
     * This function analyzes the player's behaviour to know if the aim assistance should be turned on or not. It should
     * be called (at least) at every game tick because it checks timers
     */
    private void analyseBehaviour() {
        // Settings
        final float SPEED_TO_ACTIVATE = this.getOptionF(I_CPS_TO_ACTIVATE) / 1000f;
        final int ACTIVATION_DURATION = this.getOptionI(I_DURATION);
        final int TEST_DURATION = 1500; // ms: time to check if the speed is reached

        boolean playerAttacks = this.playerAttacks();

        // First time that the player attacks
        if (this.attackCount == 0 && playerAttacks) {
            this.attackCount += 1;
            this.attackTimer.start();
        }
        // If it's not the first time that the player attacked
        else if (this.attackCount > 0 && playerAttacks) {
            this.attackCount += 1;

            // Calculate the number of attacks per miliseconds
            float speed = (float) this.attackCount / (float) this.attackTimer.getTimeElapsed();

            // If player's attack speed is greater than the speed given to toggle the assistance, then we can tell to
            // the instance that the player is interacting
            if (speed > SPEED_TO_ACTIVATE) {
                // We need to reset the variables that are used to define if the player is interacting because we know
                // that the user is interacting right now
                this.attackCount = 0;
                this.attackTimer.stop();

                this.assist = true;
                this.activationTimer.start(); // it will reset if already started, so we're all good
            }
        }
        // If the player did not attack for that period of time, we give up and reset everything
        else if (this.attackTimer.isDelayComplete(TEST_DURATION)) {
            this.attackTimer.stop();
            this.attackCount = 0;
        }

        // Stop the interaction once that the delay is reached
        if (this.activationTimer.isDelayComplete(ACTIVATION_DURATION)) {
            this.stop();
        }
    }

    /**
     * This function will move the player's crosshair. The faster this function is called, the smoother the aim
     * assistance is.
     */
    private void assistIfPossible() {
        // Assist the player by taking into account this.target, only if it's valid
        if (this.target != null && this.assist) {
            boolean isAimingEntity = false;
            if(Wrapper.MC.hitResult != null) {
                isAimingEntity = Wrapper.MC.hitResult.getType() == HitResult.Type.ENTITY;
            }

            // Settings
            final boolean stopWhenReached = this.getOptionB(I_STOP_WHEN_REACHED);
            final boolean sticky = this.getOptionB(I_STICKY);
            final float FOV_X = this.getOptionI(I_RADIUS_X);
            final float FOV_Y = this.getOptionI(I_RADIUS_Y);
            final float FORCE_X = this.getOptionF(I_STEP_X);
            final float FORCE_Y = this.getOptionF(I_STEP_Y);

            if (stopWhenReached && isAimingEntity) return; // stopWhenReached -> aim if not on entity
            if (sticky && !isAimingEntity) return; // sticky -> aim if on entity

            final float[] rotations = Wrapper.getRotationsNeeded(
                    target,
                    FOV_X, FOV_Y,
                    FORCE_X, FORCE_Y
            );

            Wrapper.setRotations(rotations[0], rotations[1]);
        }
    }

    /**
     * Stops the assistance
     */
    private void stop() {
        this.assist = false;
        this.target = null;
        this.activationTimer.stop();
    }

    /**
     * Used by the engine (reflection)
     */
    public static ArrayList<Option> getDefaultOptions(){
        return DEFAULT_OPTIONS;
    }
}
