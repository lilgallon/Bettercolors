package dev.nero.bettercolors.modules;

public class AutoSprint extends Module{

    /**
     * @param name the name.
     * @param toggle_key the toggle key (-1 -> none).
     * @param is_activated the initial state.
     * @param symbol the picture name.
     */
    public AutoSprint(int toggle_key, boolean is_activated) {
        super("Auto sprint", toggle_key, is_activated, "sprint_symbol.png", "[ASp]");
    }

    @Override
    public void onUpdate() {
        if(MC.player != null){
            if(MC.player.moveForward > 0 && !MC.player.isSprinting()){
                log_info("forcing player to run.");
                MC.player.setSprinting(true);
            }
        }
    }
}
