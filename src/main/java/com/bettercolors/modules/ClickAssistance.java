package com.bettercolors.modules;

import com.bettercolors.modules.options.Option;
import com.bettercolors.modules.options.ToggleOption;
import com.bettercolors.modules.options.ValueOption;

import java.util.ArrayList;
import java.util.Map;

public class ClickAssistance extends Module {

    private static final String PACKETS = "Packets";
    private static final String ONLY_ON_ENTITY = "Only_on_entity";
    private static final String USE_ON_MOBS = "Use_on_mobs";
    private static final String TEAM_FILTER = "Team_filter";
    private static final String ADDITIONAL_CLICKS = "Additional_clicks";
    private static final String CHANCE = "Chance";
    private static final String DURATION = "Duration";
    private static final String CLICKS_TO_ACTIVATE = "CA_Clicks_to_activate";
    private static final String TIME_TO_ACTIVATE = "CA_Time_to_activate";

    private static final ArrayList<Option> DEFAULT_OPTIONS;
    static{
        DEFAULT_OPTIONS = new ArrayList<>();

        DEFAULT_OPTIONS.add(new ToggleOption(PACKETS, false));
        DEFAULT_OPTIONS.add(new ToggleOption(ONLY_ON_ENTITY, false));
        DEFAULT_OPTIONS.add(new ToggleOption(USE_ON_MOBS, false));
        DEFAULT_OPTIONS.add(new ToggleOption(TEAM_FILTER, true));

        DEFAULT_OPTIONS.add(new ValueOption(ADDITIONAL_CLICKS, 2, 0, 5, 0, 1));
        DEFAULT_OPTIONS.add(new ValueOption(CHANCE, 80, 0, 100, 5, 25));
        DEFAULT_OPTIONS.add(new ValueOption(DURATION, 1500, 0, 10000, 200, 1000));
        DEFAULT_OPTIONS.add(new ValueOption(CLICKS_TO_ACTIVATE, 3, 0, 20, 1, 5));
        DEFAULT_OPTIONS.add(new ValueOption(TIME_TO_ACTIVATE, 1000, 0, 10000, 200, 1000));
    }

    public ClickAssistance(String name, int toggle_key, boolean is_activated, Map<String, String> options, String symbol) {

        super(name, toggle_key, is_activated, symbol);

        _options = DEFAULT_OPTIONS;
        ((ToggleOption) _options.get(0)).setActivated(Boolean.parseBoolean(options.get(PACKETS)));
        ((ToggleOption) _options.get(1)).setActivated(Boolean.parseBoolean(options.get(ONLY_ON_ENTITY)));
        ((ToggleOption) _options.get(2)).setActivated(Boolean.parseBoolean(options.get(USE_ON_MOBS)));
        ((ToggleOption) _options.get(3)).setActivated(Boolean.parseBoolean(options.get(TEAM_FILTER)));
        ((ValueOption) _options.get(4)).setVal(Integer.parseInt(options.get(ADDITIONAL_CLICKS)));
        ((ValueOption) _options.get(5)).setVal(Integer.parseInt(options.get(CHANCE)));
        ((ValueOption) _options.get(6)).setVal(Integer.parseInt(options.get(DURATION)));
        ((ValueOption) _options.get(7)).setVal(Integer.parseInt(options.get(CLICKS_TO_ACTIVATE)));
        ((ValueOption) _options.get(8)).setVal(Integer.parseInt(options.get(TIME_TO_ACTIVATE)));
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
