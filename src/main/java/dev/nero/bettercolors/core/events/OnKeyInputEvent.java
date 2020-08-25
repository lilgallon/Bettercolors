package dev.nero.bettercolors.core.events;

import dev.nero.bettercolors.core.events.fabricapi.Event;
import dev.nero.bettercolors.core.events.fabricapi.EventFactory;

public interface OnKeyInputEvent {

    Event<OnKeyInputEvent> EVENT = EventFactory.createArrayBacked(OnKeyInputEvent.class,
            (listeners) -> () -> {
                for (OnKeyInputEvent listener : listeners) {
                    listener.trigger();
                }
            });

    void trigger();
}
