/*
 * Copyright 2018-2020
 * - Bettercolors Contributors (https://github.com/N3ROO/Bettercolors) and
 * - Bettercolors Engine Contributors (https://github.com/N3ROO/BettercolorsEngine)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.nero.bettercolors.core.wrapper;

import dev.nero.bettercolors.engine.utils.Friends;
import dev.nero.bettercolors.engine.utils.TimeHelper;
import dev.nero.bettercolors.engine.view.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;

import java.awt.Robot;
import java.awt.AWTException;
import java.util.List;

/**
 * Wrapper for Minecraft 1.16
 */
public class Wrapper {

    public final static Minecraft MC = Minecraft.getInstance();
    public final static Class<PlayerEntity> playerEntityClass = PlayerEntity.class;

    private final static TimeHelper delay = new TimeHelper();
    private static Robot robot;

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            Window.ERROR("Could not create robot to generate fake clicks");
        }

        delay.start();
    }

    /**
     * @param e entity.
     * @return the team tag of the entity.
     */
    public static String exportTag(PlayerEntity e){
        String tag;
        try{
            tag = e.getDisplayName().getUnformattedComponentText().split(e.getName().getString())[0].replace(" ","");
            tag = tag.replace("§","");
        }catch(Exception exc){
            tag = "";
        }
        return tag;
    }

    /**
     * @return true if the user is in a Gui (he can't move).
     */
    public static boolean isInGui(){
        if(Wrapper.MC.player == null) return true;

        return Wrapper.MC.player.isSleeping() ||
                !MC.isGameFocused() ||
                MC.isGamePaused() ||
                (Wrapper.MC.currentScreen instanceof ContainerScreen);
    }

    /**
     * @param entity the entity (can be anything).
     * @return true if the given entity is in the same team as the player.
     */
    public static boolean isInSameTeam(Entity entity){
        if(!(entity.getClass().isInstance(Wrapper.playerEntityClass)))
            return false;

        boolean same_team = false;
        String target_tag;
        try {
            // Check friends / teammate
            target_tag = Wrapper.exportTag(Wrapper.playerEntityClass.cast(entity.getClass()));
            if (Wrapper.exportTag(Wrapper.MC.player).equalsIgnoreCase(target_tag)) {
                same_team = true;
            }

        } catch (Exception ignored) { }
        return same_team;
    }

    /**
     * Human-like click (fake mouse click).
     *
     * With a security (100 ms min between clicks) -> 10 CPS max allowed
     */
    public static void click() {
        Wrapper.click(100);
    }

    /**
     * Human-like click (fake mouse click).
     *
     * @param minDelay minimum delay between each click. If not sure, use Wrapper#click()
     */
    public static void click(int minDelay) {
        if (delay.isDelayComplete(minDelay)){
            if (robot != null) {
                robot.mouseRelease(16);
                robot.mousePress(16);
                robot.mouseRelease(16);
            }
            delay.reset();
        }
    }

    /**
     * Copied from Item#rayTrace, and updated
     *
     * @param range range to perform ray tracing
     * @return result of ray tracing from the player's view within the range
     */
    private static BlockRayTraceResult rayTrace(double range) {
        if (Wrapper.MC.player == null) return null;

        float f = Wrapper.MC.player.rotationPitch;
        float f1 = Wrapper.MC.player.rotationYaw;
        Vector3d vector3d = Wrapper.MC.player.getEyePosition(1.0F);
        float f2 =  MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 =  MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -MathHelper.cos(-f  * ((float)Math.PI / 180F));
        float f5 =  MathHelper.sin(-f  * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = range;
        Vector3d vector3d1 = vector3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);

        return Wrapper.MC.world.rayTraceBlocks(
                new RayTraceContext(
                        vector3d,
                        vector3d1,
                        RayTraceContext.BlockMode.OUTLINE,
                        RayTraceContext.FluidMode.ANY,
                        Wrapper.MC.player
                )
        );
    }

    /**
     * @param range in blocks, defines the range around the player to scan for entities
     * @param entityClass the entity type to look for (Check the Entity class: MobEntity.class for mobs for example)
     * @return all the entities that are within the given range from the player
     */
    public static List<Entity> getEntitiesAroundPlayer(float range, Class<? extends Entity> entityClass) {
        AxisAlignedBB area = new AxisAlignedBB(
                Wrapper.MC.player.getPosX() - range,
                Wrapper.MC.player.getPosY() - range,
                Wrapper.MC.player.getPosZ() - range,
                Wrapper.MC.player.getPosX() + range,
                Wrapper.MC.player.getPosY() + range,
                Wrapper.MC.player.getPosZ() + range
        );

        return Wrapper.MC.world.getEntitiesWithinAABB(entityClass, area);
    }

    /**
     * @param entities list of entities to scan
     * @return the closest entity from the list from the player's crosshair
     */
    public static Entity getClosestEntityToCrosshair(List<Entity> entities) {
        float minDist = Float.MAX_VALUE;
        Entity closest = null;

        for(Entity entity : entities){
            // Get distance between the two entities (rotations)
            float[] yawPitch = getYawPitchBetween(
                    Wrapper.MC.player, entity
            );

            // Compute the distance from the player's crosshair
            float distYaw = MathHelper.abs(MathHelper.wrapDegrees(yawPitch[0] - Wrapper.MC.player.rotationYaw));
            float distPitch = MathHelper.abs(MathHelper.wrapDegrees(yawPitch[1] - Wrapper.MC.player.rotationPitch));
            float dist = MathHelper.sqrt(distYaw*distYaw + distPitch*distPitch);

            // Get the closest entity
            if(dist < minDist) {
                closest = entity;
                minDist = dist;
            }
        }

        return closest;
    }

    /**
     * @param source the source entity
     * @param target the target of the source entity
     */
    public static float[] getYawPitchBetween(Entity source, Entity target) {
        // getPosY returns the ground position
        // getPosY + EyeHeight return the eye's position
        // getPosY + EyeHeight/1.5 returns the upper body position
        final float SHIFT_FACTOR = 1.5f;

        return Wrapper.getYawPitchBetween(
                // source
                source.getPosX(),
                source.getPosY() + source.getEyeHeight(),
                source.getPosZ(),
                // target
                target.getPosX(),
                target.getPosY() + (target.getEyeHeight() / SHIFT_FACTOR),
                target.getPosZ()
        );
    }

    /**
     * @param sourceX x position for source
     * @param sourceY y position for source
     * @param sourceZ z position for source
     * @param targetX x position for target
     * @param targetY y position for target
     * @param targetZ z position for target
     * @return the [yaw, pitch] difference between the source and the target
     */
    public static float[] getYawPitchBetween(
            double sourceX, double sourceY, double sourceZ,
            double targetX, double targetY, double targetZ) {

        double diffX = targetX - sourceX;
        double diffY = targetY - sourceY;
        double diffZ = targetZ - sourceZ;

        double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) ((Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F );
        float pitch = (float) - (Math.atan2(diffY, dist) * 180.0D / Math.PI);

        return new float[] { yaw, pitch };
    }

    /**
     * @param entity the target to aim.
     * @return the [x, y] new positions of the player crosshair
     */
    public static float[] getRotationsNeeded(Entity entity, float fovX, float fovY, float stepX, float stepY) {
        // We calculate the yaw/pitch difference between the target and the player
        float[] yawPitch = getYawPitchBetween(Wrapper.MC.player, entity);

        // We make sure that it's absolute, because the sign may change if we invert entity and MC.player
        //float yaw = MathHelper.abs(yawPitch[0]);
        //float pitch = MathHelper.abs(yawPitch[1]);
        float yaw = yawPitch[0];
        float pitch = yawPitch[1];

        // We check if the entity is within the FOV of the player
        // yaw and pitch are absolute, not relative to anything. We fix that by calling wrapDegrees and subtracting
        // the yaw & pitch to the player's rotation. Now, the yaw, and the pitch are relative to the player's view
        // So we can compare that with the given fov: radiusX, and radiusY (which are both in degrees)
        boolean inFovX = MathHelper.abs(MathHelper.wrapDegrees(yaw - MC.player.rotationYaw)) <= fovX;
        boolean inFovY = MathHelper.abs(MathHelper.wrapDegrees(pitch - MC.player.rotationPitch)) <= fovY;

        // If the targeted entity is within the fov, then, we will compute the step in yaw / pitch of the player's view
        // to get closer to the targeted entity. We will use the given stepX and stepY to compute that. Dividing by 100
        // reduces that step. Without that, we would need to show very low values to the user in the GUI, which is not
        // user-friendly. That way, instead of showing 0.05, we show 5.
        if(inFovX && inFovY) {
            float yawFinal, pitchFinal;
            yawFinal = ((MathHelper.wrapDegrees(yaw - MC.player.rotationYaw)) * stepX) / 100;
            pitchFinal = ((MathHelper.wrapDegrees(pitch - MC.player.rotationPitch)) * stepY) / 100;

            return new float[] { MC.player.rotationYaw + yawFinal, MC.player.rotationPitch + pitchFinal};
        } else {
            return new float[] { MC.player.rotationYaw, MC.player.rotationPitch};
        }
    }

    /**
     * Sets the position of the crosshair
     * @param yaw horizontal pos (degrees)
     * @param pitch vertical pos (degrees)
     */
    public static void setRotations(float yaw, float pitch) {
        Wrapper.MC.player.rotationYaw = yaw;
        Wrapper.MC.player.rotationPitch = pitch;
    }

    public static boolean canAttack(Entity entity) {
        // TODO:
        // - antibot
        // - team

        if (entity instanceof PlayerEntity) {
            return !Friends.isFriend(entity.getDisplayName().getString());
        }
        return true;
    }
}
