/*
 * Copyright 2018-2020
 * - Bettercolors Contributors (https://github.com/N3ROO/Bettercolors) and
 * - Bettercolors Engine Contributors (https://github.com/N3ROO/BettercolorsEngine)
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

package dev.nero.bettercolors.engine.module;

import dev.nero.bettercolors.engine.BettercolorsEngine;
import dev.nero.bettercolors.engine.option.Option;
import dev.nero.bettercolors.engine.option.ToggleOption;
import dev.nero.bettercolors.engine.option.ValueFloatOption;
import dev.nero.bettercolors.engine.option.ValueOption;
import dev.nero.bettercolors.engine.view.LogLevel;
import dev.nero.bettercolors.engine.view.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Module {

    // Utility
    private final String LOG_PREFIX;
    private String lastLogMessage;

    // Keys utility
    private final Map<Key, KeyState> KEY_HANDLER;
    protected enum Key { ATTACK, USE }
    protected enum KeyState { JUST_PRESSED, BEING_PRESSED, JUST_RELEASED, IDLE }

    // Module details
    private final String name;
    protected ArrayList<Option> options;
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
    protected Module(String name, Integer toggleKey, Boolean isActivated, String symbol, String log_prefix){
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
    protected void logInfo(String msg){
        if(!msg.equalsIgnoreCase(lastLogMessage)) {
            lastLogMessage = msg;
            Window.LOG(LogLevel.INFO, LOG_PREFIX + " " + msg);
        }
    }

    /**
     * It sends an error message to the window's console.
     * @param msg the message to send.
     */
    protected void logError(String msg){
        if(!msg.equalsIgnoreCase(lastLogMessage)) {
            lastLogMessage = msg;
            Window.LOG(LogLevel.ERROR, LOG_PREFIX + " " + msg);
        }
    }

    /**
     * @param Key the Key to check the state.
     * @param state the state of the Key.
     * @return true if the [Key] is currently at the state [state].
     */
    protected boolean isKeyState(Key Key, KeyState state){
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
        if(BettercolorsEngine.MC.gameSettings.keyBindAttack.isKeyDown() && KEY_HANDLER.get(Key.ATTACK) == KeyState.IDLE){
            KEY_HANDLER.replace(Key.ATTACK, KeyState.JUST_PRESSED);
        }else if(BettercolorsEngine.MC.gameSettings.keyBindAttack.isKeyDown() && KEY_HANDLER.get(Key.ATTACK) == KeyState.JUST_PRESSED) {
            KEY_HANDLER.replace(Key.ATTACK, KeyState.BEING_PRESSED);
        }else if(!BettercolorsEngine.MC.gameSettings.keyBindAttack.isKeyDown() && (KEY_HANDLER.get(Key.ATTACK) == KeyState.JUST_PRESSED || KEY_HANDLER.get(Key.ATTACK) == KeyState.BEING_PRESSED)){
            KEY_HANDLER.replace(Key.ATTACK, KeyState.JUST_RELEASED);
        } else if(!BettercolorsEngine.MC.gameSettings.keyBindAttack.isKeyDown() && KEY_HANDLER.get(Key.ATTACK) == KeyState.JUST_RELEASED){
            KEY_HANDLER.replace(Key.ATTACK, KeyState.IDLE);
        }

        if(BettercolorsEngine.MC.gameSettings.keyBindUseItem.isKeyDown() && KEY_HANDLER.get(Key.USE) == KeyState.IDLE){
            KEY_HANDLER.replace(Key.USE, KeyState.JUST_PRESSED);
        }else if(BettercolorsEngine.MC.gameSettings.keyBindUseItem.isKeyDown() && KEY_HANDLER.get(Key.USE) == KeyState.JUST_PRESSED) {
            KEY_HANDLER.replace(Key.USE, KeyState.BEING_PRESSED);
        }else if(!BettercolorsEngine.MC.gameSettings.keyBindUseItem.isKeyDown() && (KEY_HANDLER.get(Key.USE) == KeyState.JUST_PRESSED || KEY_HANDLER.get(Key.USE) == KeyState.BEING_PRESSED)){
            KEY_HANDLER.replace(Key.USE, KeyState.JUST_RELEASED);
        }else if(!BettercolorsEngine.MC.gameSettings.keyBindUseItem.isKeyDown() && KEY_HANDLER.get(Key.USE) == KeyState.JUST_RELEASED){
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
                } else if (this.options.get(index) instanceof ValueOption){
                    ((ValueOption) this.options.get(index)).setVal(Integer.parseInt(optionValue));
                }  else if (this.options.get(index) instanceof ValueFloatOption){
                    ((ValueFloatOption) this.options.get(index)).setVal(Float.parseFloat(optionValue));
                }
            }
        }
    }

    /**
     * Used in children to run the module.
     */
    protected void onUpdate() { }

    /**
     * Used in children to execute some code when they're turning on and off.
     * Only called when toggle() is called by the engine.
     * @param toggle true if turned on, false otherwise
     */
    protected void onToggle(boolean toggle) {}

    protected static ArrayList<Option> getDefaultOptions() {
        return new ArrayList<>();
    }

    // Setters
    public void setActivated(boolean activated){ isActivated = activated; }

    // Getters
    public String getName() { return name; }
    public int getToggleKey(){ return TOGGLE_KEY; }
    public boolean isActivated() { return isActivated; }
    public ArrayList<Option> getOptions() { return options; }
    public String getSymbol(){ return symbol; }
}