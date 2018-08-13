package com.bettercolors.io;

import com.bettercolors.modules.AimAssistance;
import com.bettercolors.modules.ClickAssistance;
import com.bettercolors.modules.Module;
import com.bettercolors.modules.options.Option;
import com.bettercolors.modules.options.ToggleOption;
import com.bettercolors.modules.options.ValueOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingsUtils {
    // TODO
    private static String SETTINGS_FILENAME = "settings";


    public static void setOptions(ArrayList<ArrayList<Option>> modules_options, boolean only_absents){
        Map<String, String> options = new HashMap<>();
        for(ArrayList<Option> module_options : modules_options) {
            for (Option option : module_options) {
                if (option instanceof ValueOption) {
                    options.put(option.getName(), Integer.toString(((ValueOption) option).getVal()));
                } else if (option instanceof ToggleOption) {
                    options.put(option.getName(), Boolean.toString(((ToggleOption) option).isActivated()));
                }
            }
        }

        Filer settings = new Filer(SETTINGS_FILENAME);
        settings.write(options, only_absents);
    }

    public static Map<String, String> getOptions(){
        Filer settings = new Filer(SETTINGS_FILENAME);
        return settings.readAll();
    }

    public static String getOption(String name){
        Filer settings = new Filer(SETTINGS_FILENAME);
        return settings.read(name);
    }

    public static void setOption(String option, String value){
        Map<String, String> options = new HashMap<>();
        options.put(option, value);

        Filer settings = new Filer(SETTINGS_FILENAME);
        settings.write(options, false);
    }
}
