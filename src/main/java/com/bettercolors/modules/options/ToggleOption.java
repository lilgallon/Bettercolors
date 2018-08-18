package com.bettercolors.modules.options;

import com.bettercolors.io.SettingsUtils;

public class ToggleOption extends Option {

    private boolean _activated;

    public ToggleOption(String name, boolean activated) {
        super(name);
        _activated = activated;
    }

    @Override
    void saveOption(){
        SettingsUtils.setOption(NAME, Boolean.toString(_activated));
    }

    public void setActivated(boolean activated){
        _activated = activated;
        saveOption();
    }
    public void toggle(){
        _activated = !_activated;
        saveOption();
    }

    public boolean isActivated(){
        return _activated;
    }
}
