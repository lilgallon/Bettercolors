package com.bettercolors.io;

import com.bettercolors.modules.options.Option;
import com.bettercolors.modules.options.ToggleOption;
import com.bettercolors.modules.options.ValueOption;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingsUtils {

    // TODO -> CHANGE TO NORMAL CLASS TO ADD THE POSSIBILITY TO CHANGE THE FILENAME FOR MULTIPLE CONFIGURATIONS
    // TODO: Edit : remove final modifier to be able to change it according to the selected filename (change utils to helper).
    public static String SETTINGS_FILENAME = "default";

    /**
     * It updates the configuration file with all the options given in [modules_options].
     * @param modules_options the options to update or create in the settings file.
     * @param only_absents if this variable is set to true, it will ONLY CREATE the options that were NOT ALREADY in the configuration file.
     */
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

    /**
     * @return all the options in the configuration file.
     */
    public static Map<String, String> getOptions(){
        Filer settings = new Filer(SETTINGS_FILENAME);
        return settings.readAll();
    }

    /**
     * @param name name of the option.
     * @return the value of the option.
     */
    public static String getOption(String name){
        Filer settings = new Filer(SETTINGS_FILENAME);
        return settings.read(name);
    }

    /**
     * Please note that if the option does not exist, it will create it.
     * @param option the option name.
     * @param value the new value for this option.
     */
    public static void setOption(String option, String value){
        Map<String, String> options = new HashMap<>();
        options.put(option, value);

        Filer settings = new Filer(SETTINGS_FILENAME);
        settings.write(options, false);
    }

    /**
     * @return all the settings filenames (without .properties extension)
     */
    public static DefaultListModel<String> getAllSettingsFilenames(){
        DefaultListModel<String> filenames = new DefaultListModel<>();
        File folder = Filer.getSettingsDirectory();
        File[] files = folder.listFiles();
        if(files == null) return filenames;
        for(File file : files){
            if(file.isFile() && file.getName().endsWith(".properties")){
                filenames.addElement(file.getName().replace(".properties", ""));
            }
        }
        return filenames;
    }
}
