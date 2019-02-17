package com.bettercolors.modules;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class AutoSword extends Module {

    /**
     * @param name the name.
     * @param toggle_key the toggle key (-1 -> none).
     * @param is_activated the initial state.
     * @param symbol the picture name.
     */
    public AutoSword(String name, int toggle_key, boolean is_activated, String symbol) {
        super(name, toggle_key, is_activated, symbol, "[ASw]");
    }

    @Override
    public void onUpdate() {
        if(MC.player != null){

            boolean has_clicked_on_living_entity = false;
            try {
                Entity mouseOverEntity = MC.objectMouseOver.entity;
                if ((mouseOverEntity instanceof EntityLivingBase))
                    has_clicked_on_living_entity = true;
            } catch (Exception ignored) {}

            if(isKeyState(KEY.ATTACK, KEY_STATE.JUST_PRESSED) && has_clicked_on_living_entity){
                // We find the best sword
                float max_damage = -1;
                int best_item = -1;
                for(int slot = 0; slot < 9 ; slot ++){
                    ItemStack stack = MC.player.inventory.mainInventory.get(slot);
                    if(stack.getItem() instanceof ItemSword){
                        ItemSword sword = (ItemSword) stack.getItem();
                        float damage = sword.getMaxDamage();
                        if(sword.hasEffect(stack)){
                            // The damage calculation is not correct here, but we just need to find the item with the most
                            // powerful enchantment, so we don't care.
                            // 16 for sharpness : https://www.minecraftforum.net/forums/minecraft-java-edition/redstone-discussion-and/commands-command-blocks-and/2891015-1-13-full-enchantment-ids-small-give-tutorial
                            if(Enchantment.getEnchantmentByID(16) != null)
                                damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(16), stack);
                        }
                        if(damage >= max_damage){
                            best_item = slot;
                            max_damage = damage;
                        }
                    }
                }
                // We give the best sword to the player
                if(best_item != -1 && MC.player.inventory.currentItem != best_item){
                    log_info("Better sword found (" +  MC.player.inventory.mainInventory.get(best_item).getDisplayName() + ").");
                    MC.player.inventory.currentItem = best_item;
                }
            }
        }
    }
}
