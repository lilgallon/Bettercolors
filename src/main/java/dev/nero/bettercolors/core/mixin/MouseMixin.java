package dev.nero.bettercolors.core.mixin;

import dev.nero.bettercolors.core.events.OnMouseInputCallback;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseButton(JIII)V", at = @At("TAIL"))
    private void mouseEvent(long window, int button, int action, int mods, CallbackInfo info) {
        OnMouseInputCallback.EVENT.invoker().trigger();
    }
}
