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

package dev.nero.bettercolors.engine.option;

import java.util.ArrayList;

public abstract class Option{

    final String PREFIX;
    final String NAME;

    Option(String prefix, String name){
        PREFIX = prefix ;
        NAME = name;
    }

    /**
     * That is an utility method that returns ONLY the ToggleOption instances from the options variable. In brief, it
     * filters the ToggleOption objects.
     * @param options an array containing objects that extend the option class.
     * @return an array containing all the ToggleOption instances of the given array.
     */
    public static ArrayList<ToggleOption> getToggleOptions(ArrayList<Option> options){
        ArrayList<ToggleOption> toggleOptions = new ArrayList<>();
        for (Option option : options) {
            if (option instanceof  ToggleOption) {
                toggleOptions.add((ToggleOption) option);
            }
        }
        return toggleOptions;
    }

    /**
     * That is an utility method that returns ONLY the ValueOption instances from the options variable. In brief, it
     * filters the ValueOption objects.
     * @param options an array containing objects that extend the option class.
     * @return an array containing all the ValueOption instances of the given array.
     */
    public static ArrayList<ValueOption> getValueOptions(ArrayList<Option> options){
        ArrayList<ValueOption> valueOptions = new ArrayList<>();
        for (Option option : options) {
            if (option instanceof  ValueOption) {
                valueOptions.add((ValueOption) option);
            }
        }
        return valueOptions;
    }

    /**
     * Need this function since ArrayList<Option>.indexOf(String) doesn't work whereas we have redefined the "equals"
     * function in Option class.
     * @param options the options list
     * @param option_name the option name to find in the list
     * @return the index of the option name in the list name (-1 if not found)
     */
    public static int getIndex(ArrayList<Option> options, String option_name){
        boolean found = false;
        int i = 0;
        int index = -1;

        while (!found && i < options.size()) {
            if (options.get(i).getCompleteName().equalsIgnoreCase(option_name)) {
                index = i;
                found = true;
            } else {
                i ++;
            }
        }

        return index;
    }

    /**
     * @return the complete name of the option "PREFIX_NAME".
     */
    public String getCompleteName(){
        if(PREFIX.equals("")) return NAME;
        else return PREFIX + "_" + NAME;
    }

    /**
     * @return the name of the option.
     */
    public String getName(){
        return NAME;
    }

    /**
     * It saves the option on the configuration file. It needs to be called every time the option is updated.
     */
    abstract void saveOption();

    @Override
    public boolean equals(Object obj){
        if(obj == null) return false;
        if(obj instanceof String) {
            String str = (String)obj;
            if(!PREFIX.equals(""))
                return str.equalsIgnoreCase(PREFIX + "_" + NAME);
            return str.equalsIgnoreCase(NAME);
        }
        return obj == this;
    }
}