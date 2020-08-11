package dev.nero.bettercolors.core.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface CooldownAccessorMixin {
    @Accessor("attackCooldown")
    public int getAttackCooldown();
}
