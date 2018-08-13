package com.bettercolors.modules.options;

public class ValueOption extends Option {

    private int _min;
    private int _max;
    private int _val;

    public ValueOption(String name, int val, int min, int max) {
        super(name);
        _val = val;
        _min = min;
        _max = max;
    }

    public int getMin() { return _min; }
    public int getMax() { return _max; }
    public int getVal() { return _val; }

    public void setVal(int val){
        if(val > _max) {
            throw new IllegalArgumentException("The value " + Integer.toString(val) +
                    " is bigger than its max : " + Integer.toString(_max));
        }else if(val < _min){
            throw new IllegalArgumentException("The value " + Integer.toString(val) +
                    " is lower than its min : " + Integer.toString(_min));
        }else{
            _val = val;
        }
    }
}
