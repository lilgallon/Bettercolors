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
import dev.nero.bettercolors.mod.wrapper.Wrapper;

import java.util.ArrayList;

public class AutoSprint extends Module {

    /**
     * @param toggle_key the toggle key (-1 -> none).
     * @param is_activated the initial state.
     */
    public AutoSprint(Integer toggle_key, Boolean is_activated) {
        super("Auto sprint", toggle_key, is_activated, "sprint_symbol.png", "[ASp]");
    }

    @Override
    public void onUpdate() {
        if(Wrapper.thePlayer != null){
            if(Wrapper.thePlayer.moveForward > 0 && !Wrapper.thePlayer.isSprinting()){
                logInfo("Forcing player to run");
                Wrapper.thePlayer.setSprinting(true);
            }
        }
    }

    public static ArrayList<Option> getDefaultOptions(){
        return new ArrayList<>();
    }
}
