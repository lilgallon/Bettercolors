package dev.gallon.bettercolors.core.mixin;

import dev.gallon.bettercolors.core.events.OnEntityDamageCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageTracker.class)
public class DamageTrackerMixin {

    @Shadow
    @Final
    private LivingEntity entity;

    @Inject(method = "onDamage", at = @At("HEAD"))
    private void damageEvent(DamageSource damageSource, float originalHealth, float damageAmount, CallbackInfo info) {
        OnEntityDamageCallback.EVENT.invoker().trigger(new OnEntityDamageCallback.Info(this.entity, damageSource, damageAmount, originalHealth));
    }
}
