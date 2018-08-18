package com.bettercolors.modules.options;

import com.bettercolors.io.SettingsUtils;

public class ValueOption extends Option {

    private final int MIN;
    private final int MAX;
    private final int MINOR_TICK_SPC;
    private final int MAJOR_TICK_SPC;
    private int _val;

    public ValueOption(String name, int val, int min, int max, int minor_tick_spc, int major_tick_spc) {
        super(name);
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

    @Override
    void saveOption() {
        SettingsUtils.setOption(NAME, Integer.toString(_val));
    }
}
