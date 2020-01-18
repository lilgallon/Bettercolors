package dev.nero.bettercolors.modules;

public class AutoSprint extends Module{

    /**
     * @param name the name.
     * @param toggle_key the toggle key (-1 -> none).
     * @param is_activated the initial state.
     * @param symbol the picture name.
     */
    public AutoSprint(String name, int toggle_key, boolean is_activated, String symbol) {
        super(name, toggle_key, is_activated, symbol, "[ASp]");
    }

    @Override
    public void onUpdate() {
        if(MC.thePlayer != null){
            if(MC.thePlayer.moveForward > 0 && !MC.thePlayer.isSprinting()){
                log_info("forcing player to run.");
                MC.thePlayer.setSprinting(true);
            }
        }
    }
}
