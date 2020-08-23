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

import dev.nero.bettercolors.engine.option.Option;
import dev.nero.bettercolors.engine.option.ToggleOption;
import dev.nero.bettercolors.engine.option.ValueFloatOption;
import dev.nero.bettercolors.engine.option.ValueOption;
import dev.nero.bettercolors.engine.view.LogLevel;
import dev.nero.bettercolors.engine.view.Window;

import java.util.ArrayList;
import java.util.Map;

public abstract class Module {

    // Utility
    private final String PREFIX;
    private String lastLogMessage;

    // Module details
    private final String name;
    protected ArrayList<Option> options;
    private final String symbol;

    // Module status
    private int toggleKey;
    private boolean isActivated;

    /**
     * @param name the name.
     * @param toggleKey the toggle Key (-1 -> none).
     * @param isActivated the initial state.
     * @param symbol the picture name.
     * @param prefix the prefix for console logging and settings.
     */
    protected Module(String name, Integer toggleKey, Boolean isActivated, String symbol, String prefix){
        this.name = name;
        this.isActivated = isActivated;
        this.toggleKey = toggleKey;
        this.symbol = symbol;
        this.PREFIX = prefix;

        lastLogMessage = "";

        options = new ArrayList<>();
    }

    /**
     * It toggles the module.
     *
     * @param isTriggeredByKeybind if true means that the mod has been toggled using key press
     */
    public void toggle(boolean isTriggeredByKeybind){
        isActivated = !isActivated;
        this.onToggle(isActivated, isTriggeredByKeybind);
    }

    /**
     * It sends an information message to the window's console.
     * @param msg the message to send.
     */
    protected void logInfo(String msg){
        if(!msg.equalsIgnoreCase(lastLogMessage)) {
            lastLogMessage = msg;
            Window.LOG(LogLevel.INFO, "[" + PREFIX + "]" + msg);
        }
    }

    /**
     * It sends an error message to the window's console.
     * @param msg the message to send.
     */
    protected void logError(String msg){
        if(!msg.equalsIgnoreCase(lastLogMessage)) {
            lastLogMessage = msg;
            Window.LOG(LogLevel.ERROR, "[" + PREFIX + "]" + msg);
        }
    }

    /**
     * It updates the module
     *
     * @deprecated Use Module#event instead
     */
    @Deprecated
    public void update(){
        onUpdate();
    }

    /**
     * It calls the onEvent method of the module with the event code and its details.
     * @param code the event code (the client needs to define it)
     * @param details the details (the client needs to define it)
     */
    public void event(int code, Object details) {
        this.onEvent(code, details);
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
     * @param index the index of the ValueOption in the options array
     * @return the option's value
     */
    protected int getOptionI(int index) {
        return ((ValueOption) this.options.get(index)).getVal();
    }

    /**
     *
     * @param index the index of the ValueFloatOption in the options array
     * @return the option's value
     */
    protected float getOptionF(int index) {
        return ((ValueFloatOption) this.options.get(index)).getVal();
    }

    /**
     * @param index the index of the ToggleOption in the options array
     * @return the option's value
     */
    protected boolean getOptionB(int index) {
        return ((ToggleOption) this.options.get(index)).isActivated();
    }

    /**
     * Used in children to run the module.
     *
     * @deprecated use Module#onEvent(int code, Object details) instead
     */
    @Deprecated
    protected void onUpdate() { }

    /**
     * Used in children to run the module.
     * @param code the event code (you need to define it since your modules will use it to differentiate events)
     * @param details the event details (you need to define it)
     */
    abstract void onEvent(int code, Object details);

    /**
     * Used in children to execute some code when they're turning on and off.
     * Only called when toggle() is called by the engine.
     * @param toggle true if turned on, false otherwise
     * @param isTriggeredByKeybind if true means that the mod has been toggled using key press
     */
    protected void onToggle(boolean toggle, boolean isTriggeredByKeybind) {}

    protected static ArrayList<Option> getDefaultOptions() {
        return new ArrayList<>();
    }

    // Setters
    public void setActivated(boolean activated) { this.isActivated = activated; }
    public void setToggleKey(int key) { this.toggleKey = key; }

    // Getters
    public String getName() { return name; }
    public int getToggleKey(){ return toggleKey; }
    public boolean isActivated() { return isActivated; }
    public ArrayList<Option> getOptions() { return options; }
    public String getSymbol() { return symbol; }
    public String getPrefix() { return PREFIX; }
}
