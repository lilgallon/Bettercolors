package dev.nero.bettercolors.engine.option;

import dev.nero.bettercolors.engine.io.SettingsUtils;

public class ValueFloatOption extends Option{

    private final float MIN;
    private final float MAX;
    private final float MINOR_TICK_SPC;
    private final float MAJOR_TICK_SPC;
    private float val;

    /**
     * /!\ SUPPORTS 2 DECIMAL POINTS /!\
     *
     * @param prefix prefix of the option (module name for example) -> used to prevent conflict if some modules have
     *               the same option name.
     * @param name the name.
     * @param val the initial value.
     * @param min the minimum value.
     * @param max the maximum value.
     * @param minor_tick_spc the minor tick spacing (for the slider).
     * @param major_tick_spc the major tick spacing (for the slider).
     */
    public ValueFloatOption(
            String prefix, String name,
            float val, float min, float max,
            float minor_tick_spc, float major_tick_spc) {

        super(prefix, name);
        this.val = val;
        MIN = min;
        MAX = max;
        MINOR_TICK_SPC = minor_tick_spc;
        MAJOR_TICK_SPC = major_tick_spc;
    }

    public float getMin() { return MIN; }
    public float getMax() { return MAX; }
    public float getVal() { return val; }
    public float getMajorTickSpacing(){ return MAJOR_TICK_SPC; }
    public float getMinorTickSpacing(){ return MINOR_TICK_SPC; }

    /**
     * It changes the current value of the option. This method test if the value is correct before updating it.
     * @param val the new value.
     */
    public void setVal(float val){
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
        SettingsUtils.setOption(PREFIX + "_" + NAME, Float.toString(val));
    }

    @Override
    public Object clone() {
        return new ValueFloatOption(
                this.PREFIX, this.NAME,
                this.getVal(), this.getMin(), this.getMax(),
                this.getMinorTickSpacing(), this.getMajorTickSpacing()
        );
    }
}
