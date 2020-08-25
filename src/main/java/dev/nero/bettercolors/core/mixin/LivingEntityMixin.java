package dev.nero.bettercolors.core.mixin;

import dev.nero.bettercolors.core.events.OnEntityAttackCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At("HEAD"))
    private void attackEvent(DamageSource damageSource, float damages, CallbackInfoReturnable<Boolean> info) {
        OnEntityAttackCallback.EVENT.invoker().trigger(new OnEntityAttackCallback.Info((LivingEntity) (Object) this, damageSource));
    }
}
