package dev.gallon.bettercolors;

import dev.gallon.bettercolors.core.events.*;
import dev.gallon.bettercolors.core.modules.*;
import dev.nero.bettercolors.core.events.*;
import dev.nero.bettercolors.engine.BettercolorsEngine;
import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.view.Window;
import dev.nero.bettercolors.core.modules.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

public class Bettercolors implements ModInitializer {

    private static BettercolorsEngine engine;

    @Override
    public void onInitialize() {
        // Fixes headless issue for some users
        System.setProperty("java.awt.headless", "false");

        // The engine will handle absolutely everything. We will just need to write some modules for the mod, the rest
        // is totally handled by the engine. :)
        engine = new BettercolorsEngine();

        // Specify the resources location
        Window.FONTS_DIR = "assets/" + Reference.MOD_ID + "/";
        Window.IMAGES_DIR = "assets/" + Reference.MOD_ID + "/";

        // Now we need to send some information about our mod to the engine
        HashMap<Class<? extends Module>, BettercolorsEngine.IntAndBoolean> modules = new HashMap<>();
        modules.put(AimAssistance.class, new BettercolorsEngine.IntAndBoolean(GLFW.GLFW_KEY_HOME, true));
        modules.put(ClickAssistance.class, new BettercolorsEngine.IntAndBoolean(GLFW.GLFW_KEY_PAGE_UP, false)); //pageup
        modules.put(AutoSprint.class, new BettercolorsEngine.IntAndBoolean(-1, true));
        modules.put(AutoSword.class, new BettercolorsEngine.IntAndBoolean(-1, true));
        modules.put(Reach.class, new BettercolorsEngine.IntAndBoolean(-1, false));
        modules.put(Triggerbot.class, new BettercolorsEngine.IntAndBoolean(-1, false));
        modules.put(TeamFilter.class, new BettercolorsEngine.IntAndBoolean(-1, false));
        modules.put(Antibot.class, new BettercolorsEngine.IntAndBoolean(-1, true));
        modules.put(AutoRightClick.class, new BettercolorsEngine.IntAndBoolean(-1, false));
        modules.put(AutoUse.class, new BettercolorsEngine.IntAndBoolean(-1, false));
        modules.put(Panic.class, new BettercolorsEngine.IntAndBoolean(-1, false));
        modules.put(SpeedBridging.class, new BettercolorsEngine.IntAndBoolean(-1, false));

        // Important! If GLFW is not init, we won't be able to use GLFW.getKeyName which would create a crash that is
        // super hard to debug
        OnPostMinecraftInit.EVENT.register(() -> {
            engine.init(
                    "Bettercolors " + Reference.MOD_VERSION + " for MC " + Reference.MC_VERSION + " (fabric)",
                    Reference.MOD_VERSION,
                    Reference.MOD_VERSION_SUFFIX,
                    Reference.MC_VERSION,
                    "https://api.github.com/repos/lilgallon/bettercolors/releases",
                    "https://github.com/lilgallon/bettercolors/releases",
                    "https://github.com/lilgallon/Bettercolors/issues",
                    modules,
                    GLFW.GLFW_KEY_INSERT,
                    code -> GLFW.glfwGetKeyName(code, GLFW.glfwGetKeyScancode(code))
            );

            Window.INFO("[+] Bettercolors " + Reference.MOD_VERSION + " loaded");
        });

        OnWorldLoadCallback.EVENT.register(
                () -> engine.event(EventType.WORLD_LOAD, null)
        );

        OnKeyInputEvent.EVENT.register(() -> {
            final long HANDLE = MinecraftClient.getInstance().getWindow().getHandle();

            // Window toggle key
            engine.keyEvent(Window.TOGGLE_KEY, InputUtil.isKeyPressed(HANDLE, Window.TOGGLE_KEY));

            // Modules' toggle keys
            for (Module module : engine.getModules()) {
                if (module.getToggleKey() != -1)
                    engine.keyEvent(module.getToggleKey(), InputUtil.isKeyPressed(HANDLE, module.getToggleKey()));
            }
        });

        OnRenderCallback.EVENT.register(
                () -> engine.event(EventType.RENDER, null)
        );

        OnMouseInputCallback.EVENT.register(
                (button, action, mods) -> engine.event(EventType.MOUSE_INPUT, new MouseInput(button, action))
        );

        OnClientTickCallback.EVENT.register(
                () -> engine.event(EventType.CLIENT_TICK, null)
        );

        OnEntityJoinCallback.EVENT.register(
                (entity) -> engine.event(EventType.ENTITY_JOIN, entity)
        );

        OnEntityLeaveCallback.EVENT.register(
                (entity) -> engine.event(EventType.ENTITY_LEAVE, entity)
        );

        OnEntityDamageCallback.EVENT.register(
                (info) -> engine.event(EventType.ENTITY_DAMAGE, info)
        );
    }
}
