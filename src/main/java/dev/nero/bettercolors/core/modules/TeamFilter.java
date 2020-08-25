package dev.nero.bettercolors.core.modules;

import dev.nero.bettercolors.core.wrapper.Wrapper;
import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.option.Option;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

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
    public boolean isPlayerInSameTeamAs(Entity entity) {
        if (!(entity instanceof EntityPlayer))
            return false;

        boolean same_team = false;
        String target_tag;
        try {
            // Check friends / teammate
            target_tag = this.exportTag((EntityPlayer) entity);
            if (this.exportTag(Wrapper.MC.thePlayer).equalsIgnoreCase(target_tag)) {
                same_team = true;
            }

        } catch (Exception ignored) {
        }
        return same_team;
    }

    /**
     * @param e entity.
     * @return the team tag of the entity.
     */
    private String exportTag(EntityPlayer e){
        String tag;
        try{
            tag = e.getDisplayName().getUnformattedText().split(e.getName())[0].replace(" ","");
            tag = tag.replace("§","");
        }catch(Exception exc){
            tag = "";
        }
        return tag;
    }

    /**
     * Used by the engine (reflection)
     */
    public static ArrayList<Option> getDefaultOptions(){
        return new ArrayList<>();
    }

}
