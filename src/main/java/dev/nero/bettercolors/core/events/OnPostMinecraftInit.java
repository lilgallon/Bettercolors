package dev.nero.bettercolors.core.events;

import dev.nero.bettercolors.core.events.fabricapi.Event;
import dev.nero.bettercolors.core.events.fabricapi.EventFactory;

public interface OnPostMinecraftInit {

    Event<OnPostMinecraftInit> EVENT = EventFactory.createArrayBacked(OnPostMinecraftInit.class,
            (listeners) -> () -> {
                for (OnPostMinecraftInit listener : listeners) {
                    listener.trigger();
                }
            });

    void trigger();
}
