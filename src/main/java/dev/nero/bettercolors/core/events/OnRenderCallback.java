package dev.nero.bettercolors.core.events;

import dev.nero.bettercolors.core.events.fabricapi.Event;
import dev.nero.bettercolors.core.events.fabricapi.EventFactory;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface OnRenderCallback {

    Event<OnRenderCallback> EVENT = EventFactory.createArrayBacked(OnRenderCallback.class,
            (listeners) -> () -> {
                for (OnRenderCallback listener : listeners) {
                    listener.trigger();
                }
            });

    void trigger();
}
