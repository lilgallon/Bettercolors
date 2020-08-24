/*
 * Copyright 2018-2020
 * - Bettercolors Contributors (https://github.com/N3ROO/Bettercolors) and
 * - Bettercolors Engine Contributors (https://github.com/N3ROO/BettercolorsEngine)
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

package dev.nero.bettercolors.engine.option;

import dev.nero.bettercolors.engine.io.SettingsUtils;

public class ToggleOption extends Option {

    private boolean activated;

    /**
     * @param prefix prefix of the option (module name for example) -> used to prevent conflict if some modules have the
     *               same option name.
     * @param name name of the option.
     * @param activated the initial state of the option.
     *
     * @deprecated use the other constructor with the description instead
     */
    @Deprecated
    public ToggleOption(String prefix, String name, boolean activated) {
        this(prefix, name, "", activated);
    }

    /**
     * @param prefix prefix of the option (module name for example) -> used to prevent conflict if some modules have the
     *               same option name.
     * @param name name of the option.
     * @param description the description.
     * @param activated the initial state of the option.
     */
    public ToggleOption(String prefix, String name, String description, boolean activated) {
        super(prefix, name, description);
        this.activated = activated;
    }

    /**
     * Changes the state of this option.
     * @param activated the new state of this option.
     */
    public void setActivated(boolean activated){
        this.activated = activated;
        saveOption();
    }

    /**
     * It changes the state of this option according to its current state. IT TOGGLES IT LOL!
     */
    public void toggle(){
        activated = !activated;
        saveOption();
    }

    /**
     * @return the state of the option : activated or not.
     */
    public boolean isActivated(){
        return activated;
    }

    /**
     * It saves the option to the configuration file (the selected one).
     */
    @Override
    void saveOption(){
        SettingsUtils.setOption(PREFIX + "_" + NAME, Boolean.toString(activated));
    }

    @Override
    public Object clone() {
        return new ToggleOption(this.PREFIX, this.NAME, this.activated);
    }
}