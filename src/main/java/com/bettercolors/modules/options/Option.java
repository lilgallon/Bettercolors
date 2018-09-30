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

        while (!found && i < options.size()){
            if(options.get(i).getName().equalsIgnoreCase(option_name)){
                index = i;
                found = true;
            }else{
                i ++;
            }
        }

        return index;
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
        if(obj instanceof String){
            String str = (String)obj;
            return str.equalsIgnoreCase(NAME);
        }
        return obj == this;
    }
}
