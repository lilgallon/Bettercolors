/*
 * Copyright 2018-2020
 * - Bettercolors Contributors (https://github.com/N3ROO/Bettercolors) and
 * - Bettercolors Engine Contributors (https://github.com/N3ROO/BettercolorsEngine)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.nero.bettercolors.engine.io;

import dev.nero.bettercolors.engine.option.Option;
import dev.nero.bettercolors.engine.option.ToggleOption;
import dev.nero.bettercolors.engine.option.ValueFloatOption;
import dev.nero.bettercolors.engine.option.ValueOption;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingsUtils {

    // The filename where we store the settings file that is selected by the user
    public final static String FILE_WITH_CURRENT_SETTINGS_USED = "_bc_settingsfile";

    // The settings file used. It will be used by SettingsUtils when using the functions
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
                    options.put(option.getCompleteName(), Integer.toString(((ValueOption) option).getVal()));
                } else if (option instanceof ToggleOption) {
                    options.put(option.getCompleteName(), Boolean.toString(((ToggleOption) option).isActivated()));
                } else if (option instanceof ValueFloatOption) {
                    options.put(option.getCompleteName(), Float.toString(((ValueFloatOption) option).getVal()));
                }
            }
        }

        PropertiesFiler settings = new PropertiesFiler(SETTINGS_FILENAME);
        settings.write(options, only_absents);
    }

    /**
     * @return all the options in the configuration file.
     */
    public static Map<String, String> getOptions(){
        PropertiesFiler settings = new PropertiesFiler(SETTINGS_FILENAME);
        return settings.readAll();
    }

    /**
     * @param name name of the option.
     * @return the value of the option.
     */
    public static String getOption(String name){
        PropertiesFiler settings = new PropertiesFiler(SETTINGS_FILENAME);
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

        PropertiesFiler settings = new PropertiesFiler(SETTINGS_FILENAME);
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
            if(file.isFile() && file.getName().endsWith(".properties") && file.getName().startsWith("bc_")){
                String name = file.getName().replaceFirst("bc_", "");
                name = name.replace(".properties", "");
                filenames.addElement(name);
            }
        }

        return filenames;
    }
}