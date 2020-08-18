package dev.nero.bettercolors.core.modules;

import dev.nero.bettercolors.engine.option.Option;
import dev.nero.bettercolors.engine.option.ToggleOption;
import dev.nero.bettercolors.engine.option.ValueFloatOption;
import dev.nero.bettercolors.engine.option.ValueOption;
import dev.nero.bettercolors.engine.utils.Friends;
import dev.nero.bettercolors.engine.utils.MathUtils;
import dev.nero.bettercolors.engine.utils.TimeHelper;
import com.google.common.collect.Lists;
import dev.nero.bettercolors.core.wrapper.Wrapper;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AimAssistance extends BetterModule {

    // Prefix for AimAssistance (logging and settings)
    private static final String PREFIX = "AA";

    // Options name
    private static final String STOP_ON_RIGHT_CLICK = "Stop_on_right_click";
    private static final String USE_ON_MOBS = "Use_on_mobs";
    private static final String TEAM_FILTER = "Team_filter";
    private static final String STOP_WHEN_REACHED = "Stop_when_reached";
    private static final String REFRESH_RATE = "Refresh_rate";
    private static final String STEP_X = "Step_X";
    private static final String STEP_Y = "Step_Y";
    private static final String RANGE = "Range";
    private static final String RADIUS_X = "Radius_X";
    private static final String RADIUS_Y = "Radius_Y";
    private static final String DURATION = "Duration";
    private static final String CLICKS_TO_ACTIVATE = "Clicks_to_activate";
    private static final String TIME_TO_ACTIVATE = "Time_to_activate";

    // Options index
    private static final int I_STOP_ON_RIGHT_CLICK = 0;
    private static final int I_USE_ON_MOBS = 1;
    private static final int I_TEAM_FILTER = 2;
    private static final int I_STOP_WHEN_REACHED = 3;
    private static final int I_REFRESH_RATE = 4;
    private static final int I_STEP_X = 5;
    private static final int I_STEP_Y = 6;
    private static final int I_RANGE = 7;
    private static final int I_RADIUS_X = 8;
    private static final int I_RADIUS_Y = 9;
    private static final int I_DURATION = 10;
    private static final int I_CLICKS_TO_ACTIVATE = 11;
    private static final int I_TIME_TO_ACTIVATE = 12;

    // Default options loading
    private static final ArrayList<Option> DEFAULT_OPTIONS;
    static{
        DEFAULT_OPTIONS = new ArrayList<>();

        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, STOP_ON_RIGHT_CLICK, true));
        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, USE_ON_MOBS, false));
        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, TEAM_FILTER, true));
        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, STOP_WHEN_REACHED, false));

        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, REFRESH_RATE, 2, 0, 10, 1, 5));
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, STEP_X, 5, 0, 20, 1, 5));
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, STEP_Y, 5, 0, 20, 1, 5));
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, RANGE, 5, 0, 10, 1, 5));
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, RADIUS_X, 60, 0, 180, 5, 25));
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, RADIUS_Y, 30, 0, 90, 3, 15));
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, DURATION, 2000, 0, 10000, 200, 1000));
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, CLICKS_TO_ACTIVATE, 2, 0, 20, 1, 5));
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, TIME_TO_ACTIVATE, 700, 0, 10000, 200, 1000));
    }

    private TimeHelper postActivationTimer;
    private int postActivationClickCounter;

    private TimeHelper activationTimer;
    private TimeHelper refreshRateTimer;

    private float shiftX = 0;
    private float shiftY = 0;

    /**
     * @param toggleKey the toggle key (-1 -> none)
     * @param IsActivated the initial state
     * @param givenOptions the options for the mod
     */
    public AimAssistance(Integer toggleKey, Boolean IsActivated, Map<String, String> givenOptions) {

        super("Aim assistance", toggleKey, IsActivated, "magnet.png", PREFIX);

        this.options = new ArrayList<>();

        for (Option defaultOption : DEFAULT_OPTIONS) {
            Option option = (Option) defaultOption.clone();
            String name = defaultOption.getCompleteName();

            if (option instanceof ToggleOption) {
                ((ToggleOption) option).setActivated(
                        Boolean.parseBoolean(givenOptions.get(name))
                );
            } else if (option instanceof ValueOption) {
                ((ValueOption) option).setVal(
                        Integer.parseInt(givenOptions.get(name))
                );
            } else if (option instanceof ValueFloatOption) {
                ((ValueFloatOption) option).setVal(
                        Float.parseFloat(givenOptions.get(name))
                );
            }

            this.options.add(option);
        }

        postActivationTimer = new TimeHelper();
        postActivationClickCounter = 0;

        activationTimer = new TimeHelper();
        refreshRateTimer = new TimeHelper();
    }

    @Override
    public void onUpdate() {
        if(Wrapper.MC.player != null){
            if(activationTimer.isStopped()) {
                // If the aim assist is not activated, we check if the user made the actions to activate it
                if (isKeyState(Key.ATTACK, KeyState.JUST_PRESSED) && !postActivationTimer.isStopped()) {
                    postActivationClickCounter++;
                } else if (isKeyState(Key.ATTACK, KeyState.JUST_PRESSED) && postActivationTimer.isStopped()) {
                    // Attack pressed just pressed and timer stopped
                    postActivationTimer.start();
                    postActivationClickCounter = 1;
                }

                int postActivationDuration = ((ValueOption) this.options.get(I_TIME_TO_ACTIVATE)).getVal();
                int postActivationClicks = ((ValueOption) this.options.get(I_CLICKS_TO_ACTIVATE)).getVal();
                if (postActivationTimer.isDelayComplete(postActivationDuration)
                        && postActivationClickCounter >= postActivationClicks) {

                    // The user clicked enough times in the given time, so the aim assistance turns on
                    postActivationTimer.stop();
                    activationTimer.start();
                    refreshRateTimer.start();
                    logInfo("Aim assistance started.");
                } else if(postActivationTimer.isDelayComplete(postActivationDuration)
                            && postActivationClickCounter < postActivationClicks) {
                    // The user did not click enough times in the given time, so the aim assistance turns on
                    postActivationTimer.stop();
                }
            }

            boolean rightClick = isKeyState(Key.USE, KeyState.JUST_PRESSED);
            boolean stopOnRightClick = ((ToggleOption) this.options.get(I_STOP_ON_RIGHT_CLICK)).isActivated();
            boolean timerDone = activationTimer.isDelayComplete(((ValueOption) this.options.get(I_DURATION)).getVal());

            if(!activationTimer.isStopped() && (rightClick && stopOnRightClick) || timerDone || Wrapper.isInGui()){
                activationTimer.stop();
                refreshRateTimer.stop();
                logInfo("Aim assistance stopped.");
            }

            // If the AimAssistance is turned on, then, help the user to aim
            if(!activationTimer.isStopped()){
                int refreshRate = ((ValueOption) this.options.get(I_REFRESH_RATE)).getVal();
                if(refreshRateTimer.isDelayComplete(refreshRate)){
                    useAimAssist(isKeyState(Key.ATTACK, KeyState.JUST_PRESSED));
                    refreshRateTimer.reset();
                }
            }
        }
    }

    public static ArrayList<Option> getDefaultOptions(){
        return DEFAULT_OPTIONS;
    }

    /**
     * It calls all the functions to create the aim assistance.
     * @param attackKeyPressed if set to true, it will recalculate the random shifts with the target.
     */
    private void useAimAssist(boolean attackKeyPressed){

        // Random shift from the target (so it looks human)
        // -> Can be an option for a future update !
        if(attackKeyPressed){
            // Generate new shifts
            int shiftXMax = 7;
            int shiftXMin = -7;
            shiftX = MathUtils.random(shiftXMin, shiftXMax);

            int shiftYMax = 10;
            int shiftYMin = -10;
            shiftY = MathUtils.random(shiftYMin, shiftYMax);
        }

        int range = ((ValueOption) this.options.get(I_RANGE)).getVal();

        int playerX = (int) Wrapper.MC.player.getPosX();
        int playerY = (int) Wrapper.MC.player.getPosY();
        int playerZ = (int) Wrapper.MC.player.getPosZ();
        // The area that will be scanned to find entities
        AxisAlignedBB area = new AxisAlignedBB(
                playerX - range,
                playerY - range,
                playerZ - range,
                playerX + range,
                playerY + range,
                playerZ + range
        );

        // Create the entities list by taking mobs (or not) into account
        List<LivingEntity> entities;
        if(((ToggleOption) this.options.get(I_USE_ON_MOBS)).isActivated()){
            entities = Wrapper.MC.world.getEntitiesWithinAABB(LivingEntity.class, area);
        }else{
            entities = Wrapper.MC.world.getEntitiesWithinAABB(PlayerEntity.class, area);
        }

        // We retrieve all the entities that the user can aim at
        List<LivingEntity> attackableEntities = Lists.newArrayList();
        for(Entity entity : entities){
            if(entity instanceof LivingEntity){
                if(entity instanceof ClientPlayerEntity) // If the entity is the player itself
                    continue;
                // The area is a 3D square of range * range around the player. But it does not mean that everything
                // inside of it is at or below <range> distance. If an entity is at the corner, then the distance from
                // the player is sqrt(range^2 * 3), which is 17.32 for 10 for example.
                // That's why we need to verify that the entity is within the given range
                if(Wrapper.MC.player.getDistance(entity) <= range && Wrapper.MC.player.canEntityBeSeen(entity))
                    attackableEntities.add((LivingEntity) entity);
            }
        }

        // If we can't find any entity, then we stop
        if(attackableEntities.size() == 0) return;

        // Now we chose the entity to attack: we take the closest one to the player's fov (defined by distYaw
        // and distPitch)
        LivingEntity target = null;
        float minDist = Float.MAX_VALUE;

        boolean team_filter = ((ToggleOption) this.options.get(I_TEAM_FILTER)).isActivated();
        for(LivingEntity entity : attackableEntities){
            if((team_filter && Wrapper.isInSameTeam(entity)) || Friends.isFriend(entity.getName().toString()))  continue;

            // Calculate fov
            float[] yawPitch = getYawPitchBetween(entity, Wrapper.MC.player);
            float distYaw = MathHelper.abs(MathHelper.wrapDegrees(yawPitch[0] - Wrapper.MC.player.rotationYaw));
            float distPitch = MathHelper.abs(MathHelper.wrapDegrees(yawPitch[1] - Wrapper.MC.player.rotationPitch));
            float dist = MathHelper.sqrt(distYaw*distYaw + distPitch*distPitch);

            // Take the one that is the closer to the fov (closest to the player aim)
            if(dist < minDist) {
                target = entity;
                minDist = dist;
            }
        }

        if(target == null) return;

        // If the option is toggled, we stop the aim assistance when the aim has reached the targeted entity
        boolean hasReachedALivingEntity = false;
        if(((ToggleOption) this.options.get(I_STOP_WHEN_REACHED)).isActivated()) {
            try {
                Entity mouseOverEntity = Wrapper.MC.pointedEntity;
                if ((mouseOverEntity instanceof LivingEntity))
                    hasReachedALivingEntity = true;
            } catch (Exception ignored) { }
        }

        if(!hasReachedALivingEntity){
            aimEntity(target);
        }
    }

    /**
     * @param entity the entity to aim.
     */
    private synchronized void aimEntity(LivingEntity entity) {
        final float[] rotations = getRotationsNeeded(entity);

        // Wrapper.MC.player is always null, because we call this function only if Wrapper.MC.player is not null, at least, we get rid
        // of the warning
        if (rotations != null && Wrapper.MC.player != null) {
            Wrapper.MC.player.rotationYaw = rotations[0];
            Wrapper.MC.player.rotationPitch = rotations[1];
        }

        logInfo("Aiming at entity " + entity.getName().getString() + ".");
    }

    /**
     * @param entityA an entity
     * @param entityB an other one
     * @return the [yaw, pitch] difference between the two entities
     */
    private float[] getYawPitchBetween(LivingEntity entityA, LivingEntity entityB) {
        double diffX = entityA.getPosX() - entityB.getPosX();
        double diffZ = entityA.getPosZ() - entityB.getPosZ();
        double diffY = entityA.getPosY() + entityA.getEyeHeight() - (entityB.getPosY() + entityB.getEyeHeight());

        double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) ((Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F ) + shiftX;
        float pitch = (float) - (Math.atan2(diffY, dist) * 180.0D / Math.PI) + shiftY;

        return new float[] { yaw, pitch };
    }

    /**
     * @param entity the target to aim.
     * @return the [x, y] new positions of the player aim.
     */
    private float[] getRotationsNeeded(LivingEntity entity) {
        if (entity == null) {
            return null;
        }

        // Settings about the FOV
        float radiusX = ((ValueOption) this.options.get(I_RADIUS_X)).getVal();
        float radiusY = ((ValueOption) this.options.get(I_RADIUS_Y)).getVal();

        // Settings about the force of the aim assistance
        float stepX = ((ValueOption) this.options.get(I_STEP_X)).getVal();
        float stepY = ((ValueOption) this.options.get(I_STEP_Y)).getVal();

        // We calculate the yaw/pitch difference between the entity and the player
        float[] yawPitch = getYawPitchBetween(entity, Wrapper.MC.player);

        // We make sure that it's absolute, because the sign may change if we invert entity and Wrapper.MC.player
        //float yaw = MathHelper.abs(yawPitch[0]);
        //float pitch = MathHelper.abs(yawPitch[1]);
        float yaw = yawPitch[0];
        float pitch = yawPitch[1];

        // We check if the entity is within the FOV of the player
        // yaw and pitch are absolute, not relative to anything. We fix that by calling wrapDegrees and substracting
        // the yaw & pitch to the player's rotation. Now, the yaw, and the pitch are relative to the player's view
        // So we can compare that with the given fov: radiusX, and radiusY (which are both in degrees)
        boolean inFovX = MathHelper.abs(MathHelper.wrapDegrees(yaw - Wrapper.MC.player.rotationYaw)) <= radiusX;
        boolean inFovY = MathHelper.abs(MathHelper.wrapDegrees(pitch - Wrapper.MC.player.rotationPitch)) <= radiusY;

        // If the targeted entity is within the fov, then, we will compute the step in yaw / pitch of the player's view
        // to get closer to the targeted entity. We will use the given stepX and stepY to compute that. Dividing by 100
        // reduces that step. Without that, we would need to show very low values to the user in the GUI, which is not
        // user-friendly. That way, instead of showing 0.05, we show 5.
        if(inFovX && inFovY) {
            float yawFinal, pitchFinal;
            yawFinal = ((MathHelper.wrapDegrees(yaw - Wrapper.MC.player.rotationYaw)) * stepX) / 100;
            pitchFinal = ((MathHelper.wrapDegrees(pitch - Wrapper.MC.player.rotationPitch)) * stepY) / 100;

            return new float[] { Wrapper.MC.player.rotationYaw + yawFinal, Wrapper.MC.player.rotationPitch + pitchFinal};
        } else {
            return new float[] { Wrapper.MC.player.rotationYaw, Wrapper.MC.player.rotationPitch};
        }
    }
}
