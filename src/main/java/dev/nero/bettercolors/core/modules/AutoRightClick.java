package dev.nero.bettercolors.core.modules;

import dev.nero.bettercolors.core.events.EventType;
import dev.nero.bettercolors.core.wrapper.Wrapper;
import dev.nero.bettercolors.engine.option.Option;
import dev.nero.bettercolors.engine.option.ValueFloatOption;
import dev.nero.bettercolors.engine.utils.MathUtils;
import dev.nero.bettercolors.engine.utils.TimeHelper;

import java.util.ArrayList;
import java.util.Map;

public class AutoRightClick extends BetterModule {

    // Prefix for AimAssistance (logging and settings)
    private static final String PREFIX = "ARC";

    // Description
    private static final String DESCRIPTION = "Automatically clicks for you when pressing the right click mouse button";

    // Options name
    private static final String CPS_MIN = "Clicks_per_seconds_(min)";
    private static final String CPS_MAX = "Clicks_per_seconds_(max)";

    // Options index
    private static final int I_CPS_MIN = 0;
    private static final int I_CPS_MAX = 0;

    // Options description
    private static final String DESC_CPS = "Defines the number of clicks per second the module should produce (between min and max)";

    // Default options loading
    private static final ArrayList<Option> DEFAULT_OPTIONS;
    static {
        DEFAULT_OPTIONS = new ArrayList<>();

        DEFAULT_OPTIONS.add(new ValueFloatOption(PREFIX, CPS_MIN, DESC_CPS, 5, 1, 15, 1, 5));
        DEFAULT_OPTIONS.add(new ValueFloatOption(PREFIX, CPS_MAX, DESC_CPS, 8, 1, 15, 1, 5));
    }

    // Modules variables
    private final TimeHelper clickDelay;
    private boolean startedAlready = false;

    public AutoRightClick(Integer toggleKey, Boolean isActivated, Map<String, String> givenOptions) {
        super("AutoRightClick", DESCRIPTION, toggleKey, isActivated, "click.png", PREFIX);
        this.loadOptionsAccordingTo(DEFAULT_OPTIONS, givenOptions);

        clickDelay = new TimeHelper();
        clickDelay.start();
    }

    @Override
    protected void onEvent(int code, Object details) {
        if (!this.isActivated()) return;
        if (Wrapper.MC.thePlayer == null) return;
        if (Wrapper.isInGui()) return;

        if (code == EventType.CLIENT_TICK) {
            // Those two if statements prevent clicking right after the first right click (the real one)
            if (playerHoldingUse() && !startedAlready) {
                clickDelay.start();
                startedAlready = true;
            } else if (!playerHoldingUse()){
                startedAlready = false;
            }

            long randomCps = MathUtils.random((int) (getOptionF(I_CPS_MIN)), (int) (getOptionF(I_CPS_MAX)));

            if (randomCps != 0) {
                if (playerHoldingUse() && clickDelay.isDelayComplete(1000 / randomCps)) {
                    this.ignoreNextRightClick();
                    Wrapper.click(65, false);
                    clickDelay.reset();
                }
            }
        }
    }

    /**
     * Used by the engine (reflection)
     */
    public static ArrayList<Option> getDefaultOptions(){
        return DEFAULT_OPTIONS;
    }
}
