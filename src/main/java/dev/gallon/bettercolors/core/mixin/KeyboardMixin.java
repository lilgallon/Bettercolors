package dev.gallon.bettercolors.core.mixin;

import dev.gallon.bettercolors.core.events.OnKeyInputEvent;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey(JIIII)V", at = @At("TAIL"))
    private void keyEvent(long window, int key, int scancode, int i, int j, CallbackInfo info) {
        OnKeyInputEvent.EVENT.invoker().trigger();
    }
}
