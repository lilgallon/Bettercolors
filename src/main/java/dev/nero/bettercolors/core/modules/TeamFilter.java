package dev.nero.bettercolors.core.modules;

import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.option.Option;

import java.util.ArrayList;

public class TeamFilter extends Module {

    private static TeamFilter instance;

    /**
     * @param toggleKey   the toggle Key (-1 -> none).
     * @param isActivated the initial state.
     */
    public TeamFilter(Integer toggleKey, Boolean isActivated) {
        super("Team Filter", "If enabled, all the modules will make sure that the targeted entity is not in your team", toggleKey, isActivated, "unknwon.png", "TF");
        instance = this;
    }

    public static TeamFilter getInstance() {
        return instance;
    }

    /**
     * Used by the engine (reflection)
     */
    public static ArrayList<Option> getDefaultOptions(){
        return new ArrayList<>();
    }

}
