package dev.nero.bettercolors.core.events;

import dev.nero.bettercolors.core.events.fabricapi.Event;
import dev.nero.bettercolors.core.events.fabricapi.EventFactory;

public interface OnMouseInputCallback {

    Event<OnMouseInputCallback> EVENT = EventFactory.createArrayBacked(OnMouseInputCallback.class,
            (listeners) -> () -> {
                for (OnMouseInputCallback listener : listeners) {
                    listener.trigger();
                }
            });

    void trigger();
}
