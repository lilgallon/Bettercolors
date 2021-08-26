package dev.nero.bettercolors.core.modules;

import dev.nero.bettercolors.core.events.EventType;
import dev.nero.bettercolors.core.wrapper.Wrapper;
import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.option.Option;
import dev.nero.bettercolors.engine.utils.TimeHelper;
import net.minecraft.item.SwordItem;

import java.util.ArrayList;

public class AutoUse extends Module {

    private final TimeHelper usingTimer;
    private int swordSlot = -1;
    private int prevSlot = -1;

    public AutoUse(Integer toggleKey, Boolean isActivated) {
        super("Auto use",
                "Can be used as an autorod, autopot, autosoup, ... When you select something else than a " +
                        "sword, it uses that item, and then it reselects the sword",
                toggleKey, isActivated, "unknown.png", "AU", true);

        this.usingTimer = new TimeHelper();
    }

    @Override
    protected void onEvent(int code, Object details) {
        if (!this.isActivated()) return;
        if (Wrapper.MC.player == null) return;
        if (Wrapper.isInGui()) return;

        if (code == EventType.CLIENT_TICK) {
            int currSlot = Wrapper.MC.player.getInventory().selectedSlot;

            if (!usingTimer.isStopped()) {
                if (usingTimer.isDelayComplete(100)) {
                    Wrapper.MC.player.getInventory().selectedSlot = this.swordSlot;
                    usingTimer.stop();
                }
            } else {
                if (currSlot != this.prevSlot && this.prevSlot != -1) {
                    // Now we now that the player changed its slot

                    // True means that we will use (right click) and switch to the last slot
                    boolean use = false;

                    // Check if we already know where the sword is
                    if (isSword(this.swordSlot)) {
                        // We will use that slot
                        use = true;
                    }
                    // If not, then check the previous slot
                    else if (isSword(this.prevSlot)) {
                        // If we found a slot, we save it
                        this.swordSlot = this.prevSlot;
                        // Then we will use that slot
                        use = true;
                    }

                    if (use) {
                        Wrapper.rightClick();
                        usingTimer.start();
                    }
                }
            }

            this.prevSlot = currSlot;
        }
    }

    /**
     * @param slot the slot (returns false if invalid)
     * @return true if the item at the given slot is a sword
     */
    private boolean isSword(int slot) {
        if (slot < 0 || slot > 9) return false;
        return Wrapper.MC.player.getInventory().main.get(slot).getItem() instanceof SwordItem;
    }

    /**
     * Used by the engine (reflection)
     */
    public static ArrayList<Option> getDefaultOptions(){
        return new ArrayList<>();
    }
}