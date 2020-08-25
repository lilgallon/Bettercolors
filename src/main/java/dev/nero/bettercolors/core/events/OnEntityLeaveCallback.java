package dev.nero.bettercolors.core.events;

import dev.nero.bettercolors.core.events.fabricapi.Event;
import dev.nero.bettercolors.core.events.fabricapi.EventFactory;
import net.minecraft.entity.Entity;

public interface OnEntityLeaveCallback {

    Event<OnEntityLeaveCallback> EVENT = EventFactory.createArrayBacked(OnEntityLeaveCallback.class,
            (listeners) -> (entity) -> {
                for (OnEntityLeaveCallback listener : listeners) {
                    listener.trigger(entity);
                }
            });

    void trigger(Entity entity);
}