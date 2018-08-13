package com.bettercolors.modules;

import com.bettercolors.modules.options.Option;
import com.bettercolors.modules.options.ToggleOption;
import com.bettercolors.modules.options.ValueOption;

import java.util.ArrayList;
import java.util.Map;

public class AimAssistance extends Module {
    public static final String STOP_ON_RIGHT_CLICK = "Stop_on_right_click";
    public static final String USE_ON_MOBS = "Use_on_mobs";
    public static final String TEAM_FILTER = "Team_filter";
    public static final String REFRESH_RATE = "Refresh_rate";
    public static final String STEP_X = "Step_X";
    public static final String STEP_Y = "Step_Y";
    public static final String RANGE = "Range";
    public static final String RADIUS_X = "Radius_X";
    public static final String RADIUS_Y = "Radius_Y";

    private static final ArrayList<Option> DEFAULT_OPTIONS;
    static{
        DEFAULT_OPTIONS = new ArrayList<>();

        DEFAULT_OPTIONS.add(new ToggleOption(STOP_ON_RIGHT_CLICK, true));
        DEFAULT_OPTIONS.add(new ToggleOption(USE_ON_MOBS, false));
        DEFAULT_OPTIONS.add(new ToggleOption(TEAM_FILTER, true));

        DEFAULT_OPTIONS.add(new ValueOption(REFRESH_RATE, 200, 0, 1000, 50, 200));
        DEFAULT_OPTIONS.add(new ValueOption(STEP_X, 6, 0, 20, 1, 5));
        DEFAULT_OPTIONS.add(new ValueOption(STEP_Y, 10, 0, 20, 1, 5));
        DEFAULT_OPTIONS.add(new ValueOption(RANGE, 5, 0, 10, 1, 5));
        DEFAULT_OPTIONS.add(new ValueOption(RADIUS_X, 60, 0, 180, 5, 20));
        DEFAULT_OPTIONS.add(new ValueOption(RADIUS_Y, 30, 0, 90, 5, 10));
    }

    public AimAssistance(String name, int toggle_key, boolean is_activated, Map<String, String> options) {

        super(name, toggle_key, is_activated);

        _options = DEFAULT_OPTIONS;
        ((ToggleOption) _options.get(0)).setActivated(Boolean.parseBoolean(options.get(STOP_ON_RIGHT_CLICK)));
        ((ToggleOption) _options.get(1)).setActivated(Boolean.parseBoolean(options.get(USE_ON_MOBS)));
        ((ToggleOption) _options.get(2)).setActivated(Boolean.parseBoolean(options.get(TEAM_FILTER)));
        ((ValueOption) _options.get(3)).setVal(Integer.parseInt(options.get(REFRESH_RATE)));
        ((ValueOption) _options.get(4)).setVal(Integer.parseInt(options.get(STEP_X)));
        ((ValueOption) _options.get(5)).setVal(Integer.parseInt(options.get(STEP_Y)));
        ((ValueOption) _options.get(6)).setVal(Integer.parseInt(options.get(RANGE)));
        ((ValueOption) _options.get(7)).setVal(Integer.parseInt(options.get(RADIUS_X)));
        ((ValueOption) _options.get(8)).setVal(Integer.parseInt(options.get(RADIUS_Y)));
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
