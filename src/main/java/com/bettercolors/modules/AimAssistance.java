package com.bettercolors.modules;

import com.bettercolors.modules.options.Option;
import com.bettercolors.modules.options.ToggleOption;
import com.bettercolors.modules.options.ValueOption;
import com.bettercolors.utils.MathUtils;
import com.bettercolors.utils.TimeHelper;
import com.google.common.collect.Lists;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AimAssistance extends Module {

    private static final String PREFIX = "AA";

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

    private TimeHelper _post_activation_timer;
    private int _post_activation_click_counter;

    private TimeHelper _activation_timer;
    private TimeHelper _refresh_rate_timer;

    private float shift_x = 0;
    private float shift_y = 0;

    /**
     * @param name the name.
     * @param toggle_key the toggle key (-1 -> none).
     * @param is_activated the initial state.
     * @param options the options for the mod.
     * @param symbol the picture name.
     */
    public AimAssistance(String name, int toggle_key, boolean is_activated, Map<String, String> options, String symbol) {

        super(name, toggle_key, is_activated, symbol, "[AA]");

        _options = DEFAULT_OPTIONS;
        ((ToggleOption) _options.get(I_STOP_ON_RIGHT_CLICK)).setActivated(Boolean.parseBoolean(options.get(_options.get(I_STOP_ON_RIGHT_CLICK).getCompleteName())));
        ((ToggleOption) _options.get(I_USE_ON_MOBS)).setActivated(Boolean.parseBoolean(options.get(_options.get(I_USE_ON_MOBS).getCompleteName())));
        ((ToggleOption) _options.get(I_TEAM_FILTER)).setActivated(Boolean.parseBoolean(options.get(_options.get(I_TEAM_FILTER).getCompleteName())));
        ((ValueOption) _options.get(I_REFRESH_RATE)).setVal(Integer.parseInt(options.get(_options.get(I_REFRESH_RATE).getCompleteName())));
        ((ValueOption) _options.get(I_STEP_X)).setVal(Integer.parseInt(options.get(_options.get(I_STEP_X).getCompleteName())));
        ((ValueOption) _options.get(I_STEP_Y)).setVal(Integer.parseInt(options.get(_options.get(I_STEP_Y).getCompleteName())));
        ((ValueOption) _options.get(I_RANGE)).setVal(Integer.parseInt(options.get(_options.get(I_RANGE).getCompleteName())));
        ((ValueOption) _options.get(I_RADIUS_X)).setVal(Integer.parseInt(options.get(_options.get(I_RADIUS_X).getCompleteName())));
        ((ValueOption) _options.get(I_RADIUS_Y)).setVal(Integer.parseInt(options.get(_options.get(I_RADIUS_Y).getCompleteName())));
        ((ValueOption) _options.get(I_DURATION)).setVal(Integer.parseInt(options.get(_options.get(I_DURATION).getCompleteName())));
        ((ValueOption) _options.get(I_CLICKS_TO_ACTIVATE)).setVal(Integer.parseInt(options.get(_options.get(I_CLICKS_TO_ACTIVATE).getCompleteName())));
        ((ValueOption) _options.get(I_TIME_TO_ACTIVATE)).setVal(Integer.parseInt(options.get(_options.get(I_TIME_TO_ACTIVATE).getCompleteName())));

        _post_activation_timer = new TimeHelper();
        _post_activation_click_counter = 0;

        _activation_timer = new TimeHelper();
        _refresh_rate_timer = new TimeHelper();
    }

    @Override
    public void onUpdate() {
        if(MC.player != null){
            if(_activation_timer.isStopped()) {
                // If the aim assist is not activated, we check if the user made the actions to activate it
                if (isKeyState(KEY.ATTACK, KEY_STATE.JUST_PRESSED) && !_post_activation_timer.isStopped()) {
                    _post_activation_click_counter++;
                } else if (isKeyState(KEY.ATTACK, KEY_STATE.JUST_PRESSED) && _post_activation_timer.isStopped()) {
                    // Attack pressed just pressed and timer stopped
                    _post_activation_timer.start();
                    _post_activation_click_counter = 1;
                }

                int post_activation_duration = ((ValueOption) _options.get(I_TIME_TO_ACTIVATE)).getVal();
                int post_activation_clicks = ((ValueOption) _options.get(I_CLICKS_TO_ACTIVATE)).getVal();
                if(_post_activation_timer.isDelayComplete(post_activation_duration)
                        && _post_activation_click_counter >= post_activation_clicks){
                    _post_activation_timer.stop();
                    _activation_timer.start();
                    _refresh_rate_timer.start();
                    log_info("Aim assistance started.");
                }else if(_post_activation_timer.isDelayComplete(post_activation_duration)
                            && _post_activation_click_counter < post_activation_clicks){
                    _post_activation_timer.stop();
                }
            }

            if(!_activation_timer.isStopped() &&
                    ( isKeyState(KEY.USE, KEY_STATE.JUST_PRESSED) || _activation_timer.isDelayComplete(((ValueOption) _options.get(I_DURATION)).getVal()) || isInGui())){
                _activation_timer.stop();
                _refresh_rate_timer.stop();
                log_info("Aim assistance stopped.");
            }

            if(!_activation_timer.isStopped()){
                int refresh_rate = ((ValueOption) _options.get(I_REFRESH_RATE)).getVal();
                if(_refresh_rate_timer.isDelayComplete(refresh_rate)){
                    useAimAssist(isKeyState(KEY.ATTACK, KEY_STATE.JUST_PRESSED));
                    _refresh_rate_timer.reset();
                }
            }
        }
    }

    public static ArrayList<Option> getDefaultOptions(){
        return DEFAULT_OPTIONS;
    }

    /**
     * It calls all the functions to create the aim assistance.
     * @param is_attack_key_pressed if set to true, it will recalculate the random shifts with the target.
     */
    private void useAimAssist(boolean is_attack_key_pressed){

        // Random shift from the target (so it looks human)
        // -> Can be an option for a future update !
        if(is_attack_key_pressed){
            // Generate new shifts
            int shift_x_max = 7;
            int shift_x_min = -7;
            shift_x = MathUtils.random(shift_x_min, shift_x_max);
            int shift_y_max = 10;
            int shift_y_min = -10;
            shift_y = MathUtils.random(shift_y_min, shift_y_max);
        }

        List<? extends Entity> entities;
        if(((ToggleOption) _options.get(I_USE_ON_MOBS)).isActivated()){
            entities = MC.world.loadedEntityList;
        }else{
            entities = MC.world.playerEntities;
        }

        if(entities == null) return;

        // We retrieve all the entity that the user can aim at
        int range = ((ValueOption) _options.get(I_RANGE)).getVal();
        List<EntityLivingBase> attackable_entities = Lists.newArrayList();
        for(Entity entity : entities){
            if(entity instanceof EntityLivingBase){
                if(entity instanceof EntityPlayerSP)
                    continue;
                if(MC.player.getDistance(entity) <= range && MC.player.canEntityBeSeen(entity))
                    attackable_entities.add((EntityLivingBase) entity);
            }
        }

        if(attackable_entities.size() == 0) return;

        // Now we chose the entity to attack (we take the closest one)
        EntityLivingBase target = null;
        float minDistDirect = Float.MAX_VALUE;

        boolean team_filter = ((ToggleOption) _options.get(I_TEAM_FILTER)).isActivated();
        for(EntityLivingBase entity : attackable_entities){
            if(team_filter && isInSameTeam(entity)) continue;

            // Calculate distances
            float distYaw = MathHelper.abs(getDiffFrom(entity)[0]);
            float distPitch = MathHelper.abs(getDiffFrom(entity)[1]);
            float distDirect = MathHelper.sqrt(distYaw*distYaw + distPitch*distPitch);

            if(distDirect < minDistDirect) {
                target = entity;
                minDistDirect = distDirect;
            }
        }

        if(target == null) return;

        boolean has_reached_a_living_entity = false;
        if(((ToggleOption) _options.get(I_STOP_WHEN_REACHED)).isActivated()) {
            try {
                Entity mouseOverEntity = MC.objectMouseOver.entity;
                if ((mouseOverEntity instanceof EntityLivingBase))
                    has_reached_a_living_entity = true;
            } catch (Exception ignored) {
            }
        }

        if(!has_reached_a_living_entity){
            aimEntity(target);
        }

    }

    /**
     * @param entity the target to aim.
     * @return the [x, y] difference from the player aim to the target.
     */
    private float[] getDiffFrom(EntityLivingBase entity){
        final double diffX = entity.posX - MC.player.posX;
        final double diffZ = entity.posZ - MC.player.posZ;
        double diffY = entity.posY + entity.getEyeHeight() - (MC.player.posY + MC.player.getEyeHeight());

        final double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);

        final float yaw = (float) ((Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F ) + shift_x;
        final float pitch = (float) - (Math.atan2(diffY, dist) * 180.0D / Math.PI) 		   + shift_y;

        // TODO: check if it's right (it was wrapAngleTo180_float)
        float distYaw = MathHelper.wrapDegrees(yaw - MC.player.rotationYaw);
        float distPitch = MathHelper.wrapDegrees(pitch - MC.player.rotationPitch);

        return new float[]{distYaw, distPitch};
    }

    /**
     * @param entity the entity to aim.
     */
    private synchronized void aimEntity(EntityLivingBase entity) {
        final float[] rotations = getRotationsNeeded(entity);

        if (rotations != null) {
            MC.player.rotationYaw = rotations[0];
            MC.player.rotationPitch = rotations[1];
        }

        log_info("Aiming at entity " + entity.getName() + ".");
    }

    /**
     * @param entity the target to aim.
     * @return the [x, y] new positions of the player aim.
     */
    private float[] getRotationsNeeded(EntityLivingBase entity) {
        if (entity == null) {
            return null;
        }

        float radius_x = ((ValueOption) _options.get(I_RADIUS_X)).getVal();
        float radius_y = ((ValueOption) _options.get(I_RADIUS_Y)).getVal();
        float step_x = ((ValueOption) _options.get(I_STEP_X)).getVal();
        float step_y = ((ValueOption) _options.get(I_STEP_Y)).getVal();

        final double diffX = entity.posX - MC.player.posX;
        final double diffZ = entity.posZ - MC.player.posZ;
        double diffY = entity.posY + entity.getEyeHeight() - (MC.player.posY + MC.player.getEyeHeight());

        final double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float) ((Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F ) + shift_x;
        final float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI) 		   + shift_y;

        if(MathHelper.abs(MathHelper.wrapDegrees(yaw - MC.player.rotationYaw)) <=+ radius_x
                && MathHelper.abs(MathHelper.wrapDegrees(pitch - MC.player.rotationPitch)) <= radius_y){
            float yawFinal, pitchFinal;

            yawFinal = ((MathHelper.wrapDegrees(yaw - MC.player.rotationYaw)) * step_x) / 100;
            pitchFinal = ((MathHelper.wrapDegrees(pitch - MC.player.rotationPitch)) * step_y) / 100;

            return new float[] { MC.player.rotationYaw + yawFinal, MC.player.rotationPitch + pitchFinal};
        }else{
            return new float[] { MC.player.rotationYaw, MC.player.rotationPitch};
        }
    }
}
