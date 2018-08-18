package com.bettercolors.modules;

import com.bettercolors.modules.options.Option;
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
    static Minecraft _mc = Minecraft.getMinecraft();
    // Keys utility
    private Map<KEY, KEY_STATE> _key_handler;
    enum KEY{ ATTACK, USE }
    enum KEY_STATE{ JUST_PRESSED, BEING_PRESSED, JUST_RELEASED, IDLE}

    // Module details
    private final String _name;
    ArrayList<Option> _options;
    private final String _symbol;

    // Module status
    private int _toggle_key;
    private boolean _is_activated;

    Module(String name, int toggle_key, boolean is_activated, String symbol, String log_prefix){
        _last_log_msg = "";
        _name = name;
        _is_activated = is_activated;
        _toggle_key = toggle_key;
        _symbol = symbol;
        LOG_PREFIX = log_prefix;
        _options = new ArrayList<>();

        _key_handler = new HashMap<>();
        _key_handler.put(KEY.ATTACK, KEY_STATE.IDLE);
        _key_handler.put(KEY.USE, KEY_STATE.IDLE);
    }

    public void toggle(){
        _is_activated = !_is_activated;
        if(_is_activated){
            onEnable();
        }else{
            onDisable();
        }
    }

    void log_info(String msg){
        if(!msg.equalsIgnoreCase(_last_log_msg)) {
            _last_log_msg = msg;
            Window.instance.addText(LOG_PREFIX + " " + msg, true);
        }
    }

    void log_error(String msg){
        if(!msg.equalsIgnoreCase(_last_log_msg)) {
            _last_log_msg = msg;
            Window.instance.addText(LOG_PREFIX + " " + msg, Color.RED, true);
        }
    }

    boolean isInSameTeam(Entity entity){
        if(!(entity instanceof EntityPlayer))
            return false;

        boolean same_team = false;
        String target_tag;
        try {
            // Check friends / teammate
            target_tag = exportTag((EntityPlayer) entity);
            if (exportTag(_mc.thePlayer).equalsIgnoreCase(target_tag)) {
                same_team = true;
            }

        } catch (Exception ignored) { }
        return same_team;
    }

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

    boolean isInGui(){
        if(_mc.thePlayer == null) return true;
        return _mc.thePlayer.isPlayerSleeping() || _mc.thePlayer.isDead || !(_mc.thePlayer.openContainer instanceof ContainerPlayer);
    }

    boolean isKeyState(KEY key, KEY_STATE state){
        return _key_handler.get(key) == state;
    }

    public void update(){
        if(_mc.gameSettings.keyBindAttack.isKeyDown() && _key_handler.get(KEY.ATTACK) == KEY_STATE.IDLE){
            _key_handler.replace(KEY.ATTACK, KEY_STATE.JUST_PRESSED);
        }else if(_mc.gameSettings.keyBindAttack.isKeyDown() && _key_handler.get(KEY.ATTACK) == KEY_STATE.JUST_PRESSED) {
            _key_handler.replace(KEY.ATTACK, KEY_STATE.BEING_PRESSED);
        }else if(!_mc.gameSettings.keyBindUseItem.isKeyDown() && (_key_handler.get(KEY.ATTACK) == KEY_STATE.JUST_PRESSED || _key_handler.get(KEY.ATTACK) == KEY_STATE.BEING_PRESSED)){
            _key_handler.replace(KEY.ATTACK, KEY_STATE.JUST_RELEASED);
        } else if(!_mc.gameSettings.keyBindAttack.isKeyDown() && _key_handler.get(KEY.ATTACK) == KEY_STATE.JUST_RELEASED){
            _key_handler.replace(KEY.ATTACK, KEY_STATE.IDLE);
        }

        if(_mc.gameSettings.keyBindUseItem.isKeyDown() && _key_handler.get(KEY.USE) == KEY_STATE.IDLE){
            _key_handler.replace(KEY.USE, KEY_STATE.JUST_PRESSED);
        }else if(_mc.gameSettings.keyBindUseItem.isKeyDown() && _key_handler.get(KEY.USE) == KEY_STATE.JUST_PRESSED) {
            _key_handler.replace(KEY.USE, KEY_STATE.BEING_PRESSED);
        }else if(!_mc.gameSettings.keyBindUseItem.isKeyDown() && (_key_handler.get(KEY.USE) == KEY_STATE.JUST_PRESSED || _key_handler.get(KEY.USE) == KEY_STATE.BEING_PRESSED)){
            _key_handler.replace(KEY.USE, KEY_STATE.JUST_RELEASED);
        }else if(!_mc.gameSettings.keyBindUseItem.isKeyDown() && _key_handler.get(KEY.USE) == KEY_STATE.JUST_RELEASED){
            _key_handler.replace(KEY.USE, KEY_STATE.IDLE);
        }

        onUpdate();
    }

    abstract void onUpdate();
    abstract void onEnable();
    abstract void onDisable();

    // Getters
    public String getName() { return _name; }
    public int getToggleKey(){ return _toggle_key; }
    public boolean isActivated() { return _is_activated; }
    public ArrayList<Option> getOptions() { return _options; }
    public String getSymbol(){ return _symbol; }
}
