package dev.gallon.bettercolors.core.modules;

import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.option.Option;
import dev.nero.bettercolors.engine.option.ValueFloatOption;

import java.util.ArrayList;
import java.util.Map;

public class Reach extends Module {

    // Prefix for Reach (logging and settings)
    private static final String PREFIX = "REACH";

    // Description
    private static final String DESCRIPTION = "Increases your reach";

    // Options name
    private static final String CBT_REACH_OPTION_LABEL = "Combat_Reach_increment";
    private static final String BLK_REACH_OPTION_LABEL = "Block_Reach_increment";

    // Options index
    private static final int I_CBT_REACH_INCREMENT = 0;
    private static final int I_BLK_REACH_INCREMENT = 1;

    // Options description
    private static final String DESC_CBT_REACH_OPTION_LABEL = "The default combat reach is 3.0, how much more do you want?";
    private static final String DESC_BLK_REACH_OPTION_LABEL = "The default block reach is 4.5, how much more do you want?";

    private static final ArrayList<Option> DEFAULT_OPTIONS;
    static {
        DEFAULT_OPTIONS = new ArrayList<>();

        // 10 = 1 block, so 5 means 0.5 block
        DEFAULT_OPTIONS.add(
                new ValueFloatOption(PREFIX, CBT_REACH_OPTION_LABEL, DESC_CBT_REACH_OPTION_LABEL, 0.2f, 0.01f, 3.0f, 0.01f, 0.5f)
        );
        DEFAULT_OPTIONS.add(
                new ValueFloatOption(PREFIX, BLK_REACH_OPTION_LABEL, DESC_BLK_REACH_OPTION_LABEL, 0.2f, 0.01f, 1.0f, 0.01f, 0.5f)
        );
    }

    /**
     * @param toggleKey   the toggle Key (-1 -> none).
     * @param isActivated the initial state.
     * @param givenOptions the options for the mod
     */
    public Reach(Integer toggleKey, Boolean isActivated, Map<String, String> givenOptions) {
        super("Reach", DESCRIPTION, toggleKey, isActivated, "hit.png", PREFIX);
        this.loadOptionsAccordingTo(DEFAULT_OPTIONS, givenOptions);
    }

    @Override
    protected void onToggle(boolean toggle, boolean isTriggeredByKeybind) {
        if (toggle) {
            logWarn("Don't abuse of the extended reach. It can get you banned.");
        }
    }

    /**
     * @return the combat reach increment
     */
    public float getCombatReachIncrement() {
        return this.getOptionF(I_CBT_REACH_INCREMENT);
    }

    /**
     * @return the block reach increment
     */
    public float getBlockReachIncrement() {
        return this.getOptionF(I_BLK_REACH_INCREMENT);
    }

    /**
     * Used by the engine (reflection)
     */
    public static ArrayList<Option> getDefaultOptions(){
        return DEFAULT_OPTIONS;
    }
}
