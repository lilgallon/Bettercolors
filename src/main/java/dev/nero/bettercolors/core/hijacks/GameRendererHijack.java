package dev.nero.bettercolors.core.hijacks;

import dev.nero.bettercolors.core.modules.Reach;
import dev.nero.bettercolors.engine.BettercolorsEngine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.*;

/**
 * Used to bypass Minecraft code that flags extended reach when playing online
 */
public class GameRendererHijack extends GameRenderer {

    public static GameRendererHijack hijack(GameRenderer entityRenderer) {
        return new GameRendererHijack(
                Minecraft.getInstance(),
                Minecraft.getInstance().getResourceManager(),
                Minecraft.getInstance().renderBuffers()
        );
    }

    private Entity pointedEntity;
    private Minecraft mc;
    private Reach reach;

    public GameRendererHijack(Minecraft mcIn, ResourceManager resourceManagerIn, RenderBuffers renderBuffersIn) {
        super(mcIn, resourceManagerIn, renderBuffersIn);
        this.mc = mcIn;
        this.reach = (Reach) BettercolorsEngine.getInstance().getModule("Reach");
    }

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

    @Override
    public void pick(float partialTicks) {
        // Code copied from EntityRenderer#getMouseOver
        Entity entity = this.mc.getCameraEntity();
        if (entity != null) {
            if (this.mc.level != null) {
                this.mc.getProfiler().push("pick");
                this.mc.crosshairPickEntity = null;

                double d0 = (double) this.mc.gameMode.getPickRange() + this.getBlockReachInc();
                this.mc.hitResult = entity.pick(d0, partialTicks, false);

                Vec3 vector3d = entity.getEyePosition(partialTicks);
                boolean flag = false;

                double d1 = d0;
                if (this.mc.gameMode.hasFarPickRange()) {
                    d1 = 6.0D;
                    d0 = d1;
                } else {
                    if (d0 > 3.0D) {
                        // flag true -> will verify later on that it's a block that has been hit
                        flag = true;
                    }
                }

                d1 = d1 * d1;
                if (this.mc.hitResult != null) {
                    // d1: distance to object from the player's view
                    if (this.reach.isActivated()) {
                        HitResult result = entity.pick(
                                this.reach.getCombatReachIncrement() + 3.0f, partialTicks, false
                        );
                        d1 = result.getLocation().distanceToSqr(vector3d);
                    } else {
                        d1 = this.mc.hitResult.getLocation().distanceToSqr(vector3d);
                    }
                }

                Vec3 vector3d1 = entity.getViewVector(1.0F);
                Vec3 vector3d2 = vector3d.add(vector3d1.x * d0, vector3d1.y * d0, vector3d1.z * d0);
                AABB axisalignedbb = entity.getBoundingBox().expandTowards(vector3d1.scale(d0)).inflate(1.0D, 1.0D, 1.0D);
                EntityHitResult entityraytraceresult = ProjectileUtil.getEntityHitResult(entity, vector3d, vector3d2, axisalignedbb, (p_215312_0_) -> {
                    return !p_215312_0_.isSpectator() && p_215312_0_.isPickable();
                }, d1);
                if (entityraytraceresult != null) {
                    Entity entity1 = entityraytraceresult.getEntity();
                    Vec3 vector3d3 = entityraytraceresult.getLocation();
                    double d2 = vector3d.distanceToSqr(vector3d3);

                    // Yeah, it took me some time, but as you can see they use squareDistance everywhere. So the default
                    // reach of 3.0D is actually 9.0D (3.0D*3.0D). So the reach increment needs to be multiplied by itself

                    if (flag && d2 > 9.0D + reach.getCombatReachIncrement() * reach.getCombatReachIncrement()) {
                        this.mc.hitResult = BlockHitResult.miss(vector3d3, Direction.getNearest(vector3d1.x, vector3d1.y, vector3d1.z), new BlockPos(vector3d3));
                    } else if (d2 < d1 || this.mc.hitResult == null) {
                        this.mc.hitResult = entityraytraceresult;
                        if (entity1 instanceof LivingEntity || entity1 instanceof ItemFrame) {
                            this.mc.crosshairPickEntity = entity1;
                        }
                    }
                }

                this.mc.getProfiler().pop();
            }
        }
    }
}
