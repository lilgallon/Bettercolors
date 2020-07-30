/*
 * Copyright 2018-2020 Bettercolors Contributors (https://github.com/N3ROO/Bettercolors)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.nero.bettercolors.mod.modules;

import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.option.Option;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import java.util.ArrayList;

import static dev.nero.bettercolors.mod.wrapper.Wrapper.MC;

public class AutoSword extends Module {

    /**
     * @param toggle_key the toggle key (-1 -> none)
     * @param is_activated the initial state
     */
    public AutoSword(Integer toggle_key, Boolean is_activated) {
        super("Auto sword", toggle_key, is_activated, "sword_symbol.png", "[ASw]");
    }

    @Override
    public void onUpdate() {
        if(MC.thePlayer != null){

            boolean has_clicked_on_living_entity = false;
            try {
                Entity mouseOverEntity = MC.objectMouseOver.entityHit;
                if ((mouseOverEntity instanceof EntityLivingBase))
                    has_clicked_on_living_entity = true;
            } catch (Exception ignored) {
                // Happens sometimes in MC1.8.9, did not test if it happens on 1.15.2 as well
                // When we try to get what the player is pointing at, and it's a ladder, it crashes for example
            }

            if(isKeyState(Key.ATTACK, KeyState.JUST_PRESSED) && has_clicked_on_living_entity){
                // We find the best sword
                float max_damage = -1;
                int best_item = -1;

                // We look for every slot of the hotbar, and we take the best item
                for(int slot = 0; slot < 9 ; slot ++){
                    ItemStack stack = MC.thePlayer.inventory.mainInventory[slot];
                    if(stack == null) continue;
                    if(stack.getItem() instanceof ItemSword){
                        ItemSword sword = (ItemSword) stack.getItem();
                        float damage = sword.getMaxDamage();
                        if(sword.hasEffect(stack)){
                            // The damage calculation is not correct here, but we just need to find the item with the most
                            // powerful enchantment, so we don't care.
                            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
                        }

                        if(damage >= max_damage){
                            best_item = slot;
                            max_damage = damage;
                        }
                    }
                }
                // We give the best sword to the player
                if(best_item != -1 && MC.thePlayer.inventory.currentItem != best_item){
                    logInfo("Better sword found (" +  MC.thePlayer.inventory.mainInventory[best_item].getDisplayName() + ").");
                    MC.thePlayer.inventory.currentItem = best_item;
                }
            }
        }
    }

    public static ArrayList<Option> getDefaultOptions(){
        return new ArrayList<>();
    }
}