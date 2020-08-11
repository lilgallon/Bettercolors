package dev.nero.bettercolors.core.hijacks;

import dev.nero.bettercolors.engine.BettercolorsEngine;
import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.core.modules.Reach;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/**
 * Used to bypass Minecraft code that flags extended reach when playing online
 */
public class GameRendererHijack extends GameRenderer {

    public static GameRendererHijack hijack(GameRenderer entityRenderer) {
        return new GameRendererHijack(
                MinecraftClient.getInstance(),
                MinecraftClient.getInstance().getResourceManager(),
                MinecraftClient.getInstance().getBufferBuilders()
        );
    }

    private Entity pointedEntity;
    private MinecraftClient client;

    public GameRendererHijack(MinecraftClient mcIn, ResourceManager resourceManagerIn, BufferBuilderStorage renderTypeBuffersIn) {
        super(mcIn, resourceManagerIn, renderTypeBuffersIn);
        this.client = mcIn;
    }

    private float getReachDistance() {
        float increment = 0.0f;
        Module reach = BettercolorsEngine.getInstance().getModule("Reach");

        if (reach.isActivated()) {
            increment = ((Reach) reach).getReachIncrement();
        }

        // That's basically the code from the controller
        float defaultReach = this.client.interactionManager.getReachDistance();
        // end

        return defaultReach + increment;
    }

    @Override
    public void updateTargetedEntity(float tickDelta) {
        Entity entity = this.client.getCameraEntity();
        if (entity != null) {
            if (this.client.world != null) {
                this.client.getProfiler().push("pick");
                this.client.targetedEntity = null;
                double d = (double) this.getReachDistance();  // updated to use the above method
                this.client.crosshairTarget = entity.rayTrace(d, tickDelta, false);
                Vec3d vec3d = entity.getCameraPosVec(tickDelta);
                boolean bl = false;
                // int i = true; lol
                double e = d;
                if (this.client.interactionManager.hasExtendedReach()) {
                    e = 6.0D;
                    d = e;
                } /*else {
                    if (d > 3.0D) {
                        bl = true;
                    }

                    d = d;
                }*/

                e *= e;
                if (this.client.crosshairTarget != null) {
                    e = this.client.crosshairTarget.getPos().squaredDistanceTo(vec3d);
                }

                Vec3d vec3d2 = entity.getRotationVec(1.0F);
                Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
                float f = 1.0F;
                Box box = entity.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0D, 1.0D, 1.0D);
                EntityHitResult entityHitResult = ProjectileUtil.rayTrace(entity, vec3d, vec3d3, box, (entityx) -> {
                    return !entityx.isSpectator() && entityx.collides();
                }, e);
                if (entityHitResult != null) {
                    Entity entity2 = entityHitResult.getEntity();
                    Vec3d vec3d4 = entityHitResult.getPos();
                    double g = vec3d.squaredDistanceTo(vec3d4);
                    /* no flags -> always false
                    if (bl && g > 9.0D) {
                        this.client.crosshairTarget = BlockHitResult.createMissed(vec3d4, Direction.getFacing(vec3d2.x, vec3d2.y, vec3d2.z), new BlockPos(vec3d4));
                    }
                    else */
                    if (g < e || this.client.crosshairTarget == null) {
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
