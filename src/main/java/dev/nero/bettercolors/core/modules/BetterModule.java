package dev.nero.bettercolors.core.modules;

import dev.nero.bettercolors.core.wrapper.Wrapper;
import dev.nero.bettercolors.engine.module.Module;

import java.util.HashMap;
import java.util.Map;

public class BetterModule extends Module {

    // Keys utility
    private final Map<Key, KeyState> KEY_HANDLER;
    protected enum Key { ATTACK, USE }
    protected enum KeyState { JUST_PRESSED, BEING_PRESSED, JUST_RELEASED, IDLE }

    /**
     * @param name        the name.
     * @param toggleKey   the toggle Key (-1 -> none).
     * @param isActivated the initial state.
     * @param symbol      the picture name.
     * @param prefix      the prefix for console logging and settings
     */
    protected BetterModule(String name, Integer toggleKey, Boolean isActivated, String symbol, String prefix) {
        super(name, toggleKey, isActivated, symbol, prefix);

        KEY_HANDLER = new HashMap<>();
        KEY_HANDLER.put(Key.ATTACK, KeyState.IDLE);
        KEY_HANDLER.put(Key.USE, KeyState.IDLE);
    }


    /**
     * It toggles the module.
     *
     * @param isTriggeredByKeybind if true means that the mod has been toggled using key press
     */
    @Override
    public void toggle(boolean isTriggeredByKeybind){
        this.setActivated(!this.isActivated());
        this.onToggle(this.isActivated(), isTriggeredByKeybind);

        if (!this.isActivated()) {
            // Reset Key handler
            for (Map.Entry<Key, KeyState> entry : KEY_HANDLER.entrySet()) {
                entry.setValue(KeyState.IDLE);
            }
        }
    }

    @Override
    public void update(){
        updateKeyHandler();
        onUpdate();
    }

    /**
     * It updates the Key handler
     */
    private void updateKeyHandler(){
        if(Wrapper.MC.gameSettings.keyBindAttack.isPressed() && KEY_HANDLER.get(Key.ATTACK) == KeyState.IDLE){
            KEY_HANDLER.replace(Key.ATTACK, KeyState.JUST_PRESSED);
        }else if(Wrapper.MC.gameSettings.keyBindAttack.isPressed() && KEY_HANDLER.get(Key.ATTACK) == KeyState.JUST_PRESSED) {
            KEY_HANDLER.replace(Key.ATTACK, KeyState.BEING_PRESSED);
        }else if(!Wrapper.MC.gameSettings.keyBindAttack.isPressed() && (KEY_HANDLER.get(Key.ATTACK) == KeyState.JUST_PRESSED || KEY_HANDLER.get(Key.ATTACK) == KeyState.BEING_PRESSED)){
            KEY_HANDLER.replace(Key.ATTACK, KeyState.JUST_RELEASED);
        } else if(!Wrapper.MC.gameSettings.keyBindAttack.isPressed() && KEY_HANDLER.get(Key.ATTACK) == KeyState.JUST_RELEASED){
            KEY_HANDLER.replace(Key.ATTACK, KeyState.IDLE);
        }

        if(Wrapper.MC.gameSettings.keyBindUseItem.isPressed() && KEY_HANDLER.get(Key.USE) == KeyState.IDLE){
            KEY_HANDLER.replace(Key.USE, KeyState.JUST_PRESSED);
        }else if(Wrapper.MC.gameSettings.keyBindUseItem.isPressed() && KEY_HANDLER.get(Key.USE) == KeyState.JUST_PRESSED) {
            KEY_HANDLER.replace(Key.USE, KeyState.BEING_PRESSED);
        }else if(!Wrapper.MC.gameSettings.keyBindUseItem.isPressed() && (KEY_HANDLER.get(Key.USE) == KeyState.JUST_PRESSED || KEY_HANDLER.get(Key.USE) == KeyState.BEING_PRESSED)){
            KEY_HANDLER.replace(Key.USE, KeyState.JUST_RELEASED);
        }else if(!Wrapper.MC.gameSettings.keyBindUseItem.isPressed() && KEY_HANDLER.get(Key.USE) == KeyState.JUST_RELEASED){
            KEY_HANDLER.replace(Key.USE, KeyState.IDLE);
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
}
