/*
 * Copyright 2018-2020 Bettercolors Contributors (https://github.com/N3ROO/Bettercolors)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.nero.bettercolors.modules;

import dev.nero.bettercolors.modules.options.Option;
import dev.nero.bettercolors.modules.options.ToggleOption;
import dev.nero.bettercolors.modules.options.ValueOption;
import dev.nero.bettercolors.view.Window;
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
    private String lastLogMessage;
    final static Minecraft MC = Minecraft.getMinecraft();

    // Keys utility
    private final Map<Key, KeyState> KEY_HANDLER;
    enum Key { ATTACK, USE }
    enum KeyState { JUST_PRESSED, BEING_PRESSED, JUST_RELEASED, IDLE }

    // Module details
    private final String name;
    ArrayList<Option> options;
    private final String symbol;

    // Module status
    private final int TOGGLE_KEY;
    private boolean isActivated;

    /**
     * @param name the name.
     * @param toggleKey the toggle Key (-1 -> none).
     * @param isActivated the initial state.
     * @param symbol the picture name.
     * @param log_prefix the prefix for console logging.
     */
    Module(String name, int toggleKey, boolean isActivated, String symbol, String log_prefix){
        this.name = name;
        this.isActivated = isActivated;
        this.TOGGLE_KEY = toggleKey;
        this.symbol = symbol;
        this.LOG_PREFIX = log_prefix;

        lastLogMessage = "";

        options = new ArrayList<>();

        KEY_HANDLER = new HashMap<>();
        KEY_HANDLER.put(Key.ATTACK, KeyState.IDLE);
        KEY_HANDLER.put(Key.USE, KeyState.IDLE);
    }

    /**
     * It toggles the module.
     */
    public void toggle(){
        isActivated = !isActivated;

        if (!isActivated) {
            // Reset Key handler
            for (Map.Entry<Key, KeyState> entry : KEY_HANDLER.entrySet()) {
                entry.setValue(KeyState.IDLE);
            }
        }
    }

    /**
     * It sends an information message to the window's console.
     * @param msg the message to send.
     */
    void log_info(String msg){
        if(!msg.equalsIgnoreCase(lastLogMessage)) {
            lastLogMessage = msg;
            Window.instance.addText(LOG_PREFIX + " " + msg, true);
        }
    }

    /**
     * It sends an error message to the window's console.
     * @param msg the message to send.
     */
    void log_error(String msg){
        if(!msg.equalsIgnoreCase(lastLogMessage)) {
            lastLogMessage = msg;
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
        return (MC.thePlayer.isPlayerSleeping() ||
                MC.thePlayer.isDead ||
                !(MC.thePlayer.openContainer instanceof ContainerPlayer) ||
                !MC.inGameHasFocus);
    }

    /**
     * @param Key the Key to check the state.
     * @param state the state of the Key.
     * @return true if the [Key] is currently at the state [state].
     */
    boolean isKeyState(Key Key, KeyState state){
        return KEY_HANDLER.get(Key) == state;
    }

    /**
     * It updates the module
     */
    public void update(){
        updateKeyHandler();
        onUpdate();
    }

    /**
     * It updates the Key handler
     */
    public void updateKeyHandler(){
        if(MC.gameSettings.keyBindAttack.isKeyDown() && KEY_HANDLER.get(Key.ATTACK) == KeyState.IDLE){
            KEY_HANDLER.replace(Key.ATTACK, KeyState.JUST_PRESSED);
        }else if(MC.gameSettings.keyBindAttack.isKeyDown() && KEY_HANDLER.get(Key.ATTACK) == KeyState.JUST_PRESSED) {
            KEY_HANDLER.replace(Key.ATTACK, KeyState.BEING_PRESSED);
        }else if(!MC.gameSettings.keyBindAttack.isKeyDown() && (KEY_HANDLER.get(Key.ATTACK) == KeyState.JUST_PRESSED || KEY_HANDLER.get(Key.ATTACK) == KeyState.BEING_PRESSED)){
            KEY_HANDLER.replace(Key.ATTACK, KeyState.JUST_RELEASED);
        } else if(!MC.gameSettings.keyBindAttack.isKeyDown() && KEY_HANDLER.get(Key.ATTACK) == KeyState.JUST_RELEASED){
            KEY_HANDLER.replace(Key.ATTACK, KeyState.IDLE);
        }

        if(MC.gameSettings.keyBindUseItem.isKeyDown() && KEY_HANDLER.get(Key.USE) == KeyState.IDLE){
            KEY_HANDLER.replace(Key.USE, KeyState.JUST_PRESSED);
        }else if(MC.gameSettings.keyBindUseItem.isKeyDown() && KEY_HANDLER.get(Key.USE) == KeyState.JUST_PRESSED) {
            KEY_HANDLER.replace(Key.USE, KeyState.BEING_PRESSED);
        }else if(!MC.gameSettings.keyBindUseItem.isKeyDown() && (KEY_HANDLER.get(Key.USE) == KeyState.JUST_PRESSED || KEY_HANDLER.get(Key.USE) == KeyState.BEING_PRESSED)){
            KEY_HANDLER.replace(Key.USE, KeyState.JUST_RELEASED);
        }else if(!MC.gameSettings.keyBindUseItem.isKeyDown() && KEY_HANDLER.get(Key.USE) == KeyState.JUST_RELEASED){
            KEY_HANDLER.replace(Key.USE, KeyState.IDLE);
        }
    }

    /**
     * It updates the options of the module. The options variable can contain ANY options, so it needs to check if the
     * option exists in the current module.
     */
    public void setOptions(Map<String, String> options){
        if(options == null) return;
        if(options.size() == 0) return;
        for(Map.Entry<String,String> option : options.entrySet()){
            String optionName = option.getKey();
            String optionValue = option.getValue();
            int index = Option.getIndex(this.options, optionName);
            if(index != -1){
                if(this.options.get(index) instanceof ToggleOption){
                    ((ToggleOption) this.options.get(index)).setActivated(Boolean.parseBoolean(optionValue));
                }else{
                    ((ValueOption) this.options.get(index)).setVal(Integer.parseInt(optionValue));
                }
            }
        }
    }

    /**
     * Used in children to run the module.
     */
    abstract void onUpdate();

    // Setters
    public void setActivated(boolean activated){ isActivated = activated; }

    // Getters
    public String getName() { return name; }
    public int getToggleKey(){ return TOGGLE_KEY; }
    public boolean isActivated() { return isActivated; }
    public ArrayList<Option> getOptions() { return options; }
    public String getSymbol(){ return symbol; }
}