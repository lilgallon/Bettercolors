/*
 * Copyright 2018-2020
 * - Bettercolors Contributors (https://github.com/lilgallon/Bettercolors) and
 * - Bettercolors Engine Contributors (https://github.com/lilgallon/BettercolorsEngine)
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

import dev.nero.bettercolors.core.modules.Antibot;
import dev.nero.bettercolors.core.modules.TeamFilter;
import dev.nero.bettercolors.engine.utils.Friends;
import dev.nero.bettercolors.engine.utils.TimeHelper;
import dev.nero.bettercolors.engine.view.Window;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

import java.awt.Robot;
import java.awt.AWTException;
import java.awt.event.InputEvent;
import java.util.List;
import java.util.function.Predicate;

/**
 * Wrapper for Minecraft 1.16
 */
public class Wrapper {

    public final static MinecraftClient MC = MinecraftClient.getInstance();
    private final static TimeHelper delayLeft = new TimeHelper();
    private final static TimeHelper delayRight = new TimeHelper();

    private static Robot robot;
    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            Window.ERROR("Could not create robot to generate fake clicks");
        }

        delayLeft.start();
        delayRight.start();
    }

    /**
     * @return true if the user is in a Gui (he can't move).
     */
    public static boolean isInGui(){
        if(Wrapper.MC.player == null) return true;

        return Wrapper.MC.player.isSleeping() ||
                !MC.isWindowFocused() ||
                MC.isPaused() ||
                Wrapper.MC.currentScreen != null; // null -> no gui open
    }

    /**
     * Human-like left click (fake mouse click).
     *
     * With a security (100 ms min between clicks) -> 10 CPS max allowed
     */
    public static void leftClick() {
        Wrapper.click(100, true);
    }

    /**
     * Human-like right click (fake mouse click).
     *
     * With a security (100 ms min between clicks) -> 10 CPS max allowed
     */
    public static void rightClick() {
        Wrapper.click(100, false);
    }

    /**
     * Human-like click (fake mouse click).
     *
     * @param minDelay minimum delay between each click. If not sure, use Wrapper#click()
     */
    public static void click(int minDelay, boolean left) {
        if (left ? delayLeft.isDelayComplete(minDelay) : delayRight.isDelayComplete(minDelay)){
            if (robot != null) {
                robot.mouseRelease(left ? InputEvent.BUTTON1_DOWN_MASK : InputEvent.BUTTON3_DOWN_MASK);
                robot.mousePress(left ? InputEvent.BUTTON1_DOWN_MASK : InputEvent.BUTTON3_DOWN_MASK);
                robot.mouseRelease(left ? InputEvent.BUTTON1_DOWN_MASK : InputEvent.BUTTON3_DOWN_MASK);
            }

            if (left) delayLeft.reset();
            else delayRight.reset();
        }
    }

    /**
     * @param range in blocks, defines the range around the player to scan for entities
     * @param entityClass the entity type to look for (Check the Entity class: MobEntity.class for mobs for example)
     * @return all the entities that are within the given range from the player
     */
    public static List<? extends Entity> getEntitiesAroundPlayer(float range, Class<? extends Entity> entityClass) {

        Box area = new Box(
                Wrapper.MC.player.getX() - range,
                Wrapper.MC.player.getY() - range,
                Wrapper.MC.player.getZ() - range,
                Wrapper.MC.player.getX() + range,
                Wrapper.MC.player.getY() + range,
                Wrapper.MC.player.getZ() + range
        );

        return Wrapper.MC.world.getEntitiesByClass(entityClass, area, entity -> (true));
    }

    /**
     * @param entities list of entities to scan
     * @param canAttackFilter if true, will remove entities not attackable by the player
     * @return the closest entity from the list from the player's crosshair
     */
    public static Entity getClosestEntityToCrosshair(List<? extends Entity> entities, boolean canAttackFilter) {
        float minDist = Float.MAX_VALUE;
        Entity closest = null;

        for(Entity entity : entities){
            if (entity instanceof LivingEntity)
                if (!Wrapper.canAttack((LivingEntity) entity) && canAttackFilter) continue;

            // Get distance between the two entities (rotations)
            float[] yawPitch = getYawPitchBetween(
                    Wrapper.MC.player, entity
            );

            // Compute the distance from the player's crosshair
            float distYaw = MathHelper.abs(MathHelper.wrapDegrees(yawPitch[0] - Wrapper.MC.player.getYaw()));
            float distPitch = MathHelper.abs(MathHelper.wrapDegrees(yawPitch[1] - Wrapper.MC.player.getPitch()));
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
                source.getX(),
                source.getY() + source.getHeight() / 1.5f,
                source.getZ(),
                // target
                target.getX(),
                target.getY() + (target.getHeight() / 1.5f / SHIFT_FACTOR),
                target.getZ()
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

        float dist = MathHelper.sqrt((float) (diffX * diffX + diffZ * diffZ));

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
        boolean inFovX = MathHelper.abs(MathHelper.wrapDegrees(yaw - MC.player.getYaw())) <= fovX;
        boolean inFovY = MathHelper.abs(MathHelper.wrapDegrees(pitch - MC.player.getPitch())) <= fovY;

        // If the targeted entity is within the fov, then, we will compute the step in yaw / pitch of the player's view
        // to get closer to the targeted entity. We will use the given stepX and stepY to compute that. Dividing by 100
        // reduces that step. Without that, we would need to show very low values to the user in the GUI, which is not
        // user-friendly. That way, instead of showing 0.05, we show 5.
        if(inFovX && inFovY) {
            float yawFinal, pitchFinal;
            yawFinal = ((MathHelper.wrapDegrees(yaw - MC.player.getYaw())) * stepX) / 100;
            pitchFinal = ((MathHelper.wrapDegrees(pitch - MC.player.getPitch())) * stepY) / 100;

            return new float[] { MC.player.getYaw() + yawFinal, MC.player.getPitch() + pitchFinal};
        } else {
            return new float[] { MC.player.getYaw(), MC.player.getPitch()};
        }
    }

    /**
     * Sets the position of the crosshair
     * @param yaw horizontal pos (degrees)
     * @param pitch vertical pos (degrees)
     */
    public static void setRotations(float yaw, float pitch) {
        Wrapper.MC.player.setYaw(yaw);
        Wrapper.MC.player.setPitch(pitch);
    }

    /**
     * @param entity anything as long as it's an entity
     * @return true if the player can attack the given entity
     */
    public static boolean canAttack(LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            // Check friend
            if (Friends.isFriend(entity.getDisplayName().getString())) return false;

            // Check team
            if (TeamFilter.getInstance().isActivated()) {
                if (TeamFilter.getInstance().isPlayerInSameTeamAs(entity)) return false;
            }
        }

        if (Antibot.getInstance().isActivated()) {
            if (Antibot.getInstance().isBot(entity)) return false;
        }

        return true;
    }
}
