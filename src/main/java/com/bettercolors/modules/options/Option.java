package com.bettercolors.modules.options;

public abstract class Option {

    private String _name;

    public Option(String name){
        _name = name;
    }

    public String getName(){ return _name; }
}
