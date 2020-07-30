package dev.nero.bettercolors.mod.hijacks;

import dev.nero.bettercolors.engine.BettercolorsEngine;
import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.mod.modules.Reach;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class PlayerControllerMPHijack extends PlayerControllerMP{

    /**
     * @param playerControllerMP the current playerControllerMP instance (on world creation)
     * @return the hijacked playerControllerMP
     */
    public static PlayerControllerMPHijack hijack(PlayerControllerMP playerControllerMP) {
        // The plan is to create a new instance of PlayerControllerMP, but a modified one

        // The constructor is PlayerControllerMP(Minecraft mcIn, NetHandlerPlayClient netHandler), so we need to
        // retrieve mcIn and netHandler. mcIn is Minecraft.getInstance(), so we only need netHandler!

        // NetHandlerPlayClient is private, so we use reflection to access it
        NetHandlerPlayClient net = ReflectionHelper.getPrivateValue(
                PlayerControllerMP.class,
                playerControllerMP,
                "netClientHandler"
        );
        PlayerControllerMPHijack hijackedController = new PlayerControllerMPHijack(BettercolorsEngine.MC, net);

        // The GameType class has an access to the PlayerController class. So we need to update it with the hijacked one
        // Get the private value
        WorldSettings.GameType gameType = ReflectionHelper.getPrivateValue(PlayerControllerMP.class, playerControllerMP,
                "currentGameType");
        // Update the private value
        ReflectionHelper.setPrivateValue(
                PlayerControllerMP.class,
                hijackedController,
                gameType,
                "currentGameType");

        return hijackedController;
    }

    private PlayerControllerMPHijack(Minecraft mcIn, NetHandlerPlayClient netHandler) {
        super(mcIn, netHandler);
    }

    @Override
    public float getBlockReachDistance() {
        float increment = 0.0f;
        Module reach = BettercolorsEngine.getInstance().getModule("Reach");

        if (reach.isActivated()) {
            increment = ((Reach) reach).getReachIncrement();
        }

        return super.getBlockReachDistance() + increment;
    }
}
