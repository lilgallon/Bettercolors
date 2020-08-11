package dev.nero.bettercolors.core.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface OnRenderCallback {

    Event<OnRenderCallback> EVENT = EventFactory.createArrayBacked(OnRenderCallback.class,
            (listeners) -> (tick, info) -> {
                for (OnRenderCallback listener : listeners) {
                    listener.trigger(tick, info);
                }
            });

    void trigger(boolean tick, CallbackInfo info);
}
