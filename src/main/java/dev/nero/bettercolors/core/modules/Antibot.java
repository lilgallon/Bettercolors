package dev.nero.bettercolors.core.modules;

import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.option.Option;
import net.minecraft.entity.LivingEntity;

import java.util.ArrayList;

public class Antibot extends Module {

    private static Antibot instance;

    // Prefix for Antibot (logging and settings)
    private static final String PREFIX = "ANTIBOT";

    private static final ArrayList<Option> DEFAULT_OPTIONS;
    static {
        DEFAULT_OPTIONS = new ArrayList<>();
    }

    /**
     * @param toggleKey   the toggle Key (-1 -> none).
     * @param isActivated the initial state.
     */
    public Antibot(Integer toggleKey, Boolean isActivated) {
        super("Antibot", toggleKey, isActivated, "shield.png", "AB");
    }

    public boolean isBot(LivingEntity e) {
        return false;
    }

    public static Antibot getInstance() {
        return instance;
    }

    /**
     * Used by the engine (reflection)
     */
    public static ArrayList<Option> getDefaultOptions(){
        return DEFAULT_OPTIONS;
    }
}
