package com.bettercolors.modules.options;

import com.bettercolors.io.SettingsUtils;

public class ToggleOption extends Option {

    private boolean _activated;

    /**
     * @param prefix prefix of the option (module name for example) -> used to prevent conflict if some modules have the
     *               same option name.
     * @param name name of the option.
     * @param activated the initial state of the option.
     */
    public ToggleOption(String prefix, String name, boolean activated) {
        super(prefix, name);
        _activated = activated;
    }

    /**
     * Changes the state of this option.
     * @param activated the new state of this option.
     */
    public void setActivated(boolean activated){
        _activated = activated;
        saveOption();
    }

    /**
     * It changes the state of this option according to its current state. IT TOGGLES IT LOL!
     */
    public void toggle(){
        _activated = !_activated;
        saveOption();
    }

    /**
     * @return the state of the option : activated or not.
     */
    public boolean isActivated(){
        return _activated;
    }

    /**
     * It saves the option to the configuration file (the selected one).
     */
    @Override
    void saveOption(){
        SettingsUtils.setOption(PREFIX + "_" + NAME, Boolean.toString(_activated));
    }
}
