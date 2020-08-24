package dev.nero.bettercolors.core.modules;

import dev.nero.bettercolors.core.wrapper.Wrapper;
import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.option.Option;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.Color;

import java.util.ArrayList;

public class TeamFilter extends Module {

    private static TeamFilter instance;

    /**
     * @param toggleKey   the toggle Key (-1 -> none).
     * @param isActivated the initial state.
     */
    public TeamFilter(Integer toggleKey, Boolean isActivated) {
        super("Team Filter", "If enabled, all the modules will make sure that the targeted entity is not in your team", toggleKey, isActivated, "unknwon.png", "TF");
        instance = this;
    }

    public static TeamFilter getInstance() {
        return instance;
    }

    /**
     * @param entity the entity (can be anything).
     * @return true if the given entity is in the same team as the player.
     */
    public boolean isPlayerInSameTeamAs(Entity entity){
        if(!(entity instanceof PlayerEntity))
            return false;

        if (entity.getTeam() != null && Wrapper.MC.player.getTeam() != null) {
            if (entity.getTeam().isSameTeam(Wrapper.MC.player.getTeam())) return true;
        }

        Color playerColor = getColor(Wrapper.MC.player);
        Color entityColor = getColor((PlayerEntity) entity);

        if (playerColor != null && entityColor != null) {
            return playerColor.equals(entityColor);
        } else {
            return false;
        }
    }

    /**
     * @param e entity.
     * @return the color of the prefix of the entity name.
     */
    private Color getColor(PlayerEntity e){
        if (!e.getDisplayName().getSiblings().isEmpty()) {
            return e.getDisplayName().getSiblings().get(0).getStyle().func_240711_a_();
        } else {
            return null;
        }
    }

    /**
     * Used by the engine (reflection)
     */
    public static ArrayList<Option> getDefaultOptions(){
        return new ArrayList<>();
    }

}
