package com.bettercolors.modules.options;

import java.util.ArrayList;

public abstract class Option {

    final String NAME;

    Option(String name){
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
        for(Option option : options){
            if(option instanceof  ToggleOption){
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
        for(Option option : options){
            if(option instanceof  ValueOption){
                valueOptions.add((ValueOption) option);
            }
        }
        return valueOptions;
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
}
