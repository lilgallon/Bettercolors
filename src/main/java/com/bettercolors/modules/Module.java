package com.bettercolors.modules;

import com.bettercolors.modules.options.Option;
import com.bettercolors.modules.options.ToggleOption;
import com.bettercolors.modules.options.ValueOption;
import com.bettercolors.view.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Module {

    // Utility
    private final String LOG_PREFIX;
    private String _last_log_msg;
    final static Minecraft MC = Minecraft.getMinecraft();

    // Keys utility
    private final Map<KEY, KEY_STATE> KEY_HANDLER;
    enum KEY{ ATTACK, USE }
    enum KEY_STATE{ JUST_PRESSED, BEING_PRESSED, JUST_RELEASED, IDLE}

    // Module details
    private final String _name;
    ArrayList<Option> _options;
    private final String _symbol;

    // Module status
    private final int TOGGLE_KEY;
    private boolean _is_activated;

    /**
     * @param name the name.
     * @param toggle_key the toggle key (-1 -> none).
     * @param is_activated the initial state.
     * @param symbol the picture name.
     * @param log_prefix the prefix for console logging.
     */
    Module(String name, int toggle_key, boolean is_activated, String symbol, String log_prefix){
        _last_log_msg = "";
        _name = name;
        _is_activated = is_activated;
        TOGGLE_KEY = toggle_key;
        _symbol = symbol;
        LOG_PREFIX = log_prefix;
        _options = new ArrayList<>();

        KEY_HANDLER = new HashMap<>();
        KEY_HANDLER.put(KEY.ATTACK, KEY_STATE.IDLE);
        KEY_HANDLER.put(KEY.USE, KEY_STATE.IDLE);
    }

    /**
     * It toggles the module.
     */
    public void toggle(){
        _is_activated = !_is_activated;
    }

    /**
     * It sends an information message to the window's console.
     * @param msg the message to send.
     */
    void log_info(String msg){
        if(!msg.equalsIgnoreCase(_last_log_msg)) {
            _last_log_msg = msg;
            Window.instance.addText(LOG_PREFIX + " " + msg, true);
        }
    }

    /**
     * It sends an error message to the window's console.
     * @param msg the message to send.
     */
    void log_error(String msg){
        if(!msg.equalsIgnoreCase(_last_log_msg)) {
            _last_log_msg = msg;
            Window.instance.addText(LOG_PREFIX + " " + msg, Color.RED, true);
        }
    }

    /**
     * @param entity the entity (can be anything).
     * @return true if the given entity is in the same team as the player.
     */
    boolean isInSameTeam(Entity entity){
        if(!(entity instanceof EntityPlayer))
            return false;

        boolean same_team = false;
        String target_tag;
        try {
            // Check friends / teammate
            target_tag = exportTag((EntityPlayer) entity);
            if (exportTag(MC.thePlayer).equalsIgnoreCase(target_tag)) {
                same_team = true;
            }

        } catch (Exception ignored) { }
        return same_team;
    }

    /**
     * @param e entity.
     * @return the team tag of the entity.
     */
    private String exportTag(EntityPlayer e){
        String tag;
        try{
            tag = e.getDisplayName().getUnformattedText().split(e.getName())[0].replace(" ","");
            tag = tag.replace("§","");
        }catch(Exception exc){
            tag = "";
        }
        return tag;
    }

    /**
     * @return true if the user is in a Gui (he can't move).
     */
    boolean isInGui(){
        if(MC.thePlayer == null) return true;
        return MC.thePlayer.isPlayerSleeping() || MC.thePlayer.isDead || !(MC.thePlayer.openContainer instanceof ContainerPlayer);
    }

    /**
     * @param key the key to check the state.
     * @param state the state of the key.
     * @return true if the [key] is currently at the state [state].
     */
    boolean isKeyState(KEY key, KEY_STATE state){
        return KEY_HANDLER.get(key) == state;
    }

    /**
     * It updates the module
     */
    public void update(){
        onUpdate();
    }

    /**
     * It updates the key handler
     */
    public void updateKeyHandler(){
        if(MC.gameSettings.keyBindAttack.isKeyDown() && KEY_HANDLER.get(KEY.ATTACK) == KEY_STATE.IDLE){
            KEY_HANDLER.replace(KEY.ATTACK, KEY_STATE.JUST_PRESSED);
        }else if(MC.gameSettings.keyBindAttack.isKeyDown() && KEY_HANDLER.get(KEY.ATTACK) == KEY_STATE.JUST_PRESSED) {
            KEY_HANDLER.replace(KEY.ATTACK, KEY_STATE.BEING_PRESSED);
        }else if(!MC.gameSettings.keyBindUseItem.isKeyDown() && (KEY_HANDLER.get(KEY.ATTACK) == KEY_STATE.JUST_PRESSED || KEY_HANDLER.get(KEY.ATTACK) == KEY_STATE.BEING_PRESSED)){
            KEY_HANDLER.replace(KEY.ATTACK, KEY_STATE.JUST_RELEASED);
        } else if(!MC.gameSettings.keyBindAttack.isKeyDown() && KEY_HANDLER.get(KEY.ATTACK) == KEY_STATE.JUST_RELEASED){
            KEY_HANDLER.replace(KEY.ATTACK, KEY_STATE.IDLE);
        }

        if(MC.gameSettings.keyBindUseItem.isKeyDown() && KEY_HANDLER.get(KEY.USE) == KEY_STATE.IDLE){
            KEY_HANDLER.replace(KEY.USE, KEY_STATE.JUST_PRESSED);
        }else if(MC.gameSettings.keyBindUseItem.isKeyDown() && KEY_HANDLER.get(KEY.USE) == KEY_STATE.JUST_PRESSED) {
            KEY_HANDLER.replace(KEY.USE, KEY_STATE.BEING_PRESSED);
        }else if(!MC.gameSettings.keyBindUseItem.isKeyDown() && (KEY_HANDLER.get(KEY.USE) == KEY_STATE.JUST_PRESSED || KEY_HANDLER.get(KEY.USE) == KEY_STATE.BEING_PRESSED)){
            KEY_HANDLER.replace(KEY.USE, KEY_STATE.JUST_RELEASED);
        }else if(!MC.gameSettings.keyBindUseItem.isKeyDown() && KEY_HANDLER.get(KEY.USE) == KEY_STATE.JUST_RELEASED){
            KEY_HANDLER.replace(KEY.USE, KEY_STATE.IDLE);
        }
    }

    /**
     * It updates the options of the module. The options variable can contain ANY options, so it needs to check if the
     * option exists in the current module.
     */
    public void setOptions(Map<String, String> options){
        if(_options == null) return;
        if(_options.size() == 0) return;
        for(Map.Entry<String,String> option : options.entrySet()){
            String option_name = option.getKey();
            String option_value = option.getValue();
            int index = Option.getIndex(_options, option_name);
            if(index != -1){
                if(_options.get(index) instanceof ToggleOption){
                    ((ToggleOption) _options.get(index)).setActivated(Boolean.parseBoolean(option_value));
                }else{
                    ((ValueOption) _options.get(index)).setVal(Integer.parseInt(option_value));
                }
            }
        }
    }

    /**
     * Used in children to run the module.
     */
    abstract void onUpdate();

    // Setters
    public void setActivated(boolean activated){ _is_activated = activated; }

    // Getters
    public String getName() { return _name; }
    public int getToggleKey(){ return TOGGLE_KEY; }
    public boolean isActivated() { return _is_activated; }
    public ArrayList<Option> getOptions() { return _options; }
    public String getSymbol(){ return _symbol; }
}
