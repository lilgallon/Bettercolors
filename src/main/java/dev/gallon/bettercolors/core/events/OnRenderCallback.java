package dev.gallon.bettercolors.core.events;

import dev.gallon.bettercolors.core.events.fabricapi.Event;
import dev.gallon.bettercolors.core.events.fabricapi.EventFactory;

public interface OnRenderCallback {

    Event<OnRenderCallback> EVENT = EventFactory.createArrayBacked(OnRenderCallback.class,
            (listeners) -> () -> {
                for (OnRenderCallback listener : listeners) {
                    listener.trigger();
                }
            });

    void trigger();
}
