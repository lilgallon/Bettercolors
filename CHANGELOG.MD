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
- Right click deactivate aimassist during a fight
- Fixed aim step Y in report showing the default value

**V5.1**
- Added HWID verification
- Added right SHIFT key to toggle the GUI which is closed by default

**V6.0-b1 : MAJOR UPDATE**
*Everything has been recoded from scratch. Now adding new modules is extremely easy and everything is optimized.*
- Interface :
    - New external interface made to contain a lot of modules,
    - Dynamic components : you can resize it the way you want,
    - A settings management section : you can save, load, and create settings presets.
- Modules :
    - Aim assistance :
        - Added "Stop when reached" option
    - Click Assistance :
        - Fixed the fact that it sometimes stays activated whereas it shouldn't
    - Autosword : automatically use the best sword in your hotbar,
    - Autosprint : automatically sprint when going forward.
- Misc :
    - Overall optimisation,
    - Settings file is now in the game directory,
    - Version checker.
    
**V6.0-b2**
*Settings file of this version are incompatible with the precedents*
- Aim assistance :
   - Fixed random shifts not calculating at each click (#6 - #4)
   - Improved default settings
- Auto sprint :
    - Fixed a crash (#5)
- Settings :
    - Fixed CA team filter and AA team filter (#3)
    - Improved settings design to prevent conflicts
- Interface :
    - Improved option names
    
**V6.0-b3**
- Autosprint :
    - Fixed player sprinting backwards (#11)
- Autosword :
    - Fixed autosword automatically toggling on even if not attacking living entities (#12)
- Miscellaneous
    - Fixed a rare crash probably linked to the fact that there was too much text in the console (#13)
    - Improved version checker
- Interface
    - Added margin in tabs (#9)
    - Changed default toggle key from right shift to insert
    
- Developer section :
    - Changed versioning to semantic versioning
    - Improved documentation to set up the project

**V6.0.0 MC_1.13**

Forge 1.13.2 seems to be stable at this point.
This update is backwards compatible with settings files from v6.0.0-b2 and higher.

- Added error messages in the console if some resources were not loaded
- Improved font rendering (antialiasing)
- Improved error messages (they can be sent even if the window is not ready)
- Fixed crash in mod menu (#21)
- Fixed team filter (#20)
- Fixed & improved version checker
- Fixed resource loading (forge fixed the resource loader, so I had to remove the trick to get it working)
