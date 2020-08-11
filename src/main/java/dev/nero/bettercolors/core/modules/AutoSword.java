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

package dev.nero.bettercolors.core.modules;

import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.option.Option;
import dev.nero.bettercolors.core.wrapper.Wrapper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

import java.util.ArrayList;

public class AutoSword extends Module {

    /**
     * @param toggleKey the toggle key (-1 -> none)
     * @param isActivated the initial state
     */
    public AutoSword(Integer toggleKey, Boolean isActivated) {
        super("Auto sword", toggleKey, isActivated, "sword_symbol.png", "[ASw]");
    }

    @Override
    public void onUpdate() {
        if(Wrapper.MC.player != null){

            boolean has_clicked_on_living_entity = false;
            try {
                Entity mouseOverEntity = Wrapper.MC.targetedEntity;
                if ((mouseOverEntity instanceof LivingEntity))
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
                    ItemStack stack = Wrapper.MC.player.inventory.main.get(slot);
                    if(stack.getItem() instanceof SwordItem){
                        SwordItem sword = (SwordItem) stack.getItem();
                        float damage = sword.getAttackDamage();

                        // It's not the best algorithm, but that's enough for most of the cases
                        if (stack.hasEnchantments()) {
                            damage = EnchantmentHelper.getAttackDamage(stack, EntityGroup.DEFAULT);
                        }

                        if(damage >= max_damage){
                            best_item = slot;
                            max_damage = damage;
                        }
                    }
                }
                // We give the best sword to the player
                if(best_item != -1 && Wrapper.MC.player.inventory.selectedSlot != best_item){
                    logInfo(
                            "Better sword found (" +
                                    Wrapper.MC.player.inventory
                                            .main
                                            .get(best_item)
                                            .getName()
                                            .getString()
                                    + ")."
                    );
                    Wrapper.MC.player.inventory.selectedSlot = best_item;
                }
            }
        }
    }

    public static ArrayList<Option> getDefaultOptions(){
        return new ArrayList<>();
    }
}
