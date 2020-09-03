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

package dev.nero.bettercolors;

import dev.nero.bettercolors.core.events.EventType;
import dev.nero.bettercolors.core.wrapper.Wrapper;
import dev.nero.bettercolors.engine.BettercolorsEngine;
import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.view.Window;
import dev.nero.bettercolors.core.hijacks.GameRendererHijack;
import dev.nero.bettercolors.core.modules.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
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

    private final BettercolorsEngine ENGINE;

    /**
     * Called when forge needs to init our mod. This is the first "function" called. It is actually called the
     * "constructor"
     */
    public Bettercolors(){
        // We use the annotation @SubscribeEvent. So we just need to register this class, and forge will look at all the
        // functions with that annotation, and will register those functions automatically
        MinecraftForge.EVENT_BUS.register(this);

        // The engine will handle absolutely everything. We will just need to write some modules for the mod, the rest
        // is totally handled by the engine. :)
        ENGINE = new BettercolorsEngine();

        // Now we need to send some information about our mod to the engine
        HashMap<Class<? extends Module>, BettercolorsEngine.IntAndBoolean> modules = new HashMap<>();
        modules.put(AimAssistance.class, new BettercolorsEngine.IntAndBoolean(GLFW.GLFW_KEY_HOME, true));
        modules.put(ClickAssistance.class, new BettercolorsEngine.IntAndBoolean(GLFW.GLFW_KEY_PAGE_UP, false));
        modules.put(AutoSprint.class, new BettercolorsEngine.IntAndBoolean(-1, true));
        modules.put(AutoSword.class, new BettercolorsEngine.IntAndBoolean(-1, true));
        modules.put(Reach.class, new BettercolorsEngine.IntAndBoolean(-1, false));
        modules.put(Triggerbot.class, new BettercolorsEngine.IntAndBoolean(-1, false));
        modules.put(TeamFilter.class, new BettercolorsEngine.IntAndBoolean(-1, false));
        modules.put(Antibot.class, new BettercolorsEngine.IntAndBoolean(-1, true));
        modules.put(AutoRightClick.class, new BettercolorsEngine.IntAndBoolean(-1, true));
        modules.put(AutoUse.class, new BettercolorsEngine.IntAndBoolean(-1, true));

        ENGINE.init(
                "Bettercolors " + Reference.MOD_VERSION + " for MC " + Reference.MC_VERSION + " (forge)",
                Reference.MOD_VERSION,
                Reference.MOD_VERSION_SUFFIX,
                Reference.MC_VERSION,
                "https://api.github.com/repos/n3roo/bettercolors/releases",
                "https://github.com/n3roo/bettercolors/releases",
                "https://github.com/N3ROO/Bettercolors/issues",
                modules,
                GLFW.GLFW_KEY_INSERT,
                (code) -> GLFW.glfwGetKeyName(code, GLFW.glfwGetKeyScancode(code))
        );

        Window.INFO("[+] Bettercolors " + Reference.MOD_VERSION + " loaded");

        // Everything is done. Now, the rest of the code will be run once that one of the above event is ran by forge
        // itself.
    }

    @SubscribeEvent
    public void worldLoadEvent(WorldEvent.Load event) {
        if (event.getWorld() instanceof ClientWorld) {
            if (!(Wrapper.MC.gameRenderer instanceof GameRendererHijack)) {
                // gameRenderer is final, but we want to update it ;( we will use reflection to do so

                // First, we need to find the field
                Field gameRendererField = ObfuscationReflectionHelper.findField(
                        Minecraft.class,
                        "field_71460_t"
                        // The documentation says that we need to give the SRG name, and it will be automatically
                        // resolved if MCP mappings are detected (with MCP mappings, "field_71460_t" = "gameRenderer"
                );

                // Then disable "final" keyword
                gameRendererField.setAccessible(true);

                // Hijack it
                try {
                    gameRendererField.set(Minecraft.getInstance(), GameRendererHijack.hijack(Wrapper.MC.gameRenderer));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                // We did not do anything, did we?
                gameRendererField.setAccessible(false);
            }
        }

        ENGINE.event(EventType.WORLD_LOAD, event);
    }

    @SubscribeEvent
    public void keyInputEvent(final InputEvent.KeyInputEvent event) {
        ENGINE.keyEvent(event.getKey(),event.getAction() != GLFW.GLFW_RELEASE);
    }
    
    @SubscribeEvent
    public void mouseInputEvent(final InputEvent.MouseInputEvent event) {
        ENGINE.event(EventType.MOUSE_INPUT, event);
    }

    @SubscribeEvent
    public void renderTickEvent(final TickEvent.RenderTickEvent event){
        ENGINE.event(EventType.RENDER, event);
    }

    @SubscribeEvent
    public void entityJoinEvent(final EntityJoinWorldEvent event) {
        ENGINE.event(EventType.ENTITY_JOIN, event);
    }

    @SubscribeEvent
    public void entityLeaveEvent(final EntityLeaveWorldEvent event) {
        ENGINE.event(EventType.ENTITY_LEAVE, event);
    }

    @SubscribeEvent
    public void entityAttackEvent(final LivingAttackEvent event) {
        ENGINE.event(EventType.ENTITY_ATTACK, event);
    }

    @SubscribeEvent
    public void clientTickEvent(final TickEvent.ClientTickEvent event) {
        ENGINE.event(EventType.CLIENT_TICK, event);
    }
}
