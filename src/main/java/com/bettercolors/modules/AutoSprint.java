package com.bettercolors.modules;

public class AutoSprint extends Module{

    public AutoSprint(String name, int toggle_key, boolean is_activated, String symbol) {
        super(name, toggle_key, is_activated, symbol, "[ASp]");
    }

    @Override
    public void onUpdate() {
        if(MC.thePlayer != null){
            if(MC.thePlayer.moveForward != 0){
                log_info("forcing player to run.");
                MC.thePlayer.setSprinting(true);
            }
        }
    }
}
