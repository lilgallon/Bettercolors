package com.bettercolors.modules.options;

import java.util.ArrayList;

public abstract class Option {

    final String NAME;

    Option(String name){
        NAME = name;
    }

    public String getName(){ return NAME; }

    abstract void saveOption();

    public static ArrayList<ToggleOption> getToggleOptions(ArrayList<Option> options){
        ArrayList<ToggleOption> toggleOptions = new ArrayList<>();
        for(Option option : options){
            if(option instanceof  ToggleOption){
                toggleOptions.add((ToggleOption) option);
            }
        }
        return toggleOptions;
    }

    public static ArrayList<ValueOption> getValueOptions(ArrayList<Option> options){
        ArrayList<ValueOption> valueOptions = new ArrayList<>();
        for(Option option : options){
            if(option instanceof  ValueOption){
                valueOptions.add((ValueOption) option);
            }
        }
        return valueOptions;
    }
}
