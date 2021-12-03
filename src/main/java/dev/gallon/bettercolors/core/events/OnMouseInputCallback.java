package dev.gallon.bettercolors.core.events;

import dev.gallon.bettercolors.core.events.fabricapi.Event;
import dev.gallon.bettercolors.core.events.fabricapi.EventFactory;

public interface OnMouseInputCallback {

    Event<OnMouseInputCallback> EVENT = EventFactory.createArrayBacked(OnMouseInputCallback.class,
            (listeners) -> (button, action, mod) -> {
                for (OnMouseInputCallback listener : listeners) {
                    listener.trigger(button, action, mod);
                }
            });

    void trigger(int button, int action, int mod);
}
