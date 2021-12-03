package dev.gallon.bettercolors.core.events;

import dev.gallon.bettercolors.core.events.fabricapi.Event;
import dev.gallon.bettercolors.core.events.fabricapi.EventFactory;

public interface OnClientTickCallback {
    Event<OnClientTickCallback> EVENT = EventFactory.createArrayBacked(OnClientTickCallback.class,
            (listeners) -> () -> {
                for (OnClientTickCallback listener : listeners) {
                    listener.trigger();
                }
            });

    void trigger();
}
