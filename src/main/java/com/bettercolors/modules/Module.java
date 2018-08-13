package com.bettercolors.modules;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public abstract class Module {

    // Utility
    private static Minecraft _mc = Minecraft.getMinecraft();
    private static Keyboard keyboard;

    // Module details
    private final String _name;

    // Module status
    private int _toggle_key;
    private boolean _is_activated;

    public Module(String name, int toggle_key, boolean is_activated){
        _name = name;
        _is_activated = is_activated;
        _toggle_key = toggle_key;
    }

    public void toggle(){
        _is_activated = !_is_activated;
        if(_is_activated){
            onEnable();
        }else{
            onDisable();
        }
    }

    public abstract void onUpdate();
    abstract void onEnable();
    abstract void onDisable();

    // Getters
    public String getName() {
        return _name;
    }
    public int getToggleKey(){ return _toggle_key; }
    public boolean isActivated() {
        return _is_activated;
    }

}
