package dev.nero.bettercolors.core.events;

import dev.nero.bettercolors.core.events.fabricapi.Event;
import dev.nero.bettercolors.core.events.fabricapi.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public interface OnEntityAttackCallback {

    class Info {
        LivingEntity target;
        DamageSource source;

        public Info(LivingEntity target, DamageSource source) {
            this.target = target;
            this.source = source;
        }

        public LivingEntity getTarget() {
            return target;
        }

        public DamageSource getSource() {
            return source;
        }
    }

    Event<OnEntityAttackCallback> EVENT = EventFactory.createArrayBacked(OnEntityAttackCallback.class,
            (listeners) -> (info) -> {
                for (OnEntityAttackCallback listener : listeners) {
                    listener.trigger(info);
                }
            });

    void trigger(Info info);
}
