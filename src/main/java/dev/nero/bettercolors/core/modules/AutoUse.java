package dev.nero.bettercolors.core.modules;

import dev.nero.bettercolors.core.events.EventType;
import dev.nero.bettercolors.core.wrapper.Wrapper;
import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.utils.TimeHelper;
import net.minecraft.item.SwordItem;

public class AutoUse extends Module {

    private final TimeHelper usingTimer;
    private int swordSlot = -1;
    private int prevSlot = -1;

    public AutoUse(Integer toggleKey, Boolean isActivated) {
        super("Auto use",
                "Can be used as an autorod, autopot, autosoup, ... When you select something else than a " +
                        "sword, it uses that item, and then it reselects the sword",
                toggleKey, isActivated, "unknown.png", "AU");

        this.usingTimer = new TimeHelper();
    }

    @Override
    protected void onEvent(int code, Object details) {
        if (!this.isActivated()) return;
        if (Wrapper.MC.player == null) return;
        if (Wrapper.isInGui()) return;

        if (code == EventType.CLIENT_TICK) {
            int currSlot = Wrapper.MC.player.inventory.currentItem;

            if (!usingTimer.isStopped()) {
                if (usingTimer.isDelayComplete(100)) {
                    Wrapper.MC.player.inventory.currentItem = this.swordSlot;
                    usingTimer.stop();
                }
            } else {
                if (currSlot != this.prevSlot && this.prevSlot != -1) {
                    // Now we now that the player changed its slot

                    // Check if the player was using a sword (or if we already know where the sword is in the hotbar)
                    if (Wrapper.MC.player.inventory.mainInventory.get(this.swordSlot).getItem() instanceof SwordItem) {
                        // We already know where the sword is
                        Wrapper.rightClick();
                        usingTimer.start();
                    } else if (Wrapper.MC.player.inventory.mainInventory.get(this.prevSlot).getItem() instanceof SwordItem) {
                        // We found the sword
                        Wrapper.rightClick();
                        usingTimer.start();
                        this.swordSlot = this.prevSlot;
                    }
                }
            }

            this.prevSlot = currSlot;
        }
    }
}
