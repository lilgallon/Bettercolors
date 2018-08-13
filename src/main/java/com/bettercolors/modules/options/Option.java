package com.bettercolors.modules.options;

import java.util.ArrayList;

public abstract class Option {

    String _name;

    public Option(String name){
        _name = name;
    }

    public String getName(){ return _name; }

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
