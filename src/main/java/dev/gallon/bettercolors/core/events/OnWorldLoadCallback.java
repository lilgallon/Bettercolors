package dev.gallon.bettercolors.core.events;

import dev.gallon.bettercolors.core.events.fabricapi.Event;
import dev.gallon.bettercolors.core.events.fabricapi.EventFactory;

public interface OnWorldLoadCallback {

    Event<OnWorldLoadCallback> EVENT = EventFactory.createArrayBacked(OnWorldLoadCallback.class,
            (listeners) -> () -> {
                for (OnWorldLoadCallback listener : listeners) {
                    listener.trigger();
                }
            });

    void trigger();
}
