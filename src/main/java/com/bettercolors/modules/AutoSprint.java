package com.bettercolors.modules;

public class AutoSprint extends Module{

    private final String LOG_PREFIX = "[AS] ";

    public AutoSprint(String name, int toggle_key, boolean is_activated, String symbol) {
        super(name, toggle_key, is_activated, symbol);
    }

    @Override
    public void onUpdate() {
        if(_mc.thePlayer != null){
            if(_mc.thePlayer.moveForward != 0){
                log_info(LOG_PREFIX + "forcing player to run.");
                _mc.thePlayer.setSprinting(true);
            }
        }
    }

    @Override
    void onEnable() {

    }

    @Override
    void onDisable() {

    }
}
