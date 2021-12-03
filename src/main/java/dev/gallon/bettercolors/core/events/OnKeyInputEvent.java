package dev.gallon.bettercolors.core.events;

import dev.gallon.bettercolors.core.events.fabricapi.Event;
import dev.gallon.bettercolors.core.events.fabricapi.EventFactory;

public interface OnKeyInputEvent {

    Event<OnKeyInputEvent> EVENT = EventFactory.createArrayBacked(OnKeyInputEvent.class,
            (listeners) -> () -> {
                for (OnKeyInputEvent listener : listeners) {
                    listener.trigger();
                }
            });

    void trigger();
}
