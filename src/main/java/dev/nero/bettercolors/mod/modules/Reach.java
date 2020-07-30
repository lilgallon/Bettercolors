package dev.nero.bettercolors.mod.modules;

import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.option.Option;
import dev.nero.bettercolors.engine.option.ToggleOption;
import dev.nero.bettercolors.engine.option.ValueFloatOption;
import dev.nero.bettercolors.engine.option.ValueOption;
import dev.nero.bettercolors.engine.view.Window;

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
        DEFAULT_OPTIONS.add(
                new ValueFloatOption(PREFIX, REACH_OPTION_LABEL, 0.2f, 0.01f, 1.5f, 0.01f, 0.5f)
        );
    }

    /**
     * @param toggleKey   the toggle Key (-1 -> none).
     * @param isActivated the initial state.
     * @param givenOptions the options for the mod
     */
    public Reach(Integer toggleKey, Boolean isActivated, Map<String, String> givenOptions) {
        super("Reach", toggleKey, isActivated, "sword_symbol.png", "[" + PREFIX + "]");

        this.options = new ArrayList<>();

        for (Option defaultOption : DEFAULT_OPTIONS) {
            Option option = (Option) defaultOption.clone();
            String name = defaultOption.getCompleteName();

            if (option instanceof ToggleOption) {
                ((ToggleOption) option).setActivated(
                        Boolean.parseBoolean(givenOptions.get(name))
                );
            } else if (option instanceof ValueOption) {
                ((ValueOption) option).setVal(
                        Integer.parseInt(givenOptions.get(name))
                );
            } else if (option instanceof ValueFloatOption) {
                ((ValueFloatOption) option).setVal(
                        Float.parseFloat(givenOptions.get(name))
                );
            }

            this.options.add(option);
        }
    }

    @Override
    protected void onToggle(boolean toggle) {
        if (toggle) {
            Window.getInstance().dialog("Don't abuse of the extended reach. It can get you banned.");
        }
    }

    public float getReachIncrement() {
        return ((ValueFloatOption) this.options.get(I_REACH_INCREMENT)).getVal();
    }

    public static ArrayList<Option> getDefaultOptions(){
        return DEFAULT_OPTIONS;
    }
}
