package dev.nero.bettercolors.core.events;

import dev.nero.bettercolors.core.events.fabricapi.Event;
import dev.nero.bettercolors.core.events.fabricapi.EventFactory;

public interface OnWorldLoadCallback {

    Event<OnWorldLoadCallback> EVENT = EventFactory.createArrayBacked(OnWorldLoadCallback.class,
            (listeners) -> () -> {
                for (OnWorldLoadCallback listener : listeners) {
                    listener.trigger();
                }
            });

    void trigger();
}
