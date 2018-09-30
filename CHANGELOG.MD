**V1.0:**
- Settings customization
- AimAssist

**V1.1:**
- Smoother aimassist
- Refreshrate setting removed
- Removed comments

**V1.2:**
- Added click assist (increment CPS when attacking with chance %)
- Added attacking time customization (= time in which the hacks are toggled)
- Added aimassist toggle system with HOME key

**V1.3:**
- Hack disabled while in GUI now
- Only left click works now (before right click was ok)
- Added use on mobs setting
- Code cleaning

**V1.4:**
- Debug key (END)
- Improved setting loader

**V1.5**
- Fixed debug

**V1.6**
- Added team tag filter on aim assist and click assist

**V1.7**
- Fixed hack (was not working anymore with team filter turned on)
- The hack stops automatically when no target is found after the beginning of the timer
- Optimized team filter (It does not call useAimAssist & useClickAssist when the target is a teammate)

**V2.0**
- Fixed aim toggle
- Improved hack activation: now it activates when the player is playing (before it worked while he was in Gui / outside minecraft)
- Activation settings: time & number of attacks
- Fixed settings not loaded while starting (before you had to reload to use your settings)
- Added "PAGE_UP" key to toggle click assist
- Added aim assist and click assist activated status in report

**V2.1**
- Changed mod from "better shadows" to "better colors"

**V3.0**
- Very smooth aim
- Aim refresh parameter
- Human / Artificial click assist parameter (artificial bypasses CPS counter)
- Fixed possible freeze

**V4.0**
- Added CPS use only on entity setting (before it was always true V3.0-)
- Fixed a very rare crash
- Auto save settings

**V4.1**
-  Fixed aim step Y (was the default value instead of the customized value)

**V5.0**
- Added console windows output
- Right click desactivate aimassist during a fight
- Fixed aim step Y in report showing the default value

**V5.1**
- Added HWID verification
- Added right SHIFT key to toggle the GUI which is closed by default

**V6.0 : MAJOR UPDATE**

*Everything has been recoded from scratch. Now adding new modules is extremely easy and everything is optimized.*
- Interface :
    - New external interface made to contain a lot of modules,
    - Dynamic components : you can resize it the way you want,
    - A settings management section : you can save, load, and create settings presets.
- Modules :
    - Aim assistance :
        - Added "Stop when reached" option
    - Click Assistance :
        - ...
    - Autosword : automatically use the best sword in your hotbar,
    - Autosprint : automatically sprint when going forward.
- Misc :
    - Overall optimisation,
    - Settings file is now in the game directory,
    - Version checker.