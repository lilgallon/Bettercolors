package dev.nero.bettercolors;

import dev.nero.bettercolors.core.events.OnRenderCallback;
import dev.nero.bettercolors.core.hijacks.GameRendererHijack;
import dev.nero.bettercolors.core.wrapper.Wrapper;
import dev.nero.bettercolors.engine.BettercolorsEngine;
import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.view.Window;
import dev.nero.bettercolors.core.modules.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Bettercolors implements ModInitializer {

    public static final String MODID = "mcp";
    private static BettercolorsEngine engine;
    private static boolean failedBypass = false;

    @Override
    public void onInitialize() {
        System.setProperty("java.awt.headless", "false");

        // The engine will handle absolutely everything. We will just need to write some modules for the mod, the rest
        // is totally handled by the engine. :)
        engine = new BettercolorsEngine();

        // Now we need to send some information about our mod to the engine
        HashMap<Class<? extends Module>, BettercolorsEngine.IntAndBoolean> modules = new HashMap<>();
        modules.put(AimAssistance.class, new BettercolorsEngine.IntAndBoolean(GLFW.GLFW_KEY_HOME, true));
        modules.put(ClickAssistance.class, new BettercolorsEngine.IntAndBoolean(GLFW.GLFW_KEY_PAGE_UP, false)); //pageup
        modules.put(AutoSprint.class, new BettercolorsEngine.IntAndBoolean(-1, true));
        modules.put(AutoSword.class, new BettercolorsEngine.IntAndBoolean(-1, true));
        modules.put(Reach.class, new BettercolorsEngine.IntAndBoolean(-1, false));
        modules.put(Triggerbot.class, new BettercolorsEngine.IntAndBoolean(-1, false));

        engine.init(
                Reference.MOD_VERSION,
                Reference.MC_VERSION,
                "https://github.com/N3ROO/Bettercolors/releases",
                "https://github.com/N3ROO/Bettercolors/issues",
                "https://github.com/N3ROO/Bettercolors/releases/latest",
                modules,
                new BettercolorsEngine.Key(GLFW.GLFW_KEY_INSERT, "insert"),
                MinecraftClient.getInstance()
        );

        Window.INFO("[+] Bettercolors " + Reference.MOD_VERSION + " loaded");

        ClientTickEvents.START_WORLD_TICK.register(start -> {
            if (!(Wrapper.MC.gameRenderer instanceof GameRendererHijack) && !failedBypass) {
                Window.INFO("[~] Bypassing MC to enable reach...");

                Field gameRendererField = null;
                try {
                    gameRendererField = MinecraftClient.class.getField("gameRenderer");
                } catch (NoSuchFieldException e1) {

                    try {
                        // obf field https://minidigger.github.io/MiniMappingViewer/#/mojang/client/1.16.2-rc2
                        gameRendererField = MinecraftClient.class.getField("field_1773");
                    } catch (NoSuchFieldException e2) {
                        System.out.println("Error 1:");
                        e1.printStackTrace();
                        System.out.println("--------------");
                        System.out.println("Error 2:");
                        e2.printStackTrace();

                        Window.ERROR("Error while looking for gameRenderer");
                        failedBypass = true;
                    }
                }

                if (gameRendererField != null) {
                    gameRendererField.setAccessible(true);

                    try {
                        gameRendererField.set(Wrapper.MC.getInstance(), GameRendererHijack.hijack(BettercolorsEngine.MC.gameRenderer));
                    } catch (IllegalAccessException e) {
                        Window.ERROR("Error while hijacking gameRenderer");
                        failedBypass = true;
                        e.printStackTrace();
                    }

                    // We did not do anything, did we?
                    gameRendererField.setAccessible(false);

                    Window.INFO("[+] Reach is ready to work!");
                }
            }
        });

        OnRenderCallback.EVENT.register( (tick, info) -> {
            final long HANDLE = MinecraftClient.getInstance().getWindow().getHandle();

            engine.keyEvent(GLFW.GLFW_KEY_HOME, InputUtil.isKeyPressed(HANDLE, GLFW.GLFW_KEY_HOME));
            engine.keyEvent(GLFW.GLFW_KEY_PAGE_UP, InputUtil.isKeyPressed(HANDLE, GLFW.GLFW_KEY_PAGE_UP));
            engine.keyEvent(Window.TOGGLE_KEY, InputUtil.isKeyPressed(HANDLE, Window.TOGGLE_KEY));

            engine.update();
        });
    }
}
