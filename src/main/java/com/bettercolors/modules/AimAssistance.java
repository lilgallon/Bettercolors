package com.bettercolors.modules;

import com.bettercolors.modules.options.Option;
import com.bettercolors.modules.options.ToggleOption;
import com.bettercolors.modules.options.ValueOption;

import java.util.ArrayList;

public class AimAssistance extends Module {

    private static final ArrayList<Option> DEFAULT_OPTIONS;
    static{
        DEFAULT_OPTIONS = new ArrayList<>();

        DEFAULT_OPTIONS.add(new ToggleOption("Stop_on_right_click", true));
        DEFAULT_OPTIONS.add(new ToggleOption("Use_on_mobs", false));
        DEFAULT_OPTIONS.add(new ToggleOption("Team_filter", true));

        DEFAULT_OPTIONS.add(new ValueOption("Refresh_rate", 200, 0, 1000, 50, 200));
        DEFAULT_OPTIONS.add(new ValueOption("Step_X", 6, 0, 20, 1, 5));
        DEFAULT_OPTIONS.add(new ValueOption("Step_Y", 10, 0, 20, 1, 5));
        DEFAULT_OPTIONS.add(new ValueOption("Range", 5, 0, 10, 1, 5));
        DEFAULT_OPTIONS.add(new ValueOption("Radius_X", 60, 0, 180, 5, 20));
        DEFAULT_OPTIONS.add(new ValueOption("Radius_Y", 30, 0, 90, 5, 10));
    }

    public AimAssistance(String name, int toggle_key, boolean is_activated,
                         boolean stop_on_right_click, boolean use_on_mobs, boolean team_filter,
                         int refresh_rate, int step_x, int step_y, int range, int radius_x, int radius_y) {

        super(name, toggle_key, is_activated);

        _options = DEFAULT_OPTIONS;
        ((ToggleOption) _options.get(0)).setActivated(stop_on_right_click);
        ((ToggleOption) _options.get(1)).setActivated(use_on_mobs);
        ((ToggleOption) _options.get(2)).setActivated(team_filter);
        ((ValueOption) _options.get(3)).setVal(refresh_rate);
        ((ValueOption) _options.get(4)).setVal(step_x);
        ((ValueOption) _options.get(5)).setVal(step_y);
        ((ValueOption) _options.get(6)).setVal(range);
        ((ValueOption) _options.get(7)).setVal(radius_x);
        ((ValueOption) _options.get(8)).setVal(radius_y);
    }

    @Override
    public void onUpdate() {

    }

    @Override
    void onEnable() {

    }

    @Override
    void onDisable() {

    }

    public static ArrayList<Option> getDefaultOptions(){
        return DEFAULT_OPTIONS;
    }
}
