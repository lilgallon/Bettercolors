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
import dev.nero.bettercolors.engine.BettercolorsEngine;
import dev.nero.bettercolors.engine.module.*;
import dev.nero.bettercolors.engine.view.Window;
import dev.nero.bettercolors.core.hijacks.EntityRendererHijack;
import dev.nero.bettercolors.core.modules.*;
import dev.nero.bettercolors.core.wrapper.Wrapper;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;

@Mod(modid = Reference.MOD_ID,
	 name = Reference.MOD_NAME,
	 version = Reference.MOD_VERSION,
	 acceptedMinecraftVersions = Reference.MC_VERSION)

public class Bettercolors {

    private BettercolorsEngine engine;

    /**
     * Called when forge needs to init our mod. This is the first "function" called.
     */
    @EventHandler
	public void Init(FMLInitializationEvent event)
	{
        // Forge event registering. We will use annotations to tell forge that we have some functions that need to be
        // called in specific contexts. This code says that forge has to look into this class to find these functions.
		MinecraftForge.EVENT_BUS.register(this);
		// This is a workaround to make the function clientTick work
        FMLCommonHandler.instance().bus().register(this);

        // The engine will handle absolutely everything. We will just need to write some modules for the mod, the rest
        // is totally handled by the engine. :)
        engine = new BettercolorsEngine();

        // Now we need to send some information about our mod to the engine
        HashMap<Class<? extends Module>, BettercolorsEngine.IntAndBoolean> modules = new HashMap<>();
        modules.put(AimAssistance.class, new BettercolorsEngine.IntAndBoolean(Keyboard.KEY_HOME, true));
        modules.put(ClickAssistance.class, new BettercolorsEngine.IntAndBoolean(Keyboard.KEY_PRIOR, false)); //pageup
        modules.put(AutoSprint.class, new BettercolorsEngine.IntAndBoolean(-1, true));
        modules.put(AutoSword.class, new BettercolorsEngine.IntAndBoolean(-1, true));
        modules.put(Reach.class, new BettercolorsEngine.IntAndBoolean(-1, false));
        modules.put(Triggerbot.class, new BettercolorsEngine.IntAndBoolean(-1, false));
        modules.put(TeamFilter.class, new BettercolorsEngine.IntAndBoolean(-1, false));
        modules.put(Antibot.class, new BettercolorsEngine.IntAndBoolean(-1, false));

        engine.init(
                "Bettercolors " + Reference.MOD_VERSION + " for MC " + Reference.MC_VERSION + " (forge)",
                Reference.MOD_VERSION,
                Reference.MOD_VERSION_SUFFIX,
                Reference.MC_VERSION,
                "https://api.github.com/repos/n3roo/bettercolors/releases",
                "https://github.com/n3roo/bettercolors/releases",
                "https://github.com/N3ROO/Bettercolors/issues",
                modules,
                Keyboard.KEY_INSERT,
                Keyboard::getKeyName
        );

        Window.INFO("[+] Bettercolors " + Reference.MOD_VERSION + " loaded");

        // Everything is done. Now, the rest of the code will be run once that one of the above event is ran by forge
        // itself.
	}

	@SubscribeEvent
    public void worldLoadEvent(WorldEvent.Load event) {
        if (event.world instanceof WorldClient) {
            if (!(Wrapper.MC.entityRenderer instanceof EntityRendererHijack)) {
                Wrapper.MC.entityRenderer = EntityRendererHijack.hijack(Wrapper.MC.entityRenderer);
            }
        }

        this.engine.event(EventType.RENDER, event);
    }

    @SubscribeEvent
    public void renderTickEvent(final TickEvent.RenderTickEvent event){
        this.engine.event(EventType.RENDER, event);
    }

    @SubscribeEvent
    public void entityJoinEvent(final EntityJoinWorldEvent event){
        this.engine.event(EventType.ENTITY_JOIN, event);
    }

    // no entity leave event :(

    @SubscribeEvent
    public void entityAttackEvent(final LivingAttackEvent event) {
        this.engine.event(EventType.ENTITY_ATTACK, event);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        // We don't use keyInputEvent because it's only triggered while in game. We want to be able
        // to open the window when the player is in the menus

        // Window toggle key
        engine.keyEvent(Window.TOGGLE_KEY, Keyboard.isKeyDown(Window.TOGGLE_KEY));

        // Modules' toggle keys
        for (Module module : engine.getModules()) {
            if (module.getToggleKey() != -1) {
                engine.keyEvent(module.getToggleKey(), Keyboard.isKeyDown(module.getToggleKey()));
            }
        }

        this.engine.event(EventType.CLIENT_TICK, event);
    }
}
