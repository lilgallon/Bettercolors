package dev.gallon.bettercolors.core.modules;

import dev.gallon.bettercolors.core.events.EventType;
import dev.nero.bettercolors.engine.BettercolorsEngine;
import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.option.Option;
import dev.nero.bettercolors.engine.option.ToggleOption;
import dev.nero.bettercolors.engine.option.ValueFloatOption;
import dev.nero.bettercolors.engine.utils.MathUtils;
import dev.nero.bettercolors.engine.utils.TimeHelper;
import dev.nero.bettercolors.engine.view.Window;
import dev.gallon.bettercolors.core.wrapper.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.Map;

public class Triggerbot extends Module {

    // Prefix for AimAssistance (logging and settings)
    private static final String PREFIX = "TB";

    // Description
    private static final String DESCRIPTION = "It's an autoclick that works when you're aiming an entity";

    // Options name
    private static final String USE_ON_MOBS = "Use_on_mobs";
    private static final String AUTO_CPS = "Auto_cps_sword_progress";
    private static final String CPS = "Clicks_per_seconds";

    // Options index
    private static final int I_USE_ON_MOBS = 0;
    private static final int I_AUTO_CPS = 1;
    private static final int I_CPS = 2;

    // Options description
    private static final String DESC_USE_ON_MOBS = "If enabled, the module will work on mobs";
    private static final String DESC_AUTO_CPS = "If enabled, the CPS will be automatically calculated based on the item's cooldown";
    private static final String DESC_CPS = "Defines the speed of the autoclick in clicks per seconds (only works when \"Auto CPS\" is turned off";

    // Default options loading
    private static final ArrayList<Option> DEFAULT_OPTIONS;
    static {
        DEFAULT_OPTIONS = new ArrayList<>();

        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, USE_ON_MOBS, DESC_USE_ON_MOBS, false));
        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, AUTO_CPS, DESC_AUTO_CPS, false));

        DEFAULT_OPTIONS.add(new ValueFloatOption(PREFIX, CPS, DESC_CPS, 7, 1, 9, 0.1f, 0.5f));
    }

    // Utility attributes
    private final TimeHelper timeout;

    /**
     * @param toggleKey the toggle key (-1 -> none).
     * @param isActivated the initial state.
     */
    public Triggerbot(Integer toggleKey, Boolean isActivated, Map<String, String> givenOptions) {
        super("Triggerbot", DESCRIPTION, toggleKey, isActivated, "target.png", PREFIX);
        this.loadOptionsAccordingTo(DEFAULT_OPTIONS, givenOptions);

        this.timeout = new TimeHelper();
        this.timeout.start();
    }

    @Override
    protected void onEvent(int code, Object details) {
        if (!this.isActivated()) return;
        if (Wrapper.MC.player == null) return;
        if (Wrapper.isInGui()) return;

        if (code == EventType.CLIENT_TICK) {
            if (Wrapper.MC.player.getAttackCooldownProgress(0) != 1.0 && getOptionB(I_AUTO_CPS)) return;
            if (!timeout.isDelayComplete((int) (1000f / getRandomCPS())) && !getOptionB(I_AUTO_CPS)) return;

            Entity pointedEntity = Wrapper.MC.targetedEntity;

            // Check if the entity is either a player or a mob (if it's a mob, we need to check if the option to
            // attack mobs is turned on
            if (pointedEntity instanceof PlayerEntity || (pointedEntity instanceof MobEntity && getOptionB(I_USE_ON_MOBS))) {
                // Then check if the player sees it & not in same team
                if (!pointedEntity.isInvisibleTo(Wrapper.MC.player) && Wrapper.canAttack((LivingEntity) pointedEntity)) {
                    // attack
                    timeout.start();
                    Wrapper.click(166, true); // 6 cps max
                }
            }
        }
    }

    @Override
    protected void onToggle(boolean toggle, boolean isTriggeredByKeybind) {
        if (toggle) {
            timeout.start();
            if (BettercolorsEngine.getInstance().getModule("Click assistance").isActivated()) {
                String message = "Trigger bot can't be used along with click assistance. Click assistance" +
                        "will be turned off. This feature is not as safe as click assistance. Use it at your own risk";
                if (!isTriggeredByKeybind)
                    Window.getInstance().dialog(message);
                logWarn(message);
                BettercolorsEngine.getInstance().toggleModule("Click assistance", false);
            } else {
                logWarn("This feature is not as safe as click assistance. Use it at your own risk");
            }
        } else {
            timeout.stop();
        }
    }

    /**
     * CPS +-= 1.0
     * @return random number of cps in [cps-1; cps+1]
     */
    private float getRandomCPS() {
        return MathUtils.random(
                (int) (getOptionF(I_CPS)*100f) - 100,
                (int) (getOptionF(I_CPS)*100f) + 100
        ) / 100f;
    }

    public static ArrayList<Option> getDefaultOptions(){
        return DEFAULT_OPTIONS;
    }
}
