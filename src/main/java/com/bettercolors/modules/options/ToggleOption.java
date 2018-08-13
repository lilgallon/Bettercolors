package com.bettercolors.modules.options;

public class ToggleOption extends Option {

    private boolean _activated;

    public ToggleOption(String name, boolean activated) {
        super(name);
        _activated = activated;
    }

    public boolean isActivated(){ return _activated; }
    public void toggle(){ _activated = !_activated; }
}
