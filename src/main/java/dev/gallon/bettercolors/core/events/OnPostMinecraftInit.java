package dev.gallon.bettercolors.core.events;

import dev.gallon.bettercolors.core.events.fabricapi.Event;
import dev.gallon.bettercolors.core.events.fabricapi.EventFactory;

public interface OnPostMinecraftInit {

    Event<OnPostMinecraftInit> EVENT = EventFactory.createArrayBacked(OnPostMinecraftInit.class,
            (listeners) -> () -> {
                for (OnPostMinecraftInit listener : listeners) {
                    listener.trigger();
                }
            });

    void trigger();
}
