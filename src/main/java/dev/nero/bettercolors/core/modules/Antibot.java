package dev.nero.bettercolors.core.modules;

import dev.nero.bettercolors.core.events.EventType;
import dev.nero.bettercolors.core.wrapper.Wrapper;
import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.option.Option;
import dev.nero.bettercolors.engine.option.ToggleOption;
import dev.nero.bettercolors.engine.option.ValueOption;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Antibot extends Module {

    private static Antibot instance;

    // Prefix for Antibot (logging and settings)
    private static final String PREFIX = "ANTIBOT";

    // Options name
    private static final String TAB_CHECK = "Tab_check";
    private static final String PING_CHECK = "Ping_check";
    private static final String TICKS_LIVED_CHECK = "Ticks_lived_check";
    private static final String HAS_BEEN_HIT_CHECK = "Has_been_hit_check";
    private static final String HAS_MADE_DAMAGE_CHECK = "Has_made_damage_check";
    private static final String TICKS_LIVED_MIN = "Ticks_lived_min";

    // Options index
    private static final int I_TAB_CHECK = 0;
    private static final int I_PING_CHECK = 1;
    private static final int I_TICKS_LIVED_CHECK = 2;
    private static final int I_HAS_BEEN_HIT_CHECK = 3;
    private static final int I_HAS_MADE_DAMAGE_CHECK = 4;
    private static final int I_TICKS_LIVED_MIN = 5;

    private static final ArrayList<Option> DEFAULT_OPTIONS;
    static {
        DEFAULT_OPTIONS = new ArrayList<>();

        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, TAB_CHECK, true));
        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, PING_CHECK, true));
        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, TICKS_LIVED_CHECK, true));
        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, HAS_BEEN_HIT_CHECK, true));
        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, HAS_MADE_DAMAGE_CHECK, true));

        DEFAULT_OPTIONS.add(new ValueOption(PREFIX, TICKS_LIVED_MIN, 40, 1, 100, 5, 20));
    }

    class Data {
        public int ticksLived = 0;
        public boolean hasBeenHit = false;
        public boolean attackedSomeone = false;
    }

    HashMap<Integer, Data> entities;
    private boolean entitiesLoaded = false;

    /**
     * @param toggleKey   the toggle Key (-1 -> none).
     * @param isActivated the initial state.
     */
    public Antibot(Integer toggleKey, Boolean isActivated, Map<String, String> givenOptions) {
        super("Antibot", toggleKey, isActivated, "shield.png", PREFIX);
        this.loadOptionsAccordingTo(DEFAULT_OPTIONS, givenOptions);

        this.entities = new HashMap<>();
        this.entitiesLoaded = false;
        instance = this;
    }

    @Override
    protected void onEvent(int code, Object details) {

        switch (code) {
            case EventType.WORLD_LOAD:
                this.entities = new HashMap<>();
                this.entitiesLoaded = false;

                break;

            case EventType.ENTITY_JOIN:
                EntityJoinWorldEvent joinWorldEvent = (EntityJoinWorldEvent) details;

                if (joinWorldEvent.getEntity() instanceof PlayerEntity) {
                    this.entities.put(
                            joinWorldEvent.getEntity().getEntityId(),
                            new Data()
                    );
                }

                break;

            case EventType.ENTITY_LEAVE:
                EntityLeaveWorldEvent leaveWorldEvent = (EntityLeaveWorldEvent) details;

                this.entities.remove(leaveWorldEvent.getEntity().getEntityId());
                break;

            case EventType.WORLD_TICK:
                if (!this.entitiesLoaded && Wrapper.MC.world != null) {
                    Wrapper.MC.world.getPlayers().forEach((entity -> this.entities.put(
                            entity.getEntityId(),
                            new Data()
                    )));

                    this.entitiesLoaded = true;
                }

                for (Data data : this.entities.values()) {
                    // Don't update if it's already above the legit ticks to prevent integer overflow
                    if (data.ticksLived > this.getOptionI(I_TICKS_LIVED_MIN)) continue;
                    data.ticksLived ++;
                }
                break;

            case EventType.ENTITY_HURT:
                LivingHurtEvent livingHurtEvent = (LivingHurtEvent) details;

                // Source handling
                if (livingHurtEvent.getSource().getTrueSource() != null) {
                    int sourceId = livingHurtEvent.getSource().getTrueSource().getEntityId();

                    if (livingHurtEvent.getSource().getTrueSource() instanceof PlayerEntity) {
                        if (!this.entities.containsKey(sourceId)) {
                            this.entities.put(sourceId, new Data());
                        }

                        this.entities.get(sourceId).attackedSomeone = true;
                    }
                }

                // Target handling
                int targetId = livingHurtEvent.getEntityLiving().getEntityId();
                if (!this.entities.containsKey(targetId)) {
                    this.entities.put(targetId, new Data());
                }
                this.entities.get(targetId).hasBeenHit = true;

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

        // Settings
        final boolean TAB_CHECK = this.getOptionB(I_TAB_CHECK);
        final boolean PING_CHECK = this.getOptionB(I_PING_CHECK);
        final boolean TICKS_LIVED_CHECK = this.getOptionB(I_TICKS_LIVED_CHECK);
        final boolean HAS_BEEN_HIT_CHECK = this.getOptionB(I_HAS_BEEN_HIT_CHECK);
        final boolean HAS_MADE_DAMAGE_CHECK = this.getOptionB(I_HAS_MADE_DAMAGE_CHECK);
        final int TICKS_LIVED_MIN = this.getOptionI(I_TICKS_LIVED_MIN);

        // If it's not in the tab, it's a bot
        if (!isInTab(player) && TAB_CHECK) return true;

        // If it has 0 ms, it's a bot
        if (Wrapper.MC.getConnection().getPlayerInfo(player.getUniqueID()).getResponseTime() == 0 && PING_CHECK) return true;

        Data data = this.entities.get(player.getEntityId());
        if (data != null) {
            // If it has lived less than 40 ticks, it's a bot
            if (data.ticksLived < TICKS_LIVED_MIN && TICKS_LIVED_CHECK) return true;

            // If it has not been hit yet (received damage), it's a bot
            if (!data.hasBeenHit && HAS_BEEN_HIT_CHECK) return true;

            // If it has attacked something (and made damage), it's a bot
            if (!data.attackedSomeone && HAS_MADE_DAMAGE_CHECK) return true;
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
