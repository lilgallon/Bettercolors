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

package dev.nero.bettercolors.mod;

import dev.nero.bettercolors.engine.BettercolorsEngine;
import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.view.Window;
import dev.nero.bettercolors.mod.hijacks.GameRendererHijack;
import dev.nero.bettercolors.mod.modules.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.HashMap;

@Mod(Reference.MOD_ID)
public class Bettercolors {

    private BettercolorsEngine engine;

    /**
     * Called when forge needs to init our mod. This is the first "function" called. It is actually called the
     * "constructor"
     */
    public Bettercolors(){
        // We use the annotation @SubscribeEvent. So we just need to register this class, and forge will look at all the
        // functions with that annotation, and will register those functions automatically
        MinecraftForge.EVENT_BUS.register(this);

        System.setProperty("java.awt.headless", "false");

        // The engine will handle absolutely everything. We will just need to write some modules for the mod, the rest
        // is totally handled by the engine. :)
        engine = new BettercolorsEngine();

        // Now we need to send some information about our mod to the engine
        HashMap<Class<? extends Module>, BettercolorsEngine.IntAndBoolean> modules = new HashMap<>();
        modules.put(AimAssistance.class, new BettercolorsEngine.IntAndBoolean(GLFW.GLFW_KEY_HOME, true));
        modules.put(ClickAssistance.class, new BettercolorsEngine.IntAndBoolean(GLFW.GLFW_KEY_PAGE_UP, false));
        modules.put(AutoSprint.class, new BettercolorsEngine.IntAndBoolean(-1, true));
        modules.put(AutoSword.class, new BettercolorsEngine.IntAndBoolean(-1, true));
        modules.put(Reach.class, new BettercolorsEngine.IntAndBoolean(-1, false));

        engine.init(
                Reference.MOD_VERSION,
                Reference.MC_VERSION,
                "https://github.com/N3ROO/Bettercolors/releases",
                "https://github.com/N3ROO/Bettercolors/issues",
                "https://github.com/N3ROO/Bettercolors/releases/latest",
                modules,
                new BettercolorsEngine.Key(GLFW.GLFW_KEY_INSERT, "insert"),
                Minecraft.getInstance()
        );

        Window.INFO("[+] Bettercolors " + Reference.MOD_VERSION + " loaded");

        // Everything is done. Now, the rest of the code will be run once that one of the above event is ran by forge
        // itself.
    }

    @SubscribeEvent
    public void worldLoadEvent(WorldEvent.Load event) {
        if (event.getWorld() instanceof ClientWorld) {
            if (!(BettercolorsEngine.MC.gameRenderer instanceof GameRendererHijack)) {
                // gameRenderer is final, but we want to update it ;( we will use reflection to do so

                // First, we need to find the field
                Field gameRendererField = ObfuscationReflectionHelper.findField( // TODO: find obf field
                        Minecraft.class,
                        "gameRenderer"
                );

                // Then disable "final" keyword
                gameRendererField.setAccessible(true);

                // Hijack it
                try {
                    gameRendererField.set(Minecraft.getInstance(), GameRendererHijack.hijack(BettercolorsEngine.MC.gameRenderer));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                // We did not do anything, did we?
                gameRendererField.setAccessible(false);
            }
        }
    }

    /**
     * Called when a key is pressed.
     * @param event event details (containing the key pressed)
     */
    @SubscribeEvent
    public void keyInputEvent(final InputEvent.KeyInputEvent event) {
        engine.keyEvent(event.getKey(),event.getAction() != GLFW.GLFW_RELEASE);
    }

    /**
     * A function called at every render tick event.
     * @param event the event information
     */
    @SubscribeEvent
    public void renderTickEvent(final TickEvent.RenderTickEvent event){
        engine.update();
	}
}
