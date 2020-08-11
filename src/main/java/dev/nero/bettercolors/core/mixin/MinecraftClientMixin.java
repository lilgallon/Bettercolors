package dev.nero.bettercolors.core.mixin;

import dev.nero.bettercolors.core.events.OnRenderCallback;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "net/minecraft/client/MinecraftClient.render(Z)V", at = @At("INVOKE"))
    private void renderEvent(boolean tick, CallbackInfo info) {
        OnRenderCallback.EVENT.invoker().trigger(tick, info);
    }
}
