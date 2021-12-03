package dev.gallon.bettercolors.core.events;

import dev.gallon.bettercolors.core.events.fabricapi.Event;
import dev.gallon.bettercolors.core.events.fabricapi.EventFactory;
import net.minecraft.entity.Entity;

public interface OnEntityJoinCallback {

    Event<OnEntityJoinCallback> EVENT = EventFactory.createArrayBacked(OnEntityJoinCallback.class,
            (listeners) -> (entity) -> {
                for (OnEntityJoinCallback listener : listeners) {
                    listener.trigger(entity);
                }
            });

    void trigger(Entity entity);
}
