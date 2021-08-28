package dev.nero.bettercolors.core.mixin;

import dev.nero.bettercolors.core.events.OnEntityJoinCallback;
import dev.nero.bettercolors.core.events.OnEntityLeaveCallback;
import dev.nero.bettercolors.core.events.OnWorldLoadCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void worldLoadEvent(CallbackInfo info) {
        OnWorldLoadCallback.EVENT.invoker().trigger();
    }

    @Inject(method = "addEntityPrivate(ILnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
    private void entityJoinEvent(int code, Entity entity, CallbackInfo info) {
        OnEntityJoinCallback.EVENT.invoker().trigger(entity);
    }

    @Inject(method = "removeEntity(ILnet/minecraft/entity/Entity$RemovalReason;)V", at = @At("TAIL"))
    private void entityLeaveEvent(int entityId, Entity.RemovalReason reason, CallbackInfo info) {
        Entity entity;
        if (MinecraftClient.getInstance().world != null) {
            entity = MinecraftClient.getInstance().world.getEntityById(entityId);
            if (entity != null) {
                OnEntityLeaveCallback.EVENT.invoker().trigger(entity);
            }
        }
    }
}
