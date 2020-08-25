package dev.nero.bettercolors.core.mixin;

import dev.nero.bettercolors.core.events.OnWorldLoadCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "createWorlds(Lnet/minecraft/server/WorldGenerationProgressListener;)V", at = @At("TAIL"))
    private void worldLoadEvent(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo info) {
        OnWorldLoadCallback.EVENT.invoker().trigger();
    }
}
