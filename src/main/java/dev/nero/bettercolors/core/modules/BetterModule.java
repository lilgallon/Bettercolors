package dev.nero.bettercolors.core.modules;

import dev.nero.bettercolors.core.events.EventType;
import dev.nero.bettercolors.core.wrapper.Wrapper;
import dev.nero.bettercolors.engine.module.Module;
import net.minecraftforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

public class BetterModule extends Module {

    // Keys utility
    private boolean attack = false;
    private boolean use = false;

    private boolean holdingAttack = false;
    private boolean holdingUse = false;

    // Used because events sucks and just after a RIGHT mouse press it sends a mouse release event
    // but it works as intended for left click
    private boolean fakeUseReleaseSent = false;

    /**
     * @param name        the name.
     * @param description the description.
     * @param toggleKey   the toggle Key (-1 -> none).
     * @param isActivated the initial state.
     * @param symbol      the picture name.
     * @param prefix      the prefix for console logging and settings
     */
    protected BetterModule(String name, String description, Integer toggleKey, Boolean isActivated, String symbol, String prefix) {
        super(name, description, toggleKey, isActivated, symbol, prefix);
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
            attack = false;
            use = false;
            holdingAttack = false;
            holdingUse = false;
        }
    }

    @Override
    public void event(int code, Object details) {
        if (code == EventType.MOUSE_INPUT) {
            attack = Wrapper.MC.gameSettings.keyBindAttack.isKeyDown();
            use = Wrapper.MC.gameSettings.keyBindUseItem.isKeyDown();

            InputEvent.MouseInputEvent event = (InputEvent.MouseInputEvent ) details;
            if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                holdingAttack = event.getAction() == GLFW.GLFW_PRESS;
            } else {
                if (event.getAction() == GLFW.GLFW_PRESS) {
                    holdingUse = true;
                    fakeUseReleaseSent = false;
                } else {
                    if (fakeUseReleaseSent) {
                        holdingUse = false;
                    }

                    fakeUseReleaseSent = true;
                }
            }
        }

        this.onEvent(code, details);
    }

    /**
     * ! Consumes the event
     * @return true if the player attacks (left click)
     */
    protected boolean playerAttacks(){
        boolean b = attack;
        attack = false;
        return b;
    }

    /**
     * ! Consumes the event
     * @return true if the player uses (right click)
     */
    protected boolean playerUses(){
        boolean b = use;
        use = false;
        return b;
    }

    /**
     * @return true if the left mouse click is being pressed
     */
    protected boolean playerHoldingAttack() {
        return holdingAttack;
    }

    /**
     * @return true if the right mouse click is being pressed
     */
    protected boolean playerHoldingUse() {
        return holdingUse;
    }
}
