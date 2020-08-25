package dev.nero.bettercolors.core.mixin;

import dev.nero.bettercolors.core.events.OnClientTickCallback;
import dev.nero.bettercolors.core.events.OnPostMinecraftInit;
import dev.nero.bettercolors.core.events.OnRenderCallback;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void minecraftPostInitEvent(CallbackInfo info) { OnPostMinecraftInit.EVENT.invoker().trigger(); }

    @Inject(method = "render(Z)V", at = @At("HEAD"))
    private void renderEvent(boolean tick, CallbackInfo info) {
        OnRenderCallback.EVENT.invoker().trigger();
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void tickEvent(CallbackInfo info) {
        OnClientTickCallback.EVENT.invoker().trigger();
    }
}
