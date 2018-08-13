package com.bettercolors.modules;

import com.bettercolors.modules.options.Option;
import com.bettercolors.modules.options.ToggleOption;
import com.bettercolors.modules.options.ValueOption;

import java.util.ArrayList;

public class ClickAssistance extends Module {

    private static final ArrayList<Option> DEFAULT_OPTIONS;
    static{
        DEFAULT_OPTIONS = new ArrayList<>();

        DEFAULT_OPTIONS.add(new ToggleOption("Packets", false));
        DEFAULT_OPTIONS.add(new ToggleOption("Only_on_entity", false));
        DEFAULT_OPTIONS.add(new ToggleOption("Use_on_mobs", false));
        DEFAULT_OPTIONS.add(new ToggleOption("Team_filter", true));

        DEFAULT_OPTIONS.add(new ValueOption("Additional_clicks", 2, 0, 5, 0, 1));
        DEFAULT_OPTIONS.add(new ValueOption("Chance", 80, 0, 100, 5, 20));
        DEFAULT_OPTIONS.add(new ValueOption("Duration_(ms)", 1500, 0, 10000, 250, 1000));
        DEFAULT_OPTIONS.add(new ValueOption("Clicks_to_activate", 3, 0, 20, 2, 5));
        DEFAULT_OPTIONS.add(new ValueOption("Time_to_activate", 1000, 0, 10000, 250, 1000));
    }

    public ClickAssistance(String name, int toggle_key, boolean is_activated,
                           boolean use_packets, boolean only_on_entity, boolean use_on_mobs, boolean team_filter,
                           int additional_clicks, int chance, int duration, int clicks_to_activate, int time_to_activate) {

        super(name, toggle_key, is_activated);

        _options = DEFAULT_OPTIONS;
        ((ToggleOption) _options.get(0)).setActivated(use_packets);
        ((ToggleOption) _options.get(1)).setActivated(only_on_entity);
        ((ToggleOption) _options.get(2)).setActivated(use_on_mobs);
        ((ToggleOption) _options.get(3)).setActivated(team_filter);
        ((ValueOption) _options.get(4)).setVal(additional_clicks);
        ((ValueOption) _options.get(5)).setVal(chance);
        ((ValueOption) _options.get(6)).setVal(duration);
        ((ValueOption) _options.get(7)).setVal(clicks_to_activate);
        ((ValueOption) _options.get(8)).setVal(time_to_activate);
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
