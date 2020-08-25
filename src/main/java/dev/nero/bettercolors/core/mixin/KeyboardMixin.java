package dev.nero.bettercolors.core.mixin;

import dev.nero.bettercolors.core.events.OnKeyInputEvent;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey(JIIII)V", at = @At("TAIL"))
    private void keyEvent(CallbackInfo info) {
        OnKeyInputEvent.EVENT.invoker().trigger();
    }
}
