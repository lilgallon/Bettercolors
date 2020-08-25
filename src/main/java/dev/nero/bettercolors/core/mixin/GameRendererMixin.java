package dev.nero.bettercolors.core.mixin;

import dev.nero.bettercolors.core.modules.Reach;
import dev.nero.bettercolors.engine.BettercolorsEngine;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    private Reach reach;

    private float getCombatReachInc() {
        if (this.reach.isActivated()) {
            return this.reach.getCombatReachIncrement();
        } else {
            return 0.0f;
        }
    }

    private float getBlockReachInc() {
        if (this.reach.isActivated()) {
            return this.reach.getBlockReachIncrement();
        } else {
            return 0.0f;
        }
    }

    /**
     * @author N3ROO
     * @reason reach increase
     */
    @Overwrite
    public void updateTargetedEntity(float tickDelta) {
        if (this.reach == null) {
            this.reach = (Reach) BettercolorsEngine.getInstance().getModule("Reach");
        }

        Entity entity = this.client.getCameraEntity();
        if (entity != null) {
            if (this.client.world != null) {
                this.client.getProfiler().push("pick");
                this.client.targetedEntity = null;

                double d = (double) this.client.interactionManager.getReachDistance() + getBlockReachInc();
                this.client.crosshairTarget = entity.raycast(d, tickDelta, false);

                Vec3d vec3d = entity.getCameraPosVec(tickDelta);
                boolean bl = false;

                double e = d;
                if (this.client.interactionManager.hasExtendedReach()) {
                    e = 6.0D;
                    d = e;
                } else {
                    if (d > 3.0D) {
                        bl = true;
                    }
                }

                e *= e;
                if (this.client.crosshairTarget != null) {
                    if (this.reach.isActivated()) {
                        HitResult result = entity.raycast(this.getCombatReachInc() + 3.0f, tickDelta, false);
                        e = result.getPos().squaredDistanceTo(vec3d);
                    } else {
                        e = this.client.crosshairTarget.getPos().squaredDistanceTo(vec3d);
                    }
                }

                Vec3d vec3d2 = entity.getRotationVec(1.0F);
                Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
                float f = 1.0F;
                Box box = entity.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0D, 1.0D, 1.0D);
                EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, vec3d, vec3d3, box, (entityx) -> {
                    return !entityx.isSpectator() && entityx.collides();
                }, e);
                if (entityHitResult != null) {
                    Entity entity2 = entityHitResult.getEntity();
                    Vec3d vec3d4 = entityHitResult.getPos();
                    double g = vec3d.squaredDistanceTo(vec3d4);

                    // Yeah, it took me some time, but as you can see they use squareDistance everywhere. So the default
                    // reach of 3.0D is actually 9.0D (3.0D*3.0D). So the reach increment needs to be multiplied by itthis

                    if (bl && g > 9.0D + reach.getCombatReachIncrement() * reach.getCombatReachIncrement()) {
                        this.client.crosshairTarget = BlockHitResult.createMissed(vec3d4, Direction.getFacing(vec3d2.x, vec3d2.y, vec3d2.z), new BlockPos(vec3d4));
                    } else if (g < e || this.client.crosshairTarget == null) {
                        this.client.crosshairTarget = entityHitResult;
                        if (entity2 instanceof LivingEntity || entity2 instanceof ItemFrameEntity) {
                            this.client.targetedEntity = entity2;
                        }
                    }
                }

                this.client.getProfiler().pop();
            }
        }
    }
}
