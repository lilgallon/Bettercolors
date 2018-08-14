package com.bettercolors.modules;

import com.bettercolors.modules.options.Option;
import com.bettercolors.view.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;

public abstract class Module {

    // Utility
    static Minecraft _mc = Minecraft.getMinecraft();
    String _last_log_msg;

    // Module details
    private final String _name;
    ArrayList<Option> _options;
    private final String _symbol;

    // Module status
    private int _toggle_key;
    private boolean _is_activated;

    public Module(String name, int toggle_key, boolean is_activated, String symbol){
        _last_log_msg = "";
        _name = name;
        _is_activated = is_activated;
        _toggle_key = toggle_key;
        _symbol = symbol;
        _options = new ArrayList<>();
    }

    public void toggle(){
        _is_activated = !_is_activated;
        if(_is_activated){
            onEnable();
        }else{
            onDisable();
        }
    }

    void log(String msg, Color color, boolean new_line){
        if(!msg.equalsIgnoreCase(_last_log_msg)) {
            _last_log_msg = msg;
            Window.instance.addText(msg, color, new_line);
        }
    }

    void log(String msg){
        if(!msg.equalsIgnoreCase(_last_log_msg)) {
            _last_log_msg = msg;
            Window.instance.addText(msg, true);
        }
    }

    boolean isInSameTeam(EntityLivingBase entity){
        boolean same_team = false;
        String target_tag;
        try {
            // Check friends / teammate
            if (entity instanceof EntityPlayer) {
                target_tag = exportTag((EntityPlayer) entity);
                if (exportTag(_mc.thePlayer).equalsIgnoreCase(target_tag)) {
                    same_team = true;
                }
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

    public abstract void onUpdate();
    abstract void onEnable();
    abstract void onDisable();

    // Getters
    public String getName() { return _name; }
    public int getToggleKey(){ return _toggle_key; }
    public boolean isActivated() { return _is_activated; }
    public ArrayList<Option> getOptions() { return _options; }
    public String getSymbol(){ return _symbol; }
}
