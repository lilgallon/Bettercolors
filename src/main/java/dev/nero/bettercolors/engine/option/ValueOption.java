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

public class ValueOption extends Option {

    private final int MIN;
    private final int MAX;
    private final int MINOR_TICK_SPC;
    private final int MAJOR_TICK_SPC;
    private int val;

    /**
     * @param prefix prefix of the option (module name for example) -> used to prevent conflict if some modules have
     *               the same option name.
     * @param name the name.
     * @param val the initial value.
     * @param min the minimum value.
     * @param max the maximum value.
     * @param minor_tick_spc the minor tick spacing (for the slider).
     * @param major_tick_spc the major tick spacing (for the slider).
     *
     * @deprecated use the other constructor with the description instead
     */
    @Deprecated
    public ValueOption(String prefix, String name, int val, int min, int max, int minor_tick_spc, int major_tick_spc) {
        this(prefix, name, "", val, min, max, minor_tick_spc, major_tick_spc);
    }

    /**
     * @param prefix prefix of the option (module name for example) -> used to prevent conflict if some modules have
     *               the same option name.
     * @param name the name.
     * @param description the description.
     * @param val the initial value.
     * @param min the minimum value.
     * @param max the maximum value.
     * @param minor_tick_spc the minor tick spacing (for the slider).
     * @param major_tick_spc the major tick spacing (for the slider).
     */
    public ValueOption(String prefix, String name, String description, int val, int min, int max, int minor_tick_spc, int major_tick_spc) {
        super(prefix, name, description);
        this.val = val;
        MIN = min;
        MAX = max;
        MINOR_TICK_SPC = minor_tick_spc;
        MAJOR_TICK_SPC = major_tick_spc;
    }

    public int getMin() { return MIN; }
    public int getMax() { return MAX; }
    public int getVal() { return val; }
    public int getMajorTickSpacing(){ return MAJOR_TICK_SPC; }
    public int getMinorTickSpacing(){ return MINOR_TICK_SPC; }

    /**
     * It changes the current value of the option. This method test if the value is correct before updating it.
     * @param val the new value.
     */
    public void setVal(int val){
        this.val = val;
        saveOption();

        if(val > MAX) {
            throw new IllegalArgumentException("The value " + val + " is bigger than its max : " + MAX);
        }else if(val < MIN){
            throw new IllegalArgumentException("The value " + val + " is lower than its min : " + MIN);
        }
    }

    /**
     * It saves the option to the configuration file (the selected one).
     */
    @Override
    void saveOption() {
        SettingsUtils.setOption(PREFIX + "_" + NAME, Integer.toString(val));
    }

    @Override
    public Object clone() {
        return new ValueOption(
                this.PREFIX, this.NAME, this.DESCRIPTION,
                this.getVal(), this.getMin(), this.getMax(),
                this.getMinorTickSpacing(), this.getMajorTickSpacing()
        );
    }
}