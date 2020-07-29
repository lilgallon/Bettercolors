package dev.nero.bettercolors.mod.modules;

import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.option.Option;
import dev.nero.bettercolors.engine.option.ValueOption;

import java.util.ArrayList;
import java.util.Map;

public class Reach extends Module {

    // Prefix for Reach (logging and settings)
    private static final String PREFIX = "REACH";

    // Options name
    private static final String REACH_OPTION_LABEL = "Reach_increment";

    // Options index
    private static final int I_REACH_INCREMENT = 0;

    private static final ArrayList<Option> DEFAULT_OPTIONS;
    static {
        DEFAULT_OPTIONS = new ArrayList<>();

        // 10 = 1 block, so 5 means 0.5 block
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, REACH_OPTION_LABEL, 5, 0, 20, 0, 1));
    }

    /**
     * @param toggleKey   the toggle Key (-1 -> none).
     * @param isActivated the initial state.
     * @param options the options for the mod
     */
    public Reach(Integer toggleKey, Boolean isActivated, Map<String, String> options) {
        super("Reach", toggleKey, isActivated, "click_symbol.png", "[" + PREFIX + "]");

        this.options = DEFAULT_OPTIONS;

        ((ValueOption)
            this.options.get(I_REACH_INCREMENT))
            .setVal(Integer.parseInt(options.get(this.options.get(I_REACH_INCREMENT).getCompleteName()))
        );
    }

    public float getReachIncrement() {
        return ((ValueOption) this.options.get(I_REACH_INCREMENT)).getVal() / 10.0f;
    }

    public static ArrayList<Option> getDefaultOptions(){
        return DEFAULT_OPTIONS;
    }
}
