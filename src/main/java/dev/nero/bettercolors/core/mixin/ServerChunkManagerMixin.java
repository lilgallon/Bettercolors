package dev.nero.bettercolors.core.mixin;

import dev.nero.bettercolors.core.events.OnEntityJoinCallback;
import dev.nero.bettercolors.core.events.OnEntityLeaveCallback;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerChunkManager.class)
public class ServerChunkManagerMixin {

    @Inject(method = "loadEntity(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
    private void entityJoinEvent(Entity entity, CallbackInfo info) {
        OnEntityJoinCallback.EVENT.invoker().trigger(entity);
    }

    @Inject(method = "unloadEntity(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
    private void entityLeaveEvent(Entity entity, CallbackInfo info) {
        OnEntityLeaveCallback.EVENT.invoker().trigger(entity);
    }
}
