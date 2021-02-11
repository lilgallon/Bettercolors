package dev.nero.bettercolors.core.modules;

import dev.nero.bettercolors.core.events.EventType;
import dev.nero.bettercolors.core.wrapper.Wrapper;
import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.option.Option;
import dev.nero.bettercolors.engine.option.ValueOption;
import dev.nero.bettercolors.engine.utils.MathUtils;
import dev.nero.bettercolors.engine.utils.TimeHelper;
import dev.nero.bettercolors.engine.view.Window;
import net.minecraft.client.settings.KeyBinding;

import java.util.ArrayList;
import java.util.Map;

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

        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, SNEAK_DELAY_MIN, DESC_SNEAK_DELAY_MIN, 140, 50, 1000, 25, 50));
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, SNEAK_DELAY_MAX, DESC_SNEAK_DELAY_MAX, 180, 100, 1000, 25, 50));
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, SNEAK_TIME, DESC_SNEAK_TIME, 350, 50, 1000, 25, 50));
    }

    // Utility attributes
    private final TimeHelper sneakDelay; // used to keep track of the time between each sneak
    private final TimeHelper sneakDuration; // used to keep track of the sneak duration
    private boolean forceSneak = false;

    public SpeedBridging(Integer toggleKey, Boolean isActivated, Map<String, String> givenOptions) {
        super("Speed bridging", DESCRIPTION, toggleKey, isActivated, "bridge.png", PREFIX, true);
        this.loadOptionsAccordingTo(DEFAULT_OPTIONS, givenOptions);

        this.sneakDelay = new TimeHelper();
        this.sneakDelay.start();

        this.sneakDuration = new TimeHelper();
        this.sneakDuration.start();
    }

    @Override
    protected void onToggle(boolean toggle, boolean isTriggeredByKeybind) {
        if (toggle) {
            this.sneakDelay.start();
        }
    }

    @Override
    protected void onOptionChange(Option option, Object oldValue) {
        if (option.getName().equals(SNEAK_DELAY_MIN)) {
            // If the min is greater than the max, then set the max to min +1
            if (((ValueOption) option).getVal() > this.getOptionI(I_SNEAK_DELAY_MAX)) {
                ((ValueOption) this.options.get(I_SNEAK_DELAY_MAX)).setVal(((ValueOption) option).getVal() + 1);
                Window.getInstance().synchronizeComponents();
            }
        } else if (option.getName().equals(SNEAK_DELAY_MAX)) {
            // If the max is less than the min, then set the min to max -1
            if (((ValueOption) option).getVal() < this.getOptionI(I_SNEAK_DELAY_MIN)) {
                ((ValueOption) this.options.get(I_SNEAK_DELAY_MIN)).setVal(((ValueOption) option).getVal() - 1);
                Window.getInstance().synchronizeComponents();
            }
        }
    }

    @Override
    protected void onEvent(int code, Object details) {
        if (!this.isActivated()) return;
        if (Wrapper.MC.thePlayer == null) return;
        if (Wrapper.isInGui()) return;

        if (code == EventType.CLIENT_TICK) {
            int randomDelay = MathUtils.random(getOptionI(I_SNEAK_DELAY_MIN), getOptionI(I_SNEAK_DELAY_MAX));


            if (this.sneakDelay.isStopped()) {
                if (this.sneakDuration.isDelayComplete(getOptionI(I_SNEAK_TIME))) {
                    this.sneakDuration.stop();
                    this.sneakDelay.start();

                    this.forceSneak = false;
                }
            } else if (sneakDelay.isDelayComplete(randomDelay)) {
                this.sneakDelay.stop();
                this.sneakDuration.start();

                this.forceSneak = true;
            }
        }

        if (this.forceSneak) {
            KeyBinding.setKeyBindState(Wrapper.MC.gameSettings.keyBindSneak.getKeyCode(), true);
        } else {
            KeyBinding.setKeyBindState(Wrapper.MC.gameSettings.keyBindSneak.getKeyCode(), false);
        }
    }

    /**
     * Used by the engine (reflection)
     */
    public static ArrayList<Option> getDefaultOptions(){
        return DEFAULT_OPTIONS;
    }
}
