package dev.nero.bettercolors.core.mixin;

import dev.nero.bettercolors.core.events.OnEntityJoinCallback;
import dev.nero.bettercolors.core.events.OnEntityLeaveCallback;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Inject(method = "loadEntityUnchecked(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
    private void entityJoinEvent(CallbackInfo info, Entity entity) {
        OnEntityJoinCallback.EVENT.invoker().trigger(entity);
    }

    @Inject(method = "unloadEntity(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
    private void entityLeaveEvent(CallbackInfo info, Entity entity) {
        OnEntityLeaveCallback.EVENT.invoker().trigger(entity);
    }
}
