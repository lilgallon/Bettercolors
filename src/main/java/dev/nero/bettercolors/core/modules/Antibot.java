package dev.nero.bettercolors.core.modules;

import dev.nero.bettercolors.core.events.EventType;
import dev.nero.bettercolors.core.wrapper.Wrapper;
import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.option.Option;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.WorldEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class Antibot extends Module {

    private static Antibot instance;

    // Prefix for Antibot (logging and settings)
    private static final String PREFIX = "ANTIBOT";

    private static final ArrayList<Option> DEFAULT_OPTIONS;
    static {
        DEFAULT_OPTIONS = new ArrayList<>();
    }

    HashMap<Integer, Data> entities;

    class Data {
        public int ticksLived = 0;
        public boolean hasBeenHit = false;
    }

    private final int LEGIT_ALIVE_TICKS = 40;

    /**
     * @param toggleKey   the toggle Key (-1 -> none).
     * @param isActivated the initial state.
     */
    public Antibot(Integer toggleKey, Boolean isActivated) {
        super("Antibot", toggleKey, isActivated, "shield.png", PREFIX);
        this.entities = new HashMap<>();
        instance = this;
    }

    @Override
    protected void onEvent(int code, Object details) {

        switch (code) {
            case EventType.WORLD_LOAD:
                this.entities = new HashMap<>();

                /*
                WorldEvent.Load worldEvent = (WorldEvent.Load) details;
                worldEvent.getWorld().getPlayers().forEach((entity -> this.entities.put(
                        entity.getEntityId(),
                        new Data()
                )));*/
                break;

            case EventType.ENTITY_JOIN:
                EntityJoinWorldEvent joinWorldEvent = (EntityJoinWorldEvent) details;

                this.entities.put(
                        joinWorldEvent.getEntity().getEntityId(),
                        new Data()
                );
                break;

            case EventType.ENTITY_LEAVE:
                EntityLeaveWorldEvent leaveWorldEvent = (EntityLeaveWorldEvent) details;

                this.entities.remove(leaveWorldEvent.getEntity().getEntityId());
                break;

            case EventType.WORLD_TICK:
                for (Data data : this.entities.values()) {
                    // Don't update if it's already above the legit ticks to prevent integer overflow
                    if (data.ticksLived > LEGIT_ALIVE_TICKS) continue;
                    data.ticksLived ++;
                }
                break;

            case EventType.ENTITY_HURT:
                LivingHurtEvent livingHurtEvent = (LivingHurtEvent) details;
                this.entities.get(livingHurtEvent.getEntityLiving().getEntityId()).hasBeenHit = true;
                break;
        }
    }

    /**
     * @param entity the entity
     * @return true if the given entity is a bot
     */
    public boolean isBot(LivingEntity entity) {
        // If it's not a player, it's a bot
        if (!(entity instanceof PlayerEntity)) return true;
        PlayerEntity player = (PlayerEntity) entity;

        // If it's not in the tab, it's a bot
        if (!isInTab(player)) return true;
        // If it has 0 ms, it's a bot
        if (Wrapper.MC.getConnection().getPlayerInfo(player.getUniqueID()).getResponseTime() == 0) return true;

        Data data = this.entities.get(player.getEntityId());
        if (data != null) {
            // If it has lived less than 40 ticks, it's a bot
            if (data.ticksLived < LEGIT_ALIVE_TICKS) return true;
            // If it has not been hit yet, it's a bot
            if (!data.hasBeenHit) return true;
        }

        return false;
    }

    /**
     * @param entity the entity
     * @return true if the given entity is showing on the tab
     */
    public boolean isInTab(PlayerEntity entity) {
        for(NetworkPlayerInfo info : Wrapper.MC.getConnection().getPlayerInfoMap()) {
            if (info.getGameProfile().getName().equals(entity.getName().getString())) return true;
        }

        return false;
    }

    public static Antibot getInstance() {
        return instance;
    }

    /**
     * Used by the engine (reflection)
     */
    public static ArrayList<Option> getDefaultOptions(){
        return DEFAULT_OPTIONS;
    }
}
