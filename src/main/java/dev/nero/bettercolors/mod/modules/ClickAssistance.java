/*
 * Copyright 2018-2020 Bettercolors Contributors (https://github.com/N3ROO/Bettercolors)
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

package dev.nero.bettercolors.mod.modules;

import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.option.Option;
import dev.nero.bettercolors.engine.option.ToggleOption;
import dev.nero.bettercolors.engine.option.ValueOption;
import dev.nero.bettercolors.engine.utils.MathUtils;
import dev.nero.bettercolors.engine.utils.TimeHelper;
import dev.nero.bettercolors.mod.wrapper.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class ClickAssistance extends Module {

    // Prefix for AimAssistance (logging and settings)
    private static final String PREFIX = "CA";

    // Options name
    private static final String PACKETS = "Packets";
    private static final String ONLY_ON_ENTITY = "Only_on_entity";
    private static final String TEAM_FILTER = "Team_filter";
    private static final String ADDITIONAL_CLICKS = "Additional_clicks";
    private static final String CHANCE = "Chance";
    private static final String DURATION = "Duration";
    private static final String CLICKS_TO_ACTIVATE = "Clicks_to_activate";
    private static final String TIME_TO_ACTIVATE = "Time_to_activate";

    // Options index
    private static final int I_PACKETS = 0;
    private static final int I_ONLY_ON_ENTITY = 1;
    private static final int I_TEAM_FILTER = 2;
    private static final int I_ADDITIONAL_CLICKS = 3;
    private static final int I_CHANCE = 4;
    private static final int I_DURATION = 5;
    private static final int I_CLICKS_TO_ACTIVATE = 6;
    private static final int I_TIME_TO_ACTIVATE = 7;

    // Default options loading
    private static final ArrayList<Option> DEFAULT_OPTIONS;
    static{
        DEFAULT_OPTIONS = new ArrayList<>();

        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, PACKETS, false));
        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, ONLY_ON_ENTITY, false));
        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, TEAM_FILTER, true));

        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, ADDITIONAL_CLICKS, 2, 0, 5, 0, 1));
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, CHANCE, 80, 0, 100, 5, 25));
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, DURATION, 1500, 0, 10000, 200, 1000));
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, CLICKS_TO_ACTIVATE, 3, 0, 20, 1, 5));
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, TIME_TO_ACTIVATE, 1000, 0, 10000, 200, 1000));
    }

    private TimeHelper postActivationTimer;
    private int postActivationClickCounter;

    private TimeHelper activationTimer;
    private TimeHelper clickTimer;

    /**
     * @param toggle_key the toggle key (-1 -> none).
     * @param is_activated the initial state
     * @param options the options for the mod
     */
    public ClickAssistance(Integer toggle_key, Boolean is_activated, Map<String, String> options) {

        super("Click assistance", toggle_key, is_activated, "click_symbol.png", "[CA]");

        this.options = DEFAULT_OPTIONS;
        ((ToggleOption)
            this.options.get(I_PACKETS))
            .setActivated(Boolean.parseBoolean(options.get(this.options.get(I_PACKETS).getCompleteName()))
        );

        ((ToggleOption)
            this.options.get(I_ONLY_ON_ENTITY))
            .setActivated(Boolean.parseBoolean(options.get(this.options.get(I_ONLY_ON_ENTITY).getCompleteName()))
        );

        ((ToggleOption)
            this.options.get(I_TEAM_FILTER))
            .setActivated(Boolean.parseBoolean(options.get(this.options.get(I_TEAM_FILTER).getCompleteName()))
        );

        ((ValueOption)
            this.options.get(I_ADDITIONAL_CLICKS))
            .setVal(Integer.parseInt(options.get(this.options.get(I_ADDITIONAL_CLICKS).getCompleteName()))
        );

        ((ValueOption)
            this.options.get(I_CHANCE))
            .setVal(Integer.parseInt(options.get(this.options.get(I_CHANCE).getCompleteName()))
        );

        ((ValueOption)
            this.options.get(I_DURATION))
            .setVal(Integer.parseInt(options.get(this.options.get(I_DURATION).getCompleteName()))
        );

        ((ValueOption)
            this.options.get(I_CLICKS_TO_ACTIVATE))
            .setVal(Integer.parseInt(options.get(this.options.get(I_CLICKS_TO_ACTIVATE).getCompleteName()))
        );
        
        ((ValueOption)
            this.options.get(I_TIME_TO_ACTIVATE))
            .setVal(Integer.parseInt(options.get(this.options.get(I_TIME_TO_ACTIVATE).getCompleteName()))
        );

        postActivationTimer = new TimeHelper();
        postActivationClickCounter = 0;

        activationTimer = new TimeHelper();
        clickTimer = new TimeHelper();
    }

    @Override
    public void onUpdate() {
        if(Wrapper.thePlayer != null){
            if(activationTimer.isStopped()) {
                // If the click assist is not activated, we check if the user made the actions to activate it
                if (isKeyState(Key.ATTACK, KeyState.JUST_PRESSED) && postActivationTimer.isStopped()) {
                    // Attack pressed just pressed and timer stopped
                    postActivationTimer.start();
                    postActivationClickCounter = 1;
                } else if (isKeyState(Key.ATTACK, KeyState.JUST_PRESSED) && !postActivationTimer.isStopped()) {
                    postActivationClickCounter++;
                }

                int post_activation_duration = ((ValueOption) this.options.get(I_TIME_TO_ACTIVATE)).getVal();
                int post_activation_clicks = ((ValueOption) this.options.get(I_CLICKS_TO_ACTIVATE)).getVal();
                if (postActivationTimer.isDelayComplete(post_activation_duration)
                        && postActivationClickCounter >= post_activation_clicks) {
                    // The user clicked enough times in the given time, so the click assistance turns on
                    postActivationTimer.stop();
                    activationTimer.start();
                    clickTimer.start();
                    logInfo("Click assistance started.");
                }else if (postActivationTimer.isDelayComplete(post_activation_duration)
                        && postActivationClickCounter < post_activation_clicks) {
                    // The user did not click enough times in the given time, so the click assistance turns off
                    postActivationTimer.stop();
                }
            }

            boolean timerDone = activationTimer.isDelayComplete(((ValueOption) this.options.get(I_DURATION)).getVal());

            if(!activationTimer.isStopped() && (timerDone || Wrapper.isInGui())){
                activationTimer.stop();
                clickTimer.stop();
                logInfo("Click assistance stopped.");
            }

            if(!activationTimer.isStopped()){
                int cps = ((ValueOption) this.options.get(I_ADDITIONAL_CLICKS)).getVal();
                if(cps != 0) {
                    if (clickTimer.isDelayComplete(1000 / cps)) {
                        useClickAssist();
                        clickTimer.reset();
                    }
                }
            }
        }
    }

    public static ArrayList<Option> getDefaultOptions(){
        return DEFAULT_OPTIONS;
    }

    private void useClickAssist(){
        boolean packets = ((ToggleOption) this.options.get(I_PACKETS)).isActivated();
        boolean teamFilter = ((ToggleOption) this.options.get(I_TEAM_FILTER)).isActivated();
        boolean onlyOnEntity = ((ToggleOption) this.options.get(I_ONLY_ON_ENTITY)).isActivated();
        int chance = ((ValueOption) this.options.get(I_CHANCE)).getVal();

        Entity target = null;
        try{
            target = Wrapper.MC.pointedEntity;
        }catch (Exception ignored){}

        int rand = MathUtils.random(0, 100);
        if(rand <= chance){
            // If we care about being aiming at an entity, or if we use packets, we must make sure that the user is
            // aiming at an entity that is close enough
            if( (onlyOnEntity || packets) && target != null){
                boolean reachable = Wrapper.thePlayer.getDistance(target) <= Wrapper.MC.playerController.getBlockReachDistance();
                if (reachable && (teamFilter && !Wrapper.isInSameTeam(target))) {
                    if (packets) {
                        // We basically do what the minecraft client does when attacking an entity
                        Wrapper.MC.playerController.attackEntity(Wrapper.thePlayer, target);
                        Wrapper.thePlayer.swingArm(Hand.MAIN_HAND);
                    } else {
                        click();
                    }
                }
            } else if (!onlyOnEntity && !packets) {
                click();
            }
        }
    }

    /**
     * Human-like click (fake mouse click).
     */
    private void click(){
        Robot bot;
        try {
            bot = new Robot();
            bot.mouseRelease(16);
            bot.mousePress(16);
            bot.mouseRelease(16);
        } catch (AWTException e) {
            logError("Tried to click the mouse but a problem happened");
        }
    }
}
