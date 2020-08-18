package dev.nero.bettercolors.core.modules;

import dev.nero.bettercolors.engine.BettercolorsEngine;
import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.option.Option;
import dev.nero.bettercolors.engine.option.ToggleOption;
import dev.nero.bettercolors.engine.option.ValueFloatOption;
import dev.nero.bettercolors.engine.option.ValueOption;
import dev.nero.bettercolors.engine.utils.MathUtils;
import dev.nero.bettercolors.engine.utils.TimeHelper;
import dev.nero.bettercolors.engine.view.Window;
import dev.nero.bettercolors.core.wrapper.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class Triggerbot extends Module {

    // Prefix for AimAssistance (logging and settings)
    private static final String PREFIX = "TB";

    // Options name
    private static final String USE_ON_MOBS = "Use_on_mobs";
    private static final String AUTO_CPS = "Auto_cps_sword_progress";
    private static final String CPS = "Clicks_per_seconds";

    // Options index
    private static final int I_USE_ON_MOBS = 0;
    private static final int I_AUTO_CPS = 1;
    private static final int I_CPS = 2;

    // Default options loading
    private static final ArrayList<Option> DEFAULT_OPTIONS;
    static {
        DEFAULT_OPTIONS = new ArrayList<>();

        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, USE_ON_MOBS, false));
        DEFAULT_OPTIONS.add(new ToggleOption(PREFIX, AUTO_CPS, false));

        DEFAULT_OPTIONS.add(new ValueFloatOption(PREFIX, CPS, 7, 1, 9, 0.1f, 0.5f));
    }

    // Utility attributes
    private TimeHelper timeout;

    /**
     * @param toggleKey the toggle key (-1 -> none).
     * @param isActivated the initial state.
     */
    public Triggerbot(Integer toggleKey, Boolean isActivated, Map<String, String> givenOptions) {
        super("Triggerbot", toggleKey, isActivated, "target.png", PREFIX);

        for (Option defaultOption : DEFAULT_OPTIONS) {
            Option option = (Option) defaultOption.clone();
            String name = defaultOption.getCompleteName();

            if (option instanceof ToggleOption) {
                ((ToggleOption) option).setActivated(
                        Boolean.parseBoolean(givenOptions.get(name))
                );
            } else if (option instanceof ValueOption) {
                ((ValueOption) option).setVal(
                        Integer.parseInt(givenOptions.get(name))
                );
            } else if (option instanceof ValueFloatOption) {
                ((ValueFloatOption) option).setVal(
                        Float.parseFloat(givenOptions.get(name))
                );
            }

            this.options.add(option);
        }

        this.timeout = new TimeHelper();
        this.timeout.start();
    }

    @Override
    protected void onUpdate() {
        if (Wrapper.MC.player != null) {
            if (Wrapper.isInGui()) return;

            if (Wrapper.MC.player.getCooledAttackStrength(0) != 1.0 && autoCps()) return;
            if (!timeout.isDelayComplete((int) (1000f / getRandomCPS())) && !autoCps()) return;

            Entity pointedEntity = Wrapper.MC.pointedEntity;

            // Check if the entity is either a player or a mob (if it's a mob, we need to check if the option to
            // attack mobs is turned on
            if (pointedEntity instanceof PlayerEntity || (pointedEntity instanceof MobEntity && useOnMobs())) {
                // Then check if the player sees it & not in same team
                if (!pointedEntity.isInvisibleToPlayer(Wrapper.MC.player) && !Wrapper.isInSameTeam(pointedEntity)) {
                    // attack
                    timeout.start();
                    Wrapper.click();
                }
            }
        }
    }

    @Override
    protected void onToggle(boolean toggle, boolean isTriggeredByKeybind) {
        if (toggle) {
            timeout.start();
            if (BettercolorsEngine.getInstance().getModule("Click assistance").isActivated()) {
                if (!isTriggeredByKeybind)
                    Window.getInstance().dialog("Trigger bot can't be used along with click assistance. Click assistance" +
                            "will be turned off. This feature is not as safe as click assistance. Use it at your own risk");
                BettercolorsEngine.getInstance().toggleModule("Click assistance", false);
            } else {
                if (!isTriggeredByKeybind)
                    Window.getInstance().dialog("This feature is not as safe as click assistance. Use it at your own risk");
            }
        } else {
            timeout.stop();
        }
    }

    private boolean useOnMobs() {
        return ((ToggleOption) this.options.get(I_USE_ON_MOBS)).isActivated();
    }

    private boolean autoCps() {
        return ((ToggleOption) this.options.get(I_AUTO_CPS)).isActivated();
    }

    private float getCPS() {
        return ((ValueFloatOption) this.options.get(I_CPS)).getVal();
    }

    /**
     * CPS +-= 1.0
     * @return random number of cps in [cps-1; cps+1]
     */
    private float getRandomCPS() {
        return MathUtils.random(
                (int) (getCPS()*100f) - 100,
                (int) (getCPS()*100f) + 100
        ) / 100f;
    }

    public static ArrayList<Option> getDefaultOptions(){
        return DEFAULT_OPTIONS;
    }
}
