package com.bettercolors.modules.options;

import com.bettercolors.io.SettingsUtils;

public class ValueOption extends Option {

    private final int MIN;
    private final int MAX;
    private final int MINOR_TICK_SPC;
    private final int MAJOR_TICK_SPC;
    private int _val;

    /**
     * @param prefix prefix of the option (module name for example) -> used to prevent conflict if some modules have the
     *               same option name.
     * @param name the name.
     * @param val the initial value.
     * @param min the minimum value.
     * @param max the maximum value.
     * @param minor_tick_spc the minor tick spacing (for the slider).
     * @param major_tick_spc the major tick spacing (for the slider).
     */
    public ValueOption(String prefix, String name, int val, int min, int max, int minor_tick_spc, int major_tick_spc) {
        super(prefix, name);
        _val = val;
        MIN = min;
        MAX = max;
        MINOR_TICK_SPC = minor_tick_spc;
        MAJOR_TICK_SPC = major_tick_spc;
    }

    public int getMin() { return MIN; }
    public int getMax() { return MAX; }
    public int getVal() { return _val; }
    public int getMajorTickSpacing(){ return MAJOR_TICK_SPC; }
    public int getMinorTickSpacing(){ return MINOR_TICK_SPC; }

    /**
     * It changes the current value of the option. This method test if the value is correct before updating it.
     * @param val the new value.
     */
    public void setVal(int val){
        if(val > MAX) {
            throw new IllegalArgumentException("The value " + Integer.toString(val) +
                    " is bigger than its max : " + Integer.toString(MAX));
        }else if(val < MIN){
            throw new IllegalArgumentException("The value " + Integer.toString(val) +
                    " is lower than its min : " + Integer.toString(MIN));
        }else{
            _val = val;
            saveOption();
        }
    }

    /**
     * It saves the option to the configuration file (the selected one).
     */
    @Override
    void saveOption() {
        SettingsUtils.setOption(PREFIX + "_" + NAME, Integer.toString(_val));
    }
}
