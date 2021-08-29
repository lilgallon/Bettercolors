/*
 * Copyright 2018-2020 Bettercolors Contributors (https://github.com/lilgallon/Bettercolors)
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

import dev.nero.bettercolors.core.events.EventType;
import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.option.Option;
import dev.nero.bettercolors.core.wrapper.Wrapper;

import java.util.ArrayList;

public class AutoSprint extends Module {

    /**
     * @param toggleKey the toggle key (-1 -> none).
     * @param isActivated the initial state.
     */
    public AutoSprint(Integer toggleKey, Boolean isActivated) {
        super("Auto sprint", "Sprints for you when you're going forward", toggleKey, isActivated, "unknown.png", "ASp");
    }

    @Override
    protected void onEvent(int code, Object details) {
        if (!this.isActivated()) return;
        if (Wrapper.MC.player == null) return;
        if (Wrapper.isInGui()) return;

        if (code == EventType.CLIENT_TICK) {
            if(Wrapper.MC.player.forwardSpeed > 0 && !Wrapper.MC.player.isSprinting()){
                Wrapper.MC.player.setSprinting(true);
            }
        }
    }

    /**
     * Used by the engine (reflection)
     */
    public static ArrayList<Option> getDefaultOptions(){
        return new ArrayList<>();
    }
}
