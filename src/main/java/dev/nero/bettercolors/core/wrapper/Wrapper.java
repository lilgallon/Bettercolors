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

import dev.nero.bettercolors.engine.utils.TimeHelper;
import dev.nero.bettercolors.engine.view.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.PlayerContainer;

import java.awt.*;

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
}
