package dev.nero.bettercolors.core.modules;

import dev.nero.bettercolors.core.events.EventType;
import dev.nero.bettercolors.core.wrapper.Wrapper;
import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.option.Option;
import dev.nero.bettercolors.engine.option.ValueOption;
import dev.nero.bettercolors.engine.utils.MathUtils;
import dev.nero.bettercolors.engine.utils.TimeHelper;

import java.util.ArrayList;

public class SpeedBridging extends Module {

    // Prefix for AimAssistance (logging and settings)
    private static final String PREFIX = "SB";

    // Description
    private static final String DESCRIPTION = "Automatically sneak when you're building bridges";

    // Options name
    private static final String SNEAK_DELAY_MIN = "Sneak_delay_min";
    private static final String SNEAK_DELAY_MAX = "Sneak_delay_max";
    private static final String SNEAK_TIME = "Sneak_time";

    // Options index
    private static final int I_SNEAK_DELAY_MIN = 0;
    private static final int I_SNEAK_DELAY_MAX = 1;
    private static final int I_SNEAK_TIME = 2;

    // Options description
    private static final String DESC_SNEAK_DELAY_MIN = "Defines the time delay to sneak (minimum)";
    private static final String DESC_SNEAK_DELAY_MAX = "Defines the time delay to sneak (maximum)";
    private static final String DESC_SNEAK_TIME = "Defines the duration of the sneak";

    // Default options loading
    private static final ArrayList<Option> DEFAULT_OPTIONS;
    static {
        DEFAULT_OPTIONS = new ArrayList<>();

        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, SNEAK_DELAY_MIN, DESC_SNEAK_DELAY_MIN, 100, 50, 1000, 25, 50));
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, SNEAK_DELAY_MAX, DESC_SNEAK_DELAY_MAX, 200, 100, 1000, 25, 50));
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, SNEAK_TIME, DESC_SNEAK_TIME, 100, 50, 1000, 25, 50));
    }

    // Utility attributes
    private final TimeHelper delay; // used to keep track of the time between each sneak
    private final TimeHelper timer; // used to keep track of the sneak duration
    private boolean forceSneak = false;

    protected SpeedBridging(Integer toggleKey, Boolean isActivated) {
        super("Speed bridging", DESCRIPTION, toggleKey, isActivated, "unknown.png", PREFIX);

        this.delay = new TimeHelper();
        this.delay.start();

        this.timer = new TimeHelper();
        this.timer.start();
    }

    @Override
    protected void onToggle(boolean toggle, boolean isTriggeredByKeybind) {
        if (toggle) {
            this.delay.start();
        }
    }

    @Override
    protected void onEvent(int code, Object details) {
        if (!this.isActivated()) return;
        if (Wrapper.MC.player == null) return;
        if (Wrapper.isInGui()) return;

        if (code == EventType.CLIENT_TICK) {
            int randomDelay = MathUtils.random(getOptionI(I_SNEAK_DELAY_MIN), getOptionI(I_SNEAK_DELAY_MAX));

            if (this.delay.isStopped()) {
                if (this.timer.isDelayComplete(getOptionI(I_SNEAK_TIME))) {
                    this.timer.stop();
                    this.delay.start();

                    this.forceSneak = true;
                }
            } if (delay.isDelayComplete(randomDelay)) {
                this.delay.stop();
                this.timer.start();

                this.forceSneak = false;
            }
        }

        if (this.forceSneak) {
            Wrapper.MC.player.setSneaking(true);
        }
    }

    /**
     * Used by the engine (reflection)
     */
    public static ArrayList<Option> getDefaultOptions(){
        return new ArrayList<>();
    }
}
