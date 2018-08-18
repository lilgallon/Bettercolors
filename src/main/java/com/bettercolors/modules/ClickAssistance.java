package com.bettercolors.modules;

import com.bettercolors.modules.options.Option;
import com.bettercolors.modules.options.ToggleOption;
import com.bettercolors.modules.options.ValueOption;
import com.bettercolors.utils.MathUtils;
import com.bettercolors.utils.TimeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C02PacketUseEntity;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class ClickAssistance extends Module {

    private static final String PACKETS = "Packets";
    private static final String ONLY_ON_ENTITY = "Only_on_entity";
    private static final String TEAM_FILTER = "Team_filter";
    private static final String ADDITIONAL_CLICKS = "Additional_clicks";
    private static final String CHANCE = "Chance";
    private static final String DURATION = "Duration";
    private static final String CLICKS_TO_ACTIVATE = "CA_Clicks_to_activate";
    private static final String TIME_TO_ACTIVATE = "CA_Time_to_activate";
    private static final int I_PACKETS = 0;
    private static final int I_ONLY_ON_ENTITY = 1;
    private static final int I_TEAM_FILTER = 2;
    private static final int I_ADDITIONAL_CLICKS = 3;
    private static final int I_CHANCE = 4;
    private static final int I_DURATION = 5;
    private static final int I_CLICKS_TO_ACTIVATE = 6;
    private static final int I_TIME_TO_ACTIVATE = 7;

    private static final ArrayList<Option> DEFAULT_OPTIONS;
    static{
        DEFAULT_OPTIONS = new ArrayList<>();

        DEFAULT_OPTIONS.add(new ToggleOption(PACKETS, false));
        DEFAULT_OPTIONS.add(new ToggleOption(ONLY_ON_ENTITY, false));
        DEFAULT_OPTIONS.add(new ToggleOption(TEAM_FILTER, true));

        DEFAULT_OPTIONS.add(new ValueOption(ADDITIONAL_CLICKS, 2, 0, 5, 0, 1));
        DEFAULT_OPTIONS.add(new ValueOption(CHANCE, 80, 0, 100, 5, 25));
        DEFAULT_OPTIONS.add(new ValueOption(DURATION, 1500, 0, 10000, 200, 1000));
        DEFAULT_OPTIONS.add(new ValueOption(CLICKS_TO_ACTIVATE, 3, 0, 20, 1, 5));
        DEFAULT_OPTIONS.add(new ValueOption(TIME_TO_ACTIVATE, 1000, 0, 10000, 200, 1000));
    }

    private TimeHelper _post_activation_timer;
    private int _post_activation_click_counter;

    private TimeHelper _activation_timer;
    private TimeHelper _click_timer;

    public ClickAssistance(String name, int toggle_key, boolean is_activated, Map<String, String> options, String symbol) {

        super(name, toggle_key, is_activated, symbol, "[CA]");

        _options = DEFAULT_OPTIONS;
        ((ToggleOption) _options.get(I_PACKETS)).setActivated(Boolean.parseBoolean(options.get(PACKETS)));
        ((ToggleOption) _options.get(I_ONLY_ON_ENTITY)).setActivated(Boolean.parseBoolean(options.get(ONLY_ON_ENTITY)));
        ((ToggleOption) _options.get(I_TEAM_FILTER)).setActivated(Boolean.parseBoolean(options.get(TEAM_FILTER)));
        ((ValueOption) _options.get(I_ADDITIONAL_CLICKS)).setVal(Integer.parseInt(options.get(ADDITIONAL_CLICKS)));
        ((ValueOption) _options.get(I_CHANCE)).setVal(Integer.parseInt(options.get(CHANCE)));
        ((ValueOption) _options.get(I_DURATION)).setVal(Integer.parseInt(options.get(DURATION)));
        ((ValueOption) _options.get(I_CLICKS_TO_ACTIVATE)).setVal(Integer.parseInt(options.get(CLICKS_TO_ACTIVATE)));
        ((ValueOption) _options.get(I_TIME_TO_ACTIVATE)).setVal(Integer.parseInt(options.get(TIME_TO_ACTIVATE)));

        _post_activation_timer = new TimeHelper();
        _post_activation_click_counter = 0;

        _activation_timer = new TimeHelper();
        _click_timer = new TimeHelper();
    }

    @Override
    public void onUpdate() {
        if(_mc.thePlayer != null){

            if(_activation_timer.isStopped()) {
                // If the click assist is not activated, we check if the user made the actions to activate it
                if (isKeyState(KEY.ATTACK, KEY_STATE.JUST_PRESSED) && _post_activation_timer.isStopped()) {
                    // Attack pressed just pressed and timer stopped
                    _post_activation_timer.start();
                    _post_activation_click_counter = 1;
                } else if (isKeyState(KEY.ATTACK, KEY_STATE.JUST_PRESSED) && !_post_activation_timer.isStopped()) {
                    _post_activation_click_counter++;
                }

                int post_activation_duration = ((ValueOption) _options.get(I_TIME_TO_ACTIVATE)).getVal();
                int post_activation_clicks = ((ValueOption) _options.get(I_CLICKS_TO_ACTIVATE)).getVal();
                if(_post_activation_timer.isDelayComplete(post_activation_duration)
                        && _post_activation_click_counter >= post_activation_clicks){
                    _post_activation_timer.stop();
                    _activation_timer.start();
                    _click_timer.start();
                    log_info("Click assistance started.");
                }else if(_post_activation_timer.isDelayComplete(post_activation_duration)
                        && _post_activation_click_counter < post_activation_clicks){
                    _post_activation_timer.stop();
                }
            }

            if(!_activation_timer.isStopped() &&
                    ( _activation_timer.isDelayComplete(((ValueOption) _options.get(I_DURATION)).getVal()) || isInGui())){
                _activation_timer.stop();
                _click_timer.stop();
                log_info("Click assistance stopped.");
            }

            if(!_activation_timer.isStopped()){
                int cps = ((ValueOption) _options.get(I_ADDITIONAL_CLICKS)).getVal();
                if(cps != 0) {
                    if (_click_timer.isDelayComplete(1000 / cps)) {
                        useClickAssist();
                        _click_timer.reset();
                    }
                }
            }
        }
    }

    @Override
    void onEnable() {

    }

    @Override
    void onDisable() {

    }

    public static ArrayList<Option> getDefaultOptions(){
        return DEFAULT_OPTIONS;
    }

    private void useClickAssist(){
        boolean packets = ((ToggleOption) _options.get(I_PACKETS)).isActivated();
        boolean team_filter = ((ToggleOption) _options.get(I_TEAM_FILTER)).isActivated();
        boolean only_on_entity = ((ToggleOption) _options.get(I_ONLY_ON_ENTITY)).isActivated();
        int chance = ((ValueOption) _options.get(I_CHANCE)).getVal();

        Entity target = null;
        try{
            target = _mc.objectMouseOver.entityHit;
        }catch (Exception ignored){}

        int rand = MathUtils.random(0, 100);
        if(rand <= chance){
            if( (only_on_entity || packets) && target != null){
                if (_mc.thePlayer.getDistanceToEntity(target) <= _mc.playerController.getBlockReachDistance() &&
                        (team_filter && !isInSameTeam(target))) {
                    if (packets) {
                        _mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                        _mc.thePlayer.swingItem();
                    } else {
                        click();
                    }
                }
            }else if(!only_on_entity && !packets){
                click();
            }
        }
    }

    private void click(){
        Robot bot;
        try{
            bot = new Robot();
            bot.mouseRelease(16);
            bot.mousePress(16);
            bot.mouseRelease(16);
        } catch (AWTException e){
            log_error("Tried to click the mouse but a problem happened.");
        }
    }
}
