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

package dev.nero.bettercolors.core.modules;

import dev.nero.bettercolors.core.events.EventType;
import dev.nero.bettercolors.engine.BettercolorsEngine;
import dev.nero.bettercolors.engine.option.Option;
import dev.nero.bettercolors.engine.option.ToggleOption;
import dev.nero.bettercolors.engine.option.ValueFloatOption;
import dev.nero.bettercolors.engine.option.ValueOption;
import dev.nero.bettercolors.engine.utils.MathUtils;
import dev.nero.bettercolors.engine.utils.TimeHelper;
import dev.nero.bettercolors.engine.view.Window;
import dev.nero.bettercolors.core.wrapper.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RayTraceResult;

import java.util.ArrayList;
import java.util.Map;

public class ClickAssistance extends BetterModule {

    // Prefix for AimAssistance (logging and settings)
    private static final String PREFIX = "CA";

    // Options name
    private static final String PACKETS = "Packets";
    private static final String ONLY_ON_ENTITY = "Only_on_entity";
    private static final String TEAM_FILTER = "Team_filter";
    private static final String ADDITIONAL_CPS = "Additional_CPS";
    private static final String CHANCE = "Chance";
    private static final String DURATION = "Duration";
    private static final String CPS_TO_ACTIVATE = "CPS_to_activate";

    // Options index
    private static final int I_PACKETS = 0;
    private static final int I_ONLY_ON_ENTITY = 1;
    private static final int I_ADDITIONAL_CPS = 2;
    private static final int I_CHANCE = 3;
    private static final int I_DURATION = 4;
    private static final int I_CPS_TO_ACTIVATE = 5;

    // Default options loading
    private static final ArrayList<Option> DEFAULT_OPTIONS;
    static{
        DEFAULT_OPTIONS = new ArrayList<>();

        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, PACKETS, false));
        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, ONLY_ON_ENTITY, false));
        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, TEAM_FILTER, true));

        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, ADDITIONAL_CPS, 2, 0, 5, 0, 1));
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, CHANCE, 80, 0, 100, 5, 25));
        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, DURATION, 1500, 0, 10000, 200, 1000));

        DEFAULT_OPTIONS.add(new ValueFloatOption(PREFIX, CPS_TO_ACTIVATE, 4, 0, 10, 1, 5));
    }


    private boolean assist;
    private int attackCount;
    private final TimeHelper attackTimer;
    private final TimeHelper attackDelay;
    private final TimeHelper activationTimer;

    /**
     * @param toggleKey the toggle key (-1 -> none).
     * @param IsActivated the initial state
     * @param givenOptions the options for the mod
     */
    public ClickAssistance(Integer toggleKey, Boolean IsActivated, Map<String, String> givenOptions) {
        super("Click assistance", toggleKey, IsActivated, "click.png", PREFIX);
        this.loadOptionsAccordingTo(DEFAULT_OPTIONS, givenOptions);

        this.assist = false;
        this.attackCount = 0;

        this.attackTimer = new TimeHelper();
        this.attackTimer.stop();

        this.attackDelay = new TimeHelper();
        this.attackDelay.stop();

        this.activationTimer = new TimeHelper();
        this.activationTimer.stop();
    }

    @Override
    protected void onToggle(boolean toggle, boolean isTriggeredByKeybind) {
        if (toggle) {
            if (BettercolorsEngine.getInstance().getModule("Triggerbot").isActivated()) {
                if (!isTriggeredByKeybind)
                    Window.getInstance().dialog("Click assistance can't be used along with triggerbot. Triggerbot will" +
                            " be turned off.\n Also, Don't abuse of the click assistance. It can get you banned with" +
                            " high values. Keep the values low and you will be safe.");
                BettercolorsEngine.getInstance().toggleModule("Triggerbot", false);
            } else {
                if (!isTriggeredByKeybind)
                    Window.getInstance().dialog(
                            "Don't abuse of the click assistance. It can get you banned with high values. Keep the " +
                            " values low and you will be safe."
                    );
            }
        }
    }

    @Override
    protected void onEvent(int code, Object details) {
        if (!this.isActivated()) return;
        if (Wrapper.MC.player == null) return;
        if (Wrapper.isInGui()) return;

        switch (code) {
            case EventType.MOUSE_INPUT:
                break;

            case EventType.WORLD_TICK:
                analyseBehaviour();
                assistIfPossible();
                break;
        }
    }

    private void analyseBehaviour() {
        // Settings
        final float SPEED_TO_ACTIVATE = this.getOptionF(I_CPS_TO_ACTIVATE) / 1000f;
        final int ACTIVATION_DURATION = this.getOptionI(I_DURATION);
        final int TEST_DURATION = 1500; // ms: time to check if the speed is reached

        boolean playerAttacks = this.playerAttacks();

        // First time that the player attacks
        if (this.attackCount == 0 && playerAttacks) {
            this.attackCount += 1;
            this.attackTimer.start();
        }
        // If it's not the first time that the player attacked
        else if (this.attackCount > 0 && playerAttacks) {
            this.attackCount += 1;

            // Calculate the number of attacks per miliseconds
            float speed = (float) this.attackCount / (float) this.attackTimer.getTimeElapsed();

            // If player's attack speed is greater than the speed given to toggle the assistance, then we can tell to
            // the instance that the player is interacting
            if (speed > SPEED_TO_ACTIVATE) {
                // We need to reset the variables that are used to define if the player is interacting because we know
                // that the user is interacting right now
                this.attackCount = 0;
                this.attackTimer.stop();

                this.activationTimer.start(); // it will reset if already started, so we're all good

                this.assist = true;
                this.attackDelay.start();
            }
        }
        // If the player did not attack for that period of time, we give up and reset everything
        else if (this.attackTimer.isDelayComplete(TEST_DURATION)) {
            this.attackTimer.stop();
            this.attackCount = 0;
        }

        // Stop the interaction once that the delay is reached
        if (this.activationTimer.isDelayComplete(ACTIVATION_DURATION)) {
            this.stop();
        }
    }

    private void assistIfPossible() {
        if (!this.assist) return;
        if (!this.attackDelay.isDelayComplete(1000 / this.getOptionI(I_ADDITIONAL_CPS))) return;

        // Settings
        final boolean PACKETS = this.getOptionB(I_PACKETS);
        final boolean ONLY_ON_ENTITY = this.getOptionB(I_ONLY_ON_ENTITY);
        final int CHANCE = this.getOptionI(I_CHANCE);

        boolean pointedEntity = false;
        if (Wrapper.MC.objectMouseOver != null) {
            pointedEntity = Wrapper.MC.objectMouseOver.getType() == RayTraceResult.Type.ENTITY;
        }
        if ((!pointedEntity || PACKETS) && ONLY_ON_ENTITY) return;

        final int RAND = MathUtils.random(0, 100);
        if (RAND <= CHANCE) {
            if (PACKETS) {
                Wrapper.MC.playerController.attackEntity(Wrapper.MC.player, (Entity) Wrapper.MC.objectMouseOver.hitInfo);
                Wrapper.MC.player.swingArm(Hand.MAIN_HAND);
            } else {
                Wrapper.click();
            }
        }

        this.attackDelay.start();
    }

    private void stop() {
        this.assist = false;
        this.activationTimer.stop();
        this.attackDelay.stop();
    }

    /**
     * Used by the engine (reflection)
     */
    public static ArrayList<Option> getDefaultOptions(){
        return DEFAULT_OPTIONS;
    }
}
