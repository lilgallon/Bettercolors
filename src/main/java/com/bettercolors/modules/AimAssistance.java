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
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AimAssistance extends Module {
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
    private static final String DURATION = "AA_Duration";
    private static final String CLICKS_TO_ACTIVATE = "AA_Clicks_to_activate";
    private static final String TIME_TO_ACTIVATE = "AA_Time_to_activate";
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

        DEFAULT_OPTIONS.add(new ToggleOption(STOP_ON_RIGHT_CLICK, true));
        DEFAULT_OPTIONS.add(new ToggleOption(USE_ON_MOBS, false));
        DEFAULT_OPTIONS.add(new ToggleOption(TEAM_FILTER, true));
        DEFAULT_OPTIONS.add(new ToggleOption(STOP_WHEN_REACHED, true));

        DEFAULT_OPTIONS.add(new ValueOption(REFRESH_RATE, 2, 0, 10, 1, 5));
        DEFAULT_OPTIONS.add(new ValueOption(STEP_X, 5, 0, 20, 1, 5));
        DEFAULT_OPTIONS.add(new ValueOption(STEP_Y, 5, 0, 20, 1, 5));
        DEFAULT_OPTIONS.add(new ValueOption(RANGE, 5, 0, 10, 1, 5));
        DEFAULT_OPTIONS.add(new ValueOption(RADIUS_X, 60, 0, 180, 5, 25));
        DEFAULT_OPTIONS.add(new ValueOption(RADIUS_Y, 30, 0, 90, 3, 15));
        DEFAULT_OPTIONS.add(new ValueOption(DURATION, 1500, 0, 10000, 200, 1000));
        DEFAULT_OPTIONS.add(new ValueOption(CLICKS_TO_ACTIVATE, 3, 0, 20, 1, 5));
        DEFAULT_OPTIONS.add(new ValueOption(TIME_TO_ACTIVATE, 1000, 0, 10000, 200, 1000));
    }

    private TimeHelper _post_activation_timer;
    private int _post_activation_click_counter;

    private TimeHelper _activation_timer;
    private TimeHelper _refresh_rate_timer;

    private float shift_x = 0;
    private float shift_y = 0;

    public AimAssistance(String name, int toggle_key, boolean is_activated, Map<String, String> options, String symbol) {

        super(name, toggle_key, is_activated, symbol, "[AA]");

        _options = DEFAULT_OPTIONS;
        ((ToggleOption) _options.get(I_STOP_ON_RIGHT_CLICK)).setActivated(Boolean.parseBoolean(options.get(STOP_ON_RIGHT_CLICK)));
        ((ToggleOption) _options.get(I_USE_ON_MOBS)).setActivated(Boolean.parseBoolean(options.get(USE_ON_MOBS)));
        ((ToggleOption) _options.get(I_TEAM_FILTER)).setActivated(Boolean.parseBoolean(options.get(TEAM_FILTER)));
        ((ValueOption) _options.get(I_REFRESH_RATE)).setVal(Integer.parseInt(options.get(REFRESH_RATE)));
        ((ValueOption) _options.get(I_STEP_X)).setVal(Integer.parseInt(options.get(STEP_X)));
        ((ValueOption) _options.get(I_STEP_Y)).setVal(Integer.parseInt(options.get(STEP_Y)));
        ((ValueOption) _options.get(I_RANGE)).setVal(Integer.parseInt(options.get(RANGE)));
        ((ValueOption) _options.get(I_RADIUS_X)).setVal(Integer.parseInt(options.get(RADIUS_X)));
        ((ValueOption) _options.get(I_RADIUS_Y)).setVal(Integer.parseInt(options.get(RADIUS_Y)));
        ((ValueOption) _options.get(I_DURATION)).setVal(Integer.parseInt(options.get(DURATION)));
        ((ValueOption) _options.get(I_CLICKS_TO_ACTIVATE)).setVal(Integer.parseInt(options.get(CLICKS_TO_ACTIVATE)));
        ((ValueOption) _options.get(I_TIME_TO_ACTIVATE)).setVal(Integer.parseInt(options.get(TIME_TO_ACTIVATE)));

        _post_activation_timer = new TimeHelper();
        _post_activation_click_counter = 0;

        _activation_timer = new TimeHelper();
        _refresh_rate_timer = new TimeHelper();
    }

    @Override
    public void onUpdate() {

        if(MC.thePlayer != null){

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

    private void useAimAssist(boolean is_attack_key_pressed){

        // Random shift from the target (so it looks human)
        // -> Can be an option for a future update !
        if(is_attack_key_pressed){
            // Generate new shifts
            int shift_x_max = 10;
            int shift_x_min = -10;
            shift_x = MathUtils.random(shift_x_min, shift_x_max);
            int shift_y_max = 20;
            int shift_y_min = -20;
            shift_y = MathUtils.random(shift_y_min, shift_y_max);
        }

        List<? extends Entity> entities;
        if(((ToggleOption) _options.get(I_USE_ON_MOBS)).isActivated()){
            entities = MC.theWorld.loadedEntityList;
        }else{
            entities = MC.theWorld.playerEntities;
        }

        if(entities == null) return;

        // We retrieve all the entity that the user can aim at
        int range = ((ValueOption) _options.get(I_RANGE)).getVal();
        List<EntityLivingBase> attackable_entities = Lists.newArrayList();
        for(Entity entity : entities){
            if(entity instanceof EntityLivingBase){
                if(entity instanceof EntityPlayerSP)
                    continue;
                if(MC.thePlayer.getDistanceToEntity(entity) <= range && MC.thePlayer.canEntityBeSeen(entity))
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
            float distDirect = MathHelper.sqrt_float(distYaw*distYaw + distPitch*distPitch);

            if(distDirect < minDistDirect) {
                target = entity;
                minDistDirect = distDirect;
            }
        }

        if(target == null) return;

        boolean has_reached_a_living_entity = false;
        if(((ToggleOption) _options.get(I_STOP_WHEN_REACHED)).isActivated()) {
            try {
                Entity mouseOverEntity = MC.objectMouseOver.entityHit;
                if ((mouseOverEntity instanceof EntityLivingBase))
                    has_reached_a_living_entity = true;
            } catch (Exception ignored) {
            }
        }

        if(!has_reached_a_living_entity){
            aimEntity(target);
        }

    }

    private float[] getDiffFrom(EntityLivingBase entity){
        final double diffX = entity.posX - MC.thePlayer.posX;
        final double diffZ = entity.posZ - MC.thePlayer.posZ;
        double diffY = entity.posY + entity.getEyeHeight() - (MC.thePlayer.posY + MC.thePlayer.getEyeHeight());

        final double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

        final float yaw = (float) ((Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F ) + shift_x;
        final float pitch = (float) - (Math.atan2(diffY, dist) * 180.0D / Math.PI) 		   + shift_y;

        float distYaw = MathUtils.wrapAngleTo180_float(yaw - MC.thePlayer.rotationYaw);
        float distPitch = MathUtils.wrapAngleTo180_float(pitch - MC.thePlayer.rotationPitch);

        return new float[]{distYaw, distPitch};
    }

    private synchronized void aimEntity(EntityLivingBase entity) {
        final float[] rotations = getRotationsNeeded(entity);

        if (rotations != null) {
            MC.thePlayer.rotationYaw = rotations[0];
            MC.thePlayer.rotationPitch = rotations[1];
        }

        log_info("Aiming at entity " + entity.getName() + ".");
    }

    private float[] getRotationsNeeded(EntityLivingBase entity) {
        if (entity == null) {
            return null;
        }

        float radius_x = ((ValueOption) _options.get(I_RADIUS_X)).getVal();
        float radius_y = ((ValueOption) _options.get(I_RADIUS_Y)).getVal();
        float step_x = ((ValueOption) _options.get(I_STEP_X)).getVal();
        float step_y = ((ValueOption) _options.get(I_STEP_Y)).getVal();

        final double diffX = entity.posX - MC.thePlayer.posX;
        final double diffZ = entity.posZ - MC.thePlayer.posZ;
        double diffY = entity.posY + entity.getEyeHeight() - (MC.thePlayer.posY + MC.thePlayer.getEyeHeight());

        final double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        final float yaw = (float) ((Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F ) + shift_x;
        final float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI) 		   + shift_y;

        if(MathHelper.abs(MathUtils.wrapAngleTo180_float(yaw - MC.thePlayer.rotationYaw)) <=+ radius_x
                && MathHelper.abs(MathUtils.wrapAngleTo180_float(pitch - MC.thePlayer.rotationPitch)) <= radius_y){
            float yawFinal, pitchFinal;

            yawFinal = ((MathUtils.wrapAngleTo180_float(yaw - MC.thePlayer.rotationYaw)) * step_x) / 100;
            pitchFinal = ((MathUtils.wrapAngleTo180_float(pitch - MC.thePlayer.rotationPitch)) * step_y) / 100;

            return new float[] { MC.thePlayer.rotationYaw + yawFinal, MC.thePlayer.rotationPitch + pitchFinal};
        }else{
            return new float[] { MC.thePlayer.rotationYaw, MC.thePlayer.rotationPitch};
        }
    }
}
