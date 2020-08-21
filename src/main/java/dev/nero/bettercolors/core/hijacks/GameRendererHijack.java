package dev.nero.bettercolors.core.hijacks;

import dev.nero.bettercolors.engine.BettercolorsEngine;
import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.core.modules.Reach;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderTypeBuffers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;

/**
 * Used to bypass Minecraft code that flags extended reach when playing online
 */
public class GameRendererHijack extends GameRenderer {

    public static GameRendererHijack hijack(GameRenderer entityRenderer) {
        return new GameRendererHijack(
                Minecraft.getInstance(),
                Minecraft.getInstance().getResourceManager(),
                Minecraft.getInstance().getRenderTypeBuffers()
        );
    }

    private Entity pointedEntity;
    private Minecraft mc;
    private Reach reach;

    public GameRendererHijack(Minecraft mcIn, IResourceManager resourceManagerIn, RenderTypeBuffers renderTypeBuffersIn) {
        super(mcIn, resourceManagerIn, renderTypeBuffersIn);
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
    public void getMouseOver(float partialTicks) {
        // Code copied from EntityRenderer#getMouseOver
        Entity entity = this.mc.getRenderViewEntity();
        if (entity != null) {
            if (this.mc.world != null) {
                this.mc.getProfiler().startSection("pick");
                this.mc.pointedEntity = null;

                double d0 = (double) this.mc.playerController.getBlockReachDistance() + this.getBlockReachInc();
                this.mc.objectMouseOver = entity.pick(d0, partialTicks, false);

                Vector3d vector3d = entity.getEyePosition(partialTicks);
                boolean flag = false;

                double d1 = d0;
                if (this.mc.playerController.extendedReach()) {
                    d1 = 6.0D;
                    d0 = d1;
                } else {
                    if (d0 > 3.0D) {
                        // flag true -> will verify later on that it's a block that has been hit
                        flag = true;
                    }
                }

                d1 = d1 * d1;
                if (this.mc.objectMouseOver != null) {
                    // d1: distance to object from the player's view
                    if (this.reach.isActivated()) {
                        RayTraceResult result = entity.pick(
                                this.reach.getCombatReachIncrement() + 3.0f, partialTicks, false
                        );
                        d1 = result.getHitVec().squareDistanceTo(vector3d);
                    } else {
                        d1 = this.mc.objectMouseOver.getHitVec().squareDistanceTo(vector3d);
                    }
                }

                Vector3d vector3d1 = entity.getLook(1.0F);
                Vector3d vector3d2 = vector3d.add(vector3d1.x * d0, vector3d1.y * d0, vector3d1.z * d0);
                AxisAlignedBB axisalignedbb = entity.getBoundingBox().expand(vector3d1.scale(d0)).grow(1.0D, 1.0D, 1.0D);
                EntityRayTraceResult entityraytraceresult = ProjectileHelper.rayTraceEntities(entity, vector3d, vector3d2, axisalignedbb, (p_215312_0_) -> {
                    return !p_215312_0_.isSpectator() && p_215312_0_.canBeCollidedWith();
                }, d1);
                if (entityraytraceresult != null) {
                    Entity entity1 = entityraytraceresult.getEntity();
                    Vector3d vector3d3 = entityraytraceresult.getHitVec();
                    double d2 = vector3d.squareDistanceTo(vector3d3);

                    // Yeah, it took me some time, but as you can see they use squareDistance everywhere. So the default
                    // reach of 3.0D is actually 9.0D (3.0D*3.0D). So the reach increment needs to be multiplied by itself

                    if (flag && d2 > 9.0D + reach.getCombatReachIncrement() * reach.getCombatReachIncrement()) {
                        this.mc.objectMouseOver = BlockRayTraceResult.createMiss(vector3d3, Direction.getFacingFromVector(vector3d1.x, vector3d1.y, vector3d1.z), new BlockPos(vector3d3));
                    } else if (d2 < d1 || this.mc.objectMouseOver == null) {
                        this.mc.objectMouseOver = entityraytraceresult;
                        if (entity1 instanceof LivingEntity || entity1 instanceof ItemFrameEntity) {
                            this.mc.pointedEntity = entity1;
                        }
                    }
                }

                this.mc.getProfiler().endSection();
            }
        }
    }
}
