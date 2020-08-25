package dev.nero.bettercolors.core.events;

import dev.nero.bettercolors.core.events.fabricapi.Event;
import dev.nero.bettercolors.core.events.fabricapi.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;

public interface OnEntityDamageCallback {

    class Info {
        Entity target;
        DamageSource source;
        float originalHealth;
        float damageAmount;

        public Info(Entity target, DamageSource source, float originalHealth, float damageAmount) {
            this.target = target;
            this.source = source;
            this.originalHealth = originalHealth;
            this.damageAmount = damageAmount;
        }

        public Entity getTarget() {
            return target;
        }

        public DamageSource getSource() {
            return source;
        }

        public float getOriginalHealth() {
            return originalHealth;
        }

        public float getDamageAmount() {
            return damageAmount;
        }
    }

    Event<OnEntityDamageCallback> EVENT = EventFactory.createArrayBacked(OnEntityDamageCallback.class,
            (listeners) -> (info) -> {
                for (OnEntityDamageCallback listener : listeners) {
                    listener.trigger(info);
                }
            });

    void trigger(Info info);
}
